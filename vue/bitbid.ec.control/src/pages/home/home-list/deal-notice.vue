<template>
  <div id="home-deal-notice-page" class="">
    <HomeHeader></HomeHeader>
    <div class="banner">
      <img src="../../../../static/images/home/banner.png" alt="">
    </div>
    <div class="deal-notice-warp">
      <p>当前位置：成交公示</p>
      <div class="search-box">
        <el-form :model="ruleForm" label-width="120px" class="demo-ruleForm">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="电商平台名称：" prop="name">
                <el-select v-model="ruleForm.ecPlatformId" placeholder="请选择" clearable style="width:460px;">
                  <el-option
                    v-for="item in ecPlatformList"
                    :key="item.id"
                    :label="item.userName"
                    :value="item.id">
                  </el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="商品名称：" prop="name">
                <el-input v-model="ruleForm.goodsName" clearable></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="成交时间：" prop="name">
                <el-date-picker
                  @change="changDealTime"
                  style="width:460px;"
                  v-model="ruleForm.dealTime"
                  type="daterange"
                  range-separator="至"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
                  :default-time="['00:00:00', '23:59:59']"
                  value-format="yyyy-MM-dd HH:mm:ss">
                </el-date-picker>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="24">
              <el-button type="danger" @click="searchClick" class="search-btn">搜 索</el-button>
            </el-col>
          </el-row>
        </el-form>
      </div>
    </div>
    <div class="table-box">
      <el-table
        class="table-cont"
        :data="tableData"
        height="530"
        @row-click="rowClick"
        border
        style="width: 100%">
        <el-table-column
          type="index"
          label="序号"
          align="center"
          :index="computedIndex"
          width="60">
        </el-table-column>
        <el-table-column
          prop="orderNum"
          align="center"
          label="订单编号"
          show-overflow-tooltip>
        </el-table-column>
        <el-table-column
          prop="ecPlatform"
          align="center"
          label="电商平台名称"
          show-overflow-tooltip>
        </el-table-column>
        <el-table-column
          prop="buyName"
          align="center"
          label="采购人"
          show-overflow-tooltip>
        </el-table-column>
        <el-table-column
          prop="articleName"
          align="center"
          label="商品名称"
          show-overflow-tooltip>
          <template slot-scope="scope">
            <span v-for="(item, index) in scope.row.articleNameList" :key="index">
              {{(scope.row.articleNameList.length>(index+1))? item + ', ': item}}
            </span>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="page.currentPage"
          :page-size="page.pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="page.total">
        </el-pagination>
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
  name: 'deal-notice',
  data () {
    return {
      ruleForm: {},
      ecPlatformList: [], // 电商平台名称
      tableData: [],
      page: {
        currentPage: 1,
        pageSize: 10,
        total: 0,
        pageNo: 0
      },
      StartTime: null,
      EndTime: null
    }
  },
  components: {
    HomeHeader,
    HomeFooter
  },
  methods: {
    changDealTime (time) {
      if (time && time.length) {
        this.StartTime = time[0] || null
        this.EndTime = time[1] || null
      } else {
        this.StartTime = null
        this.EndTime = null
      }
    },
    searchClick () {
      this.page.pageNo = 0
      this.page.currentPage = 1
      this.getDealTableData()
    },
    handleSizeChange (val) {
      this.page.pageSize = val
      this.getDealTableData()
    },
    handleCurrentChange (val) {
      this.page.pageNo = (val - 1) * this.page.pageSize
      this.page.currentPage = val
      this.getDealTableData()
    },
    /** 行点击 */
    rowClick (row, column, event) {
      this.$router.push({path: `/deal-notice-details/${row.id}`})
    },
    /** 序号计算 */
    computedIndex (index) {
      return index + (this.page.currentPage - 1) * this.page.pageSize + 1
    },
    /** 获取成交公示表格数据 */
    getDealTableData () {
      let query = {
        pageNo: this.page.pageNo,
        pageSize: this.page.pageSize,
        errorState: 0,
        publishStatus: 1,
        statuses: '4,5',
        goodsNameLike: this.ruleForm.goodsName,
        ecPlatformId: this.ruleForm.ecPlatformId,
        dealTimeBegin: this.StartTime,
        dealTimeEnd: this.EndTime
      }
      home.getList(query).then(res => {
        if (res.data && res.data.transactionRecordList) {
          this.tableData = res.data.transactionRecordList.list
          this.page.total = res.data.transactionRecordList.total
        }
      })
    },
    getEcnameList () {
      home.getListEcname().then(res => {
        this.ecPlatformList = res.data.users
      })
    }
  },
  mounted () {
    this.getDealTableData()
    this.getEcnameList()
  }
}
</script>
<style lang="less">
#home-deal-notice-page {
  background: #F2F6F9;
  padding-bottom: 1px;
  .deal-notice-warp p {
    text-align: left;
    width: 1200px;
    margin: 0 auto;
    padding-top: 12px;
    padding-bottom: 8px;
  }
  .search-box {
    width: 1200px;
    margin: 0 auto;
    background: #fff;
    padding: 15px 10px;
    box-sizing: border-box;
    .search-btn {
      background-color: #D42C32;
      border-color: #D42C32;
    }
  }
  .table-box {
    width: 1200px;
    margin: 0 auto;
    box-sizing: border-box;
    padding: 15px 20px;
    margin-top: 10px;
    margin-bottom: 40px;
    background: #fff;
    .pagination {
      padding-top: 10px;
    }
  }
  .el-table--enable-row-hover .el-table__body tr:hover>td{
    background-color: #F7E7E7 !important;
    cursor: pointer;
  }
}
</style>
