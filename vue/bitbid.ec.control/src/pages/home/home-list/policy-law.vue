<template>
  <div id="policy-law-page" class="policy-law">
    <HomeHeader></HomeHeader>
    <div class="banner">
      <img src="../../../../static/images/home/banner.png" alt="">
    </div>
    <div class="policy-law-warp">
      <p>当前位置：政策法规</p>
      <div class="search-box">
        <el-form :model="ruleForm" label-width="100px" class="demo-ruleForm">
          <el-row :gutter="20">
            <el-col :span=12>
              <el-form-item label="标题搜索：" prop="name">
                <el-input v-model="ruleForm.title"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span=2>
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
          :index="computedIndex"
          align="center"
          width="60">
        </el-table-column>
        <el-table-column
          prop="title"
          align="center"
          label="标题"
          show-overflow-tooltip>
        </el-table-column>
        <el-table-column
          prop="publishTime"
          align="center"
          label="发布时间"
          width="200"
          show-overflow-tooltip>
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
  name: 'policy-law',
  data () {
    return {
      ruleForm: {},
      tableData: [],
      page: {
        currentPage: 1,
        pageSize: 10,
        total: 0,
        pageNo: 0
      }
    }
  },
  components: {
    HomeHeader,
    HomeFooter
  },
  methods: {
    searchClick () {
      this.page.pageNo = 0
      this.page.currentPage = 1
      this.getLawsTableData()
    },
    handleSizeChange (val) {
      this.page.pageSize = val
      this.getLawsTableData()
    },
    handleCurrentChange (val) {
      this.page.pageNo = (val - 1) * this.page.pageSize
      this.page.currentPage = val
      this.getLawsTableData()
    },
    /** 行点击 */
    rowClick (row, column, event) {
      this.$router.push({path: `/policy-law-details/${row.id}`})
    },
    /** 序号计算 */
    computedIndex (index) {
      return index + (this.page.currentPage - 1) * this.page.pageSize + 1
    },
    getLawsTableData () {
      let query = {
        pageNo: this.page.pageNo,
        pageSize: this.page.pageSize,
        publishStatus: 1,
        titleLike: this.ruleForm.title
      }
      home.getListLaws(query).then(res => {
        if (res.data && res.data.newsPageInfo) {
          this.tableData = res.data.newsPageInfo.list
          this.page.total = res.data.newsPageInfo.total
        }
      })
    }
  },
  mounted () {
    this.getLawsTableData()
  }
}
</script>
<style lang="less">
#policy-law-page {
  background: #F2F6F9;
  padding-bottom: 1px;
  .policy-law-warp p {
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
    .el-form-item {
      margin-bottom: 0;
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
