import axios from "axios"

// 使用跨域
// axios.defaults.baseURL = "/api"

// 不使用跨域
axios.defaults.baseURL = "http://localhost:8080"

export const cardUtil = {

    historyType: {
        READ: 0,
        ADD: 1,
        UPDATE: 2,
        DELETE: 3,
        READINALL: 4
    },

    NoCardId: null,

    async getCard(id) {
        let url = "/card?id=" + id
        let resp = await axios.get(url)
        return resp
    },

    async getLatestCardIds(latestNumber = 1) {
        let url = "/history?latestNumber=" + latestNumber
        let resp = await axios.get(url)
        return resp
    },

    async getNewCardId() {
        let url = "/newCardId"
        let resp = await axios.get(url)
        return resp
    },

    async pushReadHistory(cardId, type=cardUtil.historyType.READ) {

        if (! type in [0, 4]) {
            console.log("### only READ and READINALL historyType be submited by frontend ###");
            return false
        }
        if (cardId >= 1) {
            console.log("pushReadHistory, cardId = ", cardId, "type = ", type);
            let url = "/history?cardId=" + cardId + "&type=" + type
            axios.post(url)
        } else {
            console.log("cardId error, cardId = ", cardId);
            return false
        }
    },

    async submitAddCardData(cardData) {
        console.log("addCard, cardData = ", cardData);
        let url = "/addCard"
        let resp = await axios.post(url, cardData)
        return resp
    },
    
    async submitUpdateCardData(cardData) {
        console.log("updateCard, cardData = ", cardData);
        let url = "/updateCard"
        let resp = await axios.post(url, cardData)
        return resp
    },

    async getAllCardIds() {
        let url = "/allCardIds"
        let resp = await axios.get(url)
        return resp
    },

    async getLatestReadInAllCardIds(latestNumber = 1) {
        let url = "/latestReadInAllCardIds?latestNumber=" + latestNumber
        let resp = await axios.get(url)
        return resp
    },

    async searchCard(keyword) {
        let url = "/searchCard?keyword=" + keyword
        let resp = await axios.get(url)
        return resp
    },

    async searchCardField(cardTypeName, cardFieldName, keyword) {
        let url = "/searchCardField?cardTypeName="+ cardTypeName +"&cardFieldName="+ cardFieldName +"&keyword="+ keyword
        let resp = await axios.get(url)
        return resp
    },

    async latestCardField(cardTypeName, cardFieldName, number=5) {
        let url = "/latestCardField?cardTypeName="+ cardTypeName +"&cardFieldName="+ cardFieldName +"&number="+ number
        let resp = await axios.get(url)
        return resp
    },

    async deleteCard(cardId) {
        if (cardId < 1) {
            console.log("error cardId ! cardId = ", cardId);
            return false
        }
        let url = "/deleteCard?cardId=" + cardId
        let resp = await axios.post(url)
        return resp
    },

    async tryConnectGitHub() {

        let url = "/tryConnectGitHub"
        let resp = await axios.get(url)
        if (resp.data.code == 1) {
            console.log("connect GitHub success!");
            return true
        } else {
            alert("Cannot connect to GitHub! Add, Update, and Delete Operation has been disabled! \n Please check network or set the GitHub info!")
            return false
        }
    },

    async syncData() {
        
        let url = "/sync"
        console.log("data is on sync");
        await axios.post(url)
    },

    async setGitHubInfo(data) {
        let url = "/setGitHubInfo"
        let resp = await axios.post(url, data)
        if (resp.data.code == 1) {
            console.log("set info success!");
        } else {
            alert("The info is not valid!")
        }
    },

    async closeBackend() {
        let url = "/quit"
        await axios.post(url)
    }
    
}