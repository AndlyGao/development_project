<template>
  <div class="cloudcontent" id="cloud_addsection">
    <!--面包屑-->
    <el-breadcrumb separator-class="el-icon-arrow-right" class="breadcrumb_box">
      <el-breadcrumb-item :to="{ path: '/index' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/processManage/projectProcess' }">项目进度管理</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/processManage/projectProcess/project' }">项目管理</el-breadcrumb-item>
      <el-breadcrumb-item class="active_bread">添加标段</el-breadcrumb-item>
    </el-breadcrumb>
    <!--面包屑-->
    <div class="main">
      <el-form :model="updateForm" :rules="rules" ref= "updateForm" :validate-on-rule-change="true" v-loading="loading">
        <el-row>
          <el-col :span="12">
            <el-form-item label="项目名称：">
              <span>{{updateForm.tenderProjectName}}</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="项目编号：">
              <span>{{updateForm.tenderProjectCode}}</span>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="标段名称："  prop="bidSectionName">
              <el-input v-model="updateForm.bidSectionName"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="标段编号："  prop="bidSectionCode">
              <el-input v-model="updateForm.bidSectionCode"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="标段类型："  prop="selectBidsTypeOptions">
              <el-cascader class=""
                           expand-trigger="hover"
                           :options="bidsTypeOptions"
                           v-model="updateForm.selectBidsTypeOptions"
                           @change="handleCityChange">
              </el-cascader>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="标段合同估算价(元)："  prop="contractReckonPrice">
              <el-input v-model="updateForm.contractReckonPrice"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="招标文件售价(元)：" prop="docGetFee">
              <el-input v-model="updateForm.docGetFee"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24" class="ueditor_box" >
            <el-form-item label="标段内容：" prop="sectionContent" ref="sectionContent">
              <editor ref="sectionEditor" class="ueditor" :editread="editread" :content="updateForm.sectionContent" v-model="updateForm.sectionContent"></editor>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24" class="ueditor_box">
            <el-form-item label="投标人资格条件：" prop="bidderContent" ref="bidderContent">
              <editor ref="bidderEditor" class="ueditor" :editread="editread" :content="bidderContent" v-model="updateForm.bidderContent"></editor>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item class="submit-radio">
          <el-button type="primary" @click="handleClickEvent('updateForm','submit')">提交</el-button>
          <el-button type="primary" @click="handleClickEvent('updateForm','save')">保存</el-button>
          <el-button class="cancal" @click="handleClickEvent('updateForm','cancel')">取消</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>
