<template>
  <div class="cloudcontent">
    <!--面包屑-->
    <el-breadcrumb separator-class="el-icon-arrow-right" class="breadcrumb_box">
      <el-breadcrumb-item :to="{ path: '/index' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/processManage/projectProcess' }">项目进度管理</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/processManage/projectProcess/project' }">项目管理</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/processManage/projectProcess/project/process' }">流程管理</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/processManage/projectProcess/project/process', query: {type: 'invite'}}">招标邀请</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/processManage/projectProcess/invite/add'}">添加投标邀请书</el-breadcrumb-item>
      <el-breadcrumb-item class="active_bread">添加更正邀请公告</el-breadcrumb-item>
    </el-breadcrumb>
    <div class="main">
      <el-form :model="updateForm" :rules="rules" ref="updateForm" :validate-on-rule-change="true">
        <el-row>
          <el-col :span="12">
            <el-form-item label="项目编号：">
              <el-input v-model="updateForm.tenderProjectCode" disabled></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="项目名称：">
              <el-input v-model="updateForm.tenderProjectName" disabled></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="标段编号：">
              <el-input v-model="updateForm.bidSectionCode" disabled></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="标段名称：">
              <el-input v-model="updateForm.bidSectionName" disabled></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="更正邀请公告名称："  prop="title">
              <el-input v-model="updateForm.title"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="递交投标文件方法："  prop="bidDocReferMethod">
              <el-input v-model="updateForm.bidDocReferMethod"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="公告URL："  prop="bulletinUrl">
              <el-input v-model="updateForm.bulletinUrl"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="发布网站："  prop="publishUrl">
              <el-input v-model="updateForm.publishUrl"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="标段信息："  prop="bidSectionList">
              <el-table
                :data="updateForm.bidSectionList"
                border
                style="width: 100%" header-cell-class-name="tableheader"
                @selection-change="handleSelectionChange">
                <el-table-column
                  type="selection"
                  width="50">
                </el-table-column>
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
            <el-form-item label="供应商信息："  prop="supplierBaseList">
              <el-button class="addbtn" @click="addSupplierBtn">+ 新增</el-button>
              <el-table
                :data="updateForm.supplierBaseList"
                border
                style="width: 100%" header-cell-class-name="tableheader">
                <el-table-column
                  prop="supplierName"
                  label="供应商名称"
                  show-overflow-tooltip>
                  <template slot-scope='scope'>
                    <el-input v-model="scope.row.supplierName"></el-input>
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
          <el-col :span="24" class="ueditor_box">
            <el-form-item label="邀请书内容：" class="bitianicon">
              <editor ref="ueditor" class="ueditor" :editread="editread"></editor>
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
import {tenderProject, bidSection, bulletinInfo} from '@/api/cloudplatform/index'
import {links} from '@/assets/js/validate'
export default {
  components: {
    editor,
    uploadFile
  },
  name: '',
  data () {
    // 网址验证
    let validLinks = (rule, value, callback) => {
      if (!value) {
        callback()
      } else if (!links(value)) {
        callback(new Error('请输入以http(s)开头的完整信息！'))
      } else {
        callback()
      }
    }
    return {
      isSubmiting: false,
      updateForm: {
        tenderProjectName: '',
        fileInformationList: [],
        supplierBaseList: [],
        bidSectionList: []
      },
      rules: {
        title: [
          { required: true, message: '公告名称不能为空', trigger: ['blur', 'change'] }
        ],
        bidDocReferMethod: [
          { required: true, message: '递交投标文件方法不能为空', trigger: ['blur', 'change'] }
        ],
        bulletinUrl: [
          { required: true, message: '公告URL不能为空', trigger: ['blur', 'change'] },
          {validator: validLinks, trigger: ['blur', 'change']}
        ],
        publishUrl: [
          { required: true, message: '发布网站不能为空', trigger: ['blur', 'change'] },
          {validator: validLinks, trigger: ['blur', 'change']}
        ],
        bidSectionList: [
          { required: true, message: '请选择标段', trigger: ['blur', 'change'] }
        ],
        supplierList: [
          { required: true, message: '供应商不能为空', trigger: ['blur', 'change'] }
        ]
      },
      // 富文本
      editread: false,
      bidSectionList: [], // 页面上展示出来的可选择的标段信息
      tenderProjectInfo: {} // 用于查询是否为资格预审的项目（公告信息、招标项目类型、招标组织形式）
    }
  },
  methods: {
    /** 获取当前公告信息 */
    getChooseBulletinInfoInfo () {
      bulletinInfo.getOne(this.$route.query.bulletinCode).then((res) => {
        if (res.data.bulletinInfo) {
          // 页面展示
          this.updateForm.tenderProjectName = res.data.bulletinInfo.tenderProjectName
          this.updateForm.tenderProjectCode = res.data.bulletinInfo.tenderProjectCode
          this.updateForm.bidSectionName = res.data.bulletinInfo.bidSectionName
          this.updateForm.bidSectionCode = res.data.bulletinInfo.bidSectionCode
          // 用于保存关联关系
          this.updateForm.tenderSystemCode = res.data.bulletinInfo.tenderSystemCode
          this.updateForm.sectionSystemCode = res.data.bulletinInfo.sectionSystemCode
          this.updateForm.enterpriseId = res.data.bulletinInfo.enterpriseId
          this.updateForm.departmentId = res.data.bulletinInfo.departmentId
          this.updateForm.originalTitle = res.data.bulletinInfo.title
        }
        if (res.data.chooseBidSectionList) {
          // 设置标段信息
          this.updateForm.bidSectionList = res.data.chooseBidSectionList
          if (this.updateForm.bidSectionList.length > 0) {
            this.updateForm.bidSectionList.forEach(item => {
              item.objectId = null
            })
          }
        }
        if (res.data.supplierBaseList) {
          // 设置供应商信息
          this.updateForm.supplierBaseList = res.data.supplierBaseList
          if (this.updateForm.supplierBaseList.length > 0) {
            this.updateForm.supplierBaseList.forEach(item => {
              item.objectId = null
            })
          }
        }
      })
    },
    handleSelectionChange (val) {
      this.sectionList = val
    },
    /**
     * 增加供应商
     */
    addSupplierBtn () {
      this.updateForm.supplierBaseList.push({})
    },
    delSupplierBtn (index) {
      this.updateForm.supplierBaseList.splice(index, 1)
    },
    // 上传附件
    uploadOtherSuccess (file) {
      this.updateForm.fileInformationList.push(file)
    },
    deleteSuccess (fileId) {
      this.updateForm.fileInformationList = this.updateForm.fileInformationList.filter(item => item.relativePath !== fileId)
    },
    /** 获取当前选择的标段信息 */
    getChooseBidSectionInfo () {
      bidSection.getOne(this.$route.query.code).then((res) => {
        if (res.data.bidSection) {
          // 页面展示
          this.updateForm.tenderProjectName = res.data.bidSection.tenderProjectName
          this.updateForm.tenderProjectCode = res.data.bidSection.tenderProjectCode
          this.updateForm.bidSectionName = res.data.bidSection.bidSectionName
          this.updateForm.bidSectionCode = res.data.bidSection.bidSectionCode
          // 用于保存关联关系
          this.updateForm.tenderSystemCode = res.data.bidSection.tenderSystemCode
          this.updateForm.sectionSystemCode = res.data.bidSection.code
          this.updateForm.enterpriseId = res.data.bidSection.enterpriseId
          this.updateForm.departmentId = res.data.bidSection.departmentId
        }
      })
    },
    /** 获取不在任何公告中的标段信息 */
    getBidSectionList () {
      bidSection.getNotUsedBidSection(this.$route.query.code).then((res) => {
        if (res.data.bidSectionList) {
          this.bidSectionList = res.data.bidSectionList
        }
      })
    },
    /** 根据标段code获取对应的招标项目信息 */
    getTenderProjectInfo () {
      tenderProject.getByBidSectionCode(this.$route.query.code).then((res) => {
        if (res.data.tenderProject) {
          this.tenderProjectInfo = res.data.tenderProject
          // 公告类型
          this.updateForm.type = this.tenderProjectInfo.isPreBid === 0 ? 1 : 2
          // 招标项目类型
          this.updateForm.tenderProjectType = this.tenderProjectInfo.tenderProjectType
          // 招标组织形式
          this.updateForm.tenderOrganizeForm = this.tenderProjectInfo.tenderOrganizeForm
        }
      })
    },
    init () {
      // this.getChooseBidSectionInfo()
      // this.getBidSectionList()
      // this.getTenderProjectInfo()
      this.getChooseBulletinInfoInfo()
    },
    // 提交/保存
    submit (form, status) {
      this.$refs[form].validate((valid) => {
        if (valid) {
          if (this.sectionList.length === 0) {
            this.$message.warning('请选择标段！')
            return false
          } else {
            this.updateForm.bidSectionList = this.sectionList
          }
          let content = this.$refs.ueditor.getContent()
          if (!content) {
            this.$message({
              type: 'warning',
              message: '请输入邀请书内容'
            })
            return false
          }
          this.isSubmiting = true
          // 根据type设置当前招标项目的状态
          if (Object.is(status, 1)) {
            this.updateForm.auditStatus = 4 // 暂时不考虑审批
          } else {
            this.updateForm.auditStatus = 0
          }
          this.updateForm.type = 4
          this.updateForm.content = content
          // 更新标段的流转流程
          /*  this.updateForm.bidSectionList.forEach(item => {
            item.flowStatus = 11
          }) */
          bulletinInfo.save(this.updateForm).then((res) => {
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
    }
  },
  mounted () {
    this.init()
  }
}
</script>

<style scoped>
</style>
