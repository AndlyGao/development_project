<template>
  <div class="cloudcontent">
    <!--面包屑-->
    <el-breadcrumb separator-class="el-icon-arrow-right" class="breadcrumb_box">
      <el-breadcrumb-item :to="{ path: '/index' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/processManage/projectProcess' }">项目进度管理</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/processManage/projectProcess/project' }">项目管理</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/processManage/projectProcess/project/process' }">流程管理</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/processManage/projectProcess/project/process', query: {type: 'anno'} }">招标公告/资格预审公告</el-breadcrumb-item>
      <el-breadcrumb-item class="active_bread">修改公告</el-breadcrumb-item>
    </el-breadcrumb>
    <div class="main">
      <el-form :model="updateForm" :rules="rules" ref= "updateForm" :validate-on-rule-change="true">
        <el-row>
          <el-col :span="12">
            <el-form-item label="项目编号："  prop="tenderProjectCode">
              <el-input v-model="updateForm.tenderProjectCode" disabled></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="项目名称："  prop="tenderProjectName">
              <el-input v-model="updateForm.tenderProjectName" disabled></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="标段编号："  prop="bidSectionCode">
              <el-input v-model="updateForm.bidSectionCode" disabled></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="标段名称："  prop="bidSectionName">
              <el-input v-model="updateForm.bidSectionName" disabled></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="公告名称："  prop="title">
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
                ref="multipleTable"
                :data="bidSectionList"
                border
                style="width: 100%" header-cell-class-name="tableheader"
                @selection-change="handleSelectionChange"
                :row-key="getRowKeys">
                <el-table-column
                  type="selection"
                  prop="objectId"
                  :reserve-selection="true"
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
          <el-col :span="24" class="ueditor_box">
            <el-form-item label="公告内容：" class="bitianicon">
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
        <el-button class="addbtn btn_right" @click="addNoticeBtn" v-if="false">+ 新增</el-button>
        <el-table
          :data="tableData"
          border
          style="width: 100%" header-cell-class-name="tableheader"
          v-if="false">
          <el-table-column
            type="index"
            label="序号"
            width="50">
          </el-table-column>
          <el-table-column
            prop="title"
            label="更正公告名称"
            show-overflow-tooltip>
          </el-table-column>
          <el-table-column
            prop="auditStatus"
            label="更正公告状态"
            :formatter="filterAuditStatus"
            show-overflow-tooltip>
          </el-table-column>
          <el-table-column
            prop="submitTime"
            label="提交时间"
            show-overflow-tooltip>
          </el-table-column>
          <el-table-column
            label="操作" align="center">
            <template slot-scope="scope">
              <el-button type="text" size="small" @click="handleNoticeDetail(scope)">查看</el-button>
              <el-button type="text" size="small" @click="handleNoticeEdit(scope)" v-if="scope.row.auditStatus === 0">编辑</el-button>
              <el-button type="text" size="small" @click="handleNoticeDel(scope)" v-if="scope.row.auditStatus === 0">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <!--分页-->
        <el-pagination
          background
          layout="prev, pager, next"
          :total="total"
          :page-size='pageSize'
          :current-page.sync="currentPage"
          @current-change="handleCurrentChange"
          @next-click="handleCurrentNext"
          v-if="false">
        </el-pagination>
        <!--分页-->
        <el-form-item class="submit-radio">
          <el-button type="primary" @click="handleClickEvent('updateForm','submit')" :loading="isSubmiting">提交</el-button>
          <el-button type="primary" @click="handleClickEvent('updateForm','save')" :loading="isSubmiting">保存</el-button>
          <el-button class="cancal" @click="handleClickEvent('updateForm','cancel')">取消</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import editor from '@/components/ueditor/ueditor.vue'
