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
</template>

<script setup>

/**
 * stratup screen
 */

const isShow = ref(true)

setTimeout(() => {
  isShow.value = false
}, 2000)


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
 * close the backend of this application
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
    cardUtil.closeBackend()
  }
});


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

</style>
