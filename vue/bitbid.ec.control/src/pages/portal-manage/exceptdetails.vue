<template>
  <div id="expertdetails-page" class="cont-view">
    <!-- 面包屑区域start -->
    <div class="bread-box">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>门户信息管理</el-breadcrumb-item>
        <el-breadcrumb-item :to="{ path: '/index/except-notice' }">异常公示管理</el-breadcrumb-item>
        <el-breadcrumb-item>详情</el-breadcrumb-item>
      </el-breadcrumb>
      <el-button type="info" size="small" class="go-back" @click="$router.go(-1)">返 回</el-button>
    </div>
    <!-- 面包屑区域end -->
    <div class="admin-details-warp">
      <h4 class="order-title">订单详情</h4>
      <el-form :model="detailsForm" ref="ruleForm" label-width="100px" class="demo-ruleForm">
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="订单编号:">
              <span>{{detailsForm.orderNum}}</span>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="电商平台名称:">
              <span>{{detailsForm.ecPlatform}}</span>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="付款金额:">
              <span>{{detailsForm.orderSumPrice}}</span>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="订单状态:">
              <span>{{detailsForm.orderStatus | filteredOrderStatus}}</span>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="订单金额:">
              <span>{{detailsForm.orderAmount}}</span>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="接单时间:">
              <span>{{detailsForm.orderTime | filterTime}}</span>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="发货时间:">
              <span>{{detailsForm.sendTime | filterTime}}</span>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="验收时间:">
              <span>{{detailsForm.receptionTime | filterTime}}</span>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="成交时间:">
              <span>{{detailsForm.dealTime  | filterTime}}</span>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>
     <!-- 订单详情end -->
    <div class="table-box">
      <h4 class="order-title">商品详情</h4>
      <el-table
        :data="tableData"
        border
        style="width: 100%">
        <el-table-column type="expand">
          <template slot-scope="props">
            <el-table
              :data="props.row.evaluationList"
              border
              style="width: 100%">
              <el-table-column
                label="序号"
                type="index"
                width="60"
                align="center">
              </el-table-column>
              <el-table-column
                prop="content"
                label="评价详情"
                :formatter="simpleFormatData"
                show-overflow-tooltip
                align="center">
              </el-table-column>
              <el-table-column
                prop="evaluationTime"
                label="评价时间"
                :formatter="formatDate"
                width="220"
                align="center">
              </el-table-column>
            </el-table>
          </template>
        </el-table-column>
        <el-table-column
          prop="articleNum"
          label="商品编号"
          :formatter="simpleFormatData"
          show-overflow-tooltip
          align="center"
          width="220">
        </el-table-column>
        <el-table-column
          prop="articleName"
          label="商品名称"
          :formatter="simpleFormatData"
          show-overflow-tooltip
          align="center"
          width="240">
        </el-table-column>
        <el-table-column
          prop="brand"
          label="品牌"
          :formatter="simpleFormatData"
          show-overflow-tooltip
          align="center"
          width="100">
        </el-table-column>
        <el-table-column
          prop="modelNum"
          label="型号"
          :formatter="simpleFormatData"
          show-overflow-tooltip
          align="center"
          width="220">
        </el-table-column>
        <el-table-column
          prop="unitPrice"
          label="单价"
          :formatter="simpleFormatData"
          align="center"
          show-overflow-tooltip
          width="100">
        </el-table-column>
        <el-table-column
          prop="orderAmount"
          label="数量"
          :formatter="simpleFormatData"
          align="center"
          show-overflow-tooltip
          width="100">
        </el-table-column>
        <el-table-column
          prop="articleUrl"
          label="商品链接"
          :formatter="simpleFormatData"
          show-overflow-tooltip
          align="center">
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>
<script>
import {dateFormat} from '@/assets/js/common'
import {transactionRecord} from '@/api'
export default {
  name: 'expertdetails',
  data () {
    return {
      detailsForm: {},
      tableData: [],
      id: this.$route.params.objectId
    }
  },
  filters: {
    /** 格式化订单状态 */
    filteredOrderStatus (orderStatus) {
      if (orderStatus === 1) {
        return '已接单'
      } else if (orderStatus === 2) {
        return '已发货'
      } else if (orderStatus === 3) {
        return '已验收'
      } else if (orderStatus === 4) {
        return '已成交'
      } else if (orderStatus === 5) {
        return '已评价'
      }
    },
    filterTime (val) {
      if (val) {
        return dateFormat(val, 'yyyy-MM-dd hh:mm:ss')
      } else {
        return '-----'
      }
    }
  },
  methods: {
    /** 格式化时间 */
    formatDate (row, col, cellValue) {
      return cellValue ? dateFormat(cellValue, 'yyyy-MM-dd hh:mm:ss') : '-----'
    },
    /** 普通格式化数据，空的时候展示"---" */
    simpleFormatData (row, col, cellValue) {
      return cellValue || '-----  '
    },
    getQueryOne () {
      transactionRecord.getOne(this.id).then(res => {
        if (res.data.transactionRecord) {
          this.detailsForm = res.data.transactionRecord
          this.tableData = res.data.transactionRecord.articleList
        }
      })
    }
  },
  mounted () {
    this.getQueryOne()
  }
}
</script>
<style lang="less">
#expertdetails-page {
  .demo-ruleForm {
    .el-form-item {
      margin-bottom: 0;
    }
    .el-form-item__content {
      text-align: left;
    }
  }
  .order-title {
    text-align: left;
  }
}
</style>
