<template>
<div id="wrapId" class="ueditor-wrap">
  <div :id="id" ref="ueditor" :style="ueditorStyle"></div>
</div>
</template>
<script>
// 引入编辑器配置
import 'static/ueditor/ueditor.config.js'
// 编辑器程序
import 'static/ueditor/ueditor.all.js'
// 编辑器语言
import 'static/ueditor/lang/zh-cn/zh-cn.js'
// 引入编辑器组件使用的变量、函数等资源
import {toolbars} from './basic'
// 配置项
let options = {
  // 自定义操作工具
  toolbars,
  // 关闭字数统计
  wordCount: false,
  // 元素路径
  elementPathEnabled: false
  // readonly: true
}
// 输出
export default {
// 组件名称
  name: 'UE',
  // 属性
  props: {
    // 画布宽度
    width: {type: String, default: '100%'},
    // 画布高度
    height: {type: Number, default: 200},
    // 初始化参数配置
    options: Object,
    // 工具配置
    toolbars: Array,
    // 父组件传值
    editread: Boolean
  },
  computed: {
    // 动态生成ID
    id () {
      let nowTime = new Date()
      return `ueditor_${Date.parse(nowTime)}${Math.random()}`
    },
    // 画布样式
    ueditorStyle () {
      return {
        width: this.width,
        height: this.heightStr
      }
    },
    // 高度
    heightStr () {
      let height = this.height
      return Number(height) ? height + 'px' : height
    }
  },
  data () {
    return {
      // 百度编辑器实例
      ueditor: null
    }
  },
  methods: {
    init (value) {
      if (value) {
        // 获得配置项
        options.readonly = true
      } else {
        options.readonly = false
      }
      // 获得配置项
      let opts = this.options || options
      // 获得工具配置
      let tools = this.toolbars || toolbars
      // 设置工具配置
      opts.toolbars = tools
      // 获得编辑器对象
      this.ueditor = window.UE.getEditor(this.id, opts)
    },
    // 设置内容
    setContent (val) {
      if (this.ueditor) {
        // 编辑器渲染完毕
        this.ueditor.ready(() => {
          // 设置默认内容
          this.ueditor.setContent(val)
        })
      }
    },
    // 获取内容
    getContent () {
      return this.ueditor.getContent()
    }
  },
  // 实例创建完成
  mounted () {
    // 初始化编辑器
    this.init(this.editread)
  },
  destroyed () {
    this.ueditor.destroy()
  }
}
</script>
<style lang="less">
.ueditor-wrap {
  position: relative;
  z-index: 0;
  background-color: #fff;
  width: 100%;
  box-sizing: border-box;
  text-align: left;
}
</style>
