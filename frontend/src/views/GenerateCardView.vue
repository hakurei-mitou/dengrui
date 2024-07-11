<template>
  <card-swipe :cardIds="swipeData.swipeCardIds" :initIndex="swipeData.initIndex" @onChange="onChange" ></card-swipe>
</template>

<script setup>
import CardSwipe from "@/components/CardSwipe.vue"
import { cardUtil } from '@/js/cardUtil'
import { useStore } from 'vuex' // 引入useStore 方法
import { reactive } from 'vue'
import { computed } from "vue";
import { watch } from "vue";

let mostNewReadIndex = null

const swipeData = reactive({
  initIndex: 0,
  swipeCardIds: []
})

// init

function stableUnique(arr) {
  let result = []
  for (let x of arr) {
    if (! result.includes(x)) {
      result.push(x)
    }
  }
  return result
}

function initData() {
  // history cardIds number = 30
  cardUtil.getLatestCardIds(30).then(resp => {
    let cardIds = resp.data.data

    // console.log("resp data = ", cardIds);
    cardIds = stableUnique(cardIds)
    console.log("cardIds = ", cardIds);

    cardUtil.getNewCardId().then(resp => {
      let newCardId = resp.data.data
      console.log("newCardId", newCardId);
      if (newCardId != null) {
        cardIds.push(newCardId)
      }

      if (cardIds.length == 0) {
        return
      } else if (cardIds.length == 1) {
        swipeData.initIndex = 0
        mostNewReadIndex = 0        
      } else {
        swipeData.initIndex = cardIds.length - 2
        mostNewReadIndex = cardIds.length - 2
      }

      swipeData.swipeCardIds = cardIds
      // console.log("swipeCardIds = ", swipeData.swipeCardIds);
      cardUtil.pushReadHistory(swipeData.swipeCardIds[swipeData.initIndex])
      saveSwipeStatus(swipeData.initIndex)

    })
  })
}

initData()


// change page

function onChange(index) {

  // console.log("index = ", index);

  if (index < mostNewReadIndex) {
    // browse history cards
    saveSwipeStatus(index)
  } else {
    // new CardId
    mostNewReadIndex = index
    cardUtil.pushReadHistory(swipeData.swipeCardIds[mostNewReadIndex]).then(() => {

      cardUtil.getNewCardId().then(resp => {
        let newCardId = resp.data.data

        swipeData.swipeCardIds.push(newCardId)
        swipeData.initIndex = mostNewReadIndex

        // console.log("swipeCardIds = ", swipeData.swipeCardIds);
        saveSwipeStatus(mostNewReadIndex)
      })
    })
  }
}

const store = useStore()  // 该方法用于返回store 实例

function saveSwipeStatus(index) {
  let data = {
    presentIndex: index,
    mostNewReadIndex: mostNewReadIndex
  }
  store.commit('saveSwipeStatus', data)
}

// go back to most new read card

const goBack2MostNew = computed(() => {
  return store.state.goBack2MostNew
})

watch(goBack2MostNew, (newValue, oldValue) => {
  // console.log("goBack watched! newValue = ", newValue);
  if (newValue) {
    swipeData.swipeCardIds = swipeData.swipeCardIds.slice(0)
    swipeData.initIndex = mostNewReadIndex
    saveSwipeStatus(mostNewReadIndex)
    store.commit('setGoBack2MostNew', false)
  }
})


/**
 * refresh after deleting
 */

import { provide } from 'vue';
import { nextTick } from "vue";

function quitChangeCard() {
    store.commit('setInChangeCard', false)
}

function refreshAfterDelete(deletedCardId) {
  console.log("refreshAfterDelete > deletedCardId = ", deletedCardId);
  setTimeout(() => {
    swipeData.swipeCardIds.splice(swipeData.swipeCardIds.indexOf(deletedCardId), 1)

    quitChangeCard()
  }, 100)
}

provide('refreshAfterDelete', refreshAfterDelete)

/**
 * refresh after updating
 */

function refreshAfterUpdate(updatedCardId) {
  console.log("refreshAfterUpdate > updatedCardId = ", updatedCardId);
  
  // same swipeCardIds will not trigger the watch in the CardView
  let index = swipeData.swipeCardIds.indexOf(updatedCardId)
  swipeData.swipeCardIds[index] = cardUtil.NoCardId

  setTimeout(() => {
    // wait for that backend procesess the updated card
    swipeData.swipeCardIds[index] = updatedCardId

    quitChangeCard()
  }, 100)
}

provide('refreshAfterUpdate', refreshAfterUpdate)

</script>

<style>

.van-swipe__track {
  display: flex;
}

</style>