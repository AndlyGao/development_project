<template>
  <div  id="enterprise" class="enterprise">
    <div class="headertitle">
      <!--面包屑-->
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/main' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>企业信息</el-breadcrumb-item>
        <el-breadcrumb-item v-if="changeFlag">变更企业信息</el-breadcrumb-item>
        <el-breadcrumb-item v-else>
          <span v-if="$store.getters.authUser.enterpriseStatus === 3">修改企业信息</span>
          <span v-else>编辑企业信息</span>
        </el-breadcrumb-item>
      </el-breadcrumb>
      <!--面包屑-->
    </div>
    <div class="contentbigbox">
      <div class="checktips" v-if="changeFlag">
        <span>提示:当您的企业信息有变动时，在提交审核之后将不能发布公告，您也可主动致电管理员进行优先审核！</span>
      </div>
      <div class="checktips" v-if="$store.getters.authUser.enterpriseStatus === 3">
        <span :title="ruleForm.recordOfApprovals[0].opinion">您的企业信息审核不通过！&nbsp;&nbsp;审批意见：{{ruleForm.recordOfApprovals[ruleForm.recordOfApprovals.length-1].opinion}} ,&nbsp;&nbsp;请您重新填写</span>
      </div>
      <span class="title">企业信息</span>
      <el-form  label-width="180px" :model="ruleForm" ref="ruleForm" :label-position="'right'" :rules="rule">
        <el-form-item label="角色类型 :" prop="role">
          <el-checkbox-group v-model="ruleForm.role" style="text-align: left">
            <el-checkbox label="招标人" name="role"></el-checkbox>
            <el-checkbox label="招标代理" name="role"></el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label=" 企业名称 :" prop="name">
          <el-input  v-model="ruleForm.name" placeholder="请输入企业名称"></el-input>
        </el-form-item>
        <el-form-item label=" 注册地址 : " style="text-align: left" prop="provinceId">
          <el-cascader class=""
             expand-trigger="hover"
             :options="linkageData"
             v-model="linkageArray"
             @change="handleCityChange">
          </el-cascader>
        </el-form-item>
        <el-form-item label="详细地址 : " prop="address">
          <el-input  v-model="ruleForm.address" placeholder="请输入详细地址"></el-input>
        </el-form-item>
        <el-form-item label=" 所属行业 : " style="text-align: left" prop="industryTypeFirst">
          <el-cascader class=""
                       expand-trigger="hover"
                       :options="linkageIndustry"
                       v-model="linkageIndustryArray"
                       @change="handleIndustryChange">
          </el-cascader>
        </el-form-item>
        <el-form-item label=" 统一社会信用代码 : " prop="uscCode">
          <el-input   placeholder="请输入统一社会信用代码" v-model="ruleForm.uscCode"></el-input>
        </el-form-item>
        <el-form-item label=" 法定代表人姓名 : " prop="businessLicenseName" >
          <el-input  placeholder="请输入法定代表人姓名" v-model="ruleForm.businessLicenseName"></el-input>
        </el-form-item>
        <el-form-item label=" 法定代表人身份证号码 : " prop="businessLicenseCardNo">
          <el-input   placeholder="请输入法定代表人身份证号码" v-model="ruleForm.businessLicenseCardNo"></el-input>
        </el-form-item>
        <div v-if="!changeFlag">
          <span class="title" style=" border-left: 2px solid #00a0e9;">联系人信息</span>
          <el-form-item label=" 姓名 : " prop="user.name">
            <el-input placeholder="请输入姓名" v-model="ruleForm.user.name"></el-input>
          </el-form-item>
          <el-form-item label=" 移动电话 : " prop="user.cellphone">
            <el-input placeholder="请输入移动电话" v-model="ruleForm.user.cellphone"></el-input>
          </el-form-item>
          <el-form-item label=" 电子邮箱 : " prop="user.email">
            <el-input placeholder="请输入电子邮箱" v-model="ruleForm.user.email"></el-input>
          </el-form-item>
        </div>
        <el-form-item class="submit-radio" v-if="changeFlag">
          <el-button type="primary" @click="submitChange('ruleForm')" class="btn">提交审核</el-button>
          <el-button type="primary" @click="cancel" class="btn">取消</el-button>
        </el-form-item>
        <el-form-item class="submit-radio" v-else>
          <el-button type="primary" @click="submitForm('ruleForm')" class="btn">提交审核</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>
