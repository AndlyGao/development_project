import {titleName} from '@/assets/js/common'

export default[
  {
    path: '/index/home-manage/friendship-link',
    name: 'friendship-link',
    meta: {
      permissions: [1, 2],
      title: '友情链接' + titleName,
      active: '/index/home-manage/friendship-link'
    },
    component: resolve => require(['@/pages/home-manage/friendship-link/index'], resolve)
  },
  {
    path: '/index/friendship-link/add',
    name: 'friendship-add',
    meta: {
      permissions: [1, 2],
      title: '新增友情链接' + titleName,
      active: '/index/home-manage/friendship-link'
    },
    component: resolve => require(['@/pages/home-manage/friendship-link/add'], resolve)
  },
  {
    path: '/index/friendship-link/update',
    name: 'friendship-update',
    meta: {
      permissions: [1, 2],
      title: '修改友情链接' + titleName,
      active: '/index/home-manage/friendship-link'
    },
    component: resolve => require(['@/pages/home-manage/friendship-link/update'], resolve)
  }
]
