<template>
    <div class="button" @click="delCard()">删除</div>
</template>


<script setup>
import { cardUtil } from '@/js/cardUtil';
import { inject } from 'vue';



const prop = defineProps(['cardId'])

const refreshAfterDelete = inject('refreshAfterDelete')

function delCard() {
    if (confirm("确定要删除吗？")) {
        if (confirm("删除需要再确认一次！")) {
            cardUtil.deleteCard(prop.cardId).then(resp => {
                refreshAfterDelete(prop.cardId)
            })
        }
    }
}

</script>


<style scoped>

.button {
    width: 30vw;
    height: 6vh;
    box-sizing: border-box;
    border: 1px solid black;
    margin: -19vh 35vw 18vh;
    border-radius: 1vw;
    background-color: red;
    text-align: center;
}

.button:active {
    background-color: gray;
}

</style>