<template>
  <div class="cloudcontent" id="cloud_processtype">
    <div class="box">
      <div class="topmain">
        资格预审专家
        <!--按钮-->
        <el-button class="addbutton" @click="handleAdd" v-if="operationFlag">
          <span> + 添加</span>
        </el-button>
        <!--按钮-->
      </div>
      <el-table
        :data="tableData"
        border>
        <el-table-column
          type="index"
          label="序号"
          width="50">
        </el-table-column>
        <el-table-column
          prop="expertName"
          label="专家姓名"
          width="180">
        </el-table-column>
        <el-table-column
          prop="date"
          label="专业类别"
          width="180">
          <template slot-scope="scope">
            <p v-for="(item, index) in scope.row.expertclassify" :key="index">{{item}}</p>
          </template>
        </el-table-column>
        <el-table-column
          prop="mobilePhone"
          label="联系方式"
          width="180">
        </el-table-column>
        <el-table-column
          prop="auditStatus"
          label="提交状态"
          width="180">
          <template slot-scope="scope">
            <span  v-if="scope.row.auditStatus === 0">已保存</span>
            <span  v-if="scope.row.auditStatus === 1">待审批</span>
            <span  v-if="scope.row.auditStatus === 2">已审批</span>
            <span  v-if="scope.row.auditStatus === 3">已驳回</span>
            <span  v-if="scope.row.auditStatus === 4">已提交</span>
          </template>
        </el-table-column>
        <el-table-column
          prop="submitTime"
          label="提交时间">
        </el-table-column>
        <el-table-column
          label="操作" align="center" width="260">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="handleDetail(scope)">查看</el-button>
            <el-button type="text" size="small" v-if="scope.row.auditStatus === 0 || scope.row.auditStatus === 3" @click="handleEdit(scope)">编辑</el-button>
            <el-button type="text" size="small" v-if="scope.row.auditStatus === 0 || scope.row.auditStatus === 3" @click="handleDel(scope, 'expert')">删除</el-button>
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
        @current-change="handleCurrentChange">
      </el-pagination>
      <!--分页-->
    </div>
    <div class="box">
      <div class="topmain">
        资格审查报告
        <!--按钮-->
        <el-button class="addbutton" @click="handleAddReport" v-if="operationFlag">
          <span> + 添加</span>
        </el-button>
        <!--按钮-->
      </div>
      <el-table
        :data="reportData"
        border>
        <el-table-column
          type="index"
          label="序号"
          width="50">
        </el-table-column>
        <el-table-column
          prop="fileName"
          label="资格审查报告"
          width="180">
          <template slot-scope="scope">
            <p v-for="(item, index) in scope.row.fileInformationList" :key="index">{{item.fileName}}</p>
          </template>
        </el-table-column>
        <el-table-column
          prop="auditStatus"
          label="提交状态"
          width="180">
          <template slot-scope="scope">
            <span  v-if="scope.row.auditStatus === 0">已保存</span>
            <span  v-if="scope.row.auditStatus === 1">待审批</span>
            <span  v-if="scope.row.auditStatus === 2">已审批</span>
            <span  v-if="scope.row.auditStatus === 3">已驳回</span>
            <span  v-if="scope.row.auditStatus === 4">已提交</span>
          </template>
        </el-table-column>
        <el-table-column
          prop="submitTime"
          label="提交时间">
        </el-table-column>
        <el-table-column
          label="操作" align="center" width="260">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="handleDetailReport(scope)">查看</el-button>
            <el-button type="text" size="small" v-if="scope.row.auditStatus === 0 || scope.row.auditStatus === 3" @click="handleEditReport(scope)">编辑</el-button>
            <el-button type="text" size="small" v-if="scope.row.auditStatus === 0 || scope.row.auditStatus === 3" @click="handleDel(scope, 'report')">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <!--分页-->
      <el-pagination
        background
        layout="prev, pager, next"
        :total="reportTotal"
        :page-size='reportPageSize'
        :current-page.sync="reportCurrentPage"
        @current-change="handleReportCurrentChange">
      </el-pagination>
      <!--分页-->
    </div>
    <div class="box">
      <div class="topmain">
        资格预审结果通知书
        <!--按钮-->
        <el-button class="addbutton" @click="handleAddResult" v-if="operationFlag">
          <span> + 添加</span>
        </el-button>
        <!--按钮-->
      </div>
      <el-table
        :data="resultInfoData"
        border>
        <el-table-column
          type="index"
          label="序号"
          width="50">
        </el-table-column>
        <el-table-column
          prop="bidderName"
          label="投标人"
          width="180">
        </el-table-column>
        <el-table-column
          prop="fileName"
          label="通知书附件名称"
          width="180">
          <template slot-scope="scope">
            <p v-for="(item, index) in scope.row.fileInformationList" :key="index">{{item.fileName}}</p>
          </template>
        </el-table-column>
        <el-table-column
          prop="auditStatus"
          label="提交状态"
          width="180">
          <template slot-scope="scope">
            <span  v-if="scope.row.auditStatus === 0">已保存</span>
            <span  v-if="scope.row.auditStatus === 1">待审批</span>
            <span  v-if="scope.row.auditStatus === 2">已审批</span>
            <span  v-if="scope.row.auditStatus === 3">已驳回</span>
            <span  v-if="scope.row.auditStatus === 4">已提交</span>
          </template>
        </el-table-column>
        <el-table-column
          prop="submitTime"
          label="提交时间">
        </el-table-column>
        <el-table-column
          label="操作" align="center" width="260">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="handleDetailResult(scope)">查看</el-button>
            <el-button type="text" size="small" v-if="scope.row.auditStatus === 0 || scope.row.auditStatus === 3" @click="handleEditResult(scope)">编辑</el-button>
            <el-button type="text" size="small" v-if="scope.row.auditStatus === 0 || scope.row.auditStatus === 3" @click="handleDel(scope, 'result')">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <!--分页-->
      <el-pagination
        background
        layout="prev, pager, next"
        :total="resultTotal"
        :page-size='resultPageSize'
        :current-page.sync="resultCurrentPage"
        @current-change="handleResultCurrentChange">
      </el-pagination>
      <!--分页-->
    </div>
  </div>
