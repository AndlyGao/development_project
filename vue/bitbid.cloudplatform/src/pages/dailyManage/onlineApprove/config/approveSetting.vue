<template>
  <div class="cloudcontent">
    <!--面包屑-->
    <el-breadcrumb separator-class="el-icon-arrow-right" class="breadcrumb_box">
      <el-breadcrumb-item :to="{ path: '/index' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/dailyManage/onlineapprove' }">在线审批</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/dailyManage/onlineapprove/config' }">审批配置</el-breadcrumb-item>
      <el-breadcrumb-item class="active_bread">审批设置</el-breadcrumb-item>
    </el-breadcrumb>
    <div class="main">
      <el-form :model="updateForm" :rules="rules" ref="updateForm" :validate-on-rule-change="true">
        <el-form-item label="审批节点：" prop="tenderProjectCode">
          <span>{{updateForm.flowStatusStr}}</span>
        </el-form-item>
        <el-form-item label="审批流程：" prop="processList">
          <div v-for="(item, index) in processList" :key="item.objectId">
            <div class="process">
              <el-row class="top">
                <el-col :span="2" class="top_col"><span class="top_index">{{index + 1}}</span> <span class="top_title">第{{handleProcess(index+1)}}级</span>
                </el-col>
                <el-col :span="22" class="top_col">
                  <el-button class="selectbtn" @click="handleSelectBtn(item,index)">选择</el-button>
                </el-col>
              </el-row>
              <el-row class="content">
                <el-col :span="2"><span>部门:</span></el-col>
                <el-col :span="22"><span v-if="item.approvalFlowExecutor !== null">{{item.approvalFlowExecutor.departmentsNameStr}}</span>
                </el-col>
              </el-row>
              <el-row class="content">
                <el-col :span="2"><span>角色：</span></el-col>
                <el-col :span="22"><span v-if="item.approvalFlowExecutor !== null">{{item.approvalFlowExecutor.rolesNameStr}}</span>
                </el-col>
              </el-row>
              <el-row class="content">
                <el-col :span="2"><span>人员：</span></el-col>
                <el-col :span="22"><span v-if="item.approvalFlowExecutor !== null">{{item.approvalFlowExecutor.usersNameStr}}</span>
                </el-col>
              </el-row>
            </div>
          </div>
        </el-form-item>
        <el-form-item>
          <el-button class="addbutton" @click="handleAddBtn">
            <span> + 增加一级</span>
          </el-button>
        </el-form-item>
        <!--分页-->
        <el-form-item class="submit-radio">
          <!--<el-button type="primary" @click="submit('updateForm', 1)" :loading="isSubmiting">提交</el-button>-->
          <el-button type="primary" @click="submit('updateForm')" :loading="isSubmiting">保存</el-button>
          <el-button class="cancal" @click="this.$route.go(-1)">取消</el-button>
        </el-form-item>
      </el-form>
    </div>
    <el-dialog :title="titileName" :visible.sync="dialogVisible" width="40%" :before-close="handleClose">
      <el-tabs v-model="activeTab" @tab-click="handleTabClick">
        <el-tab-pane label="按部门" name="1"></el-tab-pane>
        <el-tab-pane label="按角色" name="2"></el-tab-pane>
        <el-tab-pane label="按人员" name="3"></el-tab-pane>
      </el-tabs>
      <el-tree
        ref="treeRef"
        :props="defaultProps"
        :data="data"
        node-key="objectId"
        :default-expanded-keys="expandedKeys"
        :default-checked-keys="checkedKeys"
        show-checkbox
        @check-change="handleCheckChange">
      </el-tree>
      <div class="submit_btn">
        <el-button type="primary" @click="submitTab()" :loading="isSubmiting">保存</el-button>
        <el-button type="cancel" @click="handleClose">取消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { toChinesNum } from '@/assets/js/common'
import { approvalFlow, department, rolead, useradmin } from '../../../../api/cloudplatform/index'

