module.exports = {
  devServer: {
    proxy: {
      '/xinguan/api': {
        target: 'http://localhost:8080',
        // secure: false, // 如果是https接口，需要配置这个参数
        changeOrigin: true,
        pathRewrite: {
          '^/xinguan/api': ''
        }
      }
    }
  }
}
