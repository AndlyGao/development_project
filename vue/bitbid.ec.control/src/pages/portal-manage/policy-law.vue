<template>
  <div id="portal-policylaw-page" class="cont-view">
    <!-- 面包屑区域start -->
    <div class="bread-box">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>门户信息管理</el-breadcrumb-item>
        <el-breadcrumb-item>政策法规管理</el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    <!-- 面包屑区域end -->
    <!-- 搜索区域start -->
    <div class="search-box">
      <el-row :gutter="20">
        <el-col :span="8">
          <el-input placeholder="请输入标题" v-model="searchForm.titleLike" class="input-with-select">
            <el-button slot="append" icon="el-icon-search" @click="search"></el-button>
          </el-input>
        </el-col>
        <el-col :span="16" class="add-btn">
          <el-button type="primary" size="small" @click="addLaws" style="float:right;">新 增</el-button>
        </el-col>
      </el-row>
    </div>
    <!-- 搜索区域end -->
    <div class="table-box">
      <el-table
        :data="tableData"
        border
        style="width: 100%">
        <el-table-column
          label="序号"
          type="index"
          width="60"
          :index="computedIndex"
          align="center">
        </el-table-column>
        <el-table-column
          prop="title"
          label="标题"
          :formatter="simpleFormatData"
          show-overflow-tooltip
          align="center">
        </el-table-column>
        <el-table-column
          prop="publishTime"
          label="发布时间"
          :formatter="simpleFormatData"
          align="center">
        </el-table-column>
        <el-table-column label="操作" align="center">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="text"
              @click="handleTableBtn(scope.row, 'look')">
              查看
            </el-button>
            <el-button
              size="mini"
              type="text"
              @click="handleTableBtn(scope.row, 'edit')">
              修改
            </el-button>
            <el-button
              size="mini"
              type="text"
              @click="handleTableBtn(scope.row, 'del')">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-box">
        <!--分页-->
        <el-pagination
          background
          layout="prev, pager, next"
          :total="page.total"
          :page-size='page.pageSize'
          :current-page.sync="page.currentPage"
          @current-change="handlePage"
          @next-click="handlePage">
        </el-pagination>
        <!--分页-->
      </div>
    </div>
    <!-- 弹窗区域start -->
    <div class="dialog-box">
      <el-dialog
        :title="dialogTitle"
        :visible.sync="showVisible"
        @closed="close"
        @opened="handleOpened"
        width="40%"
        center>
        <el-form :model="ruleForm" :rules="rules" ref="ruleForm" label-width="100px" class="demo-ruleForm">
          <el-form-item label="标题:" prop="title">
            <el-input v-model="ruleForm.title"></el-input>
          </el-form-item>
          <el-form-item class="bitianicon dialog-item" label="内容:">
            <ueditor class="ueditor" ref="contentUeditor" :editread="false"></ueditor>
          </el-form-item>
          <el-form-item label="附件:">
            <upload-file
              @uploadSuccess="uploadOtherSuccess"
              @deleteSuccess="deleteSuccess"
              :fileArrays="ruleForm.fileInformationList"
              isImage="0">
            </upload-file>
          </el-form-item>
        </el-form>
        <span slot="footer" class="dialog-footer">
          <el-button @click="showVisible = false">取 消</el-button>
          <el-button type="primary" @click="submitForm">确 定</el-button>
        </span>
      </el-dialog>
      <el-dialog
        title="查看"
        :visible.sync="showLookVisible"
        @closed="colsedLook"
        @opened="handleLookOpen"
        width="40%"
        center>
        <el-form :model="ruleLookForm" ref="ruleLookForm" label-width="100px" class="demo-ruleForm">
          <el-form-item label="标题:">
            <span>{{ruleLookForm.title}}</span>
          </el-form-item>
          <el-form-item label="内容:" class="dialog-item">
            <ueditor class="ueditor" ref="lookUeditor" :editread="true"></ueditor>
          </el-form-item>
          <el-form-item label="附件:" v-if="ruleLookForm.fileInformationList.length">
            <fileDownload
              :fileObject="ruleLookForm.fileInformationList[0]">
            </fileDownload>
          </el-form-item>
        </el-form>
        <span slot="footer" class="dialog-footer">
          <el-button @click="showLookVisible = false">取 消</el-button>
        </span>
      </el-dialog>
    </div>
    <!-- 弹窗区域end -->
  </div>
