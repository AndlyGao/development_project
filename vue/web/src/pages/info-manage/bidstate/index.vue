<template>
  <div id="ad-manage">
    <div class="headertitle">
      <!--面包屑-->
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/main' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>信息管理</el-breadcrumb-item>
        <el-breadcrumb-item>招标声明</el-breadcrumb-item>
      </el-breadcrumb>
      <!--面包屑-->
    </div>
    <div class="contentbigbox">
     <!--搜索 & 添加按钮-->
        <el-row>
          <el-button type="success"  class="addbutton"  @click="addbutton"> + 添加招标声明</el-button>
          <div class="seacher_box">
            <span></span>
            <el-select v-model="searchValue" class="select" @change="selectSearch">
              <el-option
                v-for="item in options"
                :key="item.value"
                :label="item.label"
                :value="item.value">
              </el-option>
            </el-select>
            <el-input class="search" style="vertical-align: top"  :placeholder="'请输入' + searchName" v-model="search_input"></el-input>
            <el-button  slot="append" icon="el-icon-search" type="" @click="search"></el-button>
          </div>
        </el-row>
        <!--搜索 & 添加按钮-->
      <el-table
        :data="tableData"
        border
        header-cell-class-name="tabletitles">
        <el-table-column
          type="index"
          width="50"
          label="序号"
          :index='dataIndex'
          align="center">
        </el-table-column>
        <el-table-column
          prop="title"
          label="公告名称"
          align="left"
          show-overflow-tooltip>
        </el-table-column>
        <el-table-column
          prop="tenderCode"
          label="招标编号"
          align="left"
          show-overflow-tooltip>
        </el-table-column>
        <el-table-column
          prop="lastUpdateDate"
          label="修改时间"
          align="left"
          show-overflow-tooltip>
        </el-table-column>
        <el-table-column
          prop="clicksCount"
          label="点击量"
          align="left"
          show-overflow-tooltip>
        </el-table-column>
        <el-table-column
          fixed="right"
          label="操作"
          width="240"
          align="center">
          <template slot-scope="scope">
            <div class="statusbigbox">
              <el-button type="text" size="small" @click="handleTableBtn(scope.row, 'look')">
                查看
              </el-button>
              <el-button type="text" size="small" @click="handleTableBtn(scope.row, 'edit')">
                修改
              </el-button>
              <el-button type="text" size="small" @click="handleTableBtn(scope.row, 'del')">
                删除
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        class="pagebox"
        @current-change="handleCurrentChange"
        :current-page.sync="currentPage"
        :total="total"
        :page-size='pageSize'
        layout="prev, pager, next, jumper">
      </el-pagination>
    </div>
  </div>
</template>
<script>
import {infoManageBidState} from '@/api'
export default {
  data () {
    return {
      search_input: '',
      searchValue: 1,
      searchName: '公告名称',
      dataIndex: 1,
      options: [
        {
          label: '公告名称',
          value: 1
        },
        {
          label: '招标编号',
          value: 2
        }
      ],
      // 当前页
      currentPage: 1,
      pageNo: 0,
      total: 0, // 总条数
      pageSize: 10, // 每页展示条数
      tableData: []
    }
  },
  methods: {
    list () {
      let params = {
        pageNo: this.pageNo,
        pageSize: 10,
        isDelete: 0,
        type: 3,
        title: this.searchName === '公告名称' ? (this.search_input === '' ? null : this.search_input) : null,
        tenderCode: this.searchName === '招标编号' ? (this.search_input === '' ? null : this.search_input) : null
      }
      infoManageBidState.queryList(params).then((res) => {
        this.tableData = res.data.bidInvitationPageInfo.list
        this.total = res.data.bidInvitationPageInfo.total
      })
    },
    // 搜索
    search () {
      this.currentPage = 1
      this.pageNo = 0
      this.dataIndex = 1
      this.list()
    },
    // 模糊查询选择
    selectSearch (val) {
      this.searchName = val === 1 ? '公告名称' : '招标编号'
    },
    /** 新增 */
    addbutton () {
      this.$router.push({path: '/index/bidstate/add'})
    },
    /** 表格操作按钮 */
    handleTableBtn (row, type) {
      if (Object.is(type, 'del')) {
        this.delete(row)
      } else if (Object.is(type, 'look')) {
        this.$router.push({path: `/index/bidstate/detail/${row.objectId}`})
      } else if (Object.is(type, 'edit')) {
        this.$router.push({path: '/index/bidstate/update', query: {objectId: row.objectId}})
      }
    },
    /** 删除 */
    delete (row) {
      this.$confirm('确定要删除吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 执行删除
        infoManageBidState.remove(row.objectId).then((res) => {
          this.list()
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        })
      })
    },
    // 分页
    handleCurrentChange (nowNum) {
      this.pageNo = (nowNum - 1) * this.pageSize
      this.dataIndex = (nowNum - 1) * this.pageSize + 1
      this.list()
    }
  },
  mounted () {
    this.list()
  }
}
</script>
<style lang="less">
  #ad-manage {
  }
</style>
