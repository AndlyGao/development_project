<template>
  <div class="cloudcontent">
    <!--面包屑-->
    <el-breadcrumb separator-class="el-icon-arrow-right" class="breadcrumb_box">
      <el-breadcrumb-item :to="{ path: '/index' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/processManage/projectProcess' }">项目进度管理</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/processManage/projectProcess/project' }">项目管理</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/processManage/projectProcess/project/process' }">流程管理</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/processManage/projectProcess/project/process', query: {type: 'pre_file'} }">响应资格预审</el-breadcrumb-item>
      <el-breadcrumb-item class="active_bread">添加投标报名信息</el-breadcrumb-item>
    </el-breadcrumb>
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
            <el-form-item label="投标人："  prop="bidderName">
              <el-input v-model="updateForm.bidderName"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系人："  prop="contactName">
              <el-input v-model="updateForm.contactName"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="联系方式："  prop="contactNumber">
              <el-input v-model="updateForm.contactNumber"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="投标人报名时间："  prop="signUpTime">
              <el-date-picker
                v-model="updateForm.signUpTime"
                type="datetime"
                placeholder="选择日期时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="电子邮箱："  prop="contactEmail">
              <el-input v-model="updateForm.contactEmail"></el-input>
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
import {signUpInfo, bidSection} from '@/api/cloudplatform'
import uploadFile from '@/components/upload/publicFileUpload'
import {validPhoneUser, validEmail} from '@/assets/js/validate'
export default {
  components: {
    uploadFile
  },
  name: '',
  data () {
    return {
      isSubmiting: false,
      updateForm: {
        fileInformationList: []
      },
      rules: {
        bidderName: {required: true, message: '请填写投标人', trigger: 'blur'},
        contactName: {required: true, message: '请填写联系人', trigger: 'blur'},
        contactNumber: [
          {required: true, message: '请填写联系方式', trigger: 'blur'},
          {validator: validPhoneUser, trigger: 'blur'}
        ],
        contactEmail: {validator: validEmail, trigger: 'blur'}
      },
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
          this.updateForm.tenderSystemCode = this.$route.query.tenderSystemCode
          this.updateForm.sectionSystemCode = this.$route.query.sectionSystemCode
          this.updateForm.signUpTime = new Date(this.updateForm.signUpTime).getTime()
          signUpInfo.update(this.updateForm).then((res) => {
            this.isSubmiting = false
            if (res.data.resCode === '0000') {
              this.$router.go(-1)
            }
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
    // 查询当前标段信息
    getBidSection () {
      bidSection.getOne(this.$route.query.sectionSystemCode).then(res => {
        this.bidSection = res.data.bidSection
      })
    }
  },
  mounted () {
    this.getBidSection()
  }
}
</script>

<style scoped>

</style>
