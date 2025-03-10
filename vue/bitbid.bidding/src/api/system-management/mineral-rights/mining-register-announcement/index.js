/**
 * Created by admin on 2018-8-2.
 */
import request from '@/utils/axios/pubresource-axios'
export const sannoProject = {
  queryList (params) {
    return request({
      url: '/mininganno',
      method: 'get',
      params: params
    })
  },
  queryOne (objectId) {
    return request({
      url: '/mininganno/' + objectId,
      method: 'get'
    })
  },
  update (miningAnnoForm) {
    return request({
      url: '/mininganno/',
      method: 'put',
      data: miningAnnoForm
    })
  }
}
