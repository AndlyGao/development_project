<template>
  <div class="cloudcontent" id="cloud_ProjectAgent">
    <!--面包屑-->
    <el-breadcrumb separator-class="el-icon-arrow-right" class="breadcrumb_box">
      <el-breadcrumb-item :to="{ path: '/index' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/processManage/agentProject' }">代理分配项目</el-breadcrumb-item>
      <el-breadcrumb-item class="active_bread">分配项目负责人</el-breadcrumb-item>
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
            <el-form-item label="项目地址：" >
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
                <el-button class="addbtn" @click="addButton">+ 新增</el-button>
                <el-table
                  :data="tableData"
                  border
                  style="width: 100%" header-cell-class-name="tableheader">
                  <el-table-column
                    prop="userName"
                    label="姓名"
                    show-overflow-tooltip>
                    <template slot-scope="scope">
                      <el-input v-model="scope.row.userName" placeholder="请输入内容"></el-input>
                      <el-button type="primary" style="position: absolute; right: 10px; top: 12px;" @click="getEmployeeList(scope.$index)">选择</el-button>
                    </template>
                  </el-table-column>
                  <el-table-column
                    prop="positionType"
                    label="职责分工"
                    show-overflow-tooltip>
                    <template slot-scope="scope">
                      <el-select v-model="scope.row.positionType" placeholder="请选择" @change="changePositionType(scope.$index, scope.row.positionType)">
                        <el-option
                          v-for="item in options"
                          :key="item.value"
                          :label="item.label"
                          :value="item.value">
                        </el-option>
                      </el-select>
                    </template>
                  </el-table-column>
                  <el-table-column
                    label="操作" align="center" width="120">
                    <template slot-scope="scope">
                      <el-button type="text" size="small" @click="delButton(scope.$index)" v-if="scope.$index !== 0">删除</el-button>
                    </template>
                  </el-table-column>
                </el-table>
              </template>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item class="submit-radio">
          <el-button type="primary" @click="handleClickEvent('tableData','submit')">提交</el-button>
          <el-button type="primary" @click="handleClickEvent('tableData','save')">保存</el-button>
          <el-button class="cancal" @click="handleClickEvent('tableData','cancel')">取消</el-button>
        </el-form-item>
      </el-form>
      <el-dialog title="选择项目负责人" :visible.sync="dialogTableVisible"  class="bitianicon">
        <div class="lefttree">
          <el-tree :data="dataTree" :props="defaultProps" :highlight-current="true" @node-click="handleNodeClick" default-expand-all
                   :expand-on-click-node="false" >
           <span class="custom-tree-node" slot-scope="{ node, data }">
              <span :title="node.label">{{ node.label }}</span>
            </span>
          </el-tree>
        </div>
        <div class="rightselect">
          <el-table
            :data="selectData"
            border
            style="width: 100%" header-cell-class-name="tableheader">
            <el-table-column
              width="50"
              align="center"
              label="选择"
              prop="name">
              <template slot-scope="scope">
                <el-radio v-model="radio" :label="scope.$index" @change="select(scope.row)">&nbsp;</el-radio>
              </template>
            </el-table-column>
            <el-table-column
              prop="name"
              label="名称"
              show-overflow-tooltip>
            </el-table-column>
            <el-table-column
              prop="dept.name"
              label="部门"
              show-overflow-tooltip>
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
      </el-dialog>
    </div>
  </div>
