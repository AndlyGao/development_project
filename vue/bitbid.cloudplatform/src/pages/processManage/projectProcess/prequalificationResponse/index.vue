<template>
  <div class="cloudcontent" id="cloud_processtype">
    <div class="box">
      <div class="topmain">
        投标报名受理信息
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
          prop="bidderName"
          label="投标人"
          width="180">
        </el-table-column>
        <el-table-column
          prop="contactName"
          label="联系人"
          width="180">
        </el-table-column>
        <el-table-column
          prop="contactNumber"
          label="联系方式"
          width="180">
        </el-table-column>
        <el-table-column
          prop="contactEmail"
          label="电子邮箱"
          width="180">
        </el-table-column>
        <el-table-column
          prop="signUpTime"
          label="报名时间"
          width="180">
        </el-table-column>
        <el-table-column
          prop="auditStatus"
          label="提交状态"
          width="80">
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
          label="操作" align="center" width="140">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="handleDetail(scope)">查看</el-button>
            <el-button type="text" size="small" v-if="scope.row.auditStatus === 0 || scope.row.auditStatus === 3" @click="handleEdit(scope)">编辑</el-button>
            <el-button type="text" size="small" v-if="scope.row.auditStatus === 0 || scope.row.auditStatus === 3" @click="handleDel(scope, 'bond')">删除</el-button>
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
        资格预审文件费用
        <!--按钮-->
        <el-button class="addbutton" @click="handleAddCost" v-if="operationFlag">
          <span> + 添加</span>
        </el-button>
        <!--按钮-->
      </div>
      <el-table
        :data="costInfoData"
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
          prop="amount"
          label="缴费金额(元)"
          width="180">
        </el-table-column>
        <el-table-column
          prop="paymentMethod"
          label="缴费方式"
          width="180">
          <template slot-scope="scope">
            <span  v-if="scope.row.paymentMethod === 1">支付宝</span>
            <span  v-if="scope.row.paymentMethod === 2">微信</span>
            <span  v-if="scope.row.paymentMethod === 3">金额</span>
            <span  v-if="scope.row.paymentMethod === 4">银联</span>
            <span  v-if="scope.row.paymentMethod === 5">支票</span>
            <span  v-if="scope.row.paymentMethod === 6">保函</span>
            <span  v-if="scope.row.paymentMethod === 9">其他</span>
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
          prop="paymentTime"
          label="缴费时间">
        </el-table-column>
        <el-table-column
          label="操作" align="center" width="260">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="handleDetailCost(scope)">查看</el-button>
            <el-button type="text" size="small" v-if="scope.row.auditStatus === 0 || scope.row.auditStatus === 3" @click="handleEditCost(scope)">编辑</el-button>
            <el-button type="text" size="small" v-if="scope.row.auditStatus === 0 || scope.row.auditStatus === 3" @click="handleDel(scope, 'cost')">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <!--分页-->
      <el-pagination
        background
        layout="prev, pager, next"
        :total="costTotal"
        :page-size='costPageSize'
        :current-page.sync="currentPage"
        @current-change="handleCostCurrentChange">
      </el-pagination>
      <!--分页-->
    </div>
    <div class="box">
      <div class="topmain">
        递交资格预审申请文件
        <!--按钮-->
        <el-button class="addbutton" @click="handleAddFile" v-if="operationFlag">
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
          prop="fileName"
          label="资格预审申请文件名称"
          width="180">
          <template slot-scope="scope">
            <p v-for="(item, index) in scope.row.fileInformationList" :key="index">{{item.fileName}}</p>
          </template>
        </el-table-column>
        <el-table-column
          prop="bidderName"
          label="投标人"
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
            <el-button type="text" size="small" @click="handleDetailFile(scope)">查看</el-button>
            <el-button type="text" size="small" v-if="scope.row.auditStatus === 0 || scope.row.auditStatus === 3" @click="handleEditFile(scope)">编辑</el-button>
            <el-button type="text" size="small" v-if="scope.row.auditStatus === 0 || scope.row.auditStatus === 3" @click="handleDel(scope, 'file')">删除</el-button>
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
import {costInfo, signUpInfo, resultInfo} from '@/api/cloudplatform'
export default {
  components: {
  },
  props: ['tenderSystemCode', 'sectionSystemCode', 'chooseSectionSystemCode', 'operationFlag'],
  data () {
    return {
      tableData: [],
      costInfoData: [],
      resultInfoData: [],
      // 当前页
      currentPage: 1,
      pageNo: 0,
      total: null, // 总条数
      pageSize: 10, // 每页展示条数
      costCurrentPage: 1,
      costPageNo: 0,
      costTotal: null, // 总条数
      costPageSize: 10, // 每页展示条数
      resultCurrentPage: 1,
      resultPageNo: 0,
      resultTotal: null, // 总条数
      resultPageSize: 10 // 每页展示条数
    }
  },
  watch: {
    $route: 'resetView'
  },
  methods: {
    resetView () {
      this.getCostInfoList()
      this.getSignUpInfoList()
      this.getResultInfoList()
    },
    // 编辑
    handleEdit (scope) {
      this.$router.push({path: `/processManage/projectProcess/prequalification_response/update`, query: {objectId: scope.row.objectId}})
    },
    handleAdd () {
      this.$router.push({path: `/processManage/projectProcess/prequalification_response/add`,
        query: {sectionSystemCode: this.chooseSectionSystemCode ? this.chooseSectionSystemCode : this.sectionSystemCode,
          tenderSystemCode: this.tenderSystemCode}})
    },
    handleDetail (scope) {
      this.$router.push({path: `/processManage/projectProcess/prequalification_response/detail/${scope.row.objectId}`})
    },
    // 编辑
    handleEditCost (scope) {
      this.$router.push({path: `/processManage/projectProcess/prequalification_response/update_cost`, query: {objectId: scope.row.objectId}})
    },
    handleAddCost () {
      this.$router.push({path: `/processManage/projectProcess/prequalification_response/add_cost`,
        query: {sectionSystemCode: this.chooseSectionSystemCode ? this.chooseSectionSystemCode : this.sectionSystemCode,
          tenderSystemCode: this.tenderSystemCode}})
    },
    handleDetailCost (scope) {
      this.$router.push({path: `/processManage/projectProcess/prequalification_response/cost_detail/${scope.row.objectId}`})
    }, // 编辑
    handleEditFile (scope) {
      this.$router.push({path: `/processManage/projectProcess/prequalification_response/update_file`, query: {objectId: scope.row.objectId}})
    },
    handleAddFile () {
      this.$router.push({path: `/processManage/projectProcess/prequalification_response/add_file`,
        query: {sectionSystemCode: this.chooseSectionSystemCode ? this.chooseSectionSystemCode : this.sectionSystemCode,
          tenderSystemCode: this.tenderSystemCode}})
    },
    handleDetailFile (scope) {
      this.$router.push({path: `/processManage/projectProcess/prequalification_response/file_detail/${scope.row.objectId}`})
    },
    handleDel (scope, type) {
      this.$confirm('确认删除吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'error'
      }).then(() => {
        if (Object.is(type, 'cost')) {
          costInfo.deleteById(scope.row.objectId).then(res => {
            this.getCostInfoList()
          })
        } else if (Object.is(type, 'bond')) {
          signUpInfo.deleteById(scope.row.objectId).then(res => {
            this.getSignUpInfoList()
          })
        } else if (Object.is(type, 'file')) {
          resultInfo.deleteById(scope.row.objectId).then(res => {
            this.getResultInfoList()
          })
        }
      }).catch(() => {
        return false
      })
    },
    handleCurrentChange (nowNum) {
      this.currentPage = nowNum
      this.pageNo = (nowNum - 1) * this.pageSize
      this.getSignUpInfoList()
    },
    handleCostCurrentChange (nowNum) {
      this.costCurrentPage = nowNum
      this.costPageNo = (nowNum - 1) * this.costPageSize
      this.getCostInfoList()
    },
    handleResultCurrentChange (nowNum) {
      this.resultCurrentPage = nowNum
      this.resultPageNo = (nowNum - 1) * this.resultPageSize
      this.getResultInfoList()
    },
    getCostInfoList () {
      let query = {
        pageNo: this.costPageNo,
        pageSize: this.costPageSize,
        tenderSystemCode: this.tenderSystemCode,
        sectionSystemCode: this.chooseSectionSystemCode ? this.chooseSectionSystemCode : this.sectionSystemCode,
        enterpriseId: this.$store.getters.authUser.enterpriseId,
        type: 1
      }
      costInfo.getList(query).then(res => {
        let data = res.data.costInfoList
        if (data) {
          this.costInfoData = data.list
          this.costTotal = data.total
        }
      })
    },
    getSignUpInfoList () {
      let query = {
        pageNo: this.pageNo,
        pageSize: this.pageSize,
        tenderSystemCode: this.tenderSystemCode,
        sectionSystemCode: this.chooseSectionSystemCode ? this.chooseSectionSystemCode : this.sectionSystemCode,
        enterpriseId: this.$store.getters.authUser.enterpriseId
      }
      signUpInfo.getList(query).then(res => {
        let data = res.data.signUpInfoList
        if (data) {
          this.tableData = data.list
          this.total = data.total
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
        type: 1
      }
      resultInfo.getList(query).then(res => {
        let data = res.data.resultInfoList
        if (data) {
          this.resultInfoData = data.list
          this.resultTotal = data.total
        }
      })
    }
  },
  mounted () {
    this.getCostInfoList()
    this.getSignUpInfoList()
    this.getResultInfoList()
  }
}
</script>
<style lang="less">
  #cloud_processtype{
  }
</style>