import uploadFile from '@/components/upload/publicFileUpload'
import {bulletinInfo, tenderProject} from '@/api/cloudplatform/index'
export default {
  components: {
    editor,
    uploadFile
  },
  data () {
    return {
      isSubmiting: false,
      updateForm: {
        fileInformationList: []
      },
      rules: {
        title: [
          { required: true, message: '公告名称不能为空', trigger: ['blur', 'change'] }
        ],
        bidDocReferMethod: [
          { required: true, message: '递交投标文件方法不能为空', trigger: ['blur', 'change'] }
        ],
        bulletinUrl: [
          { required: true, message: '公告URL不能为空', trigger: ['blur', 'change'] }
        ],
        publishUrl: [
          { required: true, message: '发布网站不能为空', trigger: ['blur', 'change'] }
        ],
        bidSectionList: [
          { required: true, message: '请选择标段', trigger: ['blur', 'change'] }
        ],
        content: [
          {required: true, message: '公告内容不能为空', trigger: ['blur', 'change']}
        ]
      },
      // 富文本
      editread: false,
      bidSectionList: [],
      sectionList: [],
      // 当前页
      currentPage: 1,
      pageNo: 0,
      total: 100, // 总条数
      pageSize: 10, // 每页展示条数
      tableData: [],
      tenderProjectInfo: {}
    }
  },
  watch: {
    '$route': 'getTableData'
  },
  created () {
    this.getChooseBulletinInfoInfo()
    this.getTenderProjectInfo()
  },
  methods: {
    /** 审批状态(0.未提交；1.待审批；2.已审批；3.已驳回；4.已提交（不需要审批）)  */
    filterAuditStatus (row) {
      if (row.auditStatus === 0) {
        return '已保存'
      } else if (row.auditStatus === 1) {
        return '待审批'
      } else if (row.auditStatus === 2) {
        return '已审批'
      } else if (row.auditStatus === 3) {
        return '已驳回'
      } else if (row.auditStatus === 4) {
        return '已提交'
      }
    },
    /** 获取当前公告信息 */
    getChooseBulletinInfoInfo () {
      bulletinInfo.getOne(this.$route.query.code).then((res) => {
        if (res.data.bulletinInfo) {
          this.updateForm = res.data.bulletinInfo
          // 获取文本编辑器的内容
          this.$refs.ueditor.setContent(this.content = this.updateForm.content)
          // 设置标段信息
          this.bidSectionList = (res.data.chooseBidSectionList).concat(res.data.notUsedBidSectionList)
          // 为该公告绑定的公告进行选中展示
          this.$refs.multipleTable.clearSelection() // 清空上次的标段选择信息
          // 给已选择的标段设置值
          let sectionIndexList = this.getCheckedSectionIndexList(res.data.chooseBidSectionList)
          let checkedSectionList = []
          sectionIndexList.forEach(index => checkedSectionList.push(this.bidSectionList[index]))
          this.toggleSelection(checkedSectionList)
          // 设置附件信息
          if (this.updateForm.fileInformationList.length > 0) {
            this.updateForm.fileInformationList.forEach(item => {
              item.objectId = null
            })
          }
          // 查询所包含的更正公告的信息
          // this.getTableData()
        }
      })
    },
    /** 根据标段code获取对应的招标项目信息 */
    getTenderProjectInfo () {
      tenderProject.getByBidSectionCode(this.$route.query.sectionSystemCode).then((res) => {
        if (res.data.tenderProject) {
          this.tenderProjectInfo = res.data.tenderProject
        }
      })
    },
    /** 得到公告中所包含标段的下标索引 */
    getCheckedSectionIndexList (chooseSectionList) {
      let results = []
      for (let i = 0; i < this.bidSectionList.length; i++) {
        for (let j = 0; j < chooseSectionList.length; j++) {
          if (this.bidSectionList[i].objectId === chooseSectionList[j].objectId) {
            results.push(i)
          }
        }
      }
      return results
    },
    /** 弹窗-标段的默认选择 */
    toggleSelection (rows) {
      if (rows) {
        rows.forEach(row => {
          this.$refs.multipleTable.toggleRowSelection(row, true)
        })
      } else {
        this.$refs.multipleTable.clearSelection()
      }
    },
    // 获取row的key值
    getRowKeys (row) {
      return row.objectId
    },
    /* 按钮点击事件：提交、保存、取消 */
    handleClickEvent (formName, type) {
      if (Object.is(type, 'submit') || Object.is(type, 'save')) {
        if (this.sectionList.length === 0) {
          this.$message.warning('请选择标段！')
          return false
        } else {
          this.updateForm.bidSectionList = this.sectionList
        }
        if (!this.$refs.ueditor.getContent()) {
          this.$message.warning('公告内容不能为空！')
          return false
        } else {
          // 获取文本编辑器中的内容
          this.updateForm.content = this.$refs.ueditor.getContent()
        }
        this.$refs[formName].validate((valid) => {
          if (valid) {
            this.isSubmiting = true
            // 根据type设置当前招标项目的状态
            if (Object.is(type, 'submit')) {
              this.updateForm.auditStatus = 4 // 暂时不考虑审批
              // 更新标段的流转流程
              console.log(111)
              console.log(this.tenderProjectInfo)
              this.updateForm.bidSectionList.forEach(item => {
                item.flowStatus = this.tenderProjectInfo.isPreBid === 0 ? 12 : 9
              })
            } else {
              this.updateForm.auditStatus = 0
              // 更新标段的流转流程
              this.updateForm.bidSectionList.forEach(item => {
                item.flowStatus = this.tenderProjectInfo.isPreBid === 0 ? 6 : 7
              })
            }
            bulletinInfo.save(this.updateForm).then((res) => {
              if (res.data.resCode === '0000') {
                this.isSubmiting = false
                this.$router.go(-1)
                // this.$router.push({path: '/processManage/projectProcess/project/process'})
              }
            })
          } else {
            return false
          }
        })
      } else if (Object.is(type, 'cancel')) {
        this.$router.go(-1)
        // this.$router.push({path: '/processManage/projectProcess/project/process'})
      }
    },
    /** 获取列表数据 */
    getTableData () {
      bulletinInfo.getByRelaSection({
        pageNo: this.pageNo,
        pageSize: this.pageSize,
        enterpriseId: this.$store.getters.authUser.enterpriseId,
        tenderSystemCode: this.updateForm.tenderSystemCode,
        sectionSystemCode: this.updateForm.sectionSystemCode,
        type: 3
      }).then((res) => {
        if (res.data.bulletinInfoList && res.data.bulletinInfoList.list) {
          this.tableData = res.data.bulletinInfoList.list
          this.total = res.data.bulletinInfoList.total
        }
      })
    },
    // 表单分页
    handleCurrentChange (nowNum) {
      this.currentPage = nowNum
      this.pageNo = (nowNum - 1) * this.pageSize
      this.getTableData(parseInt(this.pageNo), parseInt(this.pageSize))
    },
    handleCurrentNext (nowNum) {
      this.currentPage = nowNum
      this.pageNo = (nowNum - 1) * this.pageSize
      this.getTableData(parseInt(this.pageNo), parseInt(this.pageSize))
    },
    handleSelectionChange (val) {
      this.sectionList = val
    },
    /**
     * 增加供应商
     */
    addSupplierBtn () {
      this.supplierList.push({name: 2})
    },
    delSupplierBtn (index) {
      this.supplierList.splice(index, 1)
    },
    /**
     * 增加邀请公告
     */
    addNoticeBtn () {
      this.$router.push({path: `/processManage/projectProcess/announcement/add_notice`, query: {bulletinCode: this.updateForm.code}})
    },
    handleNoticeEdit (scope) {
      this.$router.push({path: `/processManage/projectProcess/announcement/update_notice`, query: {code: scope.row.code}})
    },
    handleNoticeDetail (scope) {
      this.$router.push({path: `/processManage/projectProcess/announcement/notice_detail/${scope.row.objectId}`, query: {code: scope.row.code}})
    },
    handleNoticeDel (scop) {
      this.$confirm('确认删除吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'error'
      }).then(() => {
        // **
      }).catch(() => {
        return false
      })
    },
    // 上传附件
    uploadOtherSuccess (file) {
      this.updateForm.fileInformationList.push(file)
    },
    deleteSuccess (fileId) {
      this.updateForm.fileInformationList = this.updateForm.fileInformationList.filter(item => item.relativePath !== fileId)
    }
  },
  mounted () {
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
  .btn_right{
    float: right;
  }
</style>
