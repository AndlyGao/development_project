<template>
  <div id="service-update" class="smaincontent">
    <div class="headertitle">
      <!--面包屑-->
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/main' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>信息管理</el-breadcrumb-item>
        <el-breadcrumb-item :to="{ path: '/index/info-manage/service-guide' }">服务指南</el-breadcrumb-item>
        <el-breadcrumb-item>编辑服务指南</el-breadcrumb-item>
      </el-breadcrumb>
      <div class="returnboxbig">
        <template>
          <el-button @click="$router.go(-1)">返回</el-button>
        </template>
      </div>
      <!--面包屑-->
    </div>
    <div class="contentbigbox">
      <el-form :model="ruleForm" :rules="rules" ref="ruleForm" label-width="100px" class="demo-ruleForm">
        <el-form-item label="标题:" prop="title">
          <el-input v-model="ruleForm.title" style="width:300px"></el-input>
        </el-form-item>
        <el-form-item label="内容:" class="form-ueditor bitianicon" prop="content">
          <ueditor ref="content"></ueditor>
        </el-form-item>
        <el-form-item label="附件上传:" prop="fileInformations">
          <upload-file @uploadSuccess="uploadSuccess" @deleteSuccess="deleteSuccess" :fileArrays="ruleForm.fileInformations" fileType="9" isImage="0" fileMaxNum="5" ></upload-file>
        </el-form-item>
        <el-form-item class="submit-radio">
          <el-button type="primary" @click="submitForm('ruleForm')">确 定</el-button>
          <el-button @click="$router.go(-1)">取 消</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>
<script>
import ueditor from '@/components/ueditor/ueditor'
import uploadFile from '@/components/upload/publicFileUpload'
import {notice} from '@/api'
export default {
  data () {
    return {
      ruleForm: {},
      rules: {
        title: [
          { required: true, message: '请填写标题', trigger: 'blur' },
          { min: 1, max: 256, message: '长度在 1到 256 个字符', trigger: 'blur' }
        ],
        content: [
          { required: true, message: '请填写内容', trigger: 'change' }
        ]
      },
      fileType: '',
      oldFileName: '',
      relativePath: ''
    }
  },
  components: {
    ueditor,
    uploadFile
  },
  methods: {
    detail () {
      notice.detail(this.$route.query.objectId).then((res) => {
        this.ruleForm = res.data.notice
        this.$refs.content.setContent(res.data.notice.content)
        if (res.data.notice.fileInformations.length > 0) {
          this.oldFileName = res.data.notice.fileInformations[0].fileName
          this.relativePath = res.data.notice.fileInformations[0].relativePath
        }
      })
    },
    submitForm (name) {
      this.ruleForm.content = this.$refs.content.getContent().trim()
      this.$refs[name].validate((valid) => {
        if (valid) {
          this.ruleForm.content = this.$refs.content.getContent()
          notice.update(this.ruleForm).then((res) => {
            this.$router.push({path: '/index/info-manage/service-guide'})
          })
        } else {
          return false
        }
      })
    },
    uploadSuccess (file) {
      this.ruleForm.fileInformations.push(file)
    },
    deleteSuccess (fileId) {
      this.ruleForm.fileInformations = this.ruleForm.fileInformations.filter(item => item.relativePath !== fileId)
    }
  },
  mounted () {
    this.detail()
  }
}
</script>
<style lang="less">
  #service-update {
    .contentbigbox .demo-ruleForm {
      .el-form-item__content {
        text-align: left;
      }
      .ueditor-wrap {
        padding: 0;
      }
      .btns {
        margin-top: 0;
      }
    }
    .form-ueditor {
      .el-form-item__content {
        line-height: 0;
      }
    }
    .bitianicon .el-form-item__label::before{
      content:"*";
      color:#f66c6c;
      margin-right: 5px;
    }
  }
</style>
