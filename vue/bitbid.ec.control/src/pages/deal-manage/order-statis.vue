<template>
  <div id="order-statis-page"  class="cont-view">
    <!-- 面包屑区域start -->
    <div class="bread-box">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>成交管理</el-breadcrumb-item>
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
              <el-select v-model="searchForm.ecPlatformId" clearable placeholder="请选择">
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
                @change = "datePicker"
                style="width:260px;"
                v-model="searchForm.time"
                type="daterange"
                :default-time="['00:00:00', '23:59:59']"
                value-format="yyyy-MM-dd HH:mm:ss"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="2">
            <el-button type="primary" @click="onSearch(searchForm)">查询</el-button>
          </el-col>
        </el-form>
      </el-row>
    </div>
    <!-- 搜索区域end -->
    <div class="table-box">
      <div class="expot-btn">
        <el-button size="small" type="primary" @click="exportExcel">导出 Excel</el-button>
      </div>
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
          prop="ecPlatform"
          label="电商平台名称"
          :formatter="simpleFormatData"
          align="center">
        </el-table-column>
        <el-table-column
          prop="countNumber"
          label="成交订单数"
          align="center">
          <template slot-scope="scope">
            <el-button
              @click.native.prevent="jumpDetailsListPage(scope.$index, scope.row)"
              type="text"
              size="small">
              {{scope.row.countNumber}}
            </el-button>
          </template>
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
import {exportCountExcel, dateFormat} from '@/assets/js/common'
export default {
  name: 'order-statis',
  data () {
    return {
      searchForm: {},
      ecPlatformList: [], // 电商平台lsit
      tableData: [],
      page: {
        total: 0,
        pageSize: 10,
        currentPage: 1
      },
      startCountTime: null,
      endCountTime: null
    }
  },
  methods: {
    datePicker (value) {
      if (value) {
        this.startCountTime = value[0]
        this.endCountTime = value[1]
      } else {
        this.startCountTime = null
        this.endCountTime = null
      }
    },
    /** 搜索 */
    onSearch (from) {
      this.getCountList(from)
    },
    /** 格式化 */
    // 时间
    formatDate (row, col, cellValue) {
      return cellValue ? dateFormat(cellValue, 'yyyy-MM-dd hh:mm:ss') : '-----'
    },
    /** 导出excel */
    exportExcel () {
      if (this.tableData.length > 0) {
        let id = this.searchForm.ecPlatformId
        if (id !== null && id !== undefined) {
          window.open(`${exportCountExcel}?electricityId=${id}`)
        } else if (this.startCountTime !== null && this.endCountTime !== null) {
          let STime = this.formatDate(null, null, this.startCountTime)
          let ETime = this.formatDate(null, null, this.endCountTime)
          window.open(`${exportCountExcel}?startCountTime=${STime}&endCountTime=${ETime}`)
        } else {
          window.open(`${exportCountExcel}`)
        }
      } else {
        this.$message.warning('无订单信息,不可导出！')
      }
    },
    /** 跳转到详情列表页 */
    jumpDetailsListPage (index, row) {
      if (row.countNumber !== 0) {
        this.$router.push({path: '/index/order-statis-list', query: {ecPlatformId: row.electricityId}})
      } else {
        this.$message.warning('无订单信息！')
      }
    },
    /** 普通格式化数据，空的时候展示"---" */
    simpleFormatData (row, col, cellValue) {
      return cellValue || '----- '
    },
    /** 序号计算 */
    computedIndex (index) {
      return index + (this.page.currentPage - 1) * this.page.pageSize + 1
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
    },
    /**
     *  平台统计列表查询
     */
    getCountList () {
      let obj = {
        electricityId: this.searchForm.ecPlatformId || null,
        startCountTime: this.startCountTime || null,
        endCountTime: this.endCountTime || null
      }
      transactionRecord.getCountList(obj).then(res => {
        if (res.data.transactionRecordList) {
          this.tableData = res.data.transactionRecordList
        }
      })
    }
  },
  mounted () {
    this.getCountList()
    this.getComplainList()
  }
}
</script>
<style lang="less">
#order-statis-page {
  .expot-btn {
    padding-bottom: 10px;
    text-align: right;
  }
}
</style>
