<template>
  <div id="bidanno" class="smaincontent">
    <div class="headertitle">
      <!--面包屑-->
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/main' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>公告管理</el-breadcrumb-item>
        <el-breadcrumb-item>招标公告</el-breadcrumb-item>
      </el-breadcrumb>
      <!--面包屑-->
    </div>
    <div class="contentbigbox">
      <template>
        <!--搜索 & 添加按钮-->
        <el-row>
          <el-button type="success"  class="addbutton"  @click="addBtn"> + 添加招标公告</el-button>
          <el-button type="success"  class="addbutton"  @click="addScropBtn"> + 添加废标公告</el-button>
          <div class="anno_seacher_box">
            <span>公告状态：</span>
            <el-select v-model="status" class="select">
              <el-option
                v-for="item in statusOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value">
              </el-option>
            </el-select>
            <el-input class="search" style="vertical-align: top"  placeholder="请输入公告名称" v-model="search_input"></el-input>
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
            prop="tender_project_code"
            label="招标项目编号"
            :formatter="formatStr"
            align="left"
            show-overflow-tooltip>
          </el-table-column>
          <el-table-column
            prop="title"
            label="公告名称"
            :formatter="formatStr"
            align="left"
            show-overflow-tooltip>
          </el-table-column>
          <el-table-column
            prop="notice_nature"
            label="公告性质"
            align="left">
            <template slot-scope="scope">
              <span class="statusbox" v-if="scope.row.notice_nature === '1'">正常公告</span>
              <span class="statusbox" v-if="scope.row.notice_nature === '2'">变更公告</span>
              <span class="statusbox" v-if="scope.row.notice_nature === '3'">二次公告</span>
              <span class="statusbox" v-if="scope.row.notice_nature === '4'">废标公告</span>
              <span class="statusbox" v-if="scope.row.notice_nature === '9'">其他</span>
            </template>
          </el-table-column>
          <el-table-column
            prop="project_type"
            label="项目类型"
            align="left">
            <template slot-scope="scope">
              <div class="statusbigbox" v-if="scope.row.project_type === 1">
                <span class="statusbox">工程</span>
              </div>
              <div class="statusbigbox" v-if="scope.row.project_type === 2">
                <span class="statusbox">货物</span>
              </div>
              <div class="statusbigbox" v-if="scope.row.project_type === 3">
                <span class="statusbox">服务</span>
              </div>
              <div class="statusbigbox" v-if="scope.row.project_type === 9">
                <span class="statusbox">其他</span>
              </div>
              <div class="statusbigbox" v-else>
                <span class="statusbox">---</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column
            prop="last_update_date"
            :formatter="formatDate"
            label="修改时间"
            align="left"
            show-overflow-tooltip>
          </el-table-column>
          <el-table-column
            prop="status"
            label="公告状态"
            align="left">
            <template slot-scope="scope">
              <div class="statusbigbox" v-if="scope.row.status === 0">
                <span class="statusbox">未提交</span>
              </div>
              <div class="statusbigbox" v-if="scope.row.status === 1">
                <span class="statusbox">审核中</span>
              </div>
              <div class="statusbigbox" v-if="scope.row.status === 2">
                <span class="statusbox">审核通过</span>
              </div>
              <div class="statusbigbox" v-if="scope.row.status === 3">
                <span class="statusbox">审核不通过</span>
              </div>
              <!--<div class="statusbigbox" v-if="scope.row.status === 4">-->
                <!--<span class="statusbox">已撤回</span>-->
              <!--</div>-->
            </template>
          </el-table-column>
          <el-table-column
            fixed="right"
            label="操作" width="340" align="center">
            <template slot-scope="scope">
              <div class="statusbigbox">
                <el-button type="text" size="small" v-if="(scope.row.status === 2 && scope.row.is_change !== 1 && scope.row.notice_nature !== '4') && scope.row.enterprise_id === $store.getters.authUser.enterpriseId" @click="changeBtn(scope)">
                  变更
                </el-button>
                <el-button type="text" size="small" v-if="(scope.row.status === 2 && scope.row.is_change !== 1 && scope.row.notice_nature !== '4') && scope.row.enterprise_id === $store.getters.authUser.enterpriseId" @click="secondBtn(scope)">
                  二次公告
                </el-button>
                <el-button type="text" size="small" v-if="(scope.row.status === 2 && scope.row.is_change !== 1 && scope.row.notice_nature !== '4') && scope.row.enterprise_id === $store.getters.authUser.enterpriseId" @click="addBidWin(scope)">
                  添加中标候选人公示
                </el-button>
                <el-button type="text" size="small" v-if="(scope.row.status === 0 || scope.row.status === 3) && scope.row.enterprise_id === $store.getters.authUser.enterpriseId" @click="updateBtn(scope)">
                  修改
                </el-button>
                <el-button type="text" size="small" @click="detailBtn(scope)">
                  查看
                </el-button>
                <el-button type="text" size="small" @click="printBtn(scope)" v-if="scope.row.status === 2">
                  打印回执
                </el-button>
                <el-button type="text" size="small" v-if="(scope.row.status === 0) && scope.row.enterprise_id === $store.getters.authUser.enterpriseId" @click="deleteBtn(scope)">
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
      </template>
    </div>
  </div>
