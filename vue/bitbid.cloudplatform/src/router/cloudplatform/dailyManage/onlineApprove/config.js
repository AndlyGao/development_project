/**
 * 在线审批-审批配置
 */
export default[
  {
    path: '/dailyManage/onlineapprove/config',
    name: 'dailymanage-onlineapprov-config',
    meta: {
      title: '在线审批-审批配置'
    },
    component: resolve => require(['@/pages/dailyManage/onlineApprove/config/index'], resolve)
  },
  {
    path: '/dailyManage/onlineapprove/config/detail/:objectId',
    name: 'dailymanage-onlineapprov-config-detail',
    meta: {
      title: '在线审批-审批配置-查看'
    },
    component: resolve => require(['@/pages/dailyManage/onlineApprove/config/detail'], resolve)
  },
  {
    path: '/dailyManage/onlineapprove/config/approvesetting/:objectId',
    name: 'dailymanage-onlineapprov-config-approvesetting',
    meta: {
      title: '在线审批-审批配置-审批设置'
    },
    component: resolve => require(['@/pages/dailyManage/onlineApprove/config/approvesetting'], resolve)
  }
]