</template>
<script>
import ueditor from '@/components/ueditor/ueditor'
import uploadFile from '@/components/upload/publicFileUpload'
import {policy} from '@/api'
import fileDownload from '@/components/file-download'
export default {
  name: 'policy-law',
  data () {
    return {
      searchForm: {},
      ruleLookForm: {
        fileInformationList: []
      },
      tableData: [],
      page: {
        total: 0,
        pageSize: 10,
        pageNo: 0,
        currentPage: 1
      },
      showVisible: false,
      showLookVisible: false,
      ruleForm: {
        fileInformationList: []
      },
      rules: {
        title: [
          {required: true, message: '请填写标题', trigger: ['blur', 'change']}
        ]
      },
      dialogTitle: '新增',
      isLook: false
    }
  },
  components: {
    ueditor,
    uploadFile,
    fileDownload
  },
  methods: {
    close () {
      this.ruleForm = {fileInformationList: []}
      this.$refs['ruleForm'].resetFields()
    },
    colsedLook () {
      this.ruleLookForm = {fileInformationList: []}
      this.$refs['ruleLookForm'].resetFields()
    },
    // 上传附件
    uploadOtherSuccess (file) {
      this.ruleForm.fileInformationList = [file]
    },
    deleteSuccess (fileId) {
      this.ruleForm.fileInformationList = this.ruleForm.fileInformationList.filter(item => item.relativePath !== fileId)
    },
    /** 搜索 */
    search () {
      this.page.pageNo = 0
      this.page.currentPage = 1
      this.getTableData()
    },
    getTableData () {
      this.searchForm.pageNo = this.page.pageNo
      this.searchForm.pageSize = this.page.pageSize
      policy.queryList(this.searchForm).then(res => {
        this.tableData = res.data.newsPageInfo.list
        this.page.total = res.data.newsPageInfo.total
      })
    },
    /** 新增 */
    addLaws () {
      this.showVisible = true
      this.isLook = false
      this.dialogTitle = '新增'
    },
    handleOpened () {
      if (this.ruleForm.content) {
        this.$refs['contentUeditor'].setContent(this.ruleForm.content)
      } else {
        this.$refs['contentUeditor'].setContent('')
      }
    },
    handleLookOpen () {
      if (this.ruleLookForm.content) {
        this.$refs['lookUeditor'].setContent(this.ruleLookForm.content)
      } else {
        this.$refs['lookUeditor'].setContent('')
      }
    },
    /** 表格操作 */
    handleTableBtn (row, type) {
      if (Object.is(type, 'look')) {
        // 查看
        this.showLookVisible = true
        this.isLook = true
        this.ruleLookForm = Object.assign({fileInformationList: []}, row)
        let fileObj = {}
        fileObj.fileName = row.fileName
        fileObj.relativePath = row.fileUrl
        this.$nextTick(() => {
          this.ruleLookForm.fileInformationList.push(fileObj)
        })
      } else if (Object.is(type, 'edit')) {
        // 修改
        this.showVisible = true
        this.isLook = false
        this.dialogTitle = '修改'
        this.ruleForm = Object.assign({fileInformationList: []}, row)
        let fileObj = {}
        fileObj.fileName = row.fileName
        fileObj.relativePath = row.fileUrl
        this.$nextTick(() => {
          this.ruleForm.fileInformationList.push(fileObj)
        })
      } else if (Object.is(type, 'del')) {
        // 删除
        this.deleteMethod(row.id)
      }
    },
    /** 分页 */
    handlePage (nowNum) {
      this.page.currentPage = nowNum
      this.page.pageNo = (nowNum - 1) * this.page.pageSize
      this.getTableData()
    },
    /** 提交 */
    submitForm () {
      this.$refs['ruleForm'].validate(valid => {
        if (valid) {
          if (this.$refs['contentUeditor']) {
            this.ruleForm.content = this.$refs['contentUeditor'].getContent()
          }
          if (!this.ruleForm.content) {
            this.$message({
              type: 'warning',
              message: '请输入内容'
            })
            return false
          }
          if (this.ruleForm.fileInformationList && this.ruleForm.fileInformationList.length > 0) {
            this.ruleForm.fileName = this.ruleForm.fileInformationList[0].fileName
            this.ruleForm.fileUrl = this.ruleForm.fileInformationList[0].relativePath
          } else {
            this.ruleForm.fileName = ''
            this.ruleForm.fileUrl = ''
          }
          let form = Object.assign({}, this.ruleForm)
          delete form.fileInformationList
          delete form.createTime
          delete form.isDelete
          delete form.publishStatus
          delete form.publishTime
          policy.update(form).then(res => {
            // 成功
            if (res.data.resCode === '0000') {
              this.showVisible = false
              if (this.isLook) {
                // 新增重置搜索和分页
                this.searchForm = {}
                this.page.pageNo = 0
              }
              this.getTableData()
            }
          })
        } else {
          return false
        }
      })
    },
    /** 普通格式化数据，空的时候展示"---" */
    simpleFormatData (row, col, cellValue) {
      return cellValue || '-----'
    },
    /** 序号计算 */
    computedIndex (index) {
      return index + (this.page.currentPage - 1) * this.page.pageSize + 1
    },
    /** 删除 */
    deleteMethod (objectId) {
      this.$confirm('确认删除吗?', '提示', {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'error'
      }).then(() => {
        policy.deletePolicy(objectId).then(res => {
          if (res.data.resCode === '0000') {
            this.getTableData()
          }
        })
      }).catch(() => {
        return false
      })
    }
  },
  mounted () {
    this.getTableData()
  }
}
</script>
<style lang="less">
#portal-policylaw-page {
  background: #fff;
  .dialog-box {
    .ueditor-wrap {
      padding: 0;
    }
  }
}
.dialog-box .dialog-item .el-form-item__content {
  line-height: 0 !important;
}
</style>
