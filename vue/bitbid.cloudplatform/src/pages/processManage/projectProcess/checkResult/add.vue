<template>
  <div class="cloudcontent">
    <!--面包屑-->
    <el-breadcrumb separator-class="el-icon-arrow-right" class="breadcrumb_box">
      <el-breadcrumb-item :to="{ path: '/index' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/processManage/projectProcess' }">项目进度管理</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/processManage/projectProcess/project' }">项目管理</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/processManage/projectProcess/project/process' }">流程管理</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/processManage/projectProcess/project/process', query: {type: 'pre_file'} }">资格审查结果</el-breadcrumb-item>
      <el-breadcrumb-item class="active_bread">添加评标专家</el-breadcrumb-item>
    </el-breadcrumb>
    <div class="main">
      <el-form :model="updateForm" :rules="rules" ref="updateForm" :validate-on-rule-change="true">
        <el-row>
          <el-col :span="12">
            <el-form-item label="项目编号：">
              <el-input v-model="bidSection.tenderProjectCode" disabled></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="项目名称：">
              <el-input v-model="bidSection.tenderProjectName" disabled></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="标段编号：">
              <el-input v-model="bidSection.bidSectionCode" disabled></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="标段名称：">
              <el-input v-model="bidSection.bidSectionName" disabled></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="专家姓名："  prop="expertName">
              <el-input v-model="updateForm.expertName"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="身份证号码："  prop="idCard">
              <el-input v-model="updateForm.idCard"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="专业类别："  prop="expertType" class="bitianicon">
              <el-button
                class="addbtn"
                @click="addSelectClick">
                + 新增
              </el-button>
              <el-table
                :data="expertTypeList"
                :show-header="false"
                border
                style="width: 100%" header-cell-class-name="tableheader">
                <el-table-column
                  prop="professional"
                  label="专业">
                  <template slot-scope="scope">
                    <el-cascader class=""
                                 expand-trigger="hover"
                                 :options="expertTypeOptions"
                                 v-model="scope.row.selectexpertTypeOptions">
                    </el-cascader>
                  </template>
                </el-table-column>
                <el-table-column
                  label="操作" align="center" width="60">
                  <template slot-scope="scope">
                    <el-button type="text" size="small" @click="delExpertTypeBtn(scope.$index)">删除</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="联系方式："  prop="mobilePhone">
              <el-input v-model="updateForm.mobilePhone"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="电子邮箱："  prop="email">
              <el-input v-model="updateForm.email"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="标段信息：" class="bitianicon">
              <el-table
                :data="bidSectionList"
                border
                style="width: 100%" header-cell-class-name="tableheader"
                @selection-change="handleSelectionChange">
                <el-table-column
                  type="selection"
                  width="50">
                </el-table-column>
                <el-table-column
                  prop="bidSectionCode"
                  label="标段编号"
                  show-overflow-tooltip>
                </el-table-column>
                <el-table-column
                  prop="bidSectionName"
                  label="标段名称"
                  show-overflow-tooltip>
                </el-table-column>
              </el-table>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item class="submit-radio">
          <el-button type="primary" @click="submit('updateForm', 1)" :loading="isSubmiting">提交</el-button>
          <el-button type="primary" @click="submit('updateForm', 0)" :loading="isSubmiting">保存</el-button>
          <el-button class="cancal" @click="close">取消</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>
<script>
import * as expertClassify from '@/assets/js/expertClassify'
import {bidSection, expert} from '@/api/cloudplatform'
export default {
  components: {
  },
  name: '',
  data () {
    return {
      expertTypeOptions: expertClassify.expertClassify,
      expertTypeList: [],
      isSubmiting: false,
      updateForm: {},
      rules: {
        expertName: {required: true, message: '请填写专家姓名', trigger: 'blur'}
      },
      bidSectionList: [],
      bidSection: {}
    }
  },
  methods: {
    // 专业分类
    // 新增
    addSelectClick () {
      let obj = {
      }
      this.expertTypeList.push(obj)
    },
    delExpertTypeBtn (index) {
      this.expertTypeList.splice(index, 1)
    },
    handleSelectionChange (val) {
      this.updateForm.bidSectionList = val
    },
    /**
     * 保存专业信息
     */
    saveClassify () {
      this.updateForm.expertClassifyList = this.expertTypeList.map(item => { return item })
      if (this.expertTypeList.length) {
        for (let i = 0; i < this.expertTypeList.length; i++) {
          this.updateForm.expertClassifyList[i].firstClassify = this.expertTypeList[i].selectexpertTypeOptions[0]
          this.updateForm.expertClassifyList[i].secondClassify = this.expertTypeList[i].selectexpertTypeOptions[1]
          this.updateForm.expertClassifyList[i].thirdClassify = this.expertTypeList[i].selectexpertTypeOptions[2]
          this.updateForm.expertClassifyList[i].fourClassify = this.expertTypeList[i].selectexpertTypeOptions[3]
        }
      }
    },
    /** 校验是否存在重复的专业 */
    checkClassify () {
      // 将专业放入classifyData数组中，每次校验最新录入的一个专业是否与以上录入的重复
      // 若重复，则不可新增专业，且提示“存在重复的专业,请移除重复专业!”
      this.classifyData = []
      if (this.expertTypeList.length > 0) {
        for (let i = 0; i < this.expertTypeList.length; i++) {
          if (this.expertTypeList[i].selectexpertTypeOptions) {
            let classify = {
              selectexpertTypeOptions: this.expertTypeList[i].selectexpertTypeOptions
            }
            this.classifyData.push(classify)
          }
        }
      }
      if (this.classifyData.length > 0) {
        let length = this.classifyData.length
        for (let i = 0; i < length - 1; i++) {
          if (this.classifyData[i].selectexpertTypeOptions[0] === this.classifyData[length - 1].selectexpertTypeOptions[0] &&
            this.classifyData[i].selectexpertTypeOptions[1] === this.classifyData[length - 1].selectexpertTypeOptions[1] &&
            this.classifyData[i].selectexpertTypeOptions[2] === this.classifyData[length - 1].selectexpertTypeOptions[2] &&
            this.classifyData[i].selectexpertTypeOptions[3] === this.classifyData[length - 1].selectexpertTypeOptions[3]) {
            this.$message.error('存在重复的专业,请移除重复专业!')
            return false
          }
        }
      }
      return true
    },
    // 提交/保存
    submit (form, status) {
      if (!this.expertTypeList || this.expertTypeList.length < 1) {
        this.$message({
          message: '请添加专业类别！',
          type: 'warning'
        })
        return
      }
      if (!this.updateForm.bidSectionList || this.updateForm.bidSectionList.length < 1) {
        this.$message({
          message: '请选择标段！',
          type: 'warning'
        })
        return
      }
      if (this.checkClassify()) {
        this.saveClassify()
        this.$refs[form].validate((vaild) => {
          if (vaild) {
            this.isSubmiting = true
            if (Object.is(status, 0)) {
              this.updateForm.auditStatus = 0
            } else if (Object.is(status, 1)) {
              console.log('判断是否需要审批：审批：1 不审批：4')
              this.updateForm.auditStatus = 4
              this.updateForm.submitTime = new Date().getTime()
            }
            this.updateForm.tenderSystemCode = this.$route.query.tenderSystemCode
            this.updateForm.sectionSystemCode = this.$route.query.sectionSystemCode
            this.updateForm.type = 1
            expert.update(this.updateForm).then(() => {
              this.isSubmiting = false
              this.$router.go(-1)
            })
          } else {
            return false
          }
        })
      }
    },
    // 取消
    close () {
      this.$router.go(-1)
    },
    getBidSections () {
      let query = {
        tenderSystemCode: this.$route.query.tenderSystemCode,
        flowStatusRange: 11
      }
      bidSection.getList(query).then(res => {
        let data = res.data.bidSectionList
        if (data) {
          this.bidSectionList = data.list
        }
      })
    },
    // 查询当前标段信息
    getBidSection () {
      bidSection.getOne(this.$route.query.sectionSystemCode).then(res => {
        this.bidSection = res.data.bidSection
      })
    }
  },
  mounted () {
    this.getBidSections()
    this.getBidSection()
  }
}
</script>

<style scoped>
  .el-cascader {
    width: 100%;
  }
  .addbtn {
    border: 1px solid #3f63f6;
    height: 32px;
    padding: 7px 12px;
    margin-bottom: 12px;
  }
</style>
