import promise from 'es6-promise'
import Vue from 'vue'
import App from './App'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import VueRouter from 'vue-router'
import router from './router'
import store from './store'
import Axios from './utils/axios/common/old-axios'
import VueAxios from 'vue-axios'
// 自定义上传组件
import VueUploadWeb from '@/components/upload'

// import './mock/mockData'

promise.polyfill()
Vue.config.productionTip = false
Vue.use(ElementUI)
Vue.use(VueAxios, Axios)
Vue.use(VueRouter)
// 注册自定义组件
Vue.use(VueUploadWeb)

new Vue({
  router,
  store,
  components: { App },
  template: '<App/>'
}).$mount('#app')
