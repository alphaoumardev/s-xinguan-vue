import Vue from 'vue'
// 导入Element-UI 组件
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import App from './App.vue'
import router from './router'
import store from './store'
import gbMethods from './utils/GlobalMethod'
import SYS_CONF from '@/utils/config'
// 导入全局样式表
import './assets/css/global.css'
import dateUtils from '@/utils/DateUtils'

Vue.use(ElementUI)
Vue.use(gbMethods)
Vue.config.productionTip = false
Vue.use(SYS_CONF)
Vue.prototype.dateUtils = dateUtils

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
