<template>
  <div class="cloudcontent">
    <!--面包屑-->
    <el-breadcrumb separator-class="el-icon-arrow-right" class="breadcrumb_box">
      <el-breadcrumb-item :to="{ path: '/index' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/processManage/projectProcess' }">项目进度管理</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/processManage/projectProcess/project' }">项目管理</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/processManage/projectProcess/project/process' }">流程管理</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/processManage/projectProcess/project/process', query: {type: 'anno'}}">响应资格预审</el-breadcrumb-item>
      <el-breadcrumb-item class="active_bread">修改资格预审文件费用</el-breadcrumb-item>
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
              <el-select v-model="updateForm.paymentMethod" placeholder="请选择" clearable>
                <el-option
                  v-for="item in paymentMethods"
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
import {costInfo} from '@/api/cloudplatform'
import {validNumber} from '@/assets/js/validate'
export default {
  components: {
  },
  data () {
    return {
      isSubmiting: false,
      updateForm: {},
      rules: {
        bidderName: {required: true, message: '请填写投标人名称', trigger: 'blur'},
        amount: [
          {required: true, message: '请填写缴费金额', trigger: 'blur'},
          {validator: validNumber, trigger: 'blur'}
        ],
        paymentMethod: {required: true, message: '请选择缴费方式', trigger: 'change'}
      },
      paymentMethods: [
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
      bidSection: {}
    }
  },
  methods: {
    // 提交/保存
    submit (form, status) {
      this.$refs[form].validate((vaild) => {
        if (vaild) {
          this.isSubmiting = true
          if (Object.is(status, 0)) {
            this.updateForm.auditStatus = 0
          } else if (Object.is(status, 1)) {
            console.log('判断是否需要审批：审批：1 不审批：4')
            this.updateForm.auditStatus = 4
          }
          this.updateForm.paymentTime = new Date(this.updateForm.paymentTime).getTime()
          costInfo.update(this.updateForm).then((res) => {
            this.isSubmiting = false
            if (res.data.resCode === '0000') {
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
    getCostInfo () {
      costInfo.getById(this.$route.query.objectId).then(res => {
        this.updateForm = res.data.costInfo
        if (this.updateForm.bidSection) {
          this.bidSection = this.updateForm.bidSection
        }
      })
    }
  },
  mounted () {
    this.getCostInfo()
  }
}
</script>

<style scoped>
</style>
