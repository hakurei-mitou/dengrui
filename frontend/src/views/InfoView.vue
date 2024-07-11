<template>
    <div class="pannel">
        <div class="item">
            GitHub 用户名:
            <input type="text" v-model="data.username"/>
        </div>
        <div class="item">
            GitHub 仓库名:
            <input type="text" v-model="data.repo_name" />
        </div>
        <div class="item">
            Access Token:
            <input type="text" v-model="data.access_token"/>
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
    repo_name: "",
    access_token: ""
})


function checkLength(string) {
    string = string.trim()
    return string.lenght >= 6;
}

function submitData() {
    console.table(data);
    // some small cheacks
    if (checkLength(data.username) && checkLength(data.repo_name) && checkLength(data.access_token)) {
        console.log("format error!");
        return false
    }
    cardUtil.setGitHubInfo(data)
    setTimeout(() => {
        data.username = ""
        data.repo_name = ""
        data.access_token = ""
    }, 1000);
}

</script>


<style scoped>

.pannel {
    width: 30vw;
    margin: 5vh auto;
}

.item {
    margin-top: 5vh;
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