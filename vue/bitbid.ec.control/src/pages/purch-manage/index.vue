<template>
  <div id="purch-page"  class="cont-view">
    <!-- 面包屑区域start -->
    <div class="bread-box">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>采购人管理</el-breadcrumb-item>
        <el-breadcrumb-item>账号管理</el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    <!-- 面包屑区域end -->
    <!-- 搜索区域start -->
    <div class="search-box">
      <el-row :gutter="20">
        <el-col :span="8">
          <el-input placeholder="请输入用户名/采购人" v-model="queryModel.messageLike" class="input-with-select">
            <el-button slot="append" icon="el-icon-search" @click="getTableData"></el-button>
          </el-input>
        </el-col>
        <el-col :span="16" class="add-btn">
          <el-button type="primary" size="small" @click="addAccount" style="float:right;">新增账号</el-button>
        </el-col>
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
          :index="computedIndex"
          align="center">
        </el-table-column>
        <el-table-column
          prop="account"
          label="用户名"
          :formatter="simpleFormatData"
          align="center">
        </el-table-column>
        <el-table-column
          prop="userName"
          label="采购人"
          :formatter="simpleFormatData"
          align="center">
        </el-table-column>
        <el-table-column
          prop="contactName"
          label="联系人"
          :formatter="simpleFormatData"
          align="center">
        </el-table-column>
         <el-table-column
          prop="phone"
          label="联系方式"
          :formatter="simpleFormatData"
          align="center">
        </el-table-column>
        <el-table-column
          prop="isDisable"
          label="状态"
          :formatter="simpleFormatData"
          align="center">
          <template slot-scope="scope">
            <el-switch
              @change="forbiddenUser(scope.row)"
              v-model="scope.row.isDisable"
              :active-value="0"
              :inactive-value="1"
              active-color="#13ce66"
              inactive-color="#ff4949">
            </el-switch>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="text"
              @click="handleTableBtn(scope.row, 'reset')">
              重置密码
            </el-button>
            <el-button
              size="mini"
              type="text"
              @click="handleTableBtn(scope.row, 'edit')">
              编辑
            </el-button>
            <el-button
              size="mini"
              type="text"
              @click="handleTableBtn(scope.row, 'del')">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-box">
        <!--分页-->
        <el-pagination
          background
          layout="prev, pager, next"
          :total="page.total"
          :page-size='page.pageSize'
          :current-page.sync="page.currentPage"
          @current-change="handlePage"
          @next-click="handlePage">
        </el-pagination>
        <!--分页-->
      </div>
    </div>
    <!-- 弹窗区域start -->
    <div class="dialog-box">
      <!-- 新增、编辑人员弹窗start -->
      <el-dialog
        :title="`${titleName}账号`"
        :visible.sync="showVisible"
        @closed="close('submitForm')"
        width="30%"
        center>
        <el-form :model="submitForm" :rules="rules" ref="submitForm" label-width="110px" class="demo-ruleForm">
          <el-form-item label="用户名:" prop="account">
            <el-input v-model="submitForm.account" :clearable="!isEdit" placeholder="请输入用户名" :readonly="isEdit">
              <i class="el-icon-edit el-input__icon" v-if="!isEdit" slot="suffix"></i>
            </el-input>
          </el-form-item>
          <el-form-item label="采购人:" prop="userName">
            <el-input v-model="submitForm.userName" placeholder="请输入姓名">
              <i class="el-icon-edit el-input__icon" slot="suffix"></i>
            </el-input>
          </el-form-item>
          <el-form-item label="登录密码:" prop="password" v-if="!isEdit">
            <el-input type="password" v-model="submitForm.password" placeholder="请输入登录密码">
              <i class="el-icon-edit el-input__icon" slot="suffix"></i>
            </el-input>
          </el-form-item>
          <el-form-item label="确认密码:" prop="rePassword" v-if="!isEdit">
            <el-input type="password"  v-model="submitForm.rePassword" placeholder="请再次输入登录密码">
              <i class="el-icon-edit el-input__icon" slot="suffix"></i>
            </el-input>
          </el-form-item>
          <el-form-item label="联系人:">
            <el-input v-model="submitForm.contactName" placeholder="请输入联系人">
              <i class="el-icon-edit el-input__icon" slot="suffix"></i>
            </el-input>
          </el-form-item>
          <el-form-item label="联系方式:" prop="phone">
            <el-input v-model="submitForm.phone" placeholder="请输入联系方式">
              <i class="el-icon-edit el-input__icon" slot="suffix"></i>
            </el-input>
          </el-form-item>
        </el-form>
        <span slot="footer" class="dialog-footer">
          <el-button @click="showVisible = false">取 消</el-button>
          <el-button type="primary" @click="submit">确 定</el-button>
        </span>
      </el-dialog>
      <!-- 新增、编辑人员弹窗end -->

      <!-- 重置密码start -->
      <el-dialog
        title="重置密码"
        :visible.sync="showResetVisible"
        @closed="close('resetForm')"
        width="30%"
        center>
        <el-form :model="resetForm" :rules="resetRules" ref="resetForm" label-width="110px" class="demo-ruleForm">
          <el-form-item label="新密码:" prop="password">
            <el-input type="password" v-model="resetForm.password" placeholder="请输入新密码">
              <i class="el-icon-edit el-input__icon" slot="suffix"></i>
            </el-input>
          </el-form-item>
          <el-form-item label="确认新密码:" prop="rePassword">
            <el-input type="password" v-model="resetForm.rePassword" placeholder="请再次输入登录密码">
              <i class="el-icon-edit el-input__icon" slot="suffix"></i>
            </el-input>
          </el-form-item>
        </el-form>
        <span slot="footer" class="dialog-footer">
          <el-button @click="showResetVisible = false">取 消</el-button>
          <el-button type="primary" @click="submitRePassword">确 定</el-button>
        </span>
      </el-dialog>
      <!-- 重置密码end -->
    </div>
    <!-- 弹窗区域end -->
  </div>
