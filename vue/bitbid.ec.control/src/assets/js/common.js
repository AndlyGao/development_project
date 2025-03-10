/**
 * Created by lixuqiang on 2018/4/27.
 */

//response处理
const _RESPONSE_SUCCESS_CODE = "0000"
const checkResponse = (_this, res, callBack) => {
  if (res.data.resCode === _RESPONSE_SUCCESS_CODE){
    _this.$message({ message: res.data.resMessage, type: 'success' });
    if(typeof callBack === 'function'){
      callBack()
    }
  }else{
    _this.$message.error(res.data.resMessage)
  }
}

// 文件上传路径
const fileUploadUrl = `${process.env.BASE_API}file/upload`
const ueditorUploadUrl = `${process.env.BASE_API}file/ueditor-upload`
const fileDownloadUrl = `${process.env.BASE_API}file/`
const fileReviewUrl = `${process.env.BASE_API}file/review?filePath=`
const importMarginUrl = `${process.env.BASE_API}margin-entry-informations/margin-entry`
const importBidderUrl = `${process.env.BASE_API}bidders/import-excel`
const exportCountExcel = `${process.env.BASE_API}Transaction-record/exportExcel`
const exportProcurExcel = `${process.env.BASE_API}Transaction-record/exportProcurementExcel`
const fileReviw = `${process.env.BASE_API}file/review`

// 文件上传限制
const fileSize = (_this, file, biggerSize) => {
  // 默认上传文件大小50M
  let baseSize = biggerSize || 100
  const maxSize = file.size / 1024 / 1024 < baseSize
  const minSize = file.size
  if (!minSize) {
    _this.$message.error('上传大小不能小于0MB!')
    return false
  }
  if (!maxSize) {
    _this.$message.error(`上传大小不能超过${baseSize}MB!`)
    return false
  }
  return true
}

// 表格单元格title属性设置
const addtitle = (_this) => {
  _this.$nextTick(() => {
    var aTd = document.getElementsByTagName('td')
    for (let i = 0; i < aTd.length; i++) {
      let text = aTd[i].innerText
      if (!aTd[i].querySelector('button')) {
        aTd[i].setAttribute('title', text)
      }
    }
  })
}

// 将yyyy-MM-dd hh:mm:ss转换为Date对象
const parseDate = (date) => {
  return new Date(date.substring(0, 4), parseInt(date.substring(5, 7)) - 1, date.substring(8, 10), date.substring(11, 13), date.substring(14, 16), date.substring(17))
}

const downloadFile = (fileName, filePath) => {
  window.open(`${fileDownloadUrl}download?fileName=${encodeURI(fileName)}&filePath=${filePath}`)
}

const downloadFileMining = (fileName, filePath) => {
  window.open(`${fileDownloadUrl}download?fileName=${encodeURI(fileName)}&filePath=${filePath}`)
}

const titleName = '-菏泽政采商城管理系统'

/**  字符串去空 */
const trimStr = (str) => {
  return str.replace(/^\s+|\s+$/gm,'')
}

/** 浅拷贝 */
const extend = (destination, source) => {
  for (var property in source) {
    destination[property] = source[property]
  }
}

// date转换成指定日期格式
// 兼容ie
const dateFormat = function(date, format) {
  var time;
  if(!date)return "";
  if(typeof date === 'string'){
    time = new Date(date.replace(/-/g,'/').replace(/T|Z/g,' ').trim());
  }else if(typeof date === 'object'){
    time = new Date(date) ;
  }
  var o = {
    "M+": time.getMonth() + 1, //月份
    "d+": time.getDate(), //日
    "h+": time.getHours(), //小时
    "m+": time.getMinutes(), //分
    "s+": time.getSeconds(), //秒
    "q+": Math.floor((time.getMonth() + 3) / 3), //季度
    "S": time.getMilliseconds() //毫秒
  };
  if (/(y+)/.test(format)) format = format.replace(RegExp.$1, (time.getFullYear() + "").substr(4 - RegExp.$1.length));
  for (var k in o)
    if (new RegExp("(" + k + ")").test(format)) format = format.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
  return format
}

export  { checkResponse, fileUploadUrl, fileSize, addtitle, downloadFile, downloadFileMining, titleName, trimStr, extend, parseDate, dateFormat, importMarginUrl, importBidderUrl, exportProcurExcel, fileReviw, exportCountExcel}