</template>
<script>
import {bidanno} from '@/api'
import {dateFormat} from '@/assets/js/common'
export default {
  data () {
    return {
      search_input: '',
      search_input_company: '',
      statusOptions: [{
        value: null,
        label: '全部'
      }, {
        value: 0,
        label: '未提交'
      }, {
        value: 1,
        label: '审核中'
      }, {
        value: 2,
        label: '审核通过'
      }, {
        value: 3,
        label: '审核不通过'
      }],
      status: null,
      tableData: [],
      // 当前页
      currentPage: 1,
      pageNo: 1,
      total: 0, // 总条数
      pageSize: 10, // 每页展示条数
      dataIndex: 1
    }
  },
  methods: {
    list () {
      let params = {
        pageNo: this.pageNo,
        pageSize: 10,
        isDelete: 0,
        infoType: 11,
        creator: this.$store.getters.authUser.userId,
        status: this.status,
        orderBy: 'last_update_date',
        titleLike: this.search_input === '' ? null : this.search_input
      }
      bidanno.queryList(params).then((res) => {
        this.tableData = res.data.list
        this.total = res.data.totalNum
      })
    },
    // 转换UTC时间格式
    formatDate (row, col, cellValue) {
      return dateFormat(new Date(cellValue), 'yyyy-MM-dd hh:mm:ss')
    },
    // 格式化字符串，如果是''则为---
    formatStr (row, col, cellValue) {
      return cellValue || '---'
    },
    // 搜索
    search () {
      this.currentPage = 1
      this.pageNo = 1
      this.dataIndex = 1
      this.list()
    },
    // 修改
    updateBtn (scope) {
      this.$router.push({path: '/annomanage/bidanno/update', query: {objectId: scope.row.object_id, status: this.status}})
    },
    changeBtn (scope) {
      this.$router.push({path: '/annomanage/bidanno/change', query: {objectId: scope.row.object_id, status: this.status}})
    },
    secondBtn (scope) {
      this.$router.push({path: '/annomanage/bidanno/second', query: {objectId: scope.row.object_id, status: this.status}})
    },
    /** 删除 */
    deleteBtn (scope) {
      this.$confirm('确定要删除吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 执行删除
        bidanno.remove(scope.row.object_id).then((res) => {
          this.list()
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        })
      })
    },
    // 添加
    addBtn () {
      this.$router.push({path: '/annomanage/bidanno/add', query: {status: this.status}})
    },
    // 添加废标
    addScropBtn () {
      this.$router.push({path: '/annomanage/bidanno/add-abandoned-tender', query: {status: this.status}})
    },
    // 查看
    detailBtn (scope) {
      this.$router.push({path: `/annomanage/bidanno/detail/${scope.row.object_id}`, query: {status: this.status}})
    },
    // 打印回执
    printBtn (scope) {
      let routeData = this.$router.resolve({path: `/annomanage/bidanno/print/${scope.row.object_id}`})
      window.open(routeData.href, '_blank')
    },
    // 跳转添加中标候选人公示
    addBidWin (scope) {
      this.$router.push({path: `/annomanage/bidwin/add/${scope.row.object_id}`, query: {status: this.status}})
    },
    // 分页
    handleCurrentChange (nowNum) {
      this.pageNo = nowNum
      this.dataIndex = (nowNum - 1) * this.pageSize + 1
      this.list()
    }
  },
  mounted () {
    // 默认是全部
    this.status = this.$route.query.status ? this.$route.query.status : this.$route.query.status === 0 ? this.$route.query.status : null
    this.list()
  }
}
</script>
<style lang="less">
  #bidanno{
    .select{
      width: 14%;
    }
    .search{
      width: 20%;
    }
    .el-form-item__label{
      width: 168px !important;
    }
    .el-form-item__content{
      margin-left: 168px !important;
      width: 60%;
    }
  }
</style>
