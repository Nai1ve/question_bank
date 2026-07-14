const { getActiveRecitePlan, listRecitePlanDays } = require('../../services/recite');

const MODE_CONFIGS = [
  {
    mode: 'cn_to_en',
    label: '中译英',
    completedKey: 'cnToEnCompleted',
    recordIdKey: 'cnToEnRecordId',
    accuracyKey: 'cnToEnAccuracy'
  },
  {
    mode: 'en_to_cn',
    label: '英译中',
    completedKey: 'enToCnCompleted',
    recordIdKey: 'enToCnRecordId',
    accuracyKey: 'enToCnAccuracy'
  }
];

function buildDayCards(days) {
  return days.map((item) => {
    const modeItems = buildModeItems(item);
    const completedModeCount = modeItems.filter((modeItem) => modeItem.completed).length;
    const modeProgressPercent = completedModeCount * 50;
    return {
      ...item,
      modeItems,
      completedModeCount,
      modeProgressStyle: `width: ${modeProgressPercent}%;`,
      className: item.status === 'COMPLETED' ? 'surface-card day-card day-card-completed' : 'surface-card day-card',
      statusText: resolveStatusText(item, completedModeCount),
      actionText: resolveActionText(item, completedModeCount),
      actionClass: item.status === 'PENDING_STUDY' ? 'day-action day-action-muted' : 'day-action'
    };
  });
}

function buildModeItems(day) {
  return MODE_CONFIGS.map((config) => {
    const completed = Boolean(day[config.completedKey]);
    const accuracy = day[config.accuracyKey] || '';
    return {
      mode: config.mode,
      label: config.label,
      completed,
      recordId: day[config.recordIdKey] || '',
      accuracy,
      statusText: completed ? (accuracy || '已完成') : (day.status === 'PENDING_STUDY' ? '待学习' : '待完成'),
      className: completed ? 'mode-chip mode-chip-completed' : 'mode-chip'
    };
  });
}

function resolveStatusText(day, completedModeCount) {
  if (day.status === 'COMPLETED') {
    return '已完成';
  }
  if (day.status === 'PENDING_TEST') {
    if (completedModeCount > 0) {
      return `已完成 ${completedModeCount}/2`;
    }
    return '待测试';
  }
  return '待学习';
}

function resolveActionText(day, completedModeCount) {
  if (day.status === 'COMPLETED') {
    return '查看结果或重学';
  }
  if (day.status === 'PENDING_TEST') {
    return completedModeCount > 0 ? '继续未完成测试' : '开始测试';
  }
  return '先学习';
}

function buildModeActions(day) {
  const startActions = [];
  const summaryActions = [];
  MODE_CONFIGS.forEach((config) => {
    const completed = Boolean(day[config.completedKey]);
    const recordId = day[config.recordIdKey];
    if (!completed) {
      startActions.push({
        type: 'start',
        label: `开始${config.label}`,
        mode: config.mode
      });
      return;
    }
    if (recordId) {
      summaryActions.push({
        type: 'summary',
        label: `查看${config.label}结果`,
        recordId
      });
    }
  });
  return startActions.concat(summaryActions, {
    type: 'study',
    label: '重新学习并测试'
  });
}

Page({
  data: {
    planId: '',
    plan: null,
    days: []
  },

  onLoad(options) {
    this.setData({
      planId: options.planId || ''
    });
  },

  onShow() {
    this.loadPage();
  },

  async loadPage() {
    try {
      let planId = this.data.planId;
      if (!planId) {
        const activePlan = await getActiveRecitePlan();
        planId = activePlan && activePlan.planId ? String(activePlan.planId) : '';
      }

      if (!planId) {
        wx.showToast({
          title: '当前暂无背诵计划',
          icon: 'none'
        });
        setTimeout(() => {
          wx.redirectTo({
            url: '/pages/recite-plan/index'
          });
        }, 250);
        return;
      }

      const response = await listRecitePlanDays(planId);
      this.setData({
        planId: String(response.plan.planId),
        plan: response.plan,
        days: buildDayCards(response.days)
      });
    } catch (error) {
      wx.showToast({
        title: (error && error.message) || 'Day 列表加载失败',
        icon: 'none'
      });
    }
  },

  handleBack() {
    wx.navigateBack();
  },

  handleOpenDay(event) {
    const { dayNumber } = event.currentTarget.dataset;
    const currentDay = this.data.days.find((item) => String(item.dayNumber) === String(dayNumber));
    if (!currentDay) {
      return;
    }

    if (currentDay.status === 'COMPLETED') {
      this.handleOpenCompletedDay(currentDay);
      return;
    }

    if (currentDay.status === 'PENDING_TEST') {
      this.handleOpenModePicker(currentDay);
      return;
    }

    this.openStudyPage(dayNumber);
  },

  handleOpenCompletedDay(day) {
    const actions = buildModeActions(day);
    wx.showActionSheet({
      itemList: actions.map((item) => item.label),
      success: ({ tapIndex }) => {
        this.handleDayAction(day, actions[tapIndex]);
      }
    });
  },

  handleOpenModePicker(day) {
    const actions = buildModeActions(day);
    wx.showActionSheet({
      itemList: actions.map((item) => item.label),
      success: ({ tapIndex }) => {
        this.handleDayAction(day, actions[tapIndex]);
      }
    });
  },

  handleDayAction(day, action) {
    if (!action) {
      return;
    }
    if (action.type === 'summary') {
      wx.navigateTo({
        url: `/pages/recite-summary/index?recordId=${action.recordId}&planId=${this.data.planId}`
      });
      return;
    }
    if (action.type === 'start') {
      wx.navigateTo({
        url: `/pages/recite-session/index?planId=${this.data.planId}&dayNumber=${day.dayNumber}&mode=${action.mode}`
      });
      return;
    }
    this.openStudyPage(day.dayNumber);
  },

  openStudyPage(dayNumber) {
    wx.navigateTo({
      url: `/pages/recite-study/index?planId=${this.data.planId}&dayNumber=${dayNumber}`
    });
  }
});
