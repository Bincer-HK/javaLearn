import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import "@/assets/css/tailwindcss/tailwindcss.css"
// loading 与 message 在自动导入情况下不显示，直接引入相关样式解决
import 'element-plus/theme-chalk/el-loading.css'
import 'element-plus/theme-chalk/el-message.css'

const app = createApp(App)

app.use(router)

app.mount('#app')
