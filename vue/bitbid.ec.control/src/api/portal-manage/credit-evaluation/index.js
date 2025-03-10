import request from '@/utils/axios'

export const credit = {
  queryList (params) {
    return request({
      url: '/credit_evaluation',
      method: 'get',
      params
    })
  },
  update (data) {
    return request({
      url: '/credit_evaluation',
      method: 'post',
      data
    })
  },
  delCredit (id) {
    return request({
      url: `/credit_evaluation/${id}`,
      method: 'delete'
    })
  }
}
