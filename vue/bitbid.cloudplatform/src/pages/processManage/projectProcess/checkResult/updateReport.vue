<template>
  <div class="cloudcontent">
    <!--面包屑-->
    <el-breadcrumb separator-class="el-icon-arrow-right" class="breadcrumb_box">
      <el-breadcrumb-item :to="{ path: '/index' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/processManage/projectProcess' }">项目进度管理</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/processManage/projectProcess/project' }">项目管理</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/processManage/projectProcess/project/process' }">流程管理</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/processManage/projectProcess/project/process', query: {type: 'anno'}}">资格审查结果</el-breadcrumb-item>
      <el-breadcrumb-item class="active_bread">修改资格审查结果</el-breadcrumb-item>
    </el-breadcrumb>
    <div class="main">
      <el-form :model="updateForm" :rules="rules" ref="updateForm" :validate-on-rule-change="true">
        <el-row>
          <el-col :span="12">
            <el-form-item label="项目编号："  prop="tenderProjectCode">
              <el-input v-model="bidSection.tenderProjectCode" disabled></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="项目名称："  prop="tenderProjectName">
              <el-input v-model="bidSection.tenderProjectName" disabled></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="标段编号："  prop="bidSectionCode">
              <el-input v-model="bidSection.bidSectionCode" disabled></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="标段名称："  prop="bidSectionName">
              <el-input v-model="bidSection.bidSectionName" disabled></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="上传资格审查报告：" class="bitianicon">
              <upload-file @uploadSuccess="uploadOtherSuccess" @deleteSuccess="deleteSuccess"
                           :fileArrays="updateForm.fileInformationList"
                           fileType="9"
                           isImage="0">
              </upload-file>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24" class="ueditor_box">
            <el-form-item label="资格审查结果：" class="bitianicon">
              <el-button class="addbtn" @click="addResultBtn">+ 新增</el-button>
              <el-table
                :data="resultList"
                border
                style="width: 50%" header-cell-class-name="tableheader">
                <el-table-column
                  prop="bidderName"
                  label="投标人"
                  show-overflow-tooltip>
                  <template slot-scope='scope'>
                    <el-input v-model="scope.row.bidderName"></el-input>
                  </template>
                </el-table-column>
                <el-table-column
                  label="是否通过资格审查"
                  align="center"
                  width="150">
                  <template slot-scope="scope">
                    <el-radio-group v-model="scope.row.isPassQualificationExamine">
                      <el-radio :label="0">否</el-radio>
                      <el-radio :label="1">是</el-radio>
                    </el-radio-group>
                  </template>
                </el-table-column>
              </el-table>
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
import uploadFile from '@/components/upload/publicFileUpload'
import {documentInfo} from '@/api/cloudplatform'
export default {
  components: {
    uploadFile
  },
  data () {
    return {
      isSubmiting: false,
      updateForm: {
        fileInformationList: []
      },
      rules: {},
      // 富文本
      editread: false,
      content: '',
      resultList: [],
      bidSection: {}
    }
  },
  methods: {
    /**
     * 增加资格审查结果
     */
    addResultBtn () {
      this.resultList.push({})
    },
    // 上传附件
    uploadOtherSuccess (file) {
      this.updateForm.fileInformationList.push(file)
    },
    deleteSuccess (fileId) {
      this.updateForm.fileInformationList = this.updateForm.fileInformationList.filter(item => item.relativePath !== fileId)
    },
    // 提交/保存
    submit (form, status) {
      if (this.updateForm.fileInformationList.length < 1) {
        this.$message({
          message: '请上传资格审查报告！',
          type: 'warning'
        })
        return
      }
      if (!this.resultList || this.resultList.length < 1) {
        this.$message({
          message: '请添加资格审查结果！',
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
          let bidderName = ''
          let isPassQualificationExamine = ''
          this.resultList.map(item => {
            bidderName = bidderName + item.bidderName + ';'
            isPassQualificationExamine = isPassQualificationExamine + item.isPassQualificationExamine + ';'
          })
          this.updateForm.bidderName = bidderName.substring(0, bidderName.length - 1)
          this.updateForm.isPassQualificationExamine = isPassQualificationExamine.substring(0, isPassQualificationExamine.length - 1)
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
        let bidderName = this.updateForm.bidderName.split(';')
        let isPassQualificationExamine = this.updateForm.isPassQualificationExamine.split(';')
        for (let i = 0; i < bidderName.length; i++) {
          let result = {}
          result.bidderName = bidderName[i]
          result.isPassQualificationExamine = Number(isPassQualificationExamine[i])
          this.resultList.push(result)
        }
      })
    }
  },
  mounted () {
    this.getDocumentInfo()
  }
}
</script>

<style scoped>
  .addbtn{
    border: 1px solid #3f63f6;
    height: 32px;
    padding: 7px 12px;
    margin-bottom: 12px;
  }
</style>