</template>
<script>
import * as region from '@/assets/js/region'
import * as industry from '@/assets/js/industry_two'
import editor from '@/components/ueditor/ueditor.vue'
import {tenderProject, projectLeader} from '@/api/cloudplatform/index'
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
      tableData: [],
      input: '',
      options: [{
        value: 1,
        label: '项目经理'
      }, {
        value: 2,
        label: '项目助理'
      }],
      value: '1',
      dialogTableVisible: false,
      // 弹窗企业树形
      dataTree: [],
      defaultProps: {
        children: 'children',
        label: 'label'
      },
      //  弹窗选择项目负责人
      selectData: [],
      radio: '',
      // 当前页
      currentPage: 1,
      pageNo: 0,
      total: 100, // 总条数
      pageSize: 10, // 每页展示条数
      deptId: '', // 选中的部门id
      clickRowIndex: 0 // 为哪一行信息选择项目负责人
    }
  },
  created () {
    this.getChooseTenderProjectInfo()
  },
  methods: {
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
    addButton () {
      this.tableData.push(
        {
          userName: '',
          userId: '',
          positionType: 1
        }
      )
    },
    /** 修改职责分工 */
    changePositionType (index, value) {
      this.tableData[index].positionType = value
    },
    // 删除项目负责人
    delButton (index) {
      this.$confirm('确认删除吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'error'
      }).then(() => {
        this.tableData.splice(index, 1)
      }).catch(() => {
        return false
      })
    },
    /** 分页该企业下的用户信息 */
    getUserList () {
      projectLeader.getUsersByDept({
        pageNo: this.pageNo,
        pageSize: this.pageSize,
        enterpriseId: this.$store.getters.authUser.enterpriseId,
        objectId: this.deptId || null
      }).then((res) => {
        this.selectData = res.data.userList.list
        this.total = res.data.userList.total
      })
    },
    // 表单分页
    handleCurrentChange (nowNum) {
      this.currentPage = nowNum
      this.pageNo = (nowNum - 1) * this.pageSize
      this.getUserList(parseInt(this.pageNo), parseInt(this.pageSize))
    },
    handleCurrentNext (nowNum) {
      this.currentPage = nowNum
      this.pageNo = (nowNum - 1) * this.pageSize
      this.getUserList(parseInt(this.pageNo), parseInt(this.pageSize))
    },
    // select
    select (row) {
      this.radio = ''
      if (this.tableData.length > 0) {
        if (!this.checkUserIsChoosed(row.objectId)) {
          this.$message.warning('该用户已被选择，不能重复添加！')
          return false
        } else {
          this.tableData[this.clickRowIndex].userName = row.name
          this.tableData[this.clickRowIndex].userId = row.objectId
          this.tableData[this.clickRowIndex].positionType = 1
          this.dialogTableVisible = false
        }
      } else {
        this.tableData[this.clickRowIndex].userName = row.name
        this.tableData[this.clickRowIndex].userId = row.objectId
        this.tableData[this.clickRowIndex].positionType = 1
        this.dialogTableVisible = false
      }
    },
    /** 判断该用户是否已经被选择 */
    checkUserIsChoosed (objectId) {
      let i = 0
      for (; i < this.tableData.length; i++) {
        if (this.tableData[i].userId === objectId) {
          return false
        }
      }
      if (i === this.tableData.length) {
        return true
      }
    },
    /** 选择项目负责人 */
    handleNodeClick (data) {
      this.deptId = data.objectId
      this.getUserList()
    },
    /** 获取该企业下所有的员工信息 */
    getEmployeeList (index) {
      this.clickRowIndex = index
      projectLeader.getDeptsByEnterpriesId(this.$store.getters.authUser.enterpriseId).then((res) => {
        this.dataTree = this.getTreeNode(res.data.deptList)
        this.selectData = res.data.userList.list
        this.total = res.data.userList.total
        this.dialogTableVisible = true
      })
    },
    /** 遍历树 */
    getTreeNode (data) {
      let parentDepartmentId = 0
      for (let i = 0; i < data.length; i++) {
        data[i].label = data[i].name
        if (data[i].children.length !== 0) {
          parentDepartmentId = data[i].objectId
          for (let j = 0; j < data[i].children.length; j++) {
            data[i].children[j].parentDepartmentId = parentDepartmentId
          }
          this.getTreeNode(data[i].children)
        }
      }
      return data
    },
    /** 判断是否存在没选择人员的数据 */
    checkTableData () {
      let i = 0
      for (; i < this.tableData.length; i++) {
        if (this.tableData[i].userName === '') {
          return false
        }
      }
      if (i === this.tableData.length) {
        return true
      }
    },
    /* 按钮点击事件：提交、保存、取消 */
    handleClickEvent (formName, type) {
      if (Object.is(type, 'submit') || Object.is(type, 'save')) {
        // 判断是否存在没选择人员的数据
        if (this.tableData.length === 0) {
          this.$message.warning('请添加项目负责人信息！')
          return false
        } else {
          if (!this.checkTableData()) {
            this.$message.warning('存在未录入的项目负责人信息！')
            return false
          }
        }
        let tenderProjectInfo = {}
        tenderProjectInfo.objectId = this.addForm.objectId
        tenderProjectInfo.code = this.addForm.code
        tenderProjectInfo.tenderProjectName = this.addForm.tenderProjectName
        if (Object.is(type, 'submit')) {
          tenderProjectInfo.status = 4
        } else {
          tenderProjectInfo.status = 3
        }
        this.tableData.forEach(item => {
          item.tenderSystemCode = this.addForm.code
          item.enterpriseId = this.addForm.enterpriseId
          item.departmentId = this.addForm.departmentId
          item.tenderProject = tenderProjectInfo
        })
        // 保存项目负责人信息
        projectLeader.save(this.tableData).then((res) => {
          if (res.data.resCode === '0000') {
            this.$router.push({path: '/processManage/agentProject'})
          }
        })
      } else if (Object.is(type, 'cancel')) {
        this.$router.push({path: '/processManage/agentProject'})
      }
    }
  }
}
</script>
<style lang="less">
  #cloud_ProjectAgent {
    .el-cascader{
      width: 100%;
    }
    .addbtn{
      border: 1px solid #3f63f6;
      height: 32px;
      padding: 7px 12px;
    }
    .el-table{
      margin-top: 25px;
    }
    .el-dialog{
      overflow: hidden;
      padding-bottom: 20px;
    }
    .lefttree{
      width: 30%;
      float: left;
      padding: 10px 0;
      border: 1px solid #eeeeee;
    }
    .rightselect{
      width: 67%;
      float: right;
    }
    .rightselect .el-table{
      margin-top: 0px;
    }
  }
</style>
