<template>
  <div class="cloudcontent" id="cloud_ProjectDetail">
    <!--面包屑-->
    <el-breadcrumb separator-class="el-icon-arrow-right" class="breadcrumb_box">
      <el-breadcrumb-item :to="{ path: '/index' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/processManage/agentProject' }">代理分配项目</el-breadcrumb-item>
      <el-breadcrumb-item class="active_bread">查看项目负责人</el-breadcrumb-item>
    </el-breadcrumb>
    <!--面包屑-->
    <div class="main">
      <el-form :model="addForm" :rules="rules" ref="addForm" :validate-on-rule-change="true">
        <el-row>
          <el-col :span="12">
            <el-form-item label="项目名称：">
              <span>{{addForm.tenderProjectName}}</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="项目编号：">
              <span>{{addForm.tenderProjectCode}}</span>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="项目行业分类：">
              <span>{{addForm.industryTypeStr}}</span>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="项目地址：">
              <span>{{addForm.tenderProjectAddressStr}}</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="详细地址：">
              <span>{{addForm.address}}</span>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="招标人：">
              <span>{{addForm.tendererName}}</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="招标人单位地址：">
              <span>{{addForm.tendererUnitAddress}}</span>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="联系人：">
              <span>{{addForm.contactName}}</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系方式：">
              <span>{{addForm.contactNumber}}</span>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="邮箱：">
              <span>{{addForm.contactEmail}}</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="代理合同：">
              <span>{{addForm.agencyContractName}}</span>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24" class="ueditor_box">
            <el-form-item label="项目说明：">
              <editor ref="ueditor" class="ueditor" :editread="editread" :content="content"></editor>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24" class="ueditor_box">
            <el-form-item label="代理机构项目负责人：">
              <template>
                <el-table
                  :data="tableData"
                  border
                  style="width: 100%" header-cell-class-name="tableheader">
                  <el-table-column
                    prop="userName"
                    label="姓名"
                    show-overflow-tooltip>
                  </el-table-column>
                  <el-table-column
                    prop="positionType"
                    label="职责分工"
                    :formatter="filterPositionType"
                    show-overflow-tooltip>
                  </el-table-column>
                </el-table>
              </template>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item class="submit-radio">
          <el-button type="primary" @click="handleClickEvent">返回</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>
<script>
import * as region from '@/assets/js/region'
import * as industry from '@/assets/js/industry_two'
import editor from '@/components/ueditor/ueditor.vue'
import {tenderProject} from '@/api/cloudplatform/index'
export default {
  components: {
    editor
  },
  data () {
    return {
      addForm: {},
      rules: {
        tenderProjectName: [
          { required: true, message: '项目名称不能为空', trigger: 'blur' }
        ],
        tenderProjectCode: [
          { required: true, message: '项目编号不能为空', trigger: 'blur' }
        ],
        name: [
          { required: true, message: '项目编号不能为空', trigger: 'blur' }
        ],
        uscCode: [
          { required: true, message: '项目编号不能为空', trigger: 'blur' }
        ]
      },
      // 地址三级联动
      addressOptions: region.CityInfo,
      selectedcityOptions: [],
      // 行业三级联动
      industryOptions: industry.industry,
      selectedIndustryOptions: [],
      // 富文本
      editread: true,
      content: '',
      // 代理项目负责人
      tableData: []
    }
  },
  created () {
    this.getChooseTenderProjectInfo()
  },
  methods: {
    /** 职位类型:1.项目经理;2.项目助理 */
    filterPositionType (row) {
      if (row.positionType === 1) {
        return '项目经理'
      } else if (row.positionType === 2) {
        return '项目助理'
      }
    },
    /** 获取当前招标项目信息 */
    getChooseTenderProjectInfo () {
      tenderProject.getOne(this.$route.query.code).then((res) => {
        if (res.data.tenderProject) {
          this.addForm = res.data.tenderProject
          // 获取项目扩展信息
          this.addForm.contactName = this.addForm.tenderExpandsInfo.contactName
          this.addForm.contactNumber = this.addForm.tenderExpandsInfo.contactNumber
          this.addForm.address = this.addForm.tenderExpandsInfo.address
          this.addForm.contactEmail = this.addForm.tenderExpandsInfo.contactEmail
          this.addForm.contactAddress = this.addForm.tenderExpandsInfo.contactAddress
          this.addForm.tenderProjectAddressStr = this.getTenderExpandsInfo(this.addForm.tenderExpandsInfo, 'address')
          this.addForm.industryTypeStr = this.getTenderExpandsInfo(this.addForm.tenderExpandsInfo, 'industry')
          // 获取文本编辑器的内容
          this.$refs.ueditor.setContent(this.fileContent = this.addForm.projectContent)
          // 获取项目负责人信息
          if (res.data.projectLeaderList.length > 0) {
            this.tableData = res.data.projectLeaderList
          }
        }
      })
    },
    /** 获取招标项目扩展信息 */
    getTenderExpandsInfo (tenderExpandsInfo, type) {
      let str = ''
      if (Object.is(type, 'address')) {
        let provinceId = this.getLabelName(region.CityInfo, tenderExpandsInfo.provinceId)
        let cityId = this.getLabelName(region.CityInfo, tenderExpandsInfo.cityId)
        let countyId = this.getLabelName(region.CityInfo, tenderExpandsInfo.countyId)
        if (provinceId.node) {
          str += `${provinceId.node.label}`
        }
        if (cityId.node) {
          str += `${cityId.node.label}`
        }
        if (countyId.node) {
          str += `${countyId.node.label}`
        }
      } else if (Object.is(type, 'industry')) {
        let industryTypeFirst = this.getLabelName(industry.industry, tenderExpandsInfo.industryTypeFirst)
        let industryTypeSecond = this.getLabelName(industry.industry, tenderExpandsInfo.industryTypeSecond)
        if (industryTypeFirst.node) {
          str += `${industryTypeFirst.node.label}`
        }
        if (industryTypeSecond.node) {
          str += `${industryTypeSecond.node.label}`
        }
      }
      return str
    },
    /** treeData [原始数据集]， key 传入的key值 */
    getLabelName (treeData, key) {
      let parentNode = null
      let node = null
      function getTreeDeepArr (treeData, key) {
        for (let i = 0; i < treeData.length; i++) {
          // 1.如果没有子节点 beark
          if (node) {
            break
          }
          // 定义需要操作的数据
          let obj = treeData[i]
          // 没有节点就下一个
          if (!obj || !obj.value) {
            continue
          }
          // 2.有节点就继续找，一直递归
          if (obj.value === key) {
            node = obj
            break
          } else {
            // 3.如果有子节点就继续找
            if (obj.children) {
              // 4.递归前，记录当前节点，作为parentNode
              parentNode = obj
              // 递归
              getTreeDeepArr(obj.children, key)
            } else {
              // 跳出当前递归
              continue
            }
          }
        }
        // 如果没有找到父节点，置为null
        if (!node) {
          parentNode = null
        }
        // 返回结果
        return {
          node: node,
          parentNode: parentNode
        }
      }
      return getTreeDeepArr(treeData, key)
    },
    handleClickEvent () {
      this.$router.push({path: '/processManage/agentProject'})
    }
  }
}
</script>
<style lang="less">
  #cloud_ProjectDetail {
  }
</style>
