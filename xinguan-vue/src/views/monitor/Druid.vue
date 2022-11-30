<!--
  @author qiukangming
  @description
  @date 2021/03/28 23:16
-->
<template>
  <div style="text-align: center">
    <iframe id="frame" :src="frameSrc" :frameborder="'no'" :border="0" :marginwidth="0" :marginheight="0"  allowtransparency="yes" style="overflow: hidden;" width="100%"></iframe>
  </div>
</template>
<script>
import { getDruidLoginConfig } from '@/api/common'

export default {
  name: 'Druid',
  data () {
    return {
      username: '--',
      password: '--',
      frameSrc: null,
      info: null
    }
  },
  mounted () {
    // 获取屏幕高度
    document.getElementById('frame').style.height = this.getWindowHeight()
  },
  created () {
    if (process.env.VUE_APP_BASE_API) {
      this.frameSrc = process.env.VUE_APP_BASE_API + 'druid/login.html'
    }
    getDruidLoginConfig().then(res => {
      this.username = res.data.username
      this.password = res.data.password
      this.info = this.$notify.info({
        title: '温馨提示',
        message: '登录用户名：' + this.username + ', 登录密码：' + this.password,
        duration: 15000
      })
    }).catch().finally()
  },
  destroyed () {
    if (this.info) {
      this.info.close()
    }
  }
}
</script>
