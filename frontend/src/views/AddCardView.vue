<template>
    <div v-if="inInputCardData">
        <form id="form">
            <cell-group v-if="cardData.type == 0" inset>
                <input-with-tips class="item"
                    label="标题"
                    v-model="cardData.literature.title" >
                </input-with-tips>
                <input-with-tips class="item"
                    label="译题"
                    v-model="cardData.literature.translationTitle" >
                </input-with-tips>
                <input-with-tips class="item"
                    label="作者"
                    cardTypeName="literature" cardFieldName="author"
                    v-model="cardData.literature.author" >
                </input-with-tips>
                <input-with-tips class="item"
                    label="译者"
                    cardTypeName="literature" cardFieldName="translator"
                    v-model="cardData.literature.translator" >
                </input-with-tips>
                <input-with-tips class="item"
                    label="书籍"
                    cardTypeName="literature" cardFieldName="book"
                    v-model="cardData.literature.book" >
                </input-with-tips>
                <input-with-tips class="item"
                    label="版本"
                    cardTypeName="literature" cardFieldName="edition"
                    v-model="cardData.literature.edition" >
                </input-with-tips>
                <input-with-tips class="item"
                    label="备注"
                    cardTypeName="literature" cardFieldName="note"
                    v-model="cardData.literature.note" >
                </input-with-tips>
                <field class="item" label="原文：" v-model="cardData.literature.originalText" type="textarea" autosize rows="6"
                @focus="onFocus()" @mousedown="onMousedown()"></field>
                <field class="item" label="译文：" v-model="cardData.literature.translationText" type="textarea" autosize rows="6"
                @focus="onFocus()" @mousedown="onMousedown()"></field>
            </cell-group>
            <cell-group v-if="cardData.type == 1" inset>
                <input-with-tips class="item"
                    label="标题"
                    v-model="cardData.song.title" >
                </input-with-tips>
                <input-with-tips class="item"
                    label="译题"
                    v-model="cardData.song.translationTitle" >
                </input-with-tips>
                <input-with-tips class="item"
                    label="演唱"
                    cardTypeName="song" cardFieldName="voice"
                    v-model="cardData.song.voice" >
                </input-with-tips>
                <input-with-tips class="item"
                    label="作曲"
                    cardTypeName="song" cardFieldName="compose"
                    v-model="cardData.song.compose" >
                </input-with-tips>
                <input-with-tips class="item"
                    label="编曲"
                    cardTypeName="song" cardFieldName="arrangement"
                    v-model="cardData.song.arrangement" >
                </input-with-tips>
                <input-with-tips class="item"
                    label="作词"
                    cardTypeName="song" cardFieldName="lyric"
                    v-model="cardData.song.lyric" >
                </input-with-tips>
                <input-with-tips class="item"
                    label="专辑"
                    cardTypeName="song" cardFieldName="album"
                    v-model="cardData.song.album" >
                </input-with-tips>
                <input-with-tips class="item"
                    label="出处"
                    cardTypeName="song" cardFieldName="source"
                    v-model="cardData.song.source" >
                </input-with-tips>
                <input-with-tips class="item"
                    label="备注"
                    cardTypeName="song" cardFieldName="note"
                    v-model="cardData.song.note" >
                </input-with-tips>
                <field class="item" label="原文：" v-model="cardData.song.originalText" type="textarea" autosize rows="6"
                @focus="onFocus()" @mousedown="onMousedown()"></field>
                <field class="item" label="译文：" v-model="cardData.song.translationText" type="textarea" autosize rows="6"
                @focus="onFocus()" @mousedown="onMousedown()"></field>
            </cell-group>
            <cell-group v-if="cardData.type == 2" inset>
                <input-with-tips class="item"
                    label="语者"
                    cardTypeName="words" cardFieldName="sayer"
                    v-model="cardData.words.sayer" >
                </input-with-tips>
                <input-with-tips class="item"
                    label="译者"
                    cardTypeName="words" cardFieldName="translator"
                    v-model="cardData.words.translator" >
                </input-with-tips>
                <input-with-tips class="item"
                    label="来源"
                    cardTypeName="words" cardFieldName="source"
                    v-model="cardData.words.source" >
                </input-with-tips>
                <input-with-tips class="item"
                    label="备注"
                    cardTypeName="words" cardFieldName="note"
                    v-model="cardData.words.note" >
                </input-with-tips>
                <field class="item" label="原文：" v-model="cardData.words.originalText" type="textarea" autosize rows="6"
                @focus="onFocus()" @mousedown="onMousedown()"></field>
                <field class="item" label="译文：" v-model="cardData.words.translationText" type="textarea" autosize rows="6"
                @focus="onFocus()" @mousedown="onMousedown()"></field>
            </cell-group>
        </form>
        <button class="button" @mousedown.native="enterPreview">预览</button>
    </div>
    <div v-else-if="inPreview">
        <card-view id="card-view" :card="getCardData()" :preview-mode="true"></card-view>
        <div id="quit-preview-tabbar">
            <button id="quit-preview-button" @click="quitPreview" type="primary">退出预览</button>
        </div>
        <button class="button" id="submit-button" type="primary" @click="onSubmit()">提交</button>
    </div>
    <div v-else id="type-select">
        <div v-if="changeMode" id="changeModeNote">修改类别</div>
        <button v-if="changeMode" @click="select(cardData.type)" class="button">不修改类别</button>
        <button @click="select(0)" class="button">文学</button>
        <button @click="select(1)" class="button">歌曲</button>
        <button @click="select(2)" class="button">言语</button>
    </div>
