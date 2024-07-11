<template>
  <div id="go-button" @click="goTop()">返回顶部</div>
  <div id="search-area">
    <div id="search-bar">
      <search id="search-input" v-model="inputValue" placeholder="请输入一个关键词"> </search>
    </div>
    <span @click="beginSearch()">搜索</span>
  </div>
  <overview-list :cardIds="overviewCardIds" > </overview-list>
  <!-- <overview-list :cardIds="[1, 2, 3]" > </overview-list> -->
</template>


<script setup>

/**
 * go to top
 */
import { inject } from 'vue';

const goPosition = inject('goPosition')

const goTop = () => {
    goPosition('top')
}

/**
 * overview list
 */

import OverviewList from '@/components/OverviewList.vue';


/**
 * keyword highlight
 */

import { ref } from 'vue';

function keywordHighlight(keyword) {
  
  let cardContentNodes = document.getElementsByClassName("card-content")

  // g: global match
  // i: ignore lower or upper cases
  const pattern = new RegExp(keyword, 'gi');

  for (let i in cardContentNodes) {
    let node = cardContentNodes[i]
    // console.log(node);
    if (node.innerHTML != null) {
      // $& ---->  匹配到的原文内容的占位符
      node.innerHTML = node.innerHTML.replace(pattern, '<span class="highlight">$&</span>');    
    }
  }
}


/**
 * search bar
 */

import { Search } from 'vant';
import { cardUtil } from '@/js/cardUtil';

let inputValue = ref(null)
let overviewCardIds = ref([])

function beginSearch() {
  
  let searchValue = inputValue._value.replaceAll(" ", "")
  // console.log("input = ", searchValue);

  if (searchValue.length > 0) {
    // console.log("get", searchValue);

    cardUtil.searchCard(searchValue).then(resp => {
      overviewCardIds.value = resp.data.data
      // console.log("overviewCardIds = ", overviewCardIds.value);
      
      setTimeout(() => {
        // console.log("highlight");
        keywordHighlight(searchValue)
      }, 1000);
    })
  }  
}



</script>

<style>

#search-bar #search-input {
  width: 70vw;
  display: inline-block;
}

.highlight {
  background-color: yellow;
}

</style>

<style scoped>

#go-button {
  position: absolute;
  width: 100px;
  height: 100px;
  right: 70px;
  bottom: 110px;
  border: 1px solid black;
  border-radius: 10px;
  text-align: center;
  z-index: 1;
}

#go-button:active {
  background-color: gray;
}

#search-area {
  display: block;
  height: 60px;
  padding: 3vh 4vw;
}

#search-bar {
  display: inline-block;
  width: 70vw;
}

#search-area span {
  display: inline-block;
  text-align: center;
  width: 15vw;
  border: 1px solid black;
  border-radius: 20px;
  margin-left: 5vw;
}

#search-area span:active {
  background-color: gray;
}

</style>