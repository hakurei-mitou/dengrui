<template>
    <div id="change-view">
        <add-card-view :cardId="cardProp.cardId" ></add-card-view>
        <delete-card-view v-if="showDeleteButton" :cardId="cardProp.cardId"></delete-card-view>
    </div>
</template>


<script setup>

const cardProp = defineProps(['cardId'])

/**
 * add card
 */

import AddCardView from './AddCardView.vue';
import { ref } from 'vue';

import { computed } from 'vue';
import { useStore } from 'vuex';

const store = useStore()

const showDeleteButton = computed(() => {
    return ! store.state.inInputCardData && ! store.state.inPreview
})


/**
 * delete card
 */

import DeleteCardView from './DeleteCardView.vue';



</script>



<style scoped>

#change-view {
    position: absolute;
    top: 0;
    bottom: 0;
    background-color: gray;
    overflow-y: scroll;
}

</style>