<template>
    <div class="pannel">
        <div class="item">
            GitHub 用户名:
            <input type="text" v-model="data.username"/>
        </div>
        <div class="item">
            GitHub 仓库名:
            <input type="text" v-model="data.repoName" />
        </div>
        <div class="item">
            Access Token:
            <input type="text" v-model="data.accessToken"/>
        </div>
        <button type="submit" class="item button" @click="submitData">
            更新信息
        </button>
    </div>

</template>


<script setup>
import { reactive } from 'vue';
import { cardUtil } from '../js/cardUtil';

const data = reactive({
    username: "",
    repoName: "",
    accessToken: ""
})


function checkLength(string) {
    string = string.trim()
    return string.lenght >= 6;
}

function submitData() {
    console.table(data);
    // some small cheacks
    if (checkLength(data.username) && checkLength(data.repoName) && checkLength(data.accessToken)) {
        console.log("format error!");
        return false
    }
    cardUtil.setGitHubInfo(data)
    setTimeout(() => {
        // clean input area
        data.username = ""
        data.repoName = ""
        data.accessToken = ""
    }, 1000);
}

</script>


<style scoped>

.pannel {
    width: 30vw;
    margin: 5vh 5vw;
    font-size: 22px;
}

.item {
    margin-top: 5vh;
    height: 12vh;
}

.item input {
    display: block;
}

.button {
    border-radius: 6px;
}

.button:active {
    background-color: gray;
}


</style>