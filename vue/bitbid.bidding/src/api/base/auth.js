import request from '@/utils/axios/up-axios'

export const auth = {
  login: (data) => {
    return request({
      url: '/login',
      method: 'post',
      data: data
    })
  },
  getLoginInfo: (data) => {
    return request({
      url: '/getLoginInfo',
      method: 'get',
      data: data
    })
  },
  logout: () => {
    return request({
      url: '/logout',
      method: 'get'
    })
  },
  loginBitbid: (data) => {
    return request({
      url: '/loginBitbid',
      method: 'post',
      data: data
    })
  }
}
