<template>
  <div class="cloudcontent" id="cloud_users">
    <!--面包屑-->
    <el-breadcrumb separator-class="el-icon-arrow-right" class="breadcrumb_box">
      <el-breadcrumb-item :to="{ path: '/index' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item class="active_bread">角色管理</el-breadcrumb-item>
    </el-breadcrumb>
    <!--面包屑-->
    <div class="main">
      <div class="topmain">
        <!--按钮-->
        <div class="btnbigbox">
          <!--添加角色-->
          <div class="addrole">
            <el-button class="addbutton" @click='roleaEdit()'>
              <span> + 新增角色</span>
            </el-button>
            <el-dialog title="添加角色" width="40%" :before-close="btnClose" :visible.sync="addRoleVisible">
              <el-form :model="roleForm" :rules="rules" ref="roleForm">
                <el-form-item label="角色名称" prop="name" :label-width="formLabelWidth">
                  <el-input v-model.trim="roleForm.name" auto-complete="off"></el-input>
                </el-form-item>
                <el-form-item label="角色描述" prop="description"  :label-width="formLabelWidth">
                  <el-input
                    type="textarea"
                    :autosize="{ minRows: 2, maxRows: 4}"
                    v-model.trim="roleForm.description">
                  </el-input>
                </el-form-item>
              </el-form>
              <div slot="footer" class="dialog-footer operationbtn">
                <el-button type="info" @click="addcancel('roleForm')">取 消</el-button>
                <el-button type="primary" @click="addDetermine('roleForm',flag)">确 定</el-button>
              </div>
            </el-dialog>
          </div>
          <!--添加角色-->
        </div>
        <!--按钮-->
      </div>
      <!--表格-->
      <div class="content_tablebox">
        <template>
          <el-table
            :data="roletableData"
            border
            style="width: 100%" header-cell-class-name="tableheader">
            <el-table-column
              type="index"
              label="序号"
              width="60"
              :index='dataIndex'
              align="center">
            </el-table-column>
            <el-table-column
              prop="name"
              label="角色名称"
              width="250">
            </el-table-column>
            <el-table-column
              prop="description"
              label="角色描述">
            </el-table-column>
            <el-table-column
              label="操作" align="center" width="200">
              <template slot-scope="scope">
                <el-button type="text" size="small" @click="roleaEdit(scope)">编辑</el-button>
                <el-button type="text" size="small" @click="roleaDelect(scope)">删除</el-button>
                <el-button type="text" size="small" @click="distribution(scope)">分配权限</el-button>
              </template>
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
    </div>
  </div>
</template>
<script>
import {rolead} from '../../../api/index'
import {addtitle} from '../../../assets/js/common'
export default {
  data () {
    return {
      // 搜索框默认值
      roleName: '',
      // 角色弹框
      addRoleVisible: false,
      // 增加角色数据
      roleForm: {},
      formLabelWidth: '120px',
      pageSize: 10,
      pageNo: 0,
      total: 0, // 总条数
      currentPage: 1,
      dataIndex: 1,
      searchIs: false,
      // 角色提交验证
      rules: {
        name: [
          { required: true, message: '角色名称不能为空', trigger: 'blur' },
          { min: 1, max: 20, message: '长度在1~20个字符', trigger: 'blur' }
        ],
        description: [
          { required: true, message: '角色描述不能为空', trigger: 'blur' },
          { min: 1, max: 200, message: '长度在1~200个字符', trigger: 'blur' }
        ]
      },
      // 角色管理表格数据
      roletableData: [],
      flag: true
    }
  },
  methods: {
    // *********************************************角色************************************************
    // 列表展示
    roleList (search) {
      let url = null
      if (search) {
        url = {
          creator: this.$store.getters.authUser.userId,
          pageNo: this.pageNo,
          pageSize: this.pageSize,
          nameLike: this.roleName,
          isDelete: 0,
          _t: new Date().getTime()
        }
      } else {
        url = {
          creator: this.$store.getters.authUser.userId,
          pageNo: this.pageNo,
          pageSize: this.pageSize,
          isDelete: 0,
          _t: new Date().getTime()
        }
      }
      rolead.roleadList(url).then((res) => {
        this.roletableData = res.data.rolePageInfo.list
        this.total = res.data.rolePageInfo.total
        addtitle(this)
      })
    },
    // 列表表单分页
    handleCurrentChange (nowNum) {
      this.pageNo = (nowNum - 1) * this.pageSize
      this.dataIndex = (nowNum - 1) * this.pageSize + 1
      this.roleList(this.searchIs)
    },
    // 提交
    addDetermine (formName, flag) {
      this.$refs[formName].validate((valid) => {
        this.roleForm.enterpriseId = this.$store.getters.authUser.enterpriseId
        this.roleForm.creator = this.$store.getters.authUser.userId
        if (valid) {
          if (flag) { // 进行添加角色
            rolead.roleadAdd(this.roleForm).then((res) => {
              this.addRoleVisible = false
              this.roleForm = {}
              this.roleList()
            })
          } else { // 进行修改角色
            rolead.roleadEdit(this.roleForm).then((res) => {
              this.addRoleVisible = false
              this.roleForm = {}
              this.roleList()
            })
          }
        } else {
          return false
        }
      })
    },
    // 取消
    addcancel (roleForm) {
      this.$refs[roleForm].resetFields()
      this.addRoleVisible = false
    },
    // *********************************************分配权限************************************************
    // 编辑
    roleaEdit (scope) {
      if (scope) {
        // 1.根据角色id查询出角色的相关信息在页面进行展示
        rolead.roleadEditlook(scope.row.objectId).then((res) => {
          this.roleForm = res.data.role
        })
      } else {
        this.roleForm = {}
      }
      this.addRoleVisible = true
    },
    // 删除
    roleaDelect (scope) {
      this.$confirm('确认删除吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'error'
      }).then(() => {
        rolead.roleadDelet(scope.row.objectId).then((res) => {
          this.roleList()
        })
      }).catch(() => {
        return false
      })
    },
    // 分配权限
    distribution (scope) {
      this.$router.push({path: `/dailyManage/roles/assignPermission`, query: {objectId: scope.row.objectId}})
    },
    // 查询数据
    search () {
      this.currentPage = 1
      this.dataIndex = 1
      this.pageNo = 0
      rolead.roleadSeacher(
        {
          creator: this.$store.getters.authUser.userId,
          pageNo: this.pageNo,
          pageSize: this.pageSize,
          nameLike: this.roleName,
          isDelete: 0,
          _t: new Date().getTime()
        }
      ).then((res) => {
        this.roletableData = res.data.rolePageInfo.list
        this.total = res.data.rolePageInfo.total
        this.currentPage = 1
        this.dataIndex = 1
        this.pageNo = (this.currentPage - 1) * this.pageSize
        addtitle(this)
      })
    },
    // 关闭
    btnClose (done) {
      this.addRoleVisible = false
      this.roleForm = {}
    }
  },
  mounted () {
    this.roleList()
  }
}
</script>
<style lang="less">
  #cloud_users {
    .main .el-form-item__label{
      padding: 0 12px 0 0;
    }
    .main .content_tablebox{
      margin-top: 20px;
    }
  }
</style>