</template>
<script>
import {user} from '@/api'
import {validPhoneUser} from '@/assets/js/validate'
export default {
  name: '',
  data () {
    let validateRepeatPass = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次输入登录密码'))
      } else if (value !== this.submitForm.password) {
        callback(new Error('两次输入的登录密码不一致'))
      } else {
        callback()
      }
    }
    let validateResetRepeatPass = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次输入登录密码'))
      } else if (value !== this.resetForm.password) {
        callback(new Error('两次输入的登录密码不一致'))
      } else {
        callback()
      }
    }
    return {
      queryModel: {},
      tableData: [],
      loading: false,
      page: {
        total: 0,
        pageSize: 10,
        pageNo: 0,
        currentPage: 1
      },
      titleName: '新增',
      showVisible: false,
      submitForm: {},
      rules: {
        account: [{required: true, message: '请填写用户名', trigger: ['blur', 'change']}],
        userName: [
          {required: true, message: '请填写采购人', trigger: ['blur', 'change']},
          { min: 1, max: 20, message: '长度在1~20个字符', trigger: ['blur', 'change'] }
        ],
        password: [
          { required: true, message: '密码不能为空', trigger: ['blur', 'change'] },
          { min: 6, max: 20, message: '长度在 6到 20 个字符', trigger: ['blur', 'change'] }
        ],
        rePassword: [
          { required: true, message: '确认密码不能为空', trigger: ['blur', 'change'] },
          { validator: validateRepeatPass, trigger: ['blur', 'change'] }
        ],
        phone: [
          { required: false, validator: validPhoneUser, trigger: ['blur', 'change'] }
        ]
      },
      isEdit: false,
      showResetVisible: false, // 重置密码
      resetForm: {},
      resetRules: {
        password: [
          { required: true, message: '新密码不能为空', trigger: ['blur', 'change'] },
          { min: 6, max: 20, message: '长度在 6到 20 个字符', trigger: ['blur', 'change'] }
        ],
        rePassword: [
          { required: true, message: '确认新密码不能为空', trigger: ['blur', 'change'] },
          { validator: validateResetRepeatPass, trigger: ['blur', 'change'] }
        ]
      }
    }
  },
  methods: {
    close (formName) {
      switch (formName) {
        case 'submitForm':
          this.submitForm = {}
          break
        case 'resetForm':
          this.resetForm = {}
          break
      }
      this.$refs[formName].resetFields()
    },
    /** 搜索 */
    getTableData () {
      this.page.currentPage = 1
      this.page.pageN0 = 0
      this._getUsersList()
    },
    /** 新增账号 */
    addAccount () {
      this.showVisible = true
      this.titleName = '新增'
      this.isEdit = false
    },
    /** 状态 */
    forbiddenUser (row) {
      user.freezeUser(row.id, row.isDisable === 0 ? 0 : 1).then(res => {
        if (res.data.resCode === '0000') {
          this._getUsersList()
        }
      })
    },
    /** 表格操作 */
    handleTableBtn (row, type) {
      if (Object.is(type, 'reset')) {
        // 重置密码
        this.showResetVisible = true
        this.resetForm.id = row.id
      } else if (Object.is(type, 'edit')) {
        // 编辑
        this.showVisible = true
        this.isEdit = true
        this.titleName = '编辑'
        this.submitForm = Object.assign({}, row)
      } else if (Object.is(type, 'del')) {
        // 删除
        this.deleteMethod(row.id)
      }
    },
    /** 分页 */
    handlePage (nowNum) {
      this.page.currentPage = nowNum
      this.page.pageNo = (nowNum - 1) * this.page.pageSize
      this._getUsersList()
    },
    /** 新增-编辑-确定 */
    submit () {
      this.$refs['submitForm'].validate(valid => {
        if (valid) {
          this.submitForm.type = 3
          user.saveUsers(this.submitForm).then(res => {
            // 成功
            if (res.data.resCode === '0000') {
              this.showVisible = false
              if (!this.isEdit) {
                // 新增重置搜索和分页
                this.queryModel = {}
                this.page.pageNo = 0
              }
              this._getUsersList()
            }
          })
        } else {
          return false
        }
      })
    },
    /** 重置密码-确定 */
    submitRePassword () {
      this.$refs['resetForm'].validate(validate => {
        if (validate) {
          user.resetPassword(this.resetForm).then(res => {
            if (res.data.resCode === '0000') {
              this.showResetVisible = false
            }
          })
        }
      })
    },
    /** 获得用户信息 */
    _getUsersList () {
      this.queryModel.pageNo = this.page.pageNo
      this.queryModel.pageSize = this.page.pageSize
      this.queryModel.type = 3
      user.queryList(this.queryModel).then(res => {
        this.tableData = res.data.data.list
        this.page.total = res.data.data.total
      })
    },
    /** 普通格式化数据，空的时候展示"---" */
    simpleFormatData (row, col, cellValue) {
      return cellValue || '---'
    },
    /** 序号计算 */
    computedIndex (index) {
      return index + (this.page.currentPage - 1) * this.page.pageSize + 1
    },
    /** 删除 */
    deleteMethod (objectId) {
      this.$confirm('确认删除吗?', '提示', {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'error'
      }).then(() => {
        // 执行
        user.delUsers(objectId).then(res => {
          if (res.data.resCode === '0000') {
            this._getUsersList()
          }
        })
      }).catch(() => {
        return false
      })
    }
  },
  mounted () {
    this._getUsersList()
  }
}
</script>
<style lang='less'>
</style>
