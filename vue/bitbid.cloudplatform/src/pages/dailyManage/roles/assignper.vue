<template>
  <div class="cloudcontent" id="homejuris">
    <!--面包屑-->
    <el-breadcrumb separator-class="el-icon-arrow-right" class="breadcrumb_box">
      <el-breadcrumb-item :to="{ path: '/index' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/dailyManage/roles' }">角色管理</el-breadcrumb-item>
      <el-breadcrumb-item class="active_bread">分配权限</el-breadcrumb-item>
    </el-breadcrumb>
    <!--面包屑-->
    <div class="main">
      <div class="penserviceconbox">
        <div class="penserviceconconbox">
          <!--模块内容-->
          <div class="typebigbox">
            <el-row class="tac">
              <el-col :span="24">
                <!--<el-menu-->
                  <!--:default-active="activeIndex"-->
                  <!--class="el-menu-vertical-demo typebox">-->
                  <!--<el-menu-item :index="activeIndex+''" @click="changeModule(management.objectId,activeIndex)" v-for="(management,activeIndex) in managementList" :key="activeIndex">-->
                    <!--<span slot="title">{{management.serviceName}}</span>-->
                  <!--</el-menu-item>-->
                <!--</el-menu>-->
                <el-menu
                  default-active="1"
                  class="el-menu-vertical-demo"
                  @open="handleOpen"
                  @close="handleClose">
                  <el-submenu index="1">
                    <template slot="title">
                      <span>日常管理</span>
                    </template>
                    <el-menu-item-group>
                      <el-menu-item index="1-1">审批配置管理</el-menu-item>
                    </el-menu-item-group>
                  </el-submenu>
                  <el-submenu index="2">
                    <template slot="title">
                      <span>业务辅助</span>
                    </template>
                    <el-menu-item-group>
                      <el-menu-item index="2-1">档案管理</el-menu-item>
                      <el-menu-item index="2-2">业主管理</el-menu-item>
                      <el-menu-item index="2-3">专家管理</el-menu-item>
                      <el-menu-item index="2-4">收支管理</el-menu-item>
                    </el-menu-item-group>
                  </el-submenu>
                  <el-submenu index="3">
                    <template slot="title">
                      <span>系统管理</span>
                    </template>
                    <el-menu-item-group>
                      <el-menu-item index="3-1">资源分类</el-menu-item>
                    </el-menu-item-group>
                  </el-submenu>
                  <el-submenu index="4">
                    <template slot="title">
                      <span>招标流程管理</span>
                    </template>
                    <el-menu-item-group>
                      <el-menu-item index="4-1">登记项目信息</el-menu-item>
                      <el-menu-item index="4-2">代理分配项目</el-menu-item>
                      <el-menu-item index="4-3">项目进度管理</el-menu-item>
                      <el-menu-item index="4-4">项目流程配置管理</el-menu-item>
                      <el-menu-item index="4-5">项目流程配置管理</el-menu-item>
                    </el-menu-item-group>
                  </el-submenu>
                </el-menu>
              </el-col>
            </el-row>
          </div>
          <!--模块内容-->
          <publicjuris :moduleId="clickObjectid" @changeResultDepIds="changeResultDepIds" @changeResultPerIds="changeResultPerIds" @clearModulePerAndDepIds="clearModulePerAndDepIds"
                       :treeNodes="moduleDepTreeDatas" :permissionIds="modulePermissions"></publicjuris>
          <!--按钮-->
          <div class="jursdbtn">
            <el-button @click="submitData" type="primary">保 存</el-button>
            <el-button @click="cancel">取 消</el-button>
          </div>
          <!--按钮-->
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import publicjuris from './public-juris.vue'
import {rolead} from '../../../api/index'
export default {
  components: {
    publicjuris
  },
  data () {
    return {
      activeIndex: '0',
      // 操作权限多选
      clickObjectid: '',
      isIndeterminate: true,
      defaultProps: {
        children: 'children',
        label: 'name'
      },
      managementList: [],
      limitsJurs: [],
      flag: false,
      // 角色权限分配中的启用禁用参数 userId
      userIds: this.$store.getters.authUser.userId,
      // 定义启用禁用初始状态
      dishadeStaus: true,
      dishadnames: '启用',
      roleId: '',
      result: {}
    }
  },
  computed: {
    // moduleDepTreeDatas () {
    //   this.result[this.clickObjectid] = this.result[this.clickObjectid] || {}
    //   return this.result[this.clickObjectid]['depIds'] || []
    // },
    // modulePermissions () {
    //   this.result[this.clickObjectid] = this.result[this.clickObjectid] || {}
    //   return this.result[this.clickObjectid]['permissionIds'] || []
    // }
  },
  methods: {
    changeModule (moduleId, activeIndex) {
      this.activeIndex = activeIndex + ''
      this.clickObjectid = moduleId
    },
    changeResultDepIds (evtValue, moduleId) {
      this.result[moduleId] = this.result[moduleId] || {}
      this.result[moduleId]['depIds'] = evtValue
    },
    changeResultPerIds (evtValue, moduleId) {
      this.result[moduleId] = this.result[moduleId] || {}
      this.result[moduleId]['permissionIds'] = evtValue
    },
    clearModulePerAndDepIds (moduleId) {
      this.result[moduleId] = this.result[moduleId] || {}
      this.result[moduleId]['permissionIds'] = []
      this.result[moduleId]['depIds'] = []
    },
    submitData () {
      this.$confirm('确认保存吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'success'
      }).then(() => {
        let resultMap = this.result
        let resultRoleId = this.roleId
        let resultData = []
        for (var key in resultMap) {
          if (resultMap[key].permissionIds.length) {
            let jurisdiction = resultMap[key].depIds.length ? resultMap[key].depIds.reduce((a, b) => { return a + ',' + b }) : ''
            resultMap[key].permissionIds.forEach(element => {
              if (element) {
                let acrObject = {
                  roleId: resultRoleId,
                  jurisdiction: jurisdiction,
                  moduleMark: element,
                  moduleId: key
                }
                resultData.push(acrObject)
              }
            })
          }
        }
        if (!resultData.length) {
          resultData[0] = {'roleId': resultRoleId}
        }
        rolead.addjuris(resultData).then((res) => {
          this.$router.push({path: '/dailyManage/roles'})
        })
      }).catch(() => {
        return false
      })
    },
    // 取消
    cancel () {
      this.$router.push({path: '/dailyManage/roles'})
    },
    // 初始化列表展示
    getData () {
      this.roleId = this.$route.params.objectId
      this.$nextTick(function () {
        rolead.jurisList({
          roleId: this.roleId,
          isDelete: 0,
          enterpriseId: this.$store.getters.authUser.enterpriseId,
          _t: new Date().getTime()
        }).then((res) => {
          // 侧边列表数据
          this.managementList = res.data.managementList
          console.log(this.managementList)
          // 已经选中的数据
          this.result = res.data.resultMap
          // 第一个选择的模块
          this.clickObjectid = res.data.firstModuleId
          this.changeModule(this.clickObjectid, 0)
        })
      })
    }
  },
  mounted () {
    this.getData()
  }
}
</script>
<style lang="less">
  #homejuris{
    .penserviceconbox{
      padding: 0 20px;
    }
    .typebigbox{
      display: inline-block;
      width: 240px;
      border: 1px solid #f4f4f4;
      vertical-align: top;
    }
    .typebox{
      text-align: center;
    }
    .operation_jurisdiction{
      display: inline-block;
      vertical-align: top;
      margin-left: 20px;
      border: 1px solid #eeeeee;
      width: 580px;
    }
    .operation_juristitle{
      height: 57px;
      background: #f5f7f8;
      font-size: 14px;
      line-height: 57px;
      border-bottom: 1px solid #eeeeee;
      padding: 0 20px;
      box-sizing: border-box;
      color:#333333;
    }
    .operation_juriscon{
      padding:20px;
    }
    .datarange_bigbox{
      display: inline-block;
      vertical-align: top;
      margin-left: 20px;
      border: 1px solid #eeeeee;
    }
    .operation_datarangetitle{
      width:280px;
      height: 57px;
      background: #f5f7f8;
      font-size: 14px;
      line-height: 57px;
      padding: 0 20px;
      border-bottom: 1px solid #eeeeee;
      box-sizing: border-box;
      color:#333333;
    }
    .operation_datarangecon{
      padding:20px;
    }
    .jursdbtn{
      text-align: center;
      margin-top: 40px;
    }
    .el-button--primary {
      color: #fff;
      background-color: #3f63f6;
      border-color: #3f63f6;
    }
    .disableenable{
      display: inline-block;
      vertical-align: top;
      float: right;
      cursor: pointer;
    }
  }
</style>
