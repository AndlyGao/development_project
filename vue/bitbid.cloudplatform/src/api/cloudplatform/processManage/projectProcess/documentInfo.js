import request from '@/utils/axios/up-axios'

export const documentInfo = {
  /**
   * 文件列表查询
   * @param {*} query [查询信息]
   */
  getList (query) {
    return request({
      url: '/document-info',
      method: 'get',
      params: query
    })
  },
  /**
   * 文件详情查询
   * @param {[long]} id [主键Id]
   */
  getById (id) {
    return request({
      url: '/document-info/' + id,
      method: 'get'
    })
  },
  /**
   * 新增/修改文件
   * @param {*} data [主体数据]
   */
  update (data) {
    return request({
      url: '/document-info',
      method: 'put',
      data
    })
  },
  /**
   * 删除文件
   * @param {[long]} id [主键]
   */
  deleteById (id) {
    return request({
      url: '/document-info/' + id,
      method: 'delete'
    })
  },
  /**
   * 删除文件
   * @param {[long]} id [主键]
   */
  deleteByIdAndSectionSystemCode (id, sectionSystemCode) {
    return request({
      url: '/document-info/' + id + '/' + sectionSystemCode,
      method: 'delete'
    })
  },
  /**
   * 当前标段下的文件列表查询
   * @param {*} query [查询信息]
   */
  getByRelaSection (query) {
    return request({
      url: '/document-info/relaSection',
      method: 'get',
      params: query
    })
  }
}
