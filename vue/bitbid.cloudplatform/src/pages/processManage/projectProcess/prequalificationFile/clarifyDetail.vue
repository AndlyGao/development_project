<template>
  <div class="cloudcontent">
    <!--面包屑-->
    <el-breadcrumb separator-class="el-icon-arrow-right" class="breadcrumb_box">
      <el-breadcrumb-item :to="{ path: '/index' }">首页</el-breadcrumb-item>
      <template v-if="isApproved === '1' || isApproved === '2'">
        <el-breadcrumb separator-class="el-icon-arrow-right" class="breadcrumb_box">
          <el-breadcrumb-item :to="{ path: '/dailyManage/onlineapprove' }">在线审批</el-breadcrumb-item>
          <el-breadcrumb-item :to="{ path: '/dailyManage/onlineapprove/initiate' }" v-if="type === 'initiate'">我发起的</el-breadcrumb-item>
          <el-breadcrumb-item :to="{ path: '/dailyManage/onlineapprove/need' }" v-if="type === 'need'">待我审批</el-breadcrumb-item>
          <el-breadcrumb-item class="active_bread">{{isApproved === '1' ? '审批 ': '审批详情'}}</el-breadcrumb-item>
        </el-breadcrumb>
      </template>
      <template v-else>
        <el-breadcrumb-item :to="{ path: '/processManage/projectProcess' }">项目进度管理</el-breadcrumb-item>
        <el-breadcrumb-item :to="{ path: '/processManage/projectProcess/project' }">项目管理</el-breadcrumb-item>
        <el-breadcrumb-item :to="{ path: '/processManage/projectProcess/project/process' }">流程管理</el-breadcrumb-item>
        <el-breadcrumb-item :to="{ path: '/processManage/projectProcess/project/process', query: {type: 'anno'}}">资格预审文件</el-breadcrumb-item>
        <el-breadcrumb-item class="active_bread">查看资格预审澄清文件</el-breadcrumb-item>
      </template>
    </el-breadcrumb>
    <div class="main">
      <el-form :model="updateForm" :validate-on-rule-change="true">
        <el-row>
          <el-col :span="12">
            <el-form-item label="项目编号：">
              <span>{{bidSection.tenderProjectCode}}</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="项目名称：">
              <span>{{bidSection.tenderProjectName}}</span>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="标段编号：">
              <span>{{bidSection.bidSectionCode}}</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="标段名称：">
              <span>{{bidSection.bidSectionName}}</span>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="文件发布时间：">
              <span>{{updateForm.documentReleaseTime}}</span>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="标段信息：">
              <el-table
                :data="bidSectionList"
                border
                style="width: 100%" header-cell-class-name="tableheader">
                <el-table-column
                  prop="bidSectionCode"
                  label="标段编号"
                  show-overflow-tooltip>
                </el-table-column>
                <el-table-column
                  prop="bidSectionName"
                  label="标段名称"
                  show-overflow-tooltip>
                </el-table-column>
              </el-table>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="资格预审澄清文件：">
              <el-table
                :data="updateForm.fileInformationList"
                border
                style="width: 100%" header-cell-class-name="tableheader">
                <el-table-column
                  type="index"
                  label="序号"
                  width="60"
                  align="center">
                </el-table-column>
                <el-table-column
                  prop="fileName"
                  label="文件名称"
                  show-overflow-tooltip>
                </el-table-column>
                <el-table-column
                  label="操作" align="center" width="200">
                  <template slot-scope="scope">
                    <el-button type="text" size="small" @click="lookFile(scope.row)">查看</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24" class="ueditor_box">
            <el-form-item label="变更内容：">
              <editor ref="ueditor" class="ueditor" :editread="editread"></editor>
            </el-form-item>
          </el-col>
        </el-row>
        <approve @approveForm="approveForm" ref="approveForm" v-if="isApproved === '1' && type === 'need'"></approve>
        <el-row v-if="type === 'initiate'">
          <el-col :span="24">
            <el-form-item label="当前审批人："  prop="tenderProjectCode">
              <span>{{updateForm.tenderProjectCode}}</span>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row v-if="isApproved === '1' || isApproved === '2'">
          <el-col :span="24">
            <el-form-item label="审核记录："  prop="tenderProjectCode">
              <el-table
                :data="recordList"
                border
                style="width: 100%" header-cell-class-name="tableheader">
                <el-table-column
                  type="index"
                  label="序号">
                </el-table-column>
                <el-table-column
                  prop="name"
                  label="审核结果"
                  show-overflow-tooltip>
                </el-table-column>
                <el-table-column
                  prop="status"
                  label="审核人"
                  show-overflow-tooltip>
                </el-table-column>
                <el-table-column
                  prop="status"
                  label="审核时间"
                  show-overflow-tooltip>
                </el-table-column>
                <el-table-column
                  prop="status"
                  label="审核意见"
                  show-overflow-tooltip>
                </el-table-column>
              </el-table>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item class="submit-radio">
          <el-button type="primary" @click="cancleApprove" v-if="type === 'initiate'">撤回</el-button>
          <template v-else>
            <el-button type="primary" @click="submitApprove" v-if="isApproved === '1'">提交</el-button>
            <el-button class="cancal" @click="$router.go(-1)">返回</el-button>
          </template>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>
<script>
import editor from '@/components/ueditor/ueditor.vue'
import {documentInfo, bidSection} from '@/api/cloudplatform'
import {downloadFile} from '@/assets/js/common'
import approve from '@/components/approve.vue'
export default {
  components: {
    editor,
    approve
  },
  data () {
    return {
      // 是否显示审批（0：详情 1：审批 2：审批详情）
      isApproved: this.$route.query.isApproved || 0,
      // initiate: 我发起的 need:待审批的
      type: this.$route.query.type || '',
      recordList: [],
      updateForm: {},
      // 富文本
      editread: true,
      bidSectionList: [],
      bidSection: {}
    }
  },
  methods: {
    lookFile (file) {
      downloadFile(file.fileName, file.relativePath)
    },
    getDocumentInfo () {
      documentInfo.getById(this.$route.params.objectId).then(res => {
        this.updateForm = res.data.documentInfo
        this.bidSectionList = this.updateForm.bidSectionList
        this.$refs.ueditor.setContent(this.updateForm.content)
      })
    },
    // 查询当前标段信息
    getBidSection () {
      bidSection.getOne(this.$route.query.sectionSystemCode).then(res => {
        this.bidSection = res.data.bidSection
      })
    },
    approveForm (formV) {
      this.updateForm = formV
    },
    submitApprove () {
      // 必填验证
      this.$refs.approveForm.handleValidate()
      if (this.updateForm.auditStatus === 0 && !this.updateForm.opinion.trim()) {
        this.$message({
          message: '请填写审批意见',
          type: 'warning'
        })
        return false
      }
      // 提交数据
    },
    // 撤回审批
    cancleApprove () {
    }
  },
  mounted () {
    this.getDocumentInfo()
    this.getBidSection()
  }
}
</script>

<style scoped>
</style>
