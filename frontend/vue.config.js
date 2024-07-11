const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,

  devServer: {
    port: 8081,
    
    // 跨域配置
    proxy: {
    '^/api': {
        target: 'http://localhost:8087/',//接口的前缀
        ws:true,//代理websocked
        changeOrigin:true,//虚拟的站点需要更管origin
        pathRewrite:{
            '^/api':''//重写路径
        }
      }
    }
  },

chainWebpack: (config) => {
config.module
  .rule('vue')
  .use('vue-loader')
  .tap((options) => {
    return {
      ...options,
      defineModel: true
    }
  })
}

})
