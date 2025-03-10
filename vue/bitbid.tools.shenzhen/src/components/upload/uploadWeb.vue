<template>
    <div class="vue-upload-web">
      <p class="hw-upload_tip" ref="uploader" v-if="showDownload">
        <a class="hw-upload_link" :href="downloadFlashUrl" target="_blank">
          <slot>请先安装Adobe Flash Player，安装后请重启浏览器</slot>
        </a>
      </p>
    </div>
</template>
<script>
import WebUploader from 'webuploader'
import swgimg from './Uploader.swf'
export default {
  name: 'vue-upload-web',
  props: {
    accept: {
      type: Object,
      default: null
    },
    // 上传地址
    url: {
      type: String,
      default: ''
    },
    // 上传最大数量 默认为100
    fileNumLimit: {
      type: Number,
      default: 100
    },
    // 大小限制 默认100M
    fileSingleSizeLimit: {
      type: Number,
      default: 102400000
    },
    // 上传token等参数
    formData: {
      type: Object,
      default: null
    },
    keyGenerator: {
      type: Function,
      default (file) {
        const currentTime = new Date().getTime()
        const key = `${currentTime}.${file.name}`
        return key
      }
    },
    multiple: {
      type: Boolean,
      default: false
    },
    // 上传按钮ID
    uploadButton: {
      type: String,
      default: ''
    },
    downloadFlashUrl: {
      type: String,
      default: 'https://www.flash.cn/'
    }
  },
  data () {
    return {
      uploader: null,
      showDownload: false
    }
  },
  mounted () {
    const flashVersion = (function () {
      let version

      try {
        version = navigator.plugins['Shockwave Flash']
        version = version.description
      } catch (ex) {
        try {
          version = new window.ActiveXObject('ShockwaveFlash.ShockwaveFlash').GetVariable('$version')
        } catch (ex2) {
          version = '0.0'
        }
      }
      version = version.match(/\d+/g)
      return parseFloat(`${version[0]}.${version[1]}`, 10)
    }())

    // 判断是否WebUploader支持的Flash
    if (!WebUploader.Uploader.support('flash') && WebUploader.browser.ie < 10) {
      if (!flashVersion || (flashVersion < 11.5)) {
        // 没有安装flash player 或版本小
        this.showDownload = true
      }

      return
    }

    this.initWebUpload()
  },
  methods: {
    initWebUpload () {
      const self = this
      this.uploader = WebUploader.create({
        auto: true, // 选完文件后，是否自动上传
        swf: swgimg, // '/upload/Uploader.swf', // swf文件路径
        server: self.url, // 文件接收服务端
        pick: {
          id: self.uploadButton,
          multiple: self.multiple, // 是否多文件上传 默认false
          label: ''
        }, // 选择文件的按钮。可选

        // 允许选择文件格式。
        accept: self.accept,
        thread: 1,
        fileNumLimit: self.fileNumLimit, // 限制上传个数
        fileSingleSizeLimit: self.fileSingleSizeLimit, // 限制单个上传图片的大小
        formData: self.formData, // 上传七牛所需参数
        duplicate: true // 重复上传
      })
      // 当有文件被添加进队列的时候，添加到页面预览
      this.uploader.on('fileQueued', (file) => {
        self.$emit('before', file)
      })
      this.uploader.on('uploadStart', (file) => {
        self.uploader.options.formData.key = self.keyGenerator(file)
      })

      // 文件上传过程中创建进度条实时显示。
      this.uploader.on('uploadProgress', (file, percentage) => {
        self.$emit('progress', file, percentage)
      })
      this.uploader.on('uploadSuccess', (file, response) => {
        self.$emit('success', file, response)
      })

      this.uploader.on('error', (type) => {
        let errorMessage = ''
        if (type === 'F_EXCEED_SIZE') {
          errorMessage = `文件大小不能超过${self.fileSingleSizeLimit / (1024 * 1000)}M`
        } else if (type === 'Q_EXCEED_NUM_LIMIT') {
          errorMessage = '文件上传已达到最大上限数'
        } else {
          errorMessage = `上传出错！请检查后重新上传！`
        }
        self.$emit('error', errorMessage)
      })

      this.uploader.on('uploadComplete', (file, response) => {
        self.$emit('complete', file, response)
      })
    },
    refresh () {
      this.uploader.refresh()
    }
  }
}
</script>

<style>

  .webuploader-container {
    position: relative;
    width: 90px;
    display: inline-block;
  }
  .webuploader-element-invisible {
    position: absolute !important;
    clip: rect(1px 1px 1px 1px); /* IE6, IE7 */
    clip: rect(1px,1px,1px,1px);
  }
  .webuploader-pick {
    position: relative;
    display: inline-block;
    cursor: pointer;
    background: #00b7ee;
    padding: 0px 15px;
    color: #fff;
    text-align: center;
    border-radius: 3px;
    overflow: hidden;
  }
  .webuploader-pick-hover {
    background: #00a2d4;
  }

  .webuploader-pick-disable {
    opacity: 0.6;
    pointer-events:none;
  }
</style>
