<template>
  <div class="cloudcontent" id="cloud_assignroles">
    <!--面包屑-->
    <el-breadcrumb separator-class="el-icon-arrow-right" class="breadcrumb_box">
      <el-breadcrumb-item :to="{ path: '/index' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/dailyManage/users' }">用户管理</el-breadcrumb-item>
      <el-breadcrumb-item class="active_bread">分配角色</el-breadcrumb-item>
    </el-breadcrumb>
    <!--面包屑-->
    <div class="main">
      <div class="seacher_box">
        <span style="">角色名称：</span>
        <el-input class="search" placeholder="请输入内容" style="vertical-align: top"  v-model="roleseacher">
          <el-button  slot="append" icon="el-icon-search" type="" @click="search"></el-button>
        </el-input>
      </div>
      <!--表格-->
      <div class="content_tablebox" style="margin-top: 20px">
        <template>
          <el-table
            ref="multipleTable"
            :data="rolesTable"
            @select="handleSelectionChange"
            @select-all="handleSelectionAll"
            tooltip-effect="dark"
            border
            style="width: 100%">
            <el-table-column
              type="selection"
              width="55" reserve-selection align="center">
            </el-table-column>
            <el-table-column
              prop="name"
              label="姓名"
              width="120">
            </el-table-column>
            <el-table-column
              prop="description"
              label="描述"
              show-overflow-tooltip>
            </el-table-column>
          </el-table>
        </template>
      </div>
      <!--表格-->
      <!--分页-->
      <el-pagination
        background
        layout="prev, pager, next"
        :total="total"
        :page-size='pageSize'
        :current-page.sync="currentPage"
        @current-change="handleCurrentChange">
      </el-pagination>
      <!--分页-->
      <div class="Determinebtnbox">
        <el-button type="primary" @click="Determinebtn">确 定</el-button>
        <el-button type="info" @click="cancelBtn">取 消</el-button>
      </div>
    </div>
  </div>
</template>
<script>
import {useradmin} from '../../../api/index'
import {addtitle} from '../../../assets/js/common'
export default {
  data () {
    return {
      // 搜索框默认值
      roleseacher: '',
      formLabelWidth: '120px',
      rolesTable: [],
      multipleSelection: [],
      pageSize: 10,
      pageNo: 0,
      total: 0,
      currentPage: 1,
      dataIndex: 1,
      searchIs: false,
      // 提交时存放id的数组
      idsArray: []
    }
  },
  methods: {
    // 列表接口
    distrList (search) {
      let url = null
      if (search) {
        url = {
          userId: this.$route.query.objectIds,
          creator: this.$store.getters.authUser.userId,
          pageNo: this.pageNo,
          pageSize: this.pageSize,
          nameLike: this.roleseacher,
          isDelete: 0,
          _t: new Date().getTime()
        }
      } else {
        url = {
          userId: this.$route.query.objectIds,
          creator: this.$store.getters.authUser.userId,
          pageNo: this.pageNo,
          pageSize: this.pageSize,
          isDelete: 0,
          _t: new Date().getTime()
        }
      }
      useradmin.userDistribution(this.$route.query.enterpriseId, url).then((res) => {
        this.rolesTable = res.data.rolesList.list
        this.total = res.data.rolesList.total
        this.rolesTable.map((tableItem, index) => {
          if (tableItem.flag == 1) {
            this.$refs.multipleTable.toggleRowSelection(this.rolesTable[index], true)
          } else {
            this.$refs.multipleTable.toggleRowSelection(this.rolesTable[index], false)
          }
        })
        addtitle(this)
      })
    },
    // 列表表单分页
    handleCurrentChange (nowNum) {
      this.pageNo = (nowNum - 1) * this.pageSize
      this.dataIndex = (nowNum - 1) * this.pageSize + 1
      this.distrList(this.searchIs)
    },
    // 查询数据
    search () {
      this.currentPage = 1
      this.dataIndex = 1
      this.pageNo = 0
      let parameter = {
        userId: this.$route.query.objectIds,
        creator: this.$store.getters.authUser.userId,
        pageNo: this.pageNo,
        pageSize: this.pageSize,
        nameLike: this.roleseacher,
        isDelete: 0,
        _t: new Date().getTime()
      }
      useradmin.userDistrSearch(this.$route.query.enterpriseId, parameter).then((res) => {
        this.rolesTable = res.data.rolesList.list
        this.rolesTable.map((tableItem, index) => {
          if (tableItem.flag == 1) {
            this.$refs.multipleTable.toggleRowSelection(this.rolesTable[index], true)
          } else if (tableItem.flag == null) {
            this.$refs.multipleTable.toggleRowSelection(this.rolesTable[index], false)
          }
        })
        this.currentPage = 1
        this.dataIndex = 1
        this.pageNo = (this.currentPage - 1) * this.pageSize
        this.searchIs = true
        addtitle(this)
      })
    },
    handleSelectionChange (selection, row) {
      this.multipleSelection.push(row)
    },
    handleSelectionAll (selection) {
      console.log(selection)
      if (selection.length === 0) {
        this.multipleSelection = []
      } else {
        this.multipleSelection.push(selection)
      }
    },
    Determinebtn () {
      this.$confirm('确认保存吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'success'
      }).then(() => {
        this.multipleSelection.map((ids) => {
          this.idsArray.push(ids.objectId)
        })
        let Juris = {
          userId: this.$route.query.objectIds,
          roleIds: this.idsArray
        }
        useradmin.userDistr(Juris).then((res) => {
          this.$router.push({path: '/organ/user'})
        })
      }).catch(() => {
        return false
      })
    },
    cancelBtn () {
      this.$confirm('确认取消分配权限吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'success'
      }).then(() => {
        this.$router.push({path: '/organ/user'})
      }).catch(() => {
        return false
      })
    }
  },
  mounted () {
    this.distrList()
  }
}
</script>
<style lang="less">
  #cloud_assignroles {
    .main .el-form-item__label{
      padding: 0 12px 0 0;
    }
    .Determinebtnbox{
      text-align: center;
      margin: 20px auto;
    }
  }
</style>