<script>
import editor from '@/components/ueditor/ueditor.vue'
import * as bidsType from '@/assets/js/constants'
import {tenderProject, bidSection} from '@/api/cloudplatform/index'
import {sumType} from '@/assets/js/validate'
export default {
  components: {
    editor
  },
  data () {
    // 验证数字
    let validNum = (rule, value, callback) => {
      if (!value) {
        callback()
      } else if (!sumType(value)) {
        callback(new Error('请输入数字且最多两位小数！'))
      } else {
        callback()
      }
    }
    return {
      updateForm: {
        tenderProjectName: '',
        selectBidsTypeOptions: []
      },
      rules: {
        bidSectionName: [
          { required: true, message: '标段名称不能为空', trigger: ['blur', 'change'] }
        ],
        bidSectionCode: [
          { required: true, message: '标段编号不能为空', trigger: ['blur', 'change'] }
        ],
        selectBidsTypeOptions: [
          { required: true, message: '请选择标段类型', trigger: ['blur', 'change'] }
        ],
        contractReckonPrice: [
          { required: true, message: '标段合同估算价不能为空', trigger: ['blur', 'change'] },
          {validator: validNum, trigger: ['blur', 'change']}
        ],
        docGetFee: [
          { required: true, message: '招标文件售价不能为空', trigger: ['blur', 'change'] },
          {validator: validNum, trigger: ['blur', 'change']}
        ],
        sectionContent: [
          {required: true, message: '标段内容不能为空', trigger: ['blur', 'change']}
        ],
        bidderContent: [
          { required: true, message: '投标人资格条件不能为空', trigger: ['blur', 'change'] }
        ]
      },
      // 标段类型
      bidsTypeOptions: bidsType.bidsType,
      // 富文本
      editread: false,
      sectionContent: '',
      bidderContent: '',
      loading: false
    }
  },
  watch: {},
  created () {
    this.getChooseTenderProjectInfo()
  },
  methods: {
    /** 获取当前招标项目信息 */
    getChooseTenderProjectInfo () {
      tenderProject.getBaseByCode(this.$route.query.code).then((res) => {
        if (res.data.tenderProject) {
          // 页面展示
          this.updateForm.tenderProjectName = res.data.tenderProject.tenderProjectName
          this.updateForm.tenderProjectCode = res.data.tenderProject.tenderProjectCode
          // 用于保存关联关系
          this.updateForm.enterpriseId = res.data.tenderProject.enterpriseId
          this.updateForm.departmentId = res.data.tenderProject.departmentId
          this.updateForm.tenderSystemCode = res.data.tenderProject.code
        }
      })
    },
    /** 校验富文本框内容 */
    validContent () {
      let sectionContent = this.$refs.sectionEditor.getContent()
      if (sectionContent) {
        this.$set(this.updateForm, 'sectionContent', sectionContent)
      } else {
        this.$set(this.updateForm, 'sectionContent', '')
      }
      if (sectionContent) {
        this.$refs['sectionContent'].clearValidate()
      }
      let bidderContent = this.$refs.bidderEditor.getContent()
      if (bidderContent) {
        this.$set(this.updateForm, 'bidderContent', bidderContent)
      } else {
        this.$set(this.updateForm, 'biddernContent', '')
      }
      if (bidderContent) {
        this.$refs['bidderContent'].clearValidate()
      }
    },
    /* 按钮点击事件：提交、保存、取消 */
    handleClickEvent (formName, type) {
      // this.validContent()
      if (Object.is(type, 'submit') || Object.is(type, 'save')) {
        if (!this.$refs.sectionEditor.getContent() || !this.$refs.bidderEditor.getContent()) {
          this.$message.warning('标段内容、投标人资格条件均需要进行输入！')
          return false
        }
        this.updateForm.sectionContent = this.$refs.sectionEditor.getContent()
        this.updateForm.bidderContent = this.$refs.bidderEditor.getContent()
        this.$refs[formName].validate((valid) => {
          if (valid) {
            this.loading = true
            // 获取文本编辑器中的内容
            if (this.$refs.sectionEditor) {
              this.updateForm.bidSectionContent = this.$refs.sectionEditor.getContent()
            }
            if (this.$refs.bidderEditor) {
              this.updateForm.bidQualification = this.$refs.bidderEditor.getContent()
            }
            // 根据type设置当前招标项目的状态
            if (Object.is(type, 'submit')) {
              this.updateForm.auditStatus = 4 // 暂时不考虑审批
              this.updateForm.flowStatus = 5 // 暂时不考虑审批
            } else {
              this.updateForm.auditStatus = 0
              this.updateForm.flowStatus = 1 // 未开始
            }
            bidSection.save(this.updateForm).then((res) => {
              if (res.data.resCode === '0000') {
                this.loading = false
                // this.$router.push({path: '/processManage/projectProcess/project', query: {code: this.$route.query.code}})
                this.$router.go(-1)
              }
            })
          } else {
            return false
          }
        })
      } else if (Object.is(type, 'cancel')) {
        // this.$router.push({path: '/processManage/projectProcess/project', query: {code: this.$route.query.code}})
        this.$router.go(-1)
      }
    },
    /** 标段类型的选择 */
    handleCityChange () {
      if (this.updateForm.selectBidsTypeOptions) {
        let str = ''
        for (let i = 0; i < this.updateForm.selectBidsTypeOptions.length; i++) {
          if (i === this.updateForm.selectBidsTypeOptions.length - 1) {
            str += this.updateForm.selectBidsTypeOptions[i]
          } else {
            str += this.updateForm.selectBidsTypeOptions[i]
            str += '-'
          }
        }
        this.updateForm.tenderProjectClassifyCode = str
      }
    }
  }
}
</script>
<style lang="less">
  #cloud_addsection {
    .el-cascader{
      width: 100%;
    }
  }
</style>
