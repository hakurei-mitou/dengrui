<template>
    <div id="nullNote" v-if="cardProp.cardId == cardUtil.NoCardId">No card!</div>
    <div v-else>
        <ol id="literature" v-if="card.type == 0">
            <li class="title card-content" v-show="card.content.title">
                {{ card.content.title }}
            </li>
            <li class="title card-content" v-show="card.content.translationTitle">
                {{ card.content.translationTitle }}
            </li>
            <li class="text" v-show="card.content.originalText">
                <div class="card-content" v-html="card.content.originalText"></div>
            </li>
            <div class="info">
                <li>
                    <span class="info-item">作者：</span>
                    <span class="card-content">
                        {{ card.content.author }}
                    </span>
                </li>
                <li>
                    <span class="info-item">书籍：</span>
                    <span class="card-content">
                        {{ card.content.book }}
                    </span>
                </li>
            </div>
        </ol>

        <ol id="song" v-else-if="card.type == 1">
            <li class="title card-content" v-show="card.content.title">
                {{ card.content.title }}
            </li>
            <li class="text" v-show="card.content.originalText">
                <div class="card-content" v-html="card.content.originalText"></div>
            </li>
            <li class="title card-content" v-show="card.content.translationTitle">
                {{ card.content.translationTitle }}
            </li>
            <div class="info">
                <li>
                    <span class="info-item">演唱：</span>
                    <span class="card-content">
                        {{ card.content.voice }}
                    </span>
                </li>
                <li>
                    <span class="info-item">出处：</span>
                    <span class="card-content">
                        {{ card.content.source }}
                    </span>
                </li>
            </div>

        </ol>

        <ol id="words" v-else-if="card.type == 2">
            <li class="text" v-show="card.content.originalText">
                <div class="card-content" v-html="card.content.originalText"></div>
            </li>
            <div class="info">
                <li>
                    <span class="info-item">语者：</span>
                    <span class="card-content">
                        {{ card.content.sayer }}
                    </span>
                </li>
                <li>
                    <span class="info-item">来源：</span>
                    <span class="card-content">
                        {{ card.content.source }}
                    </span>
                </li>
            </div>
        </ol>
        <ol v-else>
            <li>no data</li>
        </ol>
    </div>
</template>

<script setup>
import {cardUtil} from "@/js/cardUtil"
import {watch, reactive} from "vue"

const cardProp = defineProps(['cardId'])

const card = reactive({
    type: null,
    content: null,
})

if (cardProp.cardId != null && cardProp.cardId != cardUtil.NoCardId) {
    cardUtil.getCard(cardProp.cardId).then(data => {
        card.type = data.type
        card.content = data.content
    })
}

watch(cardProp, (newCardProp, oldCardProp) => {
    // console.table({newCardProp, oldCardProp})
    if (newCardProp.cardId == cardUtil.NoCardId) {
        card.type = null
        card.content = null
    } else {
        cardUtil.getCard(newCardProp.cardId).then(data => {
            card.type = data.type
            card.content = data.content
        })
    }
})

</script>

<style scoped>

ol {
    text-align: right; 
}

li .info-item {
    font-size: 0.5em;
    font-weight: bold;
}

.title, #nullNote {
    text-align: center;
    font-weight: bold;
    font-size: 1em;
}

.text {
    /* 辅助样式 */
    /* border: 1px solid red; */

    max-height: 20vh;
    overflow: hidden;
    text-align: left;
}

.text div {
    /* 辅助样式 */
    /* border: 1px solid red; */

    display: inline-block;
    text-align: left;
    padding: 0 10px;
}

ol .info li {
    display: inline-block;
    text-align: left;
    margin: 10px 10px 0;
}

</style>