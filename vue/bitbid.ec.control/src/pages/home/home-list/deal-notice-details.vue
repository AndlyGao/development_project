<template>
  <div id="deal-notice-page" class="deal-notice">
    <HomeHeader></HomeHeader>
    <div class="banner">
      <img src="../../../../static/images/home/banner.png" alt="">
    </div>
    <div class="deat-details-warp">
      <p>当前位置：成交公示 > 详情页</p>
      <div class="table-box">
        <table class="table-form">
          <tr>
            <td class="table-ftd">采购人</td>
            <td colspan="3" class="table-rtd">{{tableForm.buyName}}</td>
          </tr>
          <tr>
            <td class="table-ftd">电商平台名称</td>
            <td class="table-rtd">{{tableForm.ecPlatform}}</td>
            <td class="table-ftd">订单编号</td>
            <td class="table-rtd">{{tableForm.orderNum}}</td>
          </tr>
          <tr>
            <td class="table-ftd">订单金额</td>
            <td class="table-rtd">{{tableForm.orderAmount}}</td>
            <td class="table-ftd">成交时间</td>
            <td class="table-rtd">{{tableForm.dealTime}}</td>
          </tr>
        </table>
        <el-table
          :data="tableData"
          border
          style="width: 100%">
          <el-table-column
            prop="articleName"
            label="商品名称"
            align="center">
          </el-table-column>
          <el-table-column
            prop="brand"
            label="品牌"
            align="center">
          </el-table-column>
          <el-table-column
            prop="modelNum"
            label="型号"
            align="center">
          </el-table-column>
          <el-table-column
            prop="unitPrice"
            label="单价"
            align="center">
          </el-table-column>
          <el-table-column
            prop="orderAmount"
            label="数量"
            align="center">
          </el-table-column>
        </el-table>
      </div>
    </div>
    <HomeFooter></HomeFooter>
  </div>
</template>
<script>
import HomeHeader from '../header/header'
import HomeFooter from '../footer/footer'
import {home} from '@/api'
export default {
  name: '',
  data () {
    return {
      tableForm: {},
      tableData: [],
      objectId: this.$route.params.objectId
    }
  },
  components: {
    HomeHeader,
    HomeFooter
  },
  methods: {
    getQueryOne () {
      home.queryOne(this.objectId).then(res => {
        this.tableForm = res.data.transactionRecord
        this.tableData = res.data.transactionRecord.articleList
        // console.log(res)
      })
    }
  },
  mounted () {
    this.getQueryOne()
  }
}
</script>
<style lang="less">
#deal-notice-page {
  background: #F2F6F9;
  padding-bottom: 1px;
  .deat-details-warp p {
    width: 1200px;
    text-align: left;
    margin: 0 auto;
    padding-top: 12px;
    padding-bottom: 8px;
  }
  .table-box {
    background: #fff;
    width: 1200px;
    margin: 0 auto;
    box-sizing: border-box;
    padding: 15px 20px;
    margin-top: 10px;
    margin-bottom: 40px;
    .table-form {
      width: 100%;
      border: 1px solid #EBEEF5;
      border-bottom: none;
      border-collapse: collapse;
    }
    .table-form tr, .table-form td {
      border: 1px solid #EBEEF5;
      height: 40px;
      line-height: 40px;
      padding: 4px 8px;
    }
    .table-ftd {
      background: #f9f9f9;
      width: 200px;
      font-weight: 800;
      color: #909399;
    }
    .table-rtd {
      text-align: left;
    }
  }
}
</style>
