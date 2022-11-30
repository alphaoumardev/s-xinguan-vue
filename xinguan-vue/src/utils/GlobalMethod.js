function RightMessage (obj, title, msg, type) {
  obj.$notify({
    title: title,
    message: msg,
    type: type
  })
}

function ErrorMsg (obj, error) {
  obj.$message({
    message: '调用接口出错, 请联系后台管理员\n错误信息: ' + error,
    type: 'error'
  })
}

function NormalMsg (obj, message, type) {
  obj.$message({
    message: message,
    type: type
  })
}

// 下载文件
function downloadFile (obj, name, suffix) {
  const blob = new Blob([obj])
  const downloadElement = document.createElement('a')
  const href = window.URL.createObjectURL(blob) // 创建下载的链接
  downloadElement.target = '_self'
  downloadElement.href = href
  downloadElement.download = name + new Date().getTime() + '.' + suffix // 下载后文件名
  document.body.appendChild(downloadElement)
  downloadElement.click() // 点击下载
  document.body.removeChild(downloadElement) // 下载完成移除元素
  window.URL.revokeObjectURL(href) // 释放blob对象
}

// 验证电话号码的方法
function checkPhone (rule, value, callback) {
  const phoneReg = /^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\d{8}$/
  if (!value) {
    return callback(new Error('电话号码不能为空'))
  }
  setTimeout(() => {
    // Number.isInteger是es6验证数字是否为整数的方法,实际输入的数字总是识别成字符串
    // 所以在前面加了一个+实现隐式转换
    if (!Number.isInteger(+value)) {
      callback(new Error('请输入数字值'))
    } else {
      if (phoneReg.test(value)) {
        callback()
      } else {
        callback(new Error('电话号码格式不正确'))
      }
    }
  }, 100)
}

// 验证邮箱的方法
function checkEmail (rule, value, callback) {
  const mailReg = /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(.[a-zA-Z0-9_-])+/
  if (!value) {
    return callback(new Error('邮箱不能为空'))
  }
  setTimeout(() => {
    if (mailReg.test(value)) {
      callback()
    } else {
      callback(new Error('请输入正确的邮箱格式'))
    }
  }, 100)
}

// 获取屏幕 宽高
function getWindowHeight (sub = 0.22) {
  if (sub === 0.22 && window.screen.availHeight === 1080) {
    sub -= 0.04
  }
  // 目的是减去上方导航栏高度
  return (window.screen.availHeight - Math.ceil(window.screen.availHeight * sub)) + 'px'
}

// 获取表格最大高度
function getTableMaxHeight (cell = 0.36) {
  if (cell === 0.36 && window.screen.availHeight === 1080) {
    cell -= 0.06
  }
  // 这里返回需要注意，必须返回整数，否则像 620.123243000px 这样的字符串写在样式里面不生效
  return (window.screen.availHeight - Math.ceil(window.screen.availHeight * cell)) + 'px'
}

// 默认分页size
function getPageSize () {
  return 20
}

export default {
  install: function (Vue) {
    Vue.prototype.RightMessage = RightMessage
    Vue.prototype.ErrorMsg = ErrorMsg
    Vue.prototype.NormalMsg = NormalMsg
    Vue.prototype.downloadFile = downloadFile
    Vue.prototype.checkPhone = checkPhone
    Vue.prototype.checkEmail = checkEmail
    Vue.prototype.getWindowHeight = getWindowHeight
    Vue.prototype.getTableMaxHeight = getTableMaxHeight
    Vue.prototype.getPageSize = getPageSize
  }
}
