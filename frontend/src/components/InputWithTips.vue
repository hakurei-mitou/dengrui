<template>
    <div id="input-with-tips">
        <field :label="props.label + '：'" v-model="inputValue" @blur="onBlur()" @focus="onFocus()"> </field>
        <ol id="tips" >
            <li class="item" v-show="visible"  v-for="tip in tipsData" @mousedown="select($event, tip)" >
            > {{ tip }}
            </li>
        </ol>
    </div>
</template>

<script setup>

import { Field } from 'vant';


/**
 * to synchronize the input value
 */
import { cardUtil } from '@/js/cardUtil';
import { defineModel } from 'vue';

const inputValue = defineModel()

let selected = false

function select(e, tip) {
    // console.log(e);

    // prevent the blur event
    e.preventDefault()

    // console.log("selected!");
    selected = true
    inputValue.value = tip
    setVisible(false)
}


/**
 * set list data
 */

import { ref } from 'vue';
import { watch } from 'vue';

const props = defineProps(['label', 'cardTypeName', 'cardFieldName'])

const tipsData = ref([])

function setTipsData() {
    // console.log(inputValue.value);
    if (inputValue.value.length > 0) {
        // console.log("set data");
        cardUtil.searchCardField(props.cardTypeName, props.cardFieldName, inputValue.value).then(resp => {
            let data = resp.data.data
            // console.log("data = ", data);
            if (data.length > 0) {
                tipsData.value = resp.data.data
            }
        })
    } else {
        // console.log("latest field -->", props.cardFieldName);
        cardUtil.latestCardField(props.cardTypeName, props.cardFieldName).then(resp => {
            let data = resp.data.data
            // console.log("latest = ", data);
            if (data.length > 0) {
                tipsData.value = resp.data.data
            }
        })
    }
}



if (props.cardTypeName && props.cardFieldName) {
    // console.log("props = ", props.cardTypeName, props.cardFieldName);
    let timer = null
    watch(inputValue, (newValue, oldValue)=>{
        // console.log("inputValue changed value: ", oldValue, " --> ", newValue);
        if (newValue == '') {
            return
        }
        if (selected) {
            // the selected status only be actived temporarily for skip a watch after selecting.
            selected = false
        } else {
            clearTimeout(timer)
            timer = setTimeout(() => {
                setTipsData()
                setVisible(true)
            }, 500);
        }
    })
}

/**
 * to show
 */

const visible = ref(false)

function setVisible(isShow) {
    visible.value = isShow
}

/**
 * click input to show --> focus
 * click outer area to hidden --> blur
 */

function onBlur() {
    // event sequences will be: blur (firstly) -> selected (secondly)
    // console.log("blur");
    setVisible(false)
}

function onFocus() {
    if (props.cardTypeName && props.cardFieldName) {
        if (inputValue.value.length == 0) {
            setTipsData()
        }
    }
    setVisible(true)
}


</script>


<style scoped>

#input-with-tips {
    position: relative;
}

#tips {
    position: absolute;
    width: inherit;
    z-index: 1;
    background-color: rgb(226, 226, 226);
    border-left: 1px solid black;
    border-right: 1px solid black;
}

#tips .item {
    padding: 5px 0;
    border-bottom: 1px solid black;
    overflow: hidden;
}

#tips .item:hover {
    background-color: gray;
}

#tips .item:active {
    background-color: white;
}

</style>