<template>
  <div class="cloudcontent" id="cloud_processedit">
    <!--面包屑-->
    <el-breadcrumb separator-class="el-icon-arrow-right" class="breadcrumb_box">
      <el-breadcrumb-item :to="{ path: '/index' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/processManage/projectProcess' }">项目进度管理</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/processManage/projectProcess/project' }">项目管理</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/processManage/projectProcess/project/process' }">流程管理</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/processManage/projectProcess/project/process' }">开标评标</el-breadcrumb-item>
      <el-breadcrumb-item class="active_bread">修改开标记录</el-breadcrumb-item>
    </el-breadcrumb>
    <!--面包屑-->
    <div class="main">
      <el-form :model="updateForm" :rules="rules" ref="updateForm" :validate-on-rule-change="true">
        <el-row>
          <el-col :span="12">
            <el-form-item label="项目编号：">
              <el-input v-model="bidSection.tenderProjectCode" disabled></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="项目名称：">
              <el-input v-model="bidSection.tenderProjectName" disabled></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="标段编号：">
              <el-input v-model="bidSection.bidSectionCode" disabled></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="标段名称：">
              <el-input v-model="bidSection.bidSectionName" disabled></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="开标记录名称："  prop="title">
              <el-input v-model="updateForm.title"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="开标地点："  prop="bidOpeningAddress">
              <el-input v-model="updateForm.bidOpeningAddress"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="开标时间：">
              <el-date-picker
                v-model="updateForm.bidOpeningTime"
                type="datetime"
                value-format="yyyy-MM-dd HH:mm:ss">
              </el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24" class="ueditor_box">
            <el-form-item label="开标记录内容：" class="bitianicon">
              <editor ref="ueditorContent" class="ueditor" :editread="editread"></editor>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="附件：">
              <upload-file @uploadSuccess="uploadOtherSuccess" @deleteSuccess="deleteSuccess"
                           :fileArrays="updateForm.fileInformationList"
                           fileType="9"
                           isImage="0">
              </upload-file>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item class="submit-radio">
          <el-button type="primary" @click="submit('updateForm', 1)" :loading="isSubmiting">提交</el-button>
          <el-button type="primary" @click="submit('updateForm', 0)" :loading="isSubmiting">保存</el-button>
          <el-button class="cancal" @click="close">取消</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>
<script>
import editor from '@/components/ueditor/ueditor.vue'
import uploadFile from '@/components/upload/publicFileUpload'
import {documentInfo} from '@/api/cloudplatform'
export default {
  components: {
    editor,
    uploadFile
  },
  data () {
    return {
      updateForm: {
        fileInformationList: []
      },
      rules: {},
      // 富文本
      editread: false,
      isSubmiting: false,
      bidSection: {}
    }
  },
  methods: {
    // 上传附件
    uploadOtherSuccess (file) {
      this.updateForm.fileInformationList.push(file)
    },
    deleteSuccess (fileId) {
      this.updateForm.fileInformationList = this.updateForm.fileInformationList.filter(item => item.relativePath !== fileId)
    },
    // 提交/保存
    submit (form, status) {
      if (!this.$refs.ueditorContent.getContent()) {
        this.$message({
          message: '请填写开标记录内容！',
          type: 'warning'
        })
        return
      }
      this.$refs[form].validate((vaild) => {
        if (vaild) {
          this.isSubmiting = true
          if (Object.is(status, 0)) {
            this.updateForm.auditStatus = 0
          } else if (Object.is(status, 1)) {
            console.log('判断是否需要审批：审批：1 不审批：4')
            this.updateForm.auditStatus = 4
            this.updateForm.submitTime = new Date().getTime()
          }
          this.updateForm.content = this.$refs.ueditorContent.getContent()
          this.updateForm.fileInformationList.map(item => {
            item.objectId = null
          })
          documentInfo.update(this.updateForm).then(() => {
            this.isSubmiting = false
            this.$router.go(-1)
          })
        } else {
          return false
        }
      })
    },
    // 取消
    close () {
      this.$router.go(-1)
    },
    getDocumentInfo () {
      documentInfo.getById(this.$route.query.objectId).then(res => {
        this.updateForm = res.data.documentInfo
        if (this.updateForm.bidSection) {
          this.bidSection = this.updateForm.bidSection
        }
        this.$refs.ueditorContent.setContent(this.updateForm.content)
      })
    }
  },
  mounted () {
    this.getDocumentInfo()
  }
}
</script>
<style lang="less">
  #cloud_processedit{
  }
</style>
