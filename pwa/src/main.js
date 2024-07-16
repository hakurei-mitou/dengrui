import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import '@vant/touch-emulator'
import store from './store'
import './registerServiceWorker'

createApp(App)
    .use(router)
    .use(store)
    .mount('#app')
