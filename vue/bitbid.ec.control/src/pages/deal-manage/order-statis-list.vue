<template>
  <div id="order-statis-list-page"  class="cont-view">
    <!-- 面包屑区域start -->
    <div class="bread-box">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>成交管理></el-breadcrumb-item>
        <el-breadcrumb-item :to="{ path: '/index/order-statis' }">平台订单统计</el-breadcrumb-item>
        <el-breadcrumb-item>平台订单统计列表</el-breadcrumb-item>
      </el-breadcrumb>
      <el-button type="info" size="small" class="go-back" @click="$router.go(-1)">返 回</el-button>
    </div>
    <!-- 面包屑区域end -->
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
          :formatter="simpleFormatData"
          align="center"
          show-overflow-tooltip
          width="220">
        </el-table-column>
        <el-table-column
          prop="ecPlatform"
          label="电商平台名称"
          :formatter="simpleFormatData"
          align="center"
          show-overflow-tooltip>
        </el-table-column>
        <el-table-column
          prop="articleNameList"
          label="商品名称"
          show-overflow-tooltip
          :formatter="simpleFormatData"
          align="center">
          <template slot-scope="scope">
            <span v-for="(item, index) in scope.row.articleNameList" :key="index">{{scope.row.articleNameList.length> (index+1) ? item + ', ': item}}</span>
          </template>
        </el-table-column>
        <el-table-column
          prop="orderAmount"
          label="订单金额"
          align="center">
        </el-table-column>
        <el-table-column
          prop="orderStatus"
          label="订单状态"
          :formatter="filterType"
          align="center"
          width="80">
        </el-table-column>
        <el-table-column
          prop="dealTime"
          label="成交时间"
          :formatter="formatDate"
          align="center"
          show-overflow-tooltip
          width="160">
        </el-table-column>
        <el-table-column
          prop="orderSumPrice"
          label="付款金额"
          :formatter="simpleFormatData"
          align="center">
        </el-table-column>
        <el-table-column label="操作" align="center"
                         width="60">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="text"
              @click="handleTableBtn(scope.row, 'details')">
              详情
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
import {dateFormat} from '@/assets/js/common'
import {transactionRecord} from '@/api'
export default {
  name: 'order-statis-list',
  data () {
    return {
      tableData: [],
      page: {
        total: 0,
        pageSize: 10,
        currentPage: 1,
        pageNo: 0
      }
    }
  },
  methods: {
    /** 格式化 */
    // 时间
    formatDate (row, col, cellValue) {
      return cellValue ? dateFormat(cellValue, 'yyyy-MM-dd hh:mm:ss') : '-----'
    },
    // 订单状态
    filterType (row, column) {
      let status = row.orderStatus
      if (status === 1) {
        return '已接单'
      } else if (status === 2) {
        return '已发货'
      } else if (status === 3) {
        return '已验收'
      } else if (status === 4) {
        return '已成交'
      } else if (status === 5) {
        return '已评价'
      }
    },
    /** 跳转到详情列表页 */
    handleTableBtn (row, type) {
      this.$router.push({path: '/index/order-statis-details', query: {id: row.id}})
    },
    /** 普通格式化数据，空的时候展示"---" */
    simpleFormatData (row, col, cellValue) {
      return cellValue || '----- '
    },
    /** 序号计算 */
    computedIndex (index) {
      return index + (this.page.currentPage - 1) * this.page.pageSize + 1
    },
    /** 分页 */
    handlePage (nowNum) {
      this.page.currentPage = nowNum
      this.page.pageNo = (nowNum - 1) * this.page.pageSize
      this.getList()
    },
    /**
     * 列表查询
     */
    getList () {
      let query = {
        pageNo: this.page.pageNo,
        pageSize: this.page.pageSize,
        ecPlatformId: this.$route.query.ecPlatformId,
        statuses: '4,5'
      }
      transactionRecord.getList(query).then(res => {
        if (res.data && res.data.transactionRecordList) {
          this.tableData = res.data.transactionRecordList.list
          this.page.total = res.data.transactionRecordList.total
        }
      })
    }
  },
  mounted () {
    this.getList()
  }
}
</script>
<style lang="less">
#order-statis-list {
}
</style>
