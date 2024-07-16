<template>
    <card-swipe :cardIds="data.cardIds" :initIndex="data.initIndex" @onChange="onChange" ></card-swipe>
</template>

<script setup>

import CardSwipe from "@/components/CardSwipe.vue"
import { reactive } from "vue";
import { cardUtil } from "@/js/cardUtil";
import { provide } from "vue";

const data = reactive({
    initIndex: 0,
    cardIds: [],
})

let currentIndex = 0

function setData(allCardIds) {

  cardUtil.getLatestReadInAllCardId().then(readInAllCardId => {
      
    console.log("readInAllCardId = ", readInAllCardId);
    console.log("allCardIds = ", allCardIds);

    let index = allCardIds.indexOf(readInAllCardId)
    // console.log("index = ", index);
    if (index == -1) {
      return
    }
  
    // set supplement card in the head and tail of allCardIds for swiping in the edge of the array.
    allCardIds.unshift(allCardIds.at(-1))
    allCardIds.push(allCardIds[1])

    data.cardIds = allCardIds
    data.initIndex = index + 1
    currentIndex = data.initIndex
    // console.log("initIndex = ", data.initIndex);
    // console.log("allCardIds = ", allCardIds);

    cardUtil.pushHistory(data.cardIds[data.initIndex], cardUtil.historyType.READINALL)

  })

}

cardUtil.getAllCardIds().then(allCardIds => {
  // console.log("allCardIds = ", allCardIds);
  setData(allCardIds)
})


function onChange(index) {
  /**
   * index:
   *    the global index, not the local index.
   */
  // console.log({index});
  // console.log("data.cardIds = ", data.cardIds);
  currentIndex = index
  cardUtil.pushHistory(data.cardIds[index], cardUtil.historyType.READINALL)

  // swiping in the edge of the array
  let len = data.cardIds.length
  if (index == len - 1) {
    // in the tail of the array
    data.cardIds = data.cardIds.slice()
    data.initIndex = 1
    currentIndex = data.initIndex

  } else if (index == 0) {
    // in the head of the array
    data.cardIds = data.cardIds.slice()
    data.initIndex = len - 2
    currentIndex = data.initIndex

  }

}

/**
 * refresh after deleting
 */

import { useStore } from "vuex";

const store = useStore()

function quitChangeCard() {
    store.commit('setInChangeCard', false)
}

function refreshAfterDelete(deletedCardId) {
  // console.log("deletedCardId = ", deletedCardId);
  // console.log("data cardIds = ", data.cardIds);

  let cardIds = data.cardIds.filter((value) => {
    return value != deletedCardId
  })

  setData(cardIds)

  // console.log("data cardIds after deleting = ", data.cardIds);
  // console.log("data initIndex after deleting = ", data.initIndex);

  quitChangeCard()

}

provide('refreshAfterDelete', refreshAfterDelete)


/**
 * refresh after updating
 */

function refreshAfterUpdate() {
  let cardId = data.cardIds[currentIndex]
  data.cardIds[currentIndex] = cardUtil.NoCardId
  setTimeout(() => {
    quitChangeCard()
    data.cardIds[currentIndex] = cardId
  }, 1000);
}

provide('refreshAfterUpdate', refreshAfterUpdate)

</script>

<style scoped>

</style>