const dateUtils = {
  /**
   *
   * @param timestamp
   * @param splitChar
   * @returns {string}
   */
  localDate (timestamp, splitChar) {
    splitChar = splitChar || '-'
    const date = new Date(timestamp)
    const year = date.getFullYear()
    const month = date.getMonth() + 1
    const day = date.getDate()
    return '' + year + splitChar + this.prefixAddZero(month, 2) + splitChar + this.prefixAddZero(day, 2)
  },
  /**
   *
   * @param timestamp
   * @returns {string}
   */
  localTime (timestamp) {
    const date = new Date(timestamp)
    const hours = date.getHours()
    const minutes = date.getMinutes()
    return '' + this.prefixAddZero(hours, 2) + ':' + this.prefixAddZero(minutes, 2)
  },
  localTimes (timestamp) {
    const date = new Date(timestamp)
    const hours = date.getHours()
    const minutes = date.getMinutes()
    const seconds = date.getSeconds()
    return '' + this.prefixAddZero(hours, 2) + ':' + this.prefixAddZero(minutes, 2) +
      ':' + this.prefixAddZero(seconds, 2)
  },
  /**
   * 获取日期时间 如：2019-05-15 23:00:00
   * @param timestamp
   * @param splitChar
   * @returns {string}
   */
  localDateTime (timestamp, splitChar) {
    const localDate = this.localDate(timestamp, splitChar)
    const localTime = this.localTime(timestamp)
    return localDate + ' ' + localTime
  },
  localDateTimes (timestamp, splitChar) {
    const localDate = this.localDate(timestamp, splitChar)
    const localTime = this.localTimes(timestamp)
    return localDate + ' ' + localTime
  },
  /**
   * 数字位数不够前缀补0
   * @param number 原始数字
   * @param n 个数
   * @returns {string}
   */
  prefixAddZero (number, n) {
    const str = number + ''
    let result = ''
    while ((n - str.length) > result.length) result += '0'
    result += str
    return result
  },
  ifNull (object, value) {
    return object || value
  }
}
export default dateUtils
