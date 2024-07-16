<template>
  <swipe :loop="true" @change="onChange" initial-swipe="1" :touchable="swipeData.touchable">
    <swipe-item v-for="cardId in swipeData.localSwipeCardIds" @touchstart="onTouchstart" @touchmove="onTouchmove">
      <card-view :card-id="cardId"></card-view>
    </swipe-item>
  </swipe>
</template>

<script setup>
import { Swipe, SwipeItem } from 'vant'
import CardView from '@/views/CardView.vue'
import { cardUtil } from '@/js/cardUtil'
import { reactive } from 'vue'
import { watch } from 'vue'

let startX = null
let enableSwipeLeft = true
let enableSwipeRight = true
  
let localPrevIndex = 0

let globalPresentIndex = null
let globalSwipeCardIds = []

const swipeData = reactive({
  touchable: true,
  localSwipeCardIds: [],
})

const props = defineProps(['cardIds', 'initIndex'])

function rectifyBorderCondition(props) {
  let cardIds = props.cardIds.slice(0)
  
  if (props.initIndex < 0 || (props.initIndex >= cardIds.length && cardIds.length != 0 )) {
    alert("error!")
  }

  // to use localPrevIndex, total swipeItem number should be 3
  // border condition, cardIds = 
  // 1. []
  if (cardIds.length == 0) {
    // 1.
    cardIds = [cardUtil.NoCardId]
  }
  cardIds.unshift(cardUtil.NoCardId)
  globalPresentIndex = props.initIndex + 1
  cardIds.push(cardUtil.NoCardId)

  if (cardIds[globalPresentIndex - 1] == cardUtil.NoCardId) {
    enableSwipeLeft = false
  } else {
    enableSwipeLeft = true
  }
  if (cardIds[globalPresentIndex + 2 - 1] == cardUtil.NoCardId) {   // - 1 means that end is not included
    enableSwipeRight = false
  } else {
    enableSwipeRight = true
  }
  
  return cardIds
}

let indexMapping = {
  // element is the index that arrow to (left center right) -> (0, 1, 2)
  0: [1, 2, 0],   // center(1) should be at index 0: left(0) is at index 2, right(2) is at index 1
  1: [0, 1, 2],
  2: [2, 0, 1],
}

const emit = defineEmits(['onChange'])

function outChange() {
  let outerIndex = globalPresentIndex - 1
  emit("onChange", outerIndex)
}

function setLocal(index) {
  let begin = globalPresentIndex - 1
  let end = globalPresentIndex + 2
  let cardIds = globalSwipeCardIds.slice(begin, end)
  
  // console.log("localCardIds = ", cardIds);

  let rectifiedCardIds = [null, null, null]
  rectifiedCardIds.forEach((x, i, arr)=> {
    arr[i] = cardIds[indexMapping[index][i]]
  })
  
  // console.log("rectified = ", rectifiedCardIds);
  swipeData.localSwipeCardIds = rectifiedCardIds
}


globalSwipeCardIds = rectifyBorderCondition(props)
// because of initial-swipe="1"
setLocal(1)
// console.log("globalPresentIndex = ", globalPresentIndex);
// console.log("globalSwipeCardIds = ", globalSwipeCardIds);
// console.log("localSwipeCardIds = ", swipeData.localSwipeCardIds);



function onTouchstart(e) {
  // 记录初始滑动位置
  e.preventDefault()
  // console.log(e);
  if (e.touches.length == 1) {
    startX = e.touches[0].clientX
  }
}

function onTouchmove(e) {
  // 处理每次滑动位置
  e.preventDefault()
  let currentStartX
  if (e.touches.length == 1) {
    currentStartX = e.touches[0].clientX
  }

  // 往左翻页，右滑
  if (currentStartX > startX) {

    if (! enableSwipeLeft) {
      // 禁止右滑
      swipeData.touchable = false
    } else {
      swipeData.touchable = true
    }

    // 往右翻页，左滑
  } else if (currentStartX < startX) {

    if (! enableSwipeRight) {
      // 禁止左滑
      swipeData.touchable = false
    } else {
      swipeData.touchable = true
    }

  }
}

let localSwipeIndex = 1

function onChange(index) {
  
  // console.log("index = ", index);
  localSwipeIndex = index

  if (index == localPrevIndex) {
    // swipe left
 
    // leave from most right side
    enableSwipeRight = true

    localPrevIndex = getLeftIndex(localPrevIndex)
    globalPresentIndex -= 1

    if (globalPresentIndex > 1) {
    
    } else {
      console.log("### this is the last old card ###");   
      enableSwipeLeft = false
    }
  } else {
    // swipe right

    // leave from most left side
    enableSwipeLeft = true
    
    localPrevIndex = getRightIndex(localPrevIndex)
    globalPresentIndex += 1

    if (globalPresentIndex < globalSwipeCardIds.length - 2) {

    } else {
      console.log("### this is the most new card ###");
      enableSwipeRight = false
    }
  }
  
  setLocal(index)
  // console.log("new localSwipeCardIds = ", swipeData.localSwipeCardIds);
  outChange()
}

function getLeftIndex(index) {
  return (index - 1 + 3) % 3
}

function getRightIndex(index) {
  return (index + 1) % 3
}


watch(props, (newProps, oldProps)=>{
  // console.log("watched!");
  // console.log("props cardIds = ", newProps.cardIds);
  // console.log("props initIndex = ", newProps.initIndex);
  // console.log("localSwipeCardIds = ", swipeData.localSwipeCardIds);
  globalSwipeCardIds = rectifyBorderCondition(newProps)
  setLocal(localSwipeIndex)
  // console.log("after set");
  // console.log("props cardIds = ", newProps.cardIds);
  // console.log("localSwipeCardIds = ", swipeData.localSwipeCardIds);
  // console.log("globalSwipeCardIds = ", globalSwipeCardIds);
  // console.log({enableSwipeLeft, enableSwipeRight});
})

/**
 * when change card, disable swiping
 */

import { useStore } from 'vuex'
import { computed } from 'vue'

const store = useStore()

const inChangeCard = computed(() => {
  return store.state.inChangeCard
})

watch(inChangeCard, (newValue, oldValue) => {
  if (newValue) {
    enableSwipeLeft = false
    enableSwipeRight = false
  } else {
    enableSwipeLeft = true
    enableSwipeRight = true
  }
})


</script>

<style>

.van-swipe__track {
  display: flex;
}

</style>