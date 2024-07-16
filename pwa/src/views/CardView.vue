<template>
    <div id="nullNote" v-if="(cardProp.cardId == cardUtil.NoCardId) && (cardProp.card == null) || 
        (card.type == null && card.content == null)">No card!</div>
    <div id="card" v-else>
        
        <ol id="literature" v-if="card.type == 0">
            <li class="title card-content" v-show="card.content.title">
                {{ card.content.title }}
            </li>
            <li class="text" v-show="card.content.originalText">
                <div v-html="parse_text(card.content.originalText)"></div>
            </li>
            <li class="title card-content" v-show="card.content.translationTitle">
                {{ card.content.translationTitle }}
            </li>
            <li class="text" v-show="card.content.translationText">
                <div v-html="parse_text(card.content.translationText)"></div>
            </li>
            <div class="info">
                <li>
                    <span class="info-item">作者：</span>
                    <span class="card-content">
                        {{ card.content.author }}
                    </span>
                </li>
                <li>
                    <span class="info-item">译者：</span>
                    <span class="card-content">
                        {{ card.content.translator }}
                    </span>
                </li>
                <li>
                    <span class="info-item">书籍：</span>
                    <span class="card-content">
                        {{ card.content.book }}
                    </span>
                </li>
                <li>
                    <span class="info-item">版本：</span>
                    <span class="card-content">
                        {{ card.content.edition }}
                    </span>
                </li>
                <li>
                    <span class="info-item">备注：</span>
                    <span class="card-content">
                        {{ card.content.note }}
                    </span>
                </li>
            </div>
        </ol>

        <ol id="song" v-else-if="card.type == 1">
            <li class="title card-content" v-show="card.content.title">
                {{ card.content.title }}
            </li>
            <li class="text" v-show="card.content.originalText">
                <div v-html="parse_text(card.content.originalText)"></div>
            </li>
            <li class="title card-content" v-show="card.content.translationTitle">
                {{ card.content.translationTitle }}
            </li>
            <li class="text" v-show="card.content.translationText">
                <div v-html="parse_text(card.content.translationText)"></div>
            </li>
            <div class="info">
                <li>
                    <span class="info-item">演唱：</span>
                    <span class="card-content">
                        {{ card.content.voice }}
                    </span>
                </li>
                <li>
                    <span class="info-item">作曲：</span>
                    <span class="card-content">
                        {{ card.content.compose }}
                    </span>
                </li>
                <li>
                    <span class="info-item">编曲：</span>
                    <span class="card-content">
                        {{ card.content.arrangement }}
                    </span>
                </li>
                <li>
                    <span class="info-item">作词：</span>
                    <span class="card-content">
                        {{ card.content.lyric }}
                    </span>
                </li>
                <li>
                    <span class="info-item">专辑：</span>
                    <span class="card-content">
                        {{ card.content.album }}
                    </span>
                </li>
                <li>
                    <span class="info-item">出处：</span>
                    <span class="card-content">
                        {{ card.content.source }}
                    </span>
                </li>
                <li>
                    <span class="info-item">备注：</span>
                    <span class="card-content">
                        {{ card.content.note }}
                    </span>
                </li>
            </div>

        </ol>

        <ol id="words" v-else-if="card.type == 2">
            <li class="text" v-show="card.content.originalText">
                <div v-html="parse_text(card.content.originalText)"></div>
            </li>
            <li class="text" v-show="card.content.translationText">
                <div v-html="parse_text(card.content.translationText)"></div>
            </li>
            <div class="info">
                <li>
                    <span class="info-item">语者：</span>
                    <span class="card-content">
                        {{ card.content.sayer }}
                    </span>
                </li>
                <li>
                    <span class="info-item">译者：</span>
                    <span class="card-content">
                        {{ card.content.translator }}
                    </span>
                </li>
                <li>
                    <span class="info-item">来源：</span>
                    <span class="card-content">
                        {{ card.content.source }}
                    </span>
                </li>
                <li>
                    <span class="info-item">备注：</span>
                    <span class="card-content">
                        {{ card.content.note }}
                    </span>
                </li>
            </div>
        </ol>
        <ol v-else>
            <!-- <li class="title">error! no data</li> -->
        </ol>

        <div v-if=" ! changeMode && ! cardProp.previewMode" id="change-entrance" @click="enterChangeCard()" > 修改 </div>
        <change-card-view v-if="changeMode && ! cardProp.previewMode" id="change-view" :cardId="cardProp.cardId"></change-card-view>

    </div>