</template>
<script>
import {resultInfo, expert, documentInfo} from '@/api/cloudplatform'
import * as expertClassify from '@/assets/js/expertClassify'
export default {
  components: {
  },
  props: ['tenderSystemCode', 'sectionSystemCode', 'chooseSectionSystemCode', 'operationFlag'],
  data () {
    return {
      tableData: [],
      resultInfoData: [],
      reportData: [],
      // 当前页
      currentPage: 1,
      pageNo: 0,
      total: null, // 总条数
      pageSize: 10, // 每页展示条数
      resultCurrentPage: 1,
      resultPageNo: 0,
      resultTotal: null, // 总条数
      resultPageSize: 10, // 每页展示条数
      reportCurrentPage: 1,
      reportPageNo: 0,
      reportTotal: null, // 总条数
      reportPageSize: 10 // 每页展示条数
    }
  },
  watch: {
    $route: 'resetView'
  },
  methods: {
    resetView () {
      this.getResultInfoList()
      this.getExpertList()
      this.getReportList()
    },
    // 编辑
    handleEdit (scope) {
      this.$router.push({path: `/processManage/projectProcess/checkresult/update`,
        query: {objectId: scope.row.objectId,
          sectionSystemCode: this.chooseSectionSystemCode ? this.chooseSectionSystemCode : this.sectionSystemCode,
          tenderSystemCode: this.tenderSystemCode}})
    },
    handleAdd () {
      this.$router.push({path: `/processManage/projectProcess/checkresult/add`,
        query: {sectionSystemCode: this.chooseSectionSystemCode ? this.chooseSectionSystemCode : this.sectionSystemCode,
          tenderSystemCode: this.tenderSystemCode}})
    },
    handleDetail (scope) {
      this.$router.push({path: `/processManage/projectProcess/checkresult/detail/${scope.row.objectId}`,
        query: {sectionSystemCode: this.chooseSectionSystemCode ? this.chooseSectionSystemCode : this.sectionSystemCode}})
    },
    // 编辑
    handleEditReport (scope) {
      this.$router.push({path: `/processManage/projectProcess/checkresult/update_report`, query: {objectId: scope.row.objectId}})
    },
    handleAddReport () {
      this.$router.push({path: `/processManage/projectProcess/checkresult/add_report`,
        query: {sectionSystemCode: this.chooseSectionSystemCode ? this.chooseSectionSystemCode : this.sectionSystemCode,
          tenderSystemCode: this.tenderSystemCode}})
    },
    handleDetailReport (scope) {
      this.$router.push({path: `/processManage/projectProcess/checkresult/report_detail/${scope.row.objectId}`})
    }, // 编辑
    handleEditResult (scope) {
      this.$router.push({path: `/processManage/projectProcess/checkresult/update_result`, query: {objectId: scope.row.objectId}})
    },
    handleAddResult () {
      this.$router.push({path: `/processManage/projectProcess/checkresult/add_result`,
        query: {sectionSystemCode: this.chooseSectionSystemCode ? this.chooseSectionSystemCode : this.sectionSystemCode,
          tenderSystemCode: this.tenderSystemCode}})
    },
    handleDetailResult (scope) {
      this.$router.push({path: `/processManage/projectProcess/checkresult/result_detail/${scope.row.objectId}`})
    },
    handleDel (scope, type) {
      this.$confirm('确认删除吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'error'
      }).then(() => {
        if (Object.is(type, 'result')) {
          resultInfo.deleteById(scope.row.objectId).then(res => {
            this.getResultInfoList()
          })
        } else if (Object.is(type, 'expert')) {
          expert.deleteByIdAndSectionSystemCode(scope.row.objectId,
            this.chooseSectionSystemCode ? this.chooseSectionSystemCode : this.sectionSystemCode).then(res => {
            this.getExpertList()
          })
        } else if (Object.is(type, 'report')) {
          documentInfo.deleteById(scope.row.objectId).then(res => {
            this.getExpertList()
          })
        }
      }).catch(() => {
        return false
      })
    },
    handleCurrentChange (nowNum) {
      this.currentPage = nowNum
      this.pageNo = (nowNum - 1) * this.pageSize
      this.getExpertList()
    },
    handleResultCurrentChange (nowNum) {
      this.resultCurrentPage = nowNum
      this.resultPageNo = (nowNum - 1) * this.resultPageSize
      this.getResultInfoList()
    },
    handleReportCurrentChange (nowNum) {
      this.reportCurrentPage = nowNum
      this.reportPageNo = (nowNum - 1) * this.reportPageSize
      this.getReportList()
    },
    getReportList () {
      let query = {
        pageNo: this.reportPageNo,
        pageSize: this.reportPageSize,
        tenderSystemCode: this.tenderSystemCode,
        sectionSystemCode: this.chooseSectionSystemCode ? this.chooseSectionSystemCode : this.sectionSystemCode,
        enterpriseId: this.$store.getters.authUser.enterpriseId,
        type: 7
      }
      documentInfo.getList(query).then(res => {
        let data = res.data.documentInfoList
        if (data) {
          this.reportData = data.list
          this.reportTotal = data.total
        }
      })
    },
    getResultInfoList () {
      let query = {
        pageNo: this.resultPageNo,
        pageSize: this.resultPageSize,
        tenderSystemCode: this.tenderSystemCode,
        sectionSystemCode: this.chooseSectionSystemCode ? this.chooseSectionSystemCode : this.sectionSystemCode,
        enterpriseId: this.$store.getters.authUser.enterpriseId,
        type: 4
      }
      resultInfo.getList(query).then(res => {
        let data = res.data.resultInfoList
        if (data) {
          this.resultInfoData = data.list
          this.resultTotal = data.total
        }
      })
    },
    getExpertList () {
      let query = {
        pageNo: this.pageNo,
        pageSize: this.pageSize,
        tenderSystemCode: this.tenderSystemCode,
        sectionSystemCode: this.chooseSectionSystemCode ? this.chooseSectionSystemCode : this.sectionSystemCode,
        enterpriseId: this.$store.getters.authUser.enterpriseId,
        type: 1
      }
      expert.getByRelaSection(query).then(res => {
        let data = res.data.expertList
        if (data) {
          this.total = data.total
          let handleData = data.list
          if (handleData.length > 0) {
            for (let i = 0; i < handleData.length; i++) {
              let expertArr = handleData[i].expertClassifyList
              if (expertArr) {
                handleData[i].expertclassify = this.transLabel(expertArr)
              }
            }
          }
          this.tableData = handleData
        }
      })
    },
    // 转成label值
    transLabel (expertArr) {
      let tempArr = []
      let str = ''
      for (let j = 0; j < expertArr.length; j++) {
        if (expertArr[j].firstClassify) {
          let first = this.getLabelName(expertClassify.expertClassify, expertArr[j].firstClassify)
          str += first.node.label + '/'
        }
        if (expertArr[j].secondClassify) {
          let second = this.getLabelName(expertClassify.expertClassify, expertArr[j].secondClassify)
          str += second.node.label + '/'
        }
        if (expertArr[j].thirdClassify) {
          let third = this.getLabelName(expertClassify.expertClassify, expertArr[j].thirdClassify)
          str += third.node.label + '/'
        }
        if (expertArr[j].fourClassify) {
          let fourth = this.getLabelName(expertClassify.expertClassify, expertArr[j].fourClassify)
          str += fourth.node.label
        }
        tempArr.push(str)
        str = ''
      }
      return tempArr
    },
    /*
    * treeData [原始数据集]
    * key 传入的key值
    */
    getLabelName (treeData, key) {
      let parentNode = null
      let node = null
      function getTreeDeepArr (treeData, key) {
        for (let i = 0; i < treeData.length; i++) {
          // 1.如果没有子节点 beark
          if (node && key) {
            break
          }
          // 定义需要操作的数据
          let obj = treeData[i]
          // 没有节点就下一个
          if (!obj || !obj.value) {
            continue
          }
          // 2.有节点就继续找，一直递归
          if (obj.value === key) {
            node = obj
            break
          } else {
            // 3.如果有子节点就继续找
            if (obj.children) {
              // 4.递归前，记录当前节点，作为parentNode
              parentNode = obj
              // 递归
              getTreeDeepArr(obj.children, key)
            } else {
              // 跳出当前递归
              continue
            }
          }
        }
        // 如果没有找到父节点，置为null
        if (!node) {
          parentNode = null
        }
        // 返回结果
        return {
          node: node,
          parentNode: parentNode
        }
      }
      return getTreeDeepArr(treeData, key)
    }
  },
  mounted () {
    this.getResultInfoList()
    this.getExpertList()
    this.getReportList()
  }
}
</script>
<style lang="less">
  #cloud_processtype{
  }
</style>
