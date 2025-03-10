import {titleName} from '@/assets/js/common'

export default[
  {
    path: '/annomanage/bidanno',
    name: 'bidanno',
    meta: {
      permissions: [1, 2, 3],
      title: '招标公告' + titleName,
      active: '/annomanage/bidanno'
    },
    component: resolve => require(['@/pages/anno-manage/bidanno/index'], resolve)
  },
  {
    path: '/annomanage/bidanno/add',
    name: 'bidanno-add',
    meta: {
      permissions: [1, 2, 3],
      title: '添加招标公告' + titleName,
      active: '/annomanage/bidanno'
    },
    component: resolve => require(['@/pages/anno-manage/bidanno/add'], resolve)
  },
  {
    path: '/annomanage/bidanno/update',
    name: 'bidanno-update',
    meta: {
      permissions: [1, 2, 3],
      title: '修改招标公告' + titleName,
      active: '/annomanage/bidanno'
    },
    component: resolve => require(['@/pages/anno-manage/bidanno/update'], resolve)
  },
  {
    path: '/annomanage/bidanno/detail/:objectId',
    name: 'bidanno-detail',
    meta: {
      permissions: [1, 2, 3],
      title: '查看招标公告' + titleName,
      active: '/annomanage/bidanno'
    },
    component: resolve => require(['@/pages/anno-manage/bidanno/detail'], resolve)
  }
]