</template>


<script setup>
import { Button, datePickerProps } from 'vant';
import { Form, Field, CellGroup } from 'vant';
import InputWithTips from '@/components/InputWithTips.vue';

import { reactive, ref } from 'vue';
import { computed } from 'vue';

import CardView from './CardView.vue';
import {cardUtil} from "@/js/cardUtil"

const cardData = reactive({
    id: null,
    type: null,
    
    literature: {
        title: "",
        translationTitle: "",
        originalText: "",
        translationText: "",
        author: "",
        translator: "",
        book: "",
        edition: "",
        note: ""
    },
    song: {
        title : "",
        translationTitle : "",
        originalText : "",
        translationText : "",
        lyric : "",
        voice : "",
        compose : "",
        arrangement : "",
        album : "",
        source : "",
        note: ""
    },
    words: {
        originalText: "",
        translationText: "",
        sayer: "",
        translator: "",
        source: "",
        note: ""
    }
})

function getCardData() {
    // console.log("cardData = ", cardData);
    let id = cardData.id
    let type = cardData.type
    let content = {}
    if (type == 0) {
        Object.assign(content, cardData.literature)
    } else if (type == 1) {
        Object.assign(content, cardData.song)
    } else if (type == 2) {
        Object.assign(content, cardData.words)
    } else {
        console.log("error type! type = ", type)
    }
    return {id: id, type: type, content: content}
}


/**
 * for change mode
 */

const cardProp = defineProps(['cardId'])
const changeMode = ref(false)

if (cardProp.cardId != null) {
    changeMode.value = true

    cardUtil.getCard(cardProp.cardId).then(resp => {
        let data = resp.data.data
        console.log("get Card data = ", data);
        // if do not delete these ignored atteributes, 
        // they will be upload to GitHub json when update card.
        delete data.content.createTime
        delete data.content.updateTime
        delete data.content.deleted
        
        cardData.id = data.id
        cardData.type = data.type
        if (data.type == 0) {
            Object.assign(cardData.literature, data.content)
        } else if (data.type == 1) {
            Object.assign(cardData.song, data.content)
        } else if (data.type == 2) {
            Object.assign(cardData.words, data.content)
        } else {
            console.log("error type!")
        }

        // console.log("set cardData = ", cardData);

    })
}


// select card type
import { useStore } from 'vuex';
import { inject } from 'vue';

const store = useStore()  // 该方法用于返回 store 实例

const setInInputCardData = (data) => {
    store.commit('setInInputCardData', data)
}

const disableCUD = computed(() => {
    return store.state.disableCUD
})

const select = (type) => {
    // console.log("check cardData type = ", cardData.type);
    if (disableCUD == true) {
        return
    }

    cardData.type = type
    
    setInInputCardData(true)
}

