<template>
  <div class="cloudcontent" id="cloud_processedit">
    <!--面包屑-->
    <el-breadcrumb separator-class="el-icon-arrow-right" class="breadcrumb_box">
      <el-breadcrumb-item :to="{ path: '/index' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/processManage/projectProcess' }">项目进度管理</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/processManage/projectProcess/project' }">项目管理</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/processManage/projectProcess/project/process' }">流程管理</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/processManage/projectProcess/project/process' }">定标结果</el-breadcrumb-item>
      <el-breadcrumb-item class="active_bread">添加退还保证金费用</el-breadcrumb-item>
    </el-breadcrumb>
    <!--面包屑-->
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
            <el-form-item label="投标人："  prop="bidderName">
              <el-input v-model="updateForm.bidderName"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="缴费金额："  prop="amount">
              <el-input v-model="updateForm.amount"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="缴费方式："  prop="paymentMethod">
              <el-select v-model="updateForm.paymentMethod" placeholder="请选择">
                <el-option
                  v-for="item in costMethods"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="缴费时间："  prop="paymentTime">
              <el-date-picker
                v-model="updateForm.paymentTime"
                type="datetime"
                placeholder="选择日期时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="退还金额："  prop="refundAmount">
              <el-input v-model="updateForm.refundAmount"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="退还方式："  prop="refundMethod">
              <el-select v-model="updateForm.refundMethod" placeholder="请选择">
                <el-option
                  v-for="item in costMethods"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="退还时间："  prop="refundTime">
              <el-date-picker
                v-model="updateForm.refundTime"
                type="datetime"
                placeholder="选择日期时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="操作人："  prop="operatorName">
              <el-input v-model="updateForm.operatorName"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24" class="ueditor_box">
            <el-form-item label="备注内容：">
              <editor ref="ueditor" class="ueditor" :editread="editread"></editor>
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
import editor from '@/components/ueditor/ueditor.vue'
import {costInfo, bidSection} from '@/api/cloudplatform'
export default {
  components: {
    editor
  },
  data () {
    return {
      tenderSystemCode: this.$route.query.tenderSystemCode,
      sectionSystemCode: this.$route.query.sectionSystemCode,
      isSubmiting: false,
      updateForm: {},
      rules: {
        bidderName: {required: true, message: '请填写投标人', trigger: ['blur', 'change']},
        amount: {required: true, message: '请填写缴费金额', trigger: ['blur', 'change']},
        paymentMethod: {required: true, message: '请选择缴费方式', trigger: 'change'},
        refundAmount: {required: true, message: '请填写退还金额', trigger: ['blur', 'change']},
        refundMethod: {required: true, message: '请选择退还方式', trigger: 'change'}
      },
      costMethods: [
        {
          value: 1,
          label: '支付宝'
        },
        {
          value: 2,
          label: '微信'
        },
        {
          value: 3,
          label: '现金'
        },
        {
          value: 4,
          label: '银联'
        },
        {
          value: 5,
          label: '支票'
        },
        {
          value: 6,
          label: '保函'
        },
        {
          value: 9,
          label: '其他'
        }
      ],
      // 富文本
      editread: false
    }
  },
  methods: {
    // 提交/保存
    submit (form, status) {
      let content = this.$refs.ueditor.getContent()
      this.$refs[form].validate((vaild) => {
        if (vaild) {
          this.isSubmiting = true
          if (Object.is(status, 0)) {
            // 保存
            this.updateForm.auditStatus = 0
          } else if (Object.is(status, 1)) {
            // 提交
            console.log('判断是否需要审批：审批：1 不审批：4')
            this.updateForm.auditStatus = 4
          }
          this.updateForm.tenderSystemCode = this.tenderSystemCode
          this.updateForm.sectionSystemCode = this.sectionSystemCode
          this.updateForm.type = 4
          if (!Object.is(this.updateForm.paymentTime, this.oldPaymentTime)) {
            this.updateForm.paymentTime = this.updateForm.paymentTime.getTime()
          }
          if (!Object.is(this.updateForm.refundTime, this.oldRefundTime)) {
            this.updateForm.refundTime = this.updateForm.refundTime.getTime()
          }
          this.updateForm.remark = content
          costInfo.update(this.updateForm).then((res) => {
            if (res.data.resCode === '0000') {
              this.isSubmiting = false
              this.$router.go(-1)
            }
          })
        } else {
          return false
        }
      })
    },
    // 取消
    close () {
      this.$router.go(-1)
    },
    // 查询当前标段信息
    getBidSection () {
      bidSection.getOne(this.sectionSystemCode).then(res => {
        this.bidSection = res.data.bidSection
      })
    },
    init () {
      this.getBidSection()
    }
  },
  watch: {
    '$route': 'init'
  },
  mounted () {
    this.init()
  }
}
</script>
<style lang="less">
  #cloud_processedit{
  }
</style>
