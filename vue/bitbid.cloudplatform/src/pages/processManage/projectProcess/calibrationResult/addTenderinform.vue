<template>
  <div class="cloudcontent" id="cloud_processedit">
    <!--面包屑-->
    <el-breadcrumb separator-class="el-icon-arrow-right" class="breadcrumb_box">
      <el-breadcrumb-item :to="{ path: '/index' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/processManage/projectProcess' }">项目进度管理</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/processManage/projectProcess/project' }">项目管理</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/processManage/projectProcess/project/process' }">流程管理</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/processManage/projectProcess/project/process' }">定标结果</el-breadcrumb-item>
      <el-breadcrumb-item class="active_bread">添加招标结果通知书</el-breadcrumb-item>
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
            <el-form-item label="中标结果通知书标题："  prop="content">
              <el-input v-model="updateForm.content"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="中标人："  prop="successfulBidders">
              <el-input v-model="updateForm.successfulBidders"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="未中标人：">
              <el-button class="addbtn" @click="addSupplierBtn">+ 新增</el-button>
              <el-table
                :data="updateForm.unsuccessfulBidderList"
                border
                style="width: 100%" header-cell-class-name="tableheader">
                <el-table-column
                  prop="bidderName"
                  label="投标人名称"
                  show-overflow-tooltip>
                  <template slot-scope='scope'>
                    <el-input v-model="scope.row.bidderName"></el-input>
                  </template>
                </el-table-column>
                <el-table-column
                  label="操作" align="center" width="100">
                  <template slot-scope="scope">
                    <el-button type="text" size="small" @click="delSupplierBtn(scope.$index)">删除</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="招标结果附件：">
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
import uploadFile from '@/components/upload/publicFileUpload'
import {resultInfo, bidSection} from '@/api/cloudplatform'
export default {
  components: {
    uploadFile
  },
  data () {
    return {
      tenderSystemCode: this.$route.query.tenderSystemCode,
      sectionSystemCode: this.$route.query.sectionSystemCode,
      isSubmiting: false,
      updateForm: {
        fileInformationList: [],
        unsuccessfulBidderList: []
      },
      bidSection: [],
      rules: {
        successfulBidders: {required: true, message: '请填写中标人', trigger: ['blur', 'change']},
        content: {required: true, message: '请填写中标结果通知书标题', trigger: ['blur', 'change']}
      },
      tableData: []
    }
  },
  methods: {
    /**
     * 增加供应商
     */
    addSupplierBtn () {
      this.updateForm.unsuccessfulBidderList.push({})
    },
    delSupplierBtn (index) {
      this.updateForm.unsuccessfulBidderList.splice(index, 1)
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
          this.updateForm.tenderSystemCode = this.tenderSystemCode
          this.updateForm.sectionSystemCode = this.sectionSystemCode
          this.updateForm.type = 6
          let unsuccessfulBidders = ''
          this.updateForm.unsuccessfulBidderList.map(item => {
            unsuccessfulBidders = unsuccessfulBidders + item.bidderName + ';'
          })
          this.updateForm.unsuccessfulBidders = unsuccessfulBidders.substring(0, unsuccessfulBidders.length - 1)
          resultInfo.update(this.updateForm).then((res) => {
            if (res.data.resCode === '0000') {
              this.isSubmiting = false
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
      bidSection.getOne(this.sectionSystemCode).then(res => {
        this.bidSection = res.data.bidSection
      })
    },
    init () {
      this.getBidSection()
    }
  },
  watch: {
    '$route': 'init'
  },
  mounted () {
    this.init()
  }
}
</script>
<style lang="less">
  #cloud_processedit{
    .addbtn{
      border: 1px solid #3f63f6;
      height: 32px;
      padding: 7px 12px;
      margin-bottom: 12px;
    }
  }
</style>