export default {
  name: '',
  data () {
    return {
      dialogVisible: false,
      isSubmiting: false,
      titileName: '选择审批人',
      form: {},
      dialogRules: {},
      activeTab: '1', // 当前选项卡
      oldTab: '1', // 选中当前之前的选项卡
      data: [],
      defaultProps: {
        children: 'children',
        label: 'name',
        id: 'id'
      },
      updateForm: {},
      rules: {},
      processList: [],
      expandedKeys: [],
      checkedKeys: [],
      // curItem: {
      //   flowLevel: 1,
      //   approvalNodeCode: '',
      //   approvalFlowExecutor: {
      //     departmentsNameStr: '',
      //     departmentsId: '',
      //     rolesNameStr: '',
      //     rolesId: '',
      //     usersNameStr: '',
      //     usersId: ''
      //   }},
      emptyItem: {
        flowLevel: 1,
        approvalNodeCode: '',
        approvalFlowExecutor: {
          departmentsNameStr: '',
          departmentsId: '',
          rolesNameStr: '',
          rolesId: '',
          usersNameStr: '',
          usersId: ''
        }},
      itemIndex: 0 // 流程等级索引
    }
  },
  methods: {
    // 初始化数据
    handleApprovalFlowList () {
      approvalFlow.approvalFlowList(this.$route.params.objectId).then((res) => {
        this.updateForm = res.data.approvalNode
        if (res.data.approvalFlowList !== undefined || res.data.approvalFlowList.length > 0) {
          this.processList = res.data.approvalFlowList
        }
      })
    },
    handleSelectBtn (item, index) {
      this.itemIndex = index
      this.dialogVisible = true
      this.processList[this.itemIndex].flowLevel = index + 1
      this.processList[this.itemIndex].approvalNodeCode = this.updateForm.code
      console.log(this.processList[index])
      this.setDefaultChecked()
    },
    handleTabClick: function () {
      console.log('调用更改选项卡方法')
      // 同步选项卡到表单（curItem）
      this.tabToForm()
      // 更改选项卡后更改选项卡显示项
      this.setDefaultChecked()
      // 设置旧选项卡
      this.oldTab = this.activeTab
    },
    // 关闭弹窗
    handleClose () {
      this.dialogVisible = false
      this.$refs['updateForm'].resetFields()
    },
    handleProcess (num) {
      return toChinesNum(num)
    },
    handleAddBtn () {
      console.log(JSON.stringify(this.emptyItem))
      this.processList.push(JSON.parse(JSON.stringify(this.emptyItem)))
      // this.curItem = JSON.parse(JSON.stringify(this.emptyItem))
      console.log('增加一级后的数据' + JSON.stringify(this.processList))
    },
    tabToForm () {
      console.log('------------调用同步到表单的方法--------------')
      // 选中的Id字符串
      const checkedIds = []
      const checkedNames = []
      let keys = this.$refs['treeRef'].getCheckedKeys()
      keys.map(checkedItem => {
        checkedIds.push(checkedItem)
        // checkedNames.push(checkedItem.name)
      })
      // 将选中的id、名称同步到表单中
      if (this.oldTab === '1') { // 部门
        this.processList[this.itemIndex].approvalFlowExecutor.departmentsNameStr = checkedNames.join()
        this.processList[this.itemIndex].approvalFlowExecutor.departmentsId = checkedIds.join()
      } else if (this.oldTab === '2') { // 角色
        this.processList[this.itemIndex].approvalFlowExecutor.rolesNameStr = checkedNames.join()
        this.processList[this.itemIndex].approvalFlowExecutor.rolesId = checkedIds.join()
      } else {
        this.processList[this.itemIndex].approvalFlowExecutor.usersNameStr = checkedNames.join()
        this.processList[this.itemIndex].approvalFlowExecutor.usersId = checkedIds.join()
      }
    },
    handleCheckChange (data) {},
    // 设置默认选中状态
    setDefaultChecked (item) {
      console.log('当前item' + item)
      var ids
      // 按部门
      console.log(this.activeTab)
      if (this.activeTab === '1') {
        department.henantreeList(this.$store.getters.authUser.enterpriseId).then((res) => {
          this.data = res.data.deptTreeData
          ids = this.processList[this.itemIndex].approvalFlowExecutor.departmentsId.split(',')
          console.log(ids)
          this.checkedKeys = ids
          this.expandedKeys = ids
        })
      } else if (this.activeTab === '2') { // 按角色
        rolead.getAllRoles().then((res) => {
          this.data = res.data.roleList
          ids = this.processList[this.itemIndex].approvalFlowExecutor.rolesId.split(',')
          this.checkedKeys = ids
          this.expandedKeys = ids
        })
      } else { // 按用户
        useradmin.henanuserList({
          enterpriseId: this.$store.getters.authUser.enterpriseId,
          pageNo: this.pageNo,
          pageSize: this.pageSize,
          isDelete: 0,
          _t: new Date().getTime()
        }).then((res) => {
          ids = this.processList[this.itemIndex].approvalFlowExecutor.usersId.split(',')
          this.checkedKeys = ids
          this.expandedKeys = ids
        })
      }
    },
    // 如果用户点击选项卡保存，则将数据同步到表单中
    submitTab () {
      this.tabToForm()
      // console.log(this.itemIndex)
      // this.$set(this.processList, this.itemIndex, this.curItem)
      // console.log('当前索引：' + this.itemIndex + ',当前索引所对应的数据：' + JSON.stringify(this.curItem))
      // console.log('当前表单数据：' + JSON.stringify(this.processList))
    },
    // 表单提交
    submit () {
      approvalFlow.approvalFlowListUpdate(this.processList)
    }
  },
  mounted () {
    this.handleApprovalFlowList()
  }
}
</script>

<style scoped>
  .process {
    /*padding: 12px;*/
    /*box-sizing: border-box;*/
    border: 1px solid #dedede;
    border-radius: 10px;
    padding-bottom: 30px;
    margin-bottom: 30px;
  }

  .process .top {
    background-color: #f6f6f6;
    border-top-right-radius: 10px;
    border-top-left-radius: 10px;
    padding: 6px 20px;
    box-sizing: border-box;
    vertical-align: middle;
  }

  .process .content {
    padding-right: 20px;
    padding-left: 20px;
  }

  .top_index {
    display: inline-block;
    width: 16px;
    height: 16px;
    line-height: 16px;
    border-radius: 50%;
    background: #acacac;
    color: white;
    text-align: center;
    vertical-align: middle;
  }

  .top_title {
    color: #acacac;
    vertical-align: middle;
  }

  .top_col {
    height: 40px;
    line-height: 40px;
  }

  .selectbtn {
    border: 1px solid #3f63f6;
    height: 32px;
    padding: 7px 16px;
    float: right;
    color: #3f63f6;
    margin-top: 4px;
    margin-right: 12px;
  }

  .addbutton {
    float: left;
    background: #6582f8;
    border-color: #6582f8;
    margin-bottom: 12px;
  }

  .addbutton span {
    color: #ffffff;
  }

  .submit_btn {
    text-align: center;
  }
</style>
