/**
 * Created by Administrator on 2019-3-7.
 * 资格审查结果
 */
export default [
  {
    path: '/processManage/projectProcess/checkresult/add',
    name: 'projectProcess-checkresult-add',
    meta: {
      title: '流程管理-资格审查结果-添加评标专家'
    },
    component: resolve => require(['@/pages/processManage/projectProcess/checkResult/add'], resolve)
  },
  {
    path: '/processManage/projectProcess/checkresult/update',
    name: 'projectProcess-checkresult-update',
    meta: {
      title: '流程管理-资格审查结果-编辑评标专家'
    },
    component: resolve => require(['@/pages/processManage/projectProcess/checkResult/update'], resolve)
  },
  {
    path: '/processManage/projectProcess/checkresult/detail/:objectId',
    name: 'projectProcess-checkresult-detail',
    meta: {
      title: '流程管理-资格审查结果-查看评标专家'
    },
    component: resolve => require(['@/pages/processManage/projectProcess/checkResult/detail'], resolve)
  },
  {
    path: '/processManage/projectProcess/checkresult/add_report',
    name: 'projectProcess-checkresult-add_report',
    meta: {
      title: '流程管理-资格审查结果-添加报告'
    },
    component: resolve => require(['@/pages/processManage/projectProcess/checkResult/addReport'], resolve)
  },
  {
    path: '/processManage/projectProcess/checkresult/update_report',
    name: 'projectProcess-checkresult-update_report',
    meta: {
      title: '流程管理-资格审查结果-修改报告'
    },
    component: resolve => require(['@/pages/processManage/projectProcess/checkResult/updateReport'], resolve)
  },
  {
    path: '/processManage/projectProcess/checkresult/report_detail/:objectId',
    name: 'projectProcess-checkresult-report_detail',
    meta: {
      title: '流程管理-资格审查结果-查看报告'
    },
    component: resolve => require(['@/pages/processManage/projectProcess/checkResult/reportDetail'], resolve)
  },
  {
    path: '/processManage/projectProcess/checkresult/add_result',
    name: 'projectProcess-checkresult-add_result',
    meta: {
      title: '流程管理-资格审查结果-添加结果通知书'
    },
    component: resolve => require(['@/pages/processManage/projectProcess/checkResult/addResult'], resolve)
  },
  {
    path: '/processManage/projectProcess/checkresult/update_result',
    name: 'projectProcess-checkresult-update_result',
    meta: {
      title: '流程管理-资格审查结果-修改结果通知书'
    },
    component: resolve => require(['@/pages/processManage/projectProcess/checkResult/updateResult'], resolve)
  },
  {
    path: '/processManage/projectProcess/checkresult/result_detail/:objectId',
    name: 'projectProcess-checkresult-result_detail',
    meta: {
      title: '流程管理-资格审查结果-查看结果通知书'
    },
    component: resolve => require(['@/pages/processManage/projectProcess/checkResult/resultDetail'], resolve)
  }
]