</template>

<script setup>
import {cardUtil} from "@/js/cardUtil"
import {watch, reactive, computed} from "vue"
import ChangeCardView from "./ChangeCardView.vue";

const cardProp = defineProps(['cardId', 'card', 'previewMode'])
const card = reactive({
    type: null,
    content: null,
})

if (cardProp.cardId != null && cardProp.cardId != cardUtil.NoCardId) {
    cardUtil.getCard(cardProp.cardId).then(data => {
        card.type = data.type
        card.content = data.content
    })
} else if (cardProp.card != null) {
    // for preview card
    card.type = cardProp.card.type
    card.content = cardProp.card.content
}

watch(cardProp, (newCardProp, oldCardProp) => {
    // console.table({newCardProp, oldCardProp})
    if (newCardProp.cardId == cardUtil.NoCardId) {
        card.type = null
        card.content = null
    } else {
        cardUtil.getCard(newCardProp.cardId).then(data => {
            // console.log("get card data = ", data);
            if (data == null) {
                card.type = null
                card.content = null                
            } else {
                card.type = data.type
                card.content = data.content
            }
        })
    }
})

function parse_text(text) {
    if (text == null) {
        return text
    }

    // 末尾有换行也不影响显示
    // 换分 p ，以便用 margin 控制主动换行的间距
    
    // console.log("enter text = ", text);
    // for (let i = 0; i < text.length; ++i) {
    //     console.log(text.charAt(i));
    // }

    // 半角空格
    text = text.replaceAll(" ", "&nbsp;")
    // text = text.replaceAll(" ", "x")

    // 全角空格
    text = text.replaceAll("　", "&emsp;")

    // 换段
    text = text.replaceAll("\n\n", "</p><p class='card-content'> &nbsp; </p><p class='card-content'>")
    text = text.replaceAll("\n\r", "</p><p class='card-content'> &nbsp; </p><p class='card-content'>")
    // console.log("replace paragraph text = ", text);

    // 换行
    text = text.replaceAll("\n", "</p><p class='card-content'>")
    // text = text.replaceAll("\n", "x")
    // console.log("replave line text = ", text);

    // 首尾填补
    text = "<p class='card-content'>" + text + "</p>"
    // console.log("add head and tail text = ", text)

    return text
}

/**
 * enter change mode
 */
import { useStore } from "vuex";

const store = useStore()

const changeMode = computed(() => {
    return store.state.inChangeCard
})

const disableCUD = computed(() => {
    return store.state.disableCUD
})

function enterChangeCard() {
    if (disableCUD == true) {
        return
    }
    store.commit('setInChangeCard', true)
}

</script>

<style scoped>

#card {
    position: relative;
    padding-top: 15vh;
}

ol {
    text-align: right; 
}

li {
    /* 辅助样式 */
    /* border: solid 0.5px black; */
    /* background-color: aqua; */

    min-height: 3.5vh;
}

li .info-item {
    font-size: 0.5em;
    font-weight: bold;
}

.title, #nullNote {
    text-align: center;
    padding: 0 3vw 10vh;
    font-weight: bold;
    font-size: 1.3em;
}

.text {
    min-height: 20vh;
    text-align: center;
}

.text div {
    /* 辅助样式 */
    /* border: 1px solid red; */

    display: inline-block;
    text-align: left;
    padding: 20px 5vw;
}

#words .text div {
    /* words 一般比较短，上间隔更大一点 */
    padding: 15vh 5vw;
}

ol .info {
    display: inline-block;
    text-align: left;
    padding-right: 6vh;
    padding-bottom: 10vh;
    margin-top: 10vh;
}


#change-entrance {
    width: 100px;
    height: 45px;
    border-radius: 10px;
    border: 1px solid black;
    position: absolute;
    left: 10vw;
    bottom: 5vh;
    text-align: center;
    padding-bottom: 8px;
}

#change-entrance:active {
    background-color: gray;
}
</style>

<style>

.text div p {
    /* 让主动换行比自动换行间距更大，但不要太大*/
    line-height: 1.2em;
    margin-top: 0.5em;   /* 主动换行的句间距 */
}
</style>