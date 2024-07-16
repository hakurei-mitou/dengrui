<template>
  <pop-info></pop-info>
  <div id="container" ref="scroll">
    <router-view/>
  </div>
  <nav>
    <tab-bar></tab-bar>
  </nav>
  <div id="startup-screen" v-show="isShow">
    <div class="words">
      <p class="item">灯&nbsp;蕊</p>
      <p class="item">&nbsp;&nbsp;&nbsp;&nbsp;灯光花蕊</p>
      <p class="item">&nbsp;&nbsp;&nbsp;&nbsp;ひかることば</p>
    </div>
  </div>
  <button id="quit-button" @click="quit()">X</button>
</template>

<script setup>



/**
 * stratup screen
 */

const isShow = ref(true)
// const isShow = ref(false)

setTimeout(() => {
  isShow.value = false
}, 1500)


import { useStore } from 'vuex'
import { cardUtil } from './js/cardUtil';
/**
 * test GitHub Connection
 */

const store = useStore()

cardUtil.tryConnectGitHub().then(res => {
  if (res == false) {
    store.commit('setDisableCUD', true)
  } else {
    cardUtil.syncData()
  }
})

/**
 * full screen
 */

// 获取当前浏览器全屏方法的名称
const fullScreenFn = document.documentElement.requestFullscreen || 
                  document.documentElement.mozRequestFullScreen || 
                  document.documentElement.webkitRequestFullscreen || 
                  document.documentElement.msRequestFullscreen;

// 调用全屏方法
if (fullScreenFn) {
  fullScreenFn.call(document.documentElement);
}

/**
 * scroll control
 */

import TabBar from '@/components/TabBar.vue';
import PopInfo from '@/components/PopInfo.vue';
import { ref } from 'vue';

// view position control by scroll
import { provide } from 'vue';

const scroll = ref(null)

var recordPosition = 0

const goPosition = (position) => {
    // the position from top(scrollTop)

    if (position == "top") {
      scroll.value.scrollTop = 0
    } else if (position == "bottom") {
      scroll.value.scrollTop = scroll.value.scrollHeight
    } else if (position == "record") {
      recordPosition = scroll.value.scrollTop
      // console.log("record = ", recordPosition);
    } else if (position == "go-record") {
      // console.log("goPosition = ", recordPosition);
      setTimeout(() => {
        scroll.value.scrollTop = recordPosition
      }, 1)
    }
}

provide('goPosition', goPosition)


/**
 * close the backend of this application through browser
 */
let beforeTime = 0

window.addEventListener('beforeunload', e => {
  beforeTime = Date.now()
})

window.addEventListener('unload', e => {
  let presentTime = Date.now()
  if (presentTime - beforeTime > 8) {
    // refresh operation.
    // nothing to do.
    return
  } else {
    // quit operation
    cardUtil.closeApplication()
  }
});


/**
 * close the backend of this application through quit button
 */

function quit() {
  cardUtil.closeApplication()
}


</script>

<style>
@import "@/css/reset.css";

#app {
  overflow: hidden;
  font-family: Gadugi, STKaiti;
}

#container {
  height: 95vh;
  overflow-y: scroll;
  overflow-x: hidden;
  font-size: 34px;
}

::-webkit-scrollbar {
  /* 隐藏滚动条 */
  display: none;
}

nav {
  height: 5vh;
}

#startup-screen {
  position: absolute;
  height: 100vh;
  width: 100vw;
  background-color: white;
  top: 0;
  z-index: 2;
}

#startup-screen .words {
  position: absolute;
  top: 20vh;
  right: 20vw;
  z-index: 3;
  font-size: 8vw;
  writing-mode: vertical-rl;
}

#startup-screen .words .item {
  margin-right: 5vw;
}

#quit-button {
  position: absolute;
  top: 3px; 
  right: 3px;
  width: 7vw;
  height: 7vw;
  background-color: red;
  font-size: 3vw;
  border: 1px solid black;
  border-radius: 4px;
}

#quit-button:active {
  background-color: white;
}

</style>
