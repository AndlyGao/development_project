<template>
  <div id="order-cert-page"  class="cont-view">
    <!-- 面包屑区域start -->
    <div class="bread-box">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>成交管理</el-breadcrumb-item>
        <el-breadcrumb-item>订单核销凭证打印</el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    <!-- 面包屑区域end -->
    <!-- 搜索区域start -->
    <div class="search-box">
      <el-row :gutter="20">
        <el-form :model="searchForm" label-width="110px" class="demo-ruleForm">
          <el-col :span="5">
            <el-form-item label="采购人:">
              <el-input v-model="searchForm.buyName" placeholder="采购人"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="5">
            <el-form-item label="电商平台名称:">
              <el-select v-model="searchForm.ecPlatform" clearable placeholder="请选择">
                <el-option
                  v-for="item in ecPlatformList"
                  :key="item.id"
                  :label="item.userName"
                  :value="item.id">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="5">
            <el-form-item label="打印状态:">
              <el-select v-model="searchForm.printStatus" clearable placeholder="请选择">
                <el-option
                  v-for="item in printStatusList"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="7">
            <el-form-item label="成交时间:">
              <el-date-picker
                @change="changeOrderTime"
                style="width:260px;"
                v-model="searchForm.orderTime"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                :default-time="['00:00:00', '23:59:59']"
                value-format="yyyy-MM-dd HH:mm:ss">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="2">
            <el-button type="primary" @click="onSearch">查询</el-button>
          </el-col>
        </el-form>
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
          prop="orderNum"
          label="订单编号"
          show-overflow-tooltip
          :formatter="simpleFormatData"
          align="center"
          width="220">
        </el-table-column>
        <el-table-column
          prop="ecPlatform"
          label="电商平台名称"
          show-overflow-tooltip
          :formatter="simpleFormatData"
          align="center">
        </el-table-column>
        <el-table-column
          prop="buyName"
          label="采购人"
          :formatter="simpleFormatData"
          align="center">
        </el-table-column>
        <!-- <el-table-column
          prop="name"
          label="商品编号"
          :formatter="simpleFormatData"
          align="center">
        </el-table-column> -->
        <el-table-column
          prop="name"
          label="商品名称"
          show-overflow-tooltip
          :formatter="simpleFormatData"
          align="center">
          <template slot-scope="scope">
            <span v-for="(item, index) in scope.row.articleNameList" :key="index">
              {{(scope.row.articleNameList.length > index + 1)? item+',' : item}}
            </span>
          </template>
        </el-table-column>
        <el-table-column
          prop="orderAmount"
          label="订单金额"
          :formatter="simpleFormatData"
          align="center">
        </el-table-column>
        <el-table-column
          prop="orderStatus"
          label="订单状态"
          :formatter="formateOrderStatus"
          align="center"
          width="80">
        </el-table-column>
        <el-table-column
          prop="dealTime"
          label="成交时间"
          show-overflow-tooltip
          :formatter="simpleFormatData"
          align="center"
          width="160">
        </el-table-column>
        <el-table-column
          prop="printStatus"
          label="打印状态"
          :formatter="formatePrintStatus"
          align="center"
          width="80">
        </el-table-column>
        <el-table-column
          prop="printNum"
          label="打印次数"
          :formatter="simpleFormatData"
          align="center"
          width="60">
        </el-table-column>
        <el-table-column
          prop="orderSumPrice"
          label="付款金额"
          :formatter="simpleFormatData"
          align="center">
        </el-table-column>
        <el-table-column label="操作" align="center" width="60">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="text"
              @click="handleTableBtn(scope.row, 'print')">
              打印
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
  </div>
</template>
<script>
import {transactionRecord, complain} from '@/api'
export default {
  name: 'order-cert',
  data () {
    return {
      searchForm: {},
      ecPlatformList: [], // 电商平台lsit
      printStatusList: [
        {value: 0, label: '未打印'},
        {value: 1, label: '已打印'}
      ],
      tableData: [],
      page: {
        total: 0,
        pageSize: 10,
        currentPage: 1,
        pageNo: 0
      },
      startTime: null,
      endTime: null
    }
  },
  methods: {
    /** 搜索 */
    onSearch () {
      this.getTableData()
    },
    changeOrderTime (time) {
      if (time) {
        this.startTime = time[0]
        this.endTime = time[1]
      } else {
        this.startTime = null
        this.endTime = null
      }
    },
    /** 获得电商平台名称 */
    merchantName () {
      complain.merchantName().then(res => {
        this.ecPlatformList = res.data.users
      })
    },
    /** 打印 */
    handleTableBtn (row, type) {
      this.$router.push({path: `/index/print-form/${row.id}`})
    },
    /** 获取列表数据 */
    getTableData () {
      let query = {
        pageNo: this.page.pageNo,
        pageSize: this.page.pageSize,
        messageLike: this.searchForm.buyName || null,
        ecPlatformId: this.searchForm.ecPlatform || null,
        printStatus: this.searchForm.printStatus,
        startTime: this.startTime || null,
        endTime: this.endTime || null,
        statuses: '4,5'
      }
      transactionRecord.getList(query).then(res => {
        if (res.data.transactionRecordList) {
          this.tableData = res.data.transactionRecordList.list
          this.page.total = res.data.transactionRecordList.total
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
    /** 分页 */
    handlePage (nowNum) {
      this.page.currentPage = nowNum
      this.page.pageNo = (nowNum - 1) * this.page.pageSize
      this.getTableData()
    },
    /** 订单状态 */
    formateOrderStatus (row, col, cellValue) {
      let obj = {
        1: '已接单',
        2: '已付款',
        3: '已发货',
        4: '已成交',
        5: '已评价'
      }
      return obj[cellValue]
    },
    /** 打印状态 */
    formatePrintStatus (row, col, cellValue) {
      let obj = {
        0: '未打印',
        1: '已打印'
      }
      return obj[cellValue]
    }
  },
  mounted () {
    this.getTableData()
    this.merchantName()
  }
}
</script>
<style lang="less">
#order-cert-page {
  .demo-ruleForm {
    margin-left: -50px;
  }
}
</style>
