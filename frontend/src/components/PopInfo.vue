<template>
  <div id="history-toast" v-show="enableShow" >{{ msg }}</div>
</template>

<script setup>
import { useStore } from 'vuex'
import { computed } from 'vue';
import { ref } from 'vue';
import { watch } from 'vue';

const store = useStore()

const enableShow = ref(false)

const enterHistory = computed(() => {
  // console.log("computed");  // 每次翻页会执行一次
  return store.state.presentIndex < store.state.mostNewReadIndex
})

let msg

watch(enterHistory, (newValue, oldValue) => {
    // console.log("enter history");   // 只有 enableShow 的值改变时执行

    msg = newValue ? "正在浏览最近历史" : "已返回最新"  
    enableShow.value = true

    setTimeout(() => { enableShow.value=false }, 1000)
});

</script>

<style scoped>

#history-toast {
  position: absolute;
  width: 40vw;
  height: 60px;
  left: 30vw;
  top: 30vh;
  text-align: center;
  background-color: rgba(150, 146, 146, 0.5);
  border-radius: 15px;
  font-size: 40px;
}

</style>