<script>
import * as region from '@/assets/js/regionThird'
import * as industry from '@/assets/js/industry'
import {isvalidPhone, isvalidId} from '@/assets/js/validate'
import {enterprise} from '@/api'
import store from '../../store'
export default {
  components: {},
  data () {
    let validPhone = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入电话号码'))
      } else if (!isvalidPhone(value)) {
        callback(new Error('请输入正确的11位手机号码'))
      } else {
        callback()
      }
    }
    let validIdCard = (rule, value, callback) => {
      if (!value) {
        callback()
      } else if (!isvalidId(value)) {
        callback(new Error('请输入正确的身份证号码'))
      } else {
        callback()
      }
    }
    return {
      ruleForm: {
        role: [],
        name: '',
        address: '',
        provinceId: '',
        cityId: '',
        countryId: '',
        industryTypeFirst: '',
        industryTypeSecond: '',
        businessLicenseName: '',
        uscCode: '',
        businessLicenseCardNo: '',
        user: {
          name: '',
          cellphone: '',
          email: ''
        },
        recordOfApprovals: [
          {
            opinion: ''
          }
        ]
      },
      // 三级联动
      linkageData: region.regionInfo,
      linkageArray: [],
      // 二级联动
      linkageIndustry: industry.industry,
      linkageIndustryArray: [],
      rule: {
        role: [
          { type: 'array', required: true, message: '请至少选择一个角色类型', trigger: 'blur' }
        ],
        provinceId: [
          { required: true, message: '请选择', trigger: 'blur' }
        ],
        address: [
          { required: true, message: '请输入详细地址', trigger: 'blur' },
          { min: 1, max: 256, message: '长度在 1 到 256 个字符', trigger: ['blur', 'change'] }
        ],
        industryTypeFirst: [
          { required: true, message: '请选择', trigger: 'blur' }
        ],
        name: [
          { required: true, message: '请输入企业名称', trigger: 'blur' },
          { min: 1, max: 128, message: '长度在 1 到 128 个字符', trigger: ['blur', 'change'] }
        ],
        businessLicenseName: [
          { required: true, message: '请输入法定代表人姓名', trigger: 'blur' },
          { min: 1, max: 128, message: '长度在 1 到 128 个字符', trigger: ['blur', 'change'] }
        ],
        businessLicenseCardNo: [
          { validator: validIdCard, trigger: 'blur' }
        ],
        uscCode: [
          { required: true, message: '请输入统一社会信用代码', trigger: 'blur' },
          { min: 1, max: 18, message: '长度在 1 到 18 个字符', trigger: ['blur', 'change'] }
        ],
        'user.name': [
          { required: true, message: '请输入姓名', trigger: 'blur' },
          { min: 1, max: 128, message: '长度在 1 到 128 个字符', trigger: ['blur', 'change'] }
        ],
        'user.cellphone': [
          { required: true, message: '请输入电话号码', trigger: 'blur' },
          { validator: validPhone, trigger: 'blur' }
        ],
        'user.email': [
          { required: true, message: '请输入邮箱地址', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] },
          { min: 1, max: 256, message: '长度在 1 到 256 个字符', trigger: ['blur', 'change'] }
        ]
      },
      changeFlag: false
    }
  },
  methods: {
    detail () {
      enterprise.detail(this.$store.getters.authUser.enterpriseId).then((res) => {
        this.ruleForm = res.data.enterprise
        this.linkageArray.push(res.data.enterprise.provinceId)
        this.linkageArray.push(res.data.enterprise.cityId)
        this.linkageArray.push(res.data.enterprise.countryId)
        this.linkageIndustryArray.push(res.data.enterprise.industryTypeFirst)
        this.linkageIndustryArray.push(res.data.enterprise.industryTypeSecond)
        if (this.ruleForm.role) {
          let roleArr = this.ruleForm.role.split(',')
          this.ruleForm.role = roleArr
        } else {
          this.ruleForm.role = []
        }
      })
    },
    // 三级联动选择
    handleCityChange (value) {
      if (value.length === 0) {
        return
      }
      this.ruleForm.provinceId = value[0]
      this.ruleForm.cityId = value[1]
      this.ruleForm.countryId = value[2]
    },
    handleIndustryChange (value) {
      if (value.length === 0) {
        return
      }
      this.ruleForm.industryTypeFirst = value[0]
      this.ruleForm.industryTypeSecond = value[1]
    },
    // 提交数据
    submitForm (formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          let roleStr = ''
          for (let a = 0; a < this.ruleForm.role.length; a++) {
            if (a === this.ruleForm.role.length - 1) {
              roleStr += this.ruleForm.role[a]
            } else {
              roleStr += this.ruleForm.role[a] + ','
            }
          }
          this.ruleForm.role = roleStr
          this.ruleForm.status = 1
          enterprise.update(this.ruleForm).then((res) => {
            if (res.data.resCode === '0000') {
              // 更新用户信息
              store.dispatch('GetLoginInfo').then(() => {
                this.$router.push({path: '/index/enterprise/detail'})
              })
            } else {
              return false
            }
          })
        } else {
          return false
        }
      })
    },
    // 变更企业信息
    submitChange (formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          let roleStr = ''
          for (let a = 0; a < this.ruleForm.role.length; a++) {
            if (a === this.ruleForm.role.length - 1) {
              roleStr += this.ruleForm.role[a]
            } else {
              roleStr += this.ruleForm.role[a] + ','
            }
          }
          this.ruleForm.role = roleStr
          enterprise.change(this.ruleForm).then((res) => {
            if (res.data.resCode === '0000') {
              // 更新用户信息
              store.dispatch('GetLoginInfo').then(() => {
                this.$router.push({path: '/index/enterprise/detail'})
              })
            } else {
              return false
            }
          })
        } else {
          return false
        }
      })
    },
    // 取消
    cancel () {
      this.$router.push({path: '/index/enterprise/detail'})
    }
  },
  mounted () {
    this.detail()
    this.changeFlag = this.$route.query.changeFlag
  }
}
</script>
<style lang="less">
  #enterprise{
    .el-form-item{
      padding-left: 50px;
    }
    .el-input{
      width: 500px;
    }
    .el-button--primary{
      margin-left: 180px;
    }
    span.title{
      height: 30px;
      line-height: 30px;
      text-align: left;
      display: block;
      padding-left: 20px;
      margin-bottom: 20px;
      font-size: 16px;
      font-weight: bold;
      border-left: 2px solid #2c972f;
    }
    span.hint{
      color: red;
      height: 10px;
      line-height: 10px;
      text-align: left;
      display: block;
      padding-left: 20px;
      margin-bottom: 20px;
      font-size: 16px;
      font-weight: bold;
    }
    .checktips{
      width: 100%;
      height: 32px;
      line-height: 32px;
      overflow: hidden;
      -ms-text-overflow: ellipsis;
      text-overflow: ellipsis;
      white-space: nowrap;
      background: #feeedb;
      border: 1px solid #ff8a00;
      margin-bottom: 30px;
      float: left;
    }
    .checktips span{
      font-size: 14px;
      float: left;
      margin-left: 20px;
      color: #ff4440;
    }
  }
</style>
