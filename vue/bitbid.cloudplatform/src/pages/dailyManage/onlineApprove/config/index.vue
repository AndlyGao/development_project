<template>
  <div class="cloudcontent" id="cloud_processtype">
    <!--面包屑-->
    <el-breadcrumb separator-class="el-icon-arrow-right" class="breadcrumb_box">
      <el-breadcrumb-item :to="{ path: '/index' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/dailyManage/onlineapprove' }">在线审批</el-breadcrumb-item>
      <el-breadcrumb-item class="active_bread">审批配置</el-breadcrumb-item>
    </el-breadcrumb>
    <div class="main">
      <!--按钮-->
      <el-button class="addbutton" @click="handleAdd">
        <span> + 添加</span>
      </el-button>
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
          prop="name"
          label="业务名称">
        </el-table-column>
        <el-table-column
          label="操作" align="center" width="160">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="handleDetail(scope)">查看</el-button>
            <el-button type="text" size="small" v-if="scope.row.status === '1'" @click="handleEdit(scope)">编辑</el-button>
            <el-button type="text" size="small" v-if="scope.row.status === '1'" @click="handleDel(scope)">删除</el-button>
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
        @current-change="handleCurrentChange">
      </el-pagination>
      <!--分页-->
    </div>
    <el-dialog :title="titileName" :visible.sync="dialogVisible" width="24%" :before-close="handleClose">
      <el-form :model="form" :rules="rules" ref="form">
        <el-form-item label="业务名称：" prop="userAccount" label-width="120px">
          <el-input v-model="form.userAccount"></el-input>
        </el-form-item>
        <el-form-item class="submit-radio submit_btn">
          <el-button type="primary" @click="submit('form')" :loading="isSubmiting">提交</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: '',
  data () {
    return {
      // 当前页
      currentPage: 1,
      pageNo: 0,
      total: 100, // 总条数
      pageSize: 10, // 每页展示条数
      tableData: [{
        name: '项目流程管理审批',
        status: '1'
      }, {
        name: '费用审批',
        status: '1'
      }, {
        name: '自定义审批',
        status: '2'
      }],
      dialogVisible: false,
      isSubmiting: false,
      titileName: '添加配置',
      form: {},
      rules: {}
    }
  },
  methods: {
    handleCurrentChange () {
    },
    handleAdd () {
      this.dialogVisible = true
    },
    // 关闭弹窗
    handleClose () {
      this.dialogVisible = false
      this.$refs['form'].resetFields()
    },
    handleDetail (scope) {
      this.$router.push({path: `/dailyManage/onlineapprove/config/detail/${scope.row.objectId}`})
    },
    // 编辑
    handleEdit (scope) {
      this.dialogVisible = true
    },
    handleDel (scope) {
      this.$confirm('确认删除吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'error'
      }).then(() => {
        // **
      }).catch(() => {
        return false
      })
    },
    submit (form) {
    }
  },
  mounted () {
  }
}
</script>

<style scoped>
  .main {
    width: 100%;
    padding: 0 24px;
    box-sizing: border-box;
  }
  .addbutton {
    float: right;
    background: #6582f8;
    border-color: #6582f8;
    margin-bottom: 12px;
  }
  .addbutton span{
    color: #ffffff;
  }
  .submit_btn {
    text-align: center;
  }
</style>
