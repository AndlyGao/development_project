import request from '../../../../utils/axios/up-axios'

export const department = {
  infoList (query) {
    return request({
      url: '/departments',
      method: 'get',
      params: query
    })
  },
  treeList (query) {
    return request({
      url: '/departments/' + query,
      method: 'get'
    })
  },
  treeeAdd (query) {
    return request({
      url: '/departments',
      method: 'post',
      data: query
    })
  },
  treeEdit (query) {
    return request({
      url: '/departments',
      method: 'put',
      data: query
    })
  },
  treeDelete (query) {
    return request({
      url: '/departments/' + query,
      method: 'delete'
    })
  },
  userSearch (query) {
    return request({
      url: '/users',
      method: 'get',
      params: query
    })
  },
  // *************************************************河南数据***************************************
  // 树列表
  henantreeList (query) {
    return request({
      url: `/departments/select/${query}?_t=${new Date().getTime()}`,
      method: 'get'
    })
  },
  // 增加
  henantreeeAdd (query) {
    return request({
      url: '/departments/save',
      method: 'post',
      data: query
    })
  },
  // 修改
  henantreeEdit (query) {
    return request({
      url: '/departments/update',
      method: 'put',
      data: query
    })
  },
  // 删除
  henantreeDelete (query) {
    return request({
      url: '/departments/delete/' + query,
      method: 'delete'
    })
  },
  // 侧边列表
  henaninfoList (query) {
    return request({
      url: '/users/selectAllUser',
      method: 'get',
      params: query
    })
  }
}
