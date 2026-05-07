Component({
  data: {
    selected: 0,
    tabs: [
      {
        pagePath: '/pages/study-home/index',
        text: '学习'
      },
      {
        pagePath: '/pages/mine/index',
        text: '我的'
      }
    ]
  },

  methods: {
    setSelected(selected) {
      this.setData({ selected });
    },

    handleSwitch(event) {
      const { path, index } = event.currentTarget.dataset;
      if (Number(index) === this.data.selected) {
        return;
      }
      this.setData({ selected: Number(index) });
      wx.switchTab({ url: path });
    }
  }
});
