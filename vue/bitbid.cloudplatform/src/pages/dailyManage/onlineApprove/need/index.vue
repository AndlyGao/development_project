<template>
  <div class="cloudcontent" id="cloud_processtype">
    <!--面包屑-->
    <el-breadcrumb separator-class="el-icon-arrow-right" class="breadcrumb_box">
      <el-breadcrumb-item :to="{ path: '/index' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/dailyManage/onlineapprove' }">在线审批</el-breadcrumb-item>
      <el-breadcrumb-item class="active_bread">待我审批</el-breadcrumb-item>
    </el-breadcrumb>
    <!--搜索 & 添加按钮-->
    <el-row>
      <div class="seacher_box">
        <span>业务来源：</span>
        <el-select v-model="tenderMode" class="select">
          <el-option
            v-for="item in sourceOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value">
          </el-option>
        </el-select>
        <span class="left">审批类型：</span>
        <el-select v-model="flowStatus" class="select">
          <el-option
            v-for="item in typeOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value">
          </el-option>
        </el-select>
        <span class="left">审批状态：</span>
        <el-select v-model="auditStatus" class="select left">
          <el-option
            v-for="item in statusOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value">
          </el-option>
        </el-select>
        <el-input class="search left" style="vertical-align: top"  placeholder="请输入项目名称关键字" v-model="messageLike"></el-input>
        <el-button  slot="append" icon="el-icon-search" type="" @click="search"></el-button>
      </div>
    </el-row>
      <!--搜索 & 添加按钮-->
    <div class="main">
      <el-table
        :data="tableData"
        border>
        <el-table-column
          type="index"
          label="序号"
          align="center"
          width="80">
        </el-table-column>
        <el-table-column
          prop="tenderMode"
          label="业务来源"
          width="80">
          <template slot-scope="scope">
            <span  v-if="scope.row.tenderMode === 1">公开招标</span>
            <span  v-if="scope.row.tenderMode === 2">邀请招标</span>
            <span  v-if="scope.row.tenderMode === 3">竞争性谈判</span>
            <span  v-if="scope.row.tenderMode === 4">单一来源采购</span>
            <span  v-if="scope.row.tenderMode === 5">询价采购</span>
            <span  v-if="scope.row.tenderMode === 7">竞争性磋商</span>
            <span  v-if="scope.row.tenderMode === 9">其他</span>
          </template>
        </el-table-column>
        <el-table-column
          prop="flowStatus"
          label="审批类型"
          width="80">
          <template slot-scope="scope">
            <span  v-if="scope.row.flowStatus === 1">招标文件</span>
            <span  v-if="scope.row.flowStatus === 2">招标公告</span>
          </template>
        </el-table-column>
        <el-table-column
          prop="tenderProject.tenderProjectCode"
          label="项目编号"
          show-overflow-tooltip>
        </el-table-column>
        <el-table-column
          prop="tenderProject.tenderProjectName"
          label="项目名称"
          show-overflow-tooltip>
        </el-table-column>
        <el-table-column
          prop="creatorName"
          label="申请人"
          show-overflow-tooltip>
        </el-table-column>
        <el-table-column
          prop="auditStatus"
          label="审批状态">
          <template slot-scope="scope">
            <span  v-if="scope.row.auditStatus === 0">待审批</span>
            <span  v-if="scope.row.auditStatus === 1">通过</span>
            <span  v-if="scope.row.auditStatus === 2">不通过</span>
            <span  v-if="scope.row.auditStatus === 3">已撤回</span>
          </template>
        </el-table-column>
        <el-table-column
          prop="createDate"
          label="提交时间">
        </el-table-column>
        <el-table-column
          label="操作" align="center" width="60">
          <template slot-scope="scope">
            <el-button type="text" size="small" v-if="auditStatus === '1'" @click="handleDetail(scope)">查看</el-button>
            <el-button type="text" size="small" @click="handleApprove(scope)" v-else>审批</el-button>
          </template>
        </el-table-column>
      </el-table>
      <!--分页-->
      <el-pagination
        background
        layout="prev, pager, next"
        :total="total"
        :page-size='pageSize'
        :current-page.sync="currentPage"
        @current-change="handleCurrentChange"
        @next-click="handleCurrentNext">
      </el-pagination>
      <!--分页-->
    </div>
  </div>
</template>

<script>
import { approvalTask } from '../../../../api/cloudplatform/index'
export default {
  name: '',
  data () {
    return {
      messageLike: '',
      auditStatus: '',
      tenderMode: '',
      flowStatus: '',
      // 审批状态
      statusOptions: [{
        value: null,
        label: '全部'
      }, {
        value: 0,
        label: '待审批'
      }, {
        value: 1,
        label: '通过'
      }, {
        value: 2,
        label: '不通过'
      }, {
        value: 3,
        label: '已撤回'
      }],
      // 业务来源
      sourceOptions: [{
        value: null,
        label: '全部'
      }, {
        value: 1,
        label: '公开招标'
      }, {
        value: 2,
        label: '邀请招标'
      }, {
        value: 3,
        label: '竞争性谈判'
      }, {
        value: 4,
        label: '单一来源采购'
      }, {
        value: 5,
        label: '询价采购'
      }, {
        value: 6,
        label: '竞争性磋商'
      }, {
        value: 9,
        label: '其他'
      }],
      source: null,
      typeOptions: [{
        value: null,
        label: '全部'
      }, {
        value: 1,
        label: '招标文件'
      }, {
        value: 2,
        label: '招标公告'
      }],
      type: null,
      // 当前页
      currentPage: 1,
      pageNo: 0,
      total: 100, // 总条数
      pageSize: 10, // 每页展示条数
      tableData: []
    }
  },
  methods: {
    search () {
      approvalTask.needMeApproval({
        pageNo: this.pageNo,
        pageSize: this.pageSize,
        enterpriseId: this.$store.getters.authUser.enterpriseId,
        messageLike: this.messageLike,
        tenderMode: this.tenderMode,
        flowStatus: this.flowStatus,
        auditStatus: this.auditStatus
      }).then((res) => {
        this.tableData = res.data.approvalTaskList
        this.total = res.data.total
      })
    },
    handleDetail (scope) {
      this.$router.push({path: scope.row.path, query: {isApproved: 1}})
    },
    handleApprove (scope) {
      this.$router.push({path: scope.row.path, query: {isApproved: 2}})
    },
    handleCurrentNext (nowNum) {
      this.currentPage = nowNum
      this.pageNo = (nowNum - 1) * this.pageSize
      this.search(parseInt(this.pageNo), parseInt(this.pageSize))
    },
    // 表单分页
    handleCurrentChange (nowNum) {
      this.currentPage = nowNum
      this.pageNo = (nowNum - 1) * this.pageSize
      this.search(parseInt(this.pageNo), parseInt(this.pageSize))
    }
  },
  mounted () {
    this.search()
  }
}
</script>

<style scoped>
  .main {
    width: 100%;
    padding: 0 24px;
    box-sizing: border-box;
  }
  .seacher_box {
    margin: 10px 24px;
  }
  .select{
    width: 14%;
  }
  .search{
    width: 20%;
  }
  .left {
    margin-left: 12px;
  }
</style>
