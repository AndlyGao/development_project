/**
 * Created by Administrator on 2019-3-7.
 */
export default [
  {
    path: '/processManage/projectProcess/invite/add',
    name: 'projectProcess-invite-add',
    meta: {
      title: '流程管理-招标邀请-添加'
    },
    component: resolve => require(['@/pages/processManage/projectProcess/invite/add'], resolve)
  },
  {
    path: '/processManage/projectProcess/invite/update',
    name: 'projectProcess-invite-update',
    meta: {
      title: '流程管理-招标邀请-编辑'
    },
    component: resolve => require(['@/pages/processManage/projectProcess/invite/update'], resolve)
  },
  {
    path: '/processManage/projectProcess/invite/detail/:code',
    name: 'projectProcess-invite-detail',
    meta: {
      title: '流程管理-招标邀请-查看'
    },
    component: resolve => require(['@/pages/processManage/projectProcess/invite/detail'], resolve)
  },
  {
    path: '/processManage/projectProcess/invite/add_notice',
    name: 'projectProcess-invite-add_notice',
    meta: {
      title: '流程管理-招标邀请-添加更正公告'
    },
    component: resolve => require(['@/pages/processManage/projectProcess/invite/addNotice'], resolve)
  },
  {
    path: '/processManage/projectProcess/invite/update_notice',
    name: 'projectProcess-invite-update_notice',
    meta: {
      title: '流程管理-招标邀请-添加更正公告'
    },
    component: resolve => require(['@/pages/processManage/projectProcess/invite/updateNotice'], resolve)
  },
  {
    path: '/processManage/projectProcess/invite/notice_detail/:code',
    name: 'projectProcess-invite-notice_detail',
    meta: {
      title: '流程管理-招标邀请-查看更正公告'
    },
    component: resolve => require(['@/pages/processManage/projectProcess/invite/noticeDetail'], resolve)
  },
  {
    path: '/processManage/projectProcess/invite/tender_confirm',
    name: 'projectProcess-invite-tender_confirm',
    meta: {
      title: '流程管理-招标邀请-投标人确认情况'
    },
    component: resolve => require(['@/pages/processManage/projectProcess/invite/tenderConfirm'], resolve)
  }
]
