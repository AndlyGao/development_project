<template>
  <div id="ec-details-page"  class="cont-view">
    <!-- 面包屑区域start -->
    <div class="bread-box">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>电商管理系统</el-breadcrumb-item>
        <el-breadcrumb-item :to="{ path: '/index/ec-trade-record' }">交易记录管理</el-breadcrumb-item>
        <el-breadcrumb-item>详情</el-breadcrumb-item>
      </el-breadcrumb>
      <el-button type="info" size="small" class="go-back" @click="$router.go(-1)">返 回</el-button>
    </div>
    <!-- 面包屑区域end -->
    <!-- 订单详情start -->
    <div class="order-warp">
      <h4 class="order-title">订单详情</h4>
      <el-form :model="orderDetailsForm" ref="ruleForm" label-width="100px" class="demo-ruleForm">
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="订单编号:">
              <span>{{orderDetailsForm.orderNum}}</span>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="采购人:">
              <span>{{orderDetailsForm.buyName}}</span>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="订单状态:">
              <span>{{orderDetailsForm.orderStatus | filteredOrderStatus}}</span>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="订单金额:">
              <span>{{orderDetailsForm.orderAmount}}</span>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="接单时间:">
              <span>{{orderDetailsForm.orderTime | filterTime}}</span>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="发货时间:">
              <span>{{orderDetailsForm.sendTime | filterTime}}</span>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="验收时间:">
              <span>{{orderDetailsForm.receptionTime | filterTime}}</span>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="成交时间:">
              <span>{{orderDetailsForm.dealTime  | filterTime}}</span>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="付款金额:">
              <span>{{orderDetailsForm.orderSumPrice}}</span>
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
                align="center">
              </el-table-column>
              <el-table-column
                prop="evaluationTime"
                label="评价时间"
                :formatter="formatDate"
                align="center">
              </el-table-column>
            </el-table>
          </template>
        </el-table-column>
        <el-table-column
          prop="articleNum"
          label="商品编号"
          :formatter="simpleFormatData"
          align="center">
        </el-table-column>
        <el-table-column
          prop="articleName"
          label="商品名称"
          :formatter="simpleFormatData"
          align="center">
        </el-table-column>
        <el-table-column
          prop="brand"
          label="品牌"
          :formatter="simpleFormatData"
          align="center">
        </el-table-column>
        <el-table-column
          prop="modelNum"
          label="型号"
          :formatter="simpleFormatData"
          align="center">
        </el-table-column>
        <el-table-column
          prop="unitPrice"
          label="单价"
          :formatter="simpleFormatData"
          align="center">
        </el-table-column>
        <el-table-column
          prop="orderAmount"
          label="数量"
          :formatter="simpleFormatData"
          align="center">
        </el-table-column>
        <el-table-column
          prop="articleUrl"
          label="商品链接"
          :formatter="simpleFormatData"
          align="center">
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>
<script>
import {transactionRecord} from '@/api'
import {dateFormat} from '@/assets/js/common'
export default {
  name: 'trade-details',
  data () {
    return {
      orderDetailsForm: {}, // 订单详情-表单
      tableData: [], // 商品列表
      id: this.$route.query.id
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
      return cellValue || '-----'
    },
    /**
     * 列表查询
     */
    getOneList () {
      transactionRecord.getOne(this.id).then(res => {
        if (res.data.transactionRecord) {
          this.orderDetailsForm = res.data.transactionRecord
          this.tableData = res.data.transactionRecord.articleList
        }
      })
    }
  },
  mounted () {
    this.getOneList()
  }
}
</script>
<style lang="less">
#ec-details-page {
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
.el-table__body {
  width: 100% !important;
}
.el-table__header {
  width: 100% !important;
}
</style>
