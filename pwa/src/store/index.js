import { createStore } from 'vuex'

export default createStore({
  state: {
    // GenerateCardView
    presentIndex: 0,
    mostNewReadIndex: 0,

    goBack2MostNew: false,

    // AddCardView
    inInputCardData: false,
    inPreview: false,

    // ChangeCardView
    inChangeCard: false,

    // GitHub connection management
    disableCUD: false
  },
  mutations: {
    saveSwipeStatus(state, data) {
        // console.log("enter mutations data = ", data);

        // if directly 'state = data' , the reference will lost, can not be track.
        state.presentIndex = data.presentIndex
        state.mostNewReadIndex = data.mostNewReadIndex
    },
    setGoBack2MostNew(state, data) {
      state.goBack2MostNew = data
    },
    setInInputCardData(state, data) {
      state.inInputCardData = data
    },
    setInPreview(state, data) {
      state.inPreview = data
    },
    setInChangeCard(state, data) {
      state.inChangeCard = data
    },
    setDisableCUD(state, status) {
      state.disableCUD = status
    }
  },
  getters: {
  },
  modules: {
  }
})