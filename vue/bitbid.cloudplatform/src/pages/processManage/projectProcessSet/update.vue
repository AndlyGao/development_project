<template>
  <div class="cloudcontent" id="cloud_processSet">
    <!--面包屑-->
    <el-breadcrumb separator-class="el-icon-arrow-right" class="breadcrumb_box">
      <el-breadcrumb-item :to="{ path: '/index' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/processManage/proProcessSet' }">项目流程配置管理</el-breadcrumb-item>
      <el-breadcrumb-item class="active_bread">流程管理</el-breadcrumb-item>
    </el-breadcrumb>
    <!--面包屑-->
    <div class="main">
      <el-form :model="updateForm" :validate-on-rule-change="true">
        <el-row>
          <el-col :span="24">
            <el-form-item label="项目流程名称：">
              <span>{{updateForm.processName}}</span>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="项目流程："  prop="tenderProjectCode">
              <el-table
                :data="processData"
                border
                header-cell-class-name="tableheader">
                <el-table-column
                  type="index"
                  label="顺序"
                  width="60"
                  align="center">
                </el-table-column>
                <el-table-column
                  prop="name"
                  label="流程节点"
                  show-overflow-tooltip>
                </el-table-column>
                <el-table-column
                  label="操作"
                  align="center"
                  width="200">
                  <template slot-scope="scope">
                    <el-radio v-model="scope.row.check" label="0">启用</el-radio>
                    <el-radio v-model="scope.row.check" label="1" v-if="scope.$index !== 0 && scope.$index !== processData.length - 1">禁用</el-radio>
                  </template>
                </el-table-column>
              </el-table>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item class="submit-radio">
          <el-button type="primary" @click="handleClickEvent('addForm','submit')">确定</el-button>
          <el-button type="primary" @click="handleClickEvent('addForm','cancel')">取消</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>
<script>
import {projectFlow} from '@/api/cloudplatform/index'
export default {
  data () {
    return {
      updateForm: {},
      processData: [
        {
          check: '',
          name: '资格预审公告'
        },
        {
          check: '',
          name: '资格预审文件'
        },
        {
          check: '',
          name: '响应资格预审'
        },
        {
          check: '',
          name: '资格审查结果'
        },
        {
          check: '',
          name: '招标文件'
        },
        {
          check: '',
          name: '响应招标'
        },
        {
          check: '',
          name: '开标评标'
        },
        {
          check: '',
          name: '废标结果公告'
        },
        {
          check: '',
          name: '定标结果'
        }
      ],
      projectFlowInfo: ''
    }
  },
  created () {
  },
  methods: {
    process () {
      switch (Number(this.$route.query.flowType)) {
        case 1:
          this.$set(this.updateForm, 'processName', '资格预审流程')
          break
        case 2:
          this.$set(this.updateForm, 'processName', '资格后审流程')
          break
        case 3:
          this.$set(this.updateForm, 'processName', '邀请招标流程')
          break
        case 4:
          this.$set(this.updateForm, 'processName', '竞争性谈判流程')
          break
        case 5:
          this.$set(this.updateForm, 'processName', '竞争性磋商流程')
          break
        case 6:
          this.$set(this.updateForm, 'processName', '单一来源流程')
          break
        case 7:
          this.$set(this.updateForm, 'processName', '询价流程')
          break
      }
      if (Number(this.$route.query.flowType) !== 1) {
        this.processData[0].name = '招标公告'
        if (Number(this.$route.query.flowType) === 3 || Number(this.$route.query.flowType) === 6) {
          this.processData[0].name = '招标邀请'
        }
        this.processData.splice(1, 3)
      }
      this.getProcessData()
    },
    /** 获取所以流程的默认使用状态 */
    getProcessData () {
      projectFlow.getList({
        enterpriseId: this.$store.getters.authUser.enterpriseId,
        scopeType: 1,
        flowType: this.$route.query.flowType
      }).then((res) => {
        if (res.data.projectFlowList) {
          for (let i = 0; i < this.processData.length; i++) {
            this.processData[i].check = res.data.projectFlowList[i]
          }
        }
        if (res.data.projectFlow) {
          this.projectFlowInfo = res.data.projectFlow
        }
        /* if (Number(res.data.projectFlowList[res.data.projectFlowList.length - 1]) !== 0) {
          this.projectFlowId = res.data.projectFlowList[res.data.projectFlowList.length - 1]
        } */
      })
    },
    /** 获取所有流程的使用状态 */
    getFlowInfo () {
      let str = ''
      for (let i = 0; i < this.processData.length; i++) {
        str += this.processData[i].check
        str += ','
        if (i === this.processData.length - 1) {
          str += 0 // 默认加入一个完结状态
        }
      }
      return str
    },
    /* 按钮点击事件：确定、取消 */
    handleClickEvent (formName, type) {
      if (Object.is(type, 'submit')) {
        let projectFlowInfo = {
          objectId: this.projectFlowInfo.objectId ? this.projectFlowInfo.objectId : null,
          enterpriseId: this.$store.getters.authUser.enterpriseId,
          scopeType: 1,
          flowInfo: this.getFlowInfo(),
          flowType: this.$route.query.flowType
        }
        projectFlow.save(projectFlowInfo).then((res) => {
          if (res.data.resCode === '0000') {
            this.$router.push({path: '/processManage/proProcessSet'})
          }
        })
      } else if (Object.is(type, 'cancel')) {
        this.$router.push({path: '/processManage/proProcessSet'})
      }
    }
  },
  mounted () {
    this.process()
  }
}
</script>
<style lang="less">
  #cloud_processSet {
    .el-table{
      width: 70%;
    }
  }
</style>
