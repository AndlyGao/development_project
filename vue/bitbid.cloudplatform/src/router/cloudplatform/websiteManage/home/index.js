import homeimage from './homeImage.js'
import imagenews from './imagenews.js'
import admanage from './admanage.js'
import enterpriseimage from './enterpriseimage.js'
import cooperator from './cooperator.js'
export default [
  {
    path: '/websiteManage/home',
    name: 'websiteManage-home-list',
    meta: {
      title: '网站首页管理'
    },
    component: resolve => require(['@/pages/websiteManage/home/index'], resolve)
  },
  ...homeimage,
  ...imagenews,
  ...admanage,
  ...enterpriseimage,
  ...cooperator
]
