<template>
  <div class="cloudcontent" id="cloud_processtype">
    <!--面包屑-->
    <el-breadcrumb separator-class="el-icon-arrow-right" class="breadcrumb_box">
      <el-breadcrumb-item :to="{ path: '/index' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/dailyManage/onlineapprove' }">在线审批</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/dailyManage/onlineapprove/config' }">审批配置</el-breadcrumb-item>
      <el-breadcrumb-item class="active_bread">{{breadName}}</el-breadcrumb-item>
    </el-breadcrumb>
    <div class="main">
      <!--按钮-->
      <el-button class="addbutton" @click="handleAdd">
        <span> + 添加审批</span>
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
          prop="flowStatusStr"
          label="审批节点">
        </el-table-column>
        <el-table-column
          prop="enabledStatus"
          label="启用状态"
          align="center"
          width="100">
          <template slot-scope="scope">
              <span>{{scope.row.enabledStatus === 0 ? '停用' : '启用'}}</span>
          </template>
        </el-table-column>
        <el-table-column
          label="操作" align="center" width="160">
          <template slot-scope="scope">
            <el-button type="text" size="small" v-if="scope.row.enabledStatus === 0" @click="handleEnable(scope, 1)">启用</el-button>
            <el-button type="text" size="small" v-if="scope.row.enabledStatus === 1" @click="handleEnable(scope, 0)">停用</el-button>
            <el-button type="text" size="small" @click="handleSetting(scope)">审批设置</el-button>
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
      <el-form :model="form" :rules="dialogRules" ref="form">
        <el-form-item label="审批节点名称：" prop="userAccount" label-width="120px">
          <el-input v-model="form.flowStatusStr"></el-input>
        </el-form-item>
        <el-form-item label="启用状态：" prop="userAccount" label-width="120px">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item class="submit_btn">
          <el-button type="primary" @click="submit('form')" :loading="isSubmiting">提交</el-button>
          <el-button type="cancel" @click="handleClose">取消</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script>
import editor from '@/components/ueditor/ueditor.vue'
import { approvalNode } from '../../../../api/cloudplatform/dailyManage/onlineApprove/flow/approval-node'
export default {
  components: {
    editor
  },
  name: '',
  data () {
    return {
      dialogVisible: false,
      isSubmiting: false,
      titileName: '添加审批节点',
      form: {},
      dialogRules: {},
      breadName: '项目流程审批管理',
      updateForm: {},
      rules: {},
      // 富文本
      editread: false,
      content: '',
      tableData: [],
      radio: '',
      sectionList: [],
      // 当前页
      currentPage: 1,
      pageNo: 0,
      total: 100, // 总条数
      pageSize: 10 // 每页展示条数
    }
  },
  methods: {
    handleApprovalNodeList () {
      approvalNode.approvalNodeList({
        pageNo: this.pageNo,
        pageSize: this.pageSize
        // enterpriseId: this.$store.getters.authUser.enterpriseId
      }).then((res) => {
        this.tableData = res.data.approvalNodeList.list
        this.total = res.data.approvalNodeList.total
      })
    },
    handleCurrentChange (nowNum) {
      this.currentPage = nowNum
      this.pageNo = (nowNum - 1) * this.pageSize
      this.handleApprovalNodeList()
    },
    handleAdd () {
      this.dialogVisible = true
    },
    // 关闭弹窗
    handleClose () {
      this.dialogVisible = false
      this.$refs['form'].resetFields()
    },
    handleEnable (scope, status) {
      approvalNode.approvalNodeUpdateStatus(scope.row.objectId)
      scope.row.enabledStatus = status
    },
    handleSetting (scope) {
      this.$router.push({path: `/dailyManage/onlineapprove/config/approveSetting/${scope.row.objectId}`})
    },
    submit (form) {
    }
  },
  mounted () {
    this.handleApprovalNodeList()
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
