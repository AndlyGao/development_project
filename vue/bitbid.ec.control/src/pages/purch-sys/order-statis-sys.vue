<template>
  <div id="order-statis-sys-page"  class="cont-view">
    <!-- 面包屑区域start -->
    <div class="bread-box">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>采购管理系统</el-breadcrumb-item>
        <el-breadcrumb-item>平台订单统计</el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    <!-- 面包屑区域end -->
    <!-- 搜索区域start -->
    <div class="search-box">
      <el-row :gutter="20">
        <el-form :model="searchForm" label-width="110px" class="demo-ruleForm">
          <el-col :span="5">
            <el-form-item label="电商平台名称:">
              <el-select v-model="searchForm.electricityId" clearable placeholder="请选择">
                <el-option
                  v-for="item in ecPlatformList"
                  :key="item.id"
                  :label="item.userName"
                  :value="item.id">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="7">
            <el-form-item label="统计时间:">
              <el-date-picker
                style="width:260px;"
                v-model="searchForm.time"
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
          align="center">
        </el-table-column>
        <el-table-column
          prop="ecPlatform"
          label="电商平台名称"
          :formatter="simpleFormatData"
          align="center">
        </el-table-column>
        <el-table-column
          prop="countNumber"
          label="成交订单数"
          align="center">
        </el-table-column>
        <el-table-column
          prop="countMoney"
          label="成交订单金额"
          :formatter="simpleFormatData"
          align="center">
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>
<script>
import {complain, transactionRecord} from '@/api'
export default {
  name: 'order-statis-sys',
  data () {
    return {
      searchForm: {},
      ecPlatformList: [],
      tableData: []
    }
  },
  methods: {
    /** 搜索 */
    onSearch () {
      this.getTableData()
    },
    /** 普通格式化数据，空的时候展示"---" */
    simpleFormatData (row, col, cellValue) {
      return cellValue || '-----'
    },
    getTableData () {
      this.searchForm.buyer = this.$store.getters.authUser.userId
      if (this.searchForm.time && this.searchForm.time.length > 1) {
        this.searchForm.startCountTime = this.searchForm.time[0]
        this.searchForm.endCountTime = this.searchForm.time[1]
      } else {
        delete this.searchForm.startCountTime
        delete this.searchForm.endCountTime
      }
      let queryModel = Object.assign({}, this.searchForm)
      delete queryModel.time
      transactionRecord.getCountListByBuyer(queryModel).then(res => {
        this.tableData = res.data.transactionRecordList
      })
    },
    /**
     *电商平台名称列表
     * */
    getComplainList () {
      complain.merchantName().then(res => {
        if (res.data.users) {
          this.ecPlatformList = res.data.users
        }
      })
    }
  },
  mounted () {
    this.getComplainList()
    this.getTableData()
  }
}
</script>
<style lang="less">
#order-statis-sys-page {
  .expot-btn {
    padding-bottom: 10px;
    text-align: right;
  }
}
</style>