const inInputCardData = computed(() => {
    return store.state.inInputCardData
})

// preview


const checkAllNull = () => {
    let content = getCardData().content
    let hasData = false
    for (let i in content) {
        if (content[i] != "" && content[i] != null) {
            hasData = true
        }
    }
    // console.log("checkAllNull cardData = ", cardData);
    return ! hasData
}

const setInPreview = (data) => {
    store.commit('setInPreview', data)
}

const inPreview = computed(() => {
    return store.state.inPreview
})

const enterPreview = (e) => {

    e.preventDefault()
    // console.log(e);

    // mouseDown 的默认事件被阻止，用于阻止 blur 事件。
    // button 的点击 active 状态颜色变换事件发生在 mousedown 之后，所以设置 timeout ，变色后再进入 preview 。
    setTimeout(() => {
        if (checkAllNull()) {
            alert("card 数据不能全部为空！")
            return
        }
        setInInputCardData(false)
        setInPreview(true)
        goTop()
        // console.log("enterPreview cardData = ", cardData);
    }, 20)
}

const quitPreview = () => {
    setInPreview(false)
    setInInputCardData(true)
    setTimeout(() => {
        goBottom()
        // console.log("quitPreview cardData = ", cardData);
    }, 10)
}

// view position control by scroll


const goPosition = inject('goPosition')

const goTop = () => {
    goPosition('top')
}

const goBottom = () => {
    goPosition('bottom')
}

// submit data


function resetObject(object) {
    for (let i in object) {
        object[i] = ""
    }
}

function resetCardData() {
    cardData.type = null
    resetObject(cardData.literature)
    resetObject(cardData.song)
    resetObject(cardData.words)
}


function resetAddView() {
    setInPreview(false)
    setInInputCardData(false)
    resetCardData()
    goTop()
}

if (changeMode.value) {
    var refreshAfterUpdate = inject('refreshAfterUpdate')
}

function onSubmit() {
    if (changeMode.value) {
        if (!confirm("确认提交修改吗？")) {
            changeMode.value = false
            return
        }
    }

    let data = getCardData()
    // console.log("submit data = ", data);

    if (changeMode.value) {
        cardUtil.submitUpdateCardData(data).then(resp => {
            resetAddView()
            refreshAfterUpdate(data.id)
        })
    } else {
        cardUtil.submitAddCardData(data).then(resp => {
            resetAddView()
        })
    }

}


// to control page scroller jump when blur and focus

const goRecord = () => {
    goPosition('go-record')
}

const recordPosition = () => {
    goPosition('record')
}

// const onBlur = () => {
//     console.log("blur");
// }

const onFocus = () => {
    // console.log("focus");
    goRecord()
}

const onMousedown = () => {
    // console.log("mousedown");
    recordPosition()
}

</script>

<style>
#page {
    height: 100vh;
    overflow-y: scroll;
}

.van-field__control {
    width: 90vw;
}

.van-field__label {
    font-weight: bold;
}
</style>

<style scoped>

.button:active {
    background-color: gray;
}

#type-select {
    height: 100vh;
    padding-top: 20vh;
}

#type-select button {
    margin: 5vh 35vw;
}

#form {
    width: 100vw;
}

.item {
    width: 90vw;
    margin: 0 auto 3vh;
}

.button {
    width: 30vw;
    height: 6vh;
    box-sizing: border-box;
    border: 1px solid black;
    margin: 8vh 35vw 18vh;
    border-radius: 1vw;
}

#quit-preview-tabbar {
    position: absolute;
    bottom: 0;
    height: 6vh;
    width: 100vw;
    text-align: center;
}

#quit-preview-button {
    /* height: 5vh; */
    width: 20vw;
    box-sizing: border-box;
    border: 1px solid black;
    border-radius: 2vw;
}

#quit-preview-button:active {
    background-color: gray;
}

#submit-button {
    margin-top: 3vh;
}

#changeModeNote {
    width: 90vw;
    margin: 0 auto 5vh;
    text-align: center;
    background-color: white;
    font-size: 45px;
}

#card-view {
    background-color: white;
}

</style>