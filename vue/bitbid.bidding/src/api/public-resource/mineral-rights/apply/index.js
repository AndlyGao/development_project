/**
 * Created by lvke on 2018/8/2.
 */

import request from '@/utils/axios/pubresource-axios'

export const apply = {
  // 查询列表
  applyList (query) {
    return request({
      url: '/apply',
      method: 'get',
      params: query
    })
  },
  // 查询详情
  applyDetail (query) {
    return request({
      url: '/apply/' + query,
      method: 'get'
    })
  },
  // 报名
  applyAdd (query) {
    return request({
      url: '/apply',
      method: 'post',
      data: query
    })
  },
  // 修改报名信息
  applyUpdate (query) {
    return request({
      url: '/apply',
      method: 'put',
      data: query
    })
  },
  // 查询列表包含资格确认书信息
  applyListConQua (query) {
    return request({
      url: '/apply/quaCon',
      method: 'get',
      params: query
    })
  },
  // 出让人查看报名情况
  applyListTrans (query) {
    return request({
      url: '/apply/transferor',
      method: 'get',
      params: query
    })
  },
  // 项目里判断是否报名 (受让人)
  applyProject (query) {
    return request({
      url: '/apply/queryCount',
      method: 'get',
      params: query
    })
  }
}
