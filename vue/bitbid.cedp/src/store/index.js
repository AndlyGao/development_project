import Vuex from 'vuex'
import Vue from 'vue'
import { base } from '@/api/base'
import {dataDictionary} from '@/api/system'
import {archivesClassify} from '@/api/archives'
import {getToken, setToken, removeToken} from '@/utils/auth'
import {Message} from 'element-ui'

Vue.use(Vuex)

const state = {
  // 用户信息：{userId: xxx, enterpriseId: xxx, userName: xxx, enterpriseName: xxx, userType: xxx\}
  authUser: '',
  permissions: [],
  scopeOfControlType: null,
  scopeOfControlDepartmentIds: [],
  token: getToken(),
  modules: [], // 用户所拥有的模块权限
  enterpriseSave: '', // 保存企业信息标识
  certification: true
}

const getters = {
  authUser: state => state.authUser,
  token: state => state.token,
  permissions: state => state.permissions,
  modules: state => state.modules,
  enterpriseSave: state => state.enterpriseSave,
  certification: state => state.certification
}

const mutations = {
  SET_USER: (state, user) => {
    state.authUser = user
    // window.allCommentMethod.Change_User()
  },
  SET_TOKEN: (state, token) => {
    state.token = token
  },
  SET_PERMISSIONS: (state, permissions) => {
    state.permissions = permissions
  },
  SET_MODULES: (state, modules) => {
    state.modules = modules
  },
  SET_ENTERPRISE_SAVE: (state, enterpriseSave) => {
    state.enterpriseSave = enterpriseSave
  },
  SET_CERTIFICATION: (state, certification) => {
    state.certification = certification
  }
}

const actions = {
  /** 登录 */
  Login: ({commit, dispatch}, userInfo) => {
    return new Promise((resolve, reject) => {
      Message.closeAll()
      base.login(userInfo).then(response => {
        if (response.data.resCode === '0000') {
          dispatch('SaveLoginInfo', response.data)
          resolve(response.data.userType)
        } else {
          reject(response.data.resCode)
        }
      })
    })
  },
  SaveLoginInfo: ({commit}, data) => {
    // state保存token
    commit('SET_TOKEN', data.token)
    // 保存token到cookie中
    setToken(data.token)
  },
  /**  初始化企业信息:
   * 1.保存资质主体-企业名称数据
   * 2.初始化企业下的文件目录
   */
  InitEnterpriseModels: ({state}) => {
    // 保存资质主体-企业名称数据
    let task1 = new Promise((resolve, reject) => {
      dataDictionary.initZZZT().then(() => {
        resolve()
      }).catch(error => {
        reject(error)
      })
    })
    // 保存资质主体-企业名称数据
    let task2 = new Promise((resolve, reject) => {
      archivesClassify.initEnterpriseClassify().then(() => {
        resolve()
      }).catch(error => {
        reject(error)
      })
    })
    return Promise.all([task1, task2])
  },
  /** 获取用户信息 */
  GetLoginInfo: ({commit, state}) => {
    return new Promise((resolve, reject) => {
      // TODO 目前先不考虑根据模块获取权限列表
      base.getLoginInfo(state.token).then(response => {
        // 保存用户信息放到state中
        commit('SET_USER', response.data.loginUserInfo)
        // 保存权限信息到state中
        commit('SET_PERMISSIONS', response.data.permissions)
        // 保存模块信息到state中
        commit('SET_MODULES', response.data.modules)
        resolve()
      }).catch(error => {
        reject(error)
      })
    })
  },
  /** 本次登录不进行企业认证操作 */
  SkipCertification: ({commit}) => {
    commit('SET_CERTIFICATION', false)
  },
  /** 退出 */
  Logout: ({dispatch, commit}) => {
    return new Promise((resolve, reject) => {
      Message.closeAll()
      base.logout().then(response => {
        // 清除数据
        dispatch('ClearLoginInfo')
        resolve()
      }).catch(error => {
        reject(error)
      })
    })
  },
  /** 清除数据 */
  ClearLoginInfo: ({commit}) => {
    // 清除数据
    commit('SET_USER', '')
    commit('SET_TOKEN', '')
    commit('SET_PERMISSIONS', '')
    commit('SET_ENTERPRISE_SAVE', '')
    commit('SET_CERTIFICATION', true)
    // 清除cookie中的token
    removeToken()
    state.openTab = []
    sessionStorage.clear()
  },
  /** 修改密码 */
  UpdatePwd: ({commit}, pwdForm) => {
    return new Promise((resolve, reject) => {
      Message.closeAll()
      base.updatePwd(pwdForm).then(response => {
        if (response.data.resCode === '0000') {
          resolve(response)
        } else {
          reject(response.data.resCode)
        }
      }).catch(error => {
        reject(error)
      })
    })
  },
  /** 重置密码 */
  RestPwd: ({commit}, account) => {
    return new Promise((resolve, reject) => {
      let submitForm = {
        account,
        password: '123456'
      }
      base.resetPwd(submitForm).then(res => {
        if (res.data.resCode === '0000') {
          resolve(res)
        } else {
          reject(res.data.resCode)
        }
      }).catch(error => {
        reject(error)
      })
    })
  },
  /** 判断操作范围 */
  JudgeScopeOfController: ({state}, {departmentListIds, userIds}) => {
    let flag = false
    if (userIds.includes(state.authUser.userId) || state.scopeOfControlType === 3) {
      flag = true
    }
    if (state.authUser.scopeOfControlType === 2 && departmentListIds !== null) {
      let departmentIdsArr = departmentListIds.split(',')
      let departmentId = departmentIdsArr[departmentIdsArr.length - 1]
      if (state.scopeOfControlDepartmentIds.includes(departmentId)) {
        flag = true
      }
    }
    if (!flag) {
      Message({
        showClose: true,
        message: '您无权操作该数据',
        type: 'warning',
        duration: 3 * 1000
      })
    }
    return flag
  }
}

export default new Vuex.Store({
  state,
  getters,
  mutations,
  actions
})
