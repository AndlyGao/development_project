// 校验账号，数字字母组合
export function validateAccount (str) {
  // const reg = /^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{1,50}/
  const reg = /^(\w+$){1,50}/
  return reg.test(str)
}
export function validateMobilePhone (str) {
  const reg = /^1[3|4|5|6|7|8|9][0-9]\d{8}$/
  return reg.test(str)
}
// 身份证号验证
export function validateIdCard (str) {
  const reg = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/
  return reg.test(str)
}
// 身份证号验证
export function validateInteger (str) {
  const reg = /^(0|\+?[1-9][0-9]*)$/
  return reg.test(str)
}
export function validateZipcode (str) {
  const reg = /^[0-9][0-9]{5}$/
  return reg.test(str)
}
export function validateAge (str) {
  const reg = /^[0-9]*$/
  return reg.test(str)
}
export function validateNumber (str) {
  const reg = /^-?\d+\.?\d{0,10}$/
  return reg.test(str)
}
export function validateSpaceType (str) {
  const reg = /\s+/g
  return reg.test(str)
}
export function validateDecimal (str) {
  const reg = /^-?\d+\.?\d{0,4}$/
  return reg.test(str)
}
export function validateLinks (str) {
  const reg = /^http(s)?:\/\/([\w-]+\.)+[\w-]+(\/[\w- ./?%&=]*)?$/
  return reg.test(str)
}
export function validateWhoInteger (str) {
  const reg = /^\+?[1-9][0-9]*$/
  return reg.test(str)
}
// 金额判断
export function validateMoney (str) {
  const reg = /^(([1-9][0-9]*)|(([0]\.\d{1,2}|[1-9][0-9]*\.\d{1,2})))$/
  return reg.test(str)
}
// 固话和手机号一起验证
export function validateFixedPhone (str) {
  const reg = /^1[3|4|5|6|7|8|9][0-9]\d{8}$|^0\d{2,3}-?\d{7,8}$/
  return reg.test(str)
}

// zhangting_20190307
// 邮箱的通用验证
// 长度不限，可以使用英文（包括大小写）、数字、点号、下划线、减号，首字母必须是字母或数字
export function validateEmail (str) {
  const reg = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/
  return reg.test(str)
}

// zhangting_20190318
// 验证百分比: 正则匹配0-100之间的整数
export function validatePercentage (str) {
  const reg = /^([1-9][0-9]{0,1}|100)$/
  return reg.test(str)
}

// zhangting_20190318
// 验证百分比: 正则匹配0-100之间的整数,保留兩位小數
export function validatePercentageFor2 (str) {
  const reg = /^(\d|[1-9]\d|100)(\.\d{1,2})?$/
  return reg.test(str)
}

export function validPhoneUser (rule, value, callback) {
  if (!value) {
    callback()
  } else if (!validateFixedPhone(value)) {
    callback(new Error('请输入正确的11位手机号码或带区号的固话'))
  } else {
    callback()
  }
}

export function validEmail (rule, value, callback) {
  if (!value) {
    callback()
  } else if (!validateEmail(value)) {
    callback(new Error('电子邮箱有误，请重新录入！'))
  } else {
    callback()
  }
}

export function validMoney (rule, value, callback) {
  if (!value) {
    callback()
  } else if (!validateMoney(value)) {
    callback(new Error('输入有误，请重新录入！'))
  } else {
    callback()
  }
}

export function validWhoInteger (rule, value, callback) {
  if (!value) {
    callback()
  } else if (!validateWhoInteger(value)) {
    callback(new Error('输入有误，请重新录入！'))
  } else {
    callback()
  }
}

export function validLinks (rule, value, callback) {
  if (!value) {
    callback()
  } else if (!validateLinks(value)) {
    callback(new Error('网址输入有误，请重新录入！'))
  } else {
    callback()
  }
}

export function validPercentage (rule, value, callback) {
  if (!value) {
    callback()
  } else if (!validatePercentage(value)) {
    callback(new Error('输入有误，请重新录入！'))
  } else {
    callback()
  }
}

export function validMobilePhone (rule, value, callback) {
  if (!value) {
    callback()
  } else if (!validateMobilePhone(value)) {
    callback(new Error('请输入正确的11位手机号码'))
  } else {
    callback()
  }
}

export function validIdCard (rule, value, callback) {
  if (!value) {
    callback()
  } else if (!validateIdCard(value)) {
    callback(new Error('输入有误，请重新录入！'))
  } else {
    callback()
  }
}

export function validZipcode (rule, value, callback) {
  if (!value) {
    callback()
  } else if (!validateZipcode(value)) {
    callback(new Error('输入有误，请重新录入！'))
  } else {
    callback()
  }
}
// 正数正则
export function validatePositiveNumber (str) {
  const reg = /^([1-9][0-9]*(\.\d{1,2})?)|(0\.\d{1,2})$/
  return reg.test(str)
}
