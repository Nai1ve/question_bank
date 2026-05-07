const { getActiveRecitePlan, listRecitePlanDays } = require('../../services/recite');

function buildDayCards(days) {
  return days.map((item) => ({
    ...item,
    className: item.status === 'COMPLETED' ? 'surface-card day-card day-card-completed' : 'surface-card day-card',
    statusText: resolveStatusText(item.status),
    actionText: resolveActionText(item.status)
  }));
}

function resolveStatusText(status) {
  if (status === 'COMPLETED') {
    return '已完成';
  }
  if (status === 'PENDING_TEST') {
    return '待测试';
  }
  return '待学习';
}

function resolveActionText(status) {
  if (status === 'COMPLETED') {
    return '查看结果或重学';
  }
  if (status === 'PENDING_TEST') {
    return '继续测试';
  }
  return '先学习';
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
      this.handleOpenModePicker(dayNumber);
      return;
    }

    this.openStudyPage(dayNumber);
  },

  handleOpenCompletedDay(day) {
    wx.showActionSheet({
      itemList: ['查看最近一次结果', '重新学习并测试'],
      success: ({ tapIndex }) => {
        if (tapIndex === 0) {
          if (!day.latestRecordId) {
            wx.showToast({
              title: '当前暂无可查看结果',
              icon: 'none'
            });
            return;
          }
          wx.navigateTo({
            url: `/pages/recite-summary/index?recordId=${day.latestRecordId}&planId=${this.data.planId}`
          });
          return;
        }
        this.openStudyPage(day.dayNumber);
      }
    });
  },

  handleOpenModePicker(dayNumber) {
    wx.showActionSheet({
      itemList: ['开始中译英', '开始英译中'],
      success: ({ tapIndex }) => {
        const mode = tapIndex === 0 ? 'cn_to_en' : 'en_to_cn';
        wx.navigateTo({
          url: `/pages/recite-session/index?planId=${this.data.planId}&dayNumber=${dayNumber}&mode=${mode}`
        });
      }
    });
  },

  openStudyPage(dayNumber) {
    wx.navigateTo({
      url: `/pages/recite-study/index?planId=${this.data.planId}&dayNumber=${dayNumber}`
    });
  }
});
