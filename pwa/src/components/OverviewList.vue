<template>
    <list>
        <cell v-for="(cardId, index) in allCardIds" :key="index" class="cell" @click="onClick(cardId, index)">
            <div class="item-browser">
                <card-view v-show="itemBrowser.unfoldIndex == index" :card-id="cardId"> </card-view>
                <overview-item v-show="itemBrowser.unfoldIndex != index" :card-id="cardId"></overview-item>
            </div>
		</cell>
    </list>
</template>


<script setup>

/**
 * basic setting
 */

import { List, Cell } from 'vant';
import { defineProps, reactive, ref, watch } from 'vue';
import OverviewItem from './OverviewItem.vue';

const props = defineProps(['cardIds'])

const allCardIds = ref(props.cardIds)

watch(props, (newProps, oldProps)=>{
	// console.log("watched!");
	// console.log("props.cardIds = ", props.cardIds);
    allCardIds.value = props.cardIds
})

/**
 * click to see full card
 */

import CardView from '@/views/CardView.vue';
import { cardUtil } from '@/js/cardUtil';

const itemBrowser = reactive({
    unfoldIndex: null
})


import { useStore } from "vuex";
import { computed } from 'vue';

const store = useStore()

const changeMode = computed(() => {
    return store.state.inChangeCard
})


function onClick(cardId, index) {
    // console.log("on Click!");

    if (changeMode.value) {
        // console.log("changeMode", changeMode.value);
        return
    }


    if (itemBrowser.unfoldIndex != index) {
        // unfold
        itemBrowser.unfoldIndex = index
        cardUtil.pushHistory(cardId)
    } else {
        // fold
        itemBrowser.unfoldIndex = null
    }
}

/**
 * refresh after deleting
 */

import { provide } from 'vue';

function quitChangeCard() {
    store.commit('setInChangeCard', false)
}

function refreshAfterDelete(deletedCardId) {
//   console.log("deletedCardId = ", deletedCardId);
  allCardIds.value = allCardIds.value.filter((value) => {
    return value != deletedCardId
  })
  // console.log("allCardIds after deleting = ", allCardIds.value);
  quitChangeCard()

}

provide('refreshAfterDelete', refreshAfterDelete)

/**
 * refresh after updating
 */

function refreshAfterUpdate(updatedCardId) {
  allCardIds.value[itemBrowser.unfoldIndex] = cardUtil.NoCardId
  setTimeout(() => {
    allCardIds.value[itemBrowser.unfoldIndex] = updatedCardId
    quitChangeCard()
  }, 100);
}

provide('refreshAfterUpdate', refreshAfterUpdate)

</script>


<style scoped>

.cell {
	border: solid 1px black ;
    padding-top: 20px;
    padding-bottom: 10px;
}

.cell:active {
    background-color: gray;
}

</style>