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
import Clipboard from 'clipboard'

export default {
  name: 'DocUI',
  data () {
    return {
      frameSrc: null,
      token: sessionStorage.getItem('token'),
      info: null
    }
  },
  mounted () {
    // 获取屏幕高度
    document.getElementById('frame').style.height = this.getWindowHeight()
  },
  created () {
    if (process.env.VUE_APP_BASE_API) {
      this.frameSrc = process.env.VUE_APP_BASE_API + 'doc.html'
    }
    this.info = this.$notify.info({
      title: '点击复制接口访问Authorization',
      duration: 0,
      onClick: () => {
        this.copy(this.token)
      }
    })
  },
  destroyed () {
    if (this.info) {
      this.info.close()
    }
  },
  methods: {
    copy (data) {
      const clipboard = new Clipboard('.el-notification', {
        text: () => {
          return data
        }
      })
      clipboard.on('success', (ignore) => {
        console.log('success')
        this.NormalMsg(this, '复制成功！', 'success')
        // 释放内存
        clipboard.destroy()
      })
      clipboard.on('error', (ignore) => {
        this.NormalMsg(this, '复制成功！', 'error')
        clipboard.destroy()
      })
    }
  }
}
</script>
