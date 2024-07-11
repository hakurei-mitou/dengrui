<template>
    <div v-if="inReadHistory" class="tabbar">
        <div class="item button turn-back" @click="goBack2MostNewGenerateViewCard">转回最新</div>
    </div>
    <div v-else-if="inInputCardData" class="tabbar">
        <div class="item button turn-back" @click="turnBack2AddView">返回</div>
    </div>
    <div v-else-if="inPreview" class="tabbar">
        <!-- <div class="item button turn-back" @click="">强制预览完</div> -->
    </div>
    <div v-else-if="inChangeCard" class="tabbar">
        <div class="item button turn-back" @click="quitChangeCard()">退出修改</div>
    </div>
    <tabbar v-else class="tabbar" v-model="active" active-color="white">
        <tabbar-item to="home" class="item">随机</tabbar-item>
        <tabbar-item to="all" class="item">全部</tabbar-item>
        <tabbar-item to="add" class="item">添加</tabbar-item>
        <tabbar-item to="search" class="item">搜索</tabbar-item>
        <tabbar-item to="info" class="item">信息</tabbar-item>
    </tabbar>
</template>

<script setup>
import { Tabbar, TabbarItem } from 'vant';
import { computed } from 'vue';
import { ref } from 'vue';
import { useStore } from 'vuex'

// the index of the active tabbar-item
const active = ref(0)

const store = useStore()

const inReadHistory = computed(() => {
    return store.state.presentIndex < store.state.mostNewReadIndex
})

const inInputCardData = computed(() => {
    return store.state.inInputCardData
})

const inPreview = computed(() => {
    return store.state.inPreview
})


// turn back to most new read card

function goBack2MostNewGenerateViewCard(e) {
    store.commit('setGoBack2MostNew', true)
}

function turnBack2AddView(e) {
  store.commit('setInInputCardData', false)
}

// ChangeCardView

const inChangeCard = computed(() => {
    return store.state.inChangeCard
})

function quitChangeCard() {
    store.commit('setInChangeCard', false)
}

</script>

<style scoped>

.button:active {
    background-color: gray;
}

.tabbar {
    text-align: center;
}

.item {
    width: 10vw;
    height: 4vh;
    box-sizing: border-box;
    display: inline-block;
    border-radius: 2vw;
    border: solid 1.5px black;
    margin: 0 3vw;
    font-size: 21px;
    font-weight: bold;
}

.turn-back {
    width: 20vw;
}

.van-tabbar-item--active {
    background-color: gray;
}

</style>