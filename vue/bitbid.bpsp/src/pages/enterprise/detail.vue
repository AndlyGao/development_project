<template>
  <div id="enterpriseDetail" class="enterpriseDetail">
    <div class="headertitle">
      <!--面包屑-->
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/main' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>企业信息</el-breadcrumb-item>
        <el-breadcrumb-item>查看企业信息</el-breadcrumb-item>
      </el-breadcrumb>
      <!--面包屑-->
    </div>
    <div class="contentbigbox">
      <el-button type="primary" size="medium " style="float: right;margin-top: 15px; margin-right: 10px;" @click="change" v-if="$store.getters.authUser.enterpriseStatus === 2">企业信息变更</el-button>
      <div class="checktips" v-if="$store.getters.authUser.enterpriseStatus === 1">
        <span title="你填写的信息已经成功提交，正在审核中 ！">你填写的信息已经成功提交，正在审核中 ！</span>
      </div>
      <div class="checktips checktipwarning" v-if="$store.getters.authUser.enterpriseStatus === 3">
        <span title="你填写的信息未审核通过！">你填写的信息未审核通过！</span>
      </div>
      <div class="maindetail">
        <span class="title">企业信息</span>
        <el-form  label-width="180px" :model="ruleForm" ref="ruleForm" :label-position="'right'" :rules="rule">
          <el-form-item label="角色类型 :">
            <span style="text-align: left;display: block;padding-left: 30px">{{ruleForm.role}}</span>
          </el-form-item>
          <el-form-item label=" 企业名称 :">
            <span style="text-align: left;display: block;padding-left: 30px">{{ruleForm.name}}</span>
          </el-form-item>
          <el-form-item label=" 注册地址 : " style="text-align: left">
            <span style="text-align: left;display: block;padding-left: 30px">{{ruleForm.registerAddress}}</span>
          </el-form-item>
          <el-form-item label=" 详细地址 : ">
            <span style="text-align: left;display: block;padding-left: 30px">{{ruleForm.address}}</span>
          </el-form-item>
          <el-form-item label=" 所属行业 : " style="text-align: left" prop="industryTypeFirst">
            <span style="text-align: left;display: block;padding-left: 30px">{{ruleForm.industry}}</span>
          </el-form-item>
          <el-form-item label=" 统一社会信用代码 : ">
            <span style="text-align: left;display: block;padding-left: 30px">{{ruleForm.uscCode}}</span>
          </el-form-item>
          <el-form-item label=" 法定代表人姓名 : ">
            <span style="text-align: left;display: block;padding-left: 30px">{{ruleForm.businessLicenseName}}</span>
          </el-form-item>
          <el-form-item label=" 法定代表人身份证号码 : " prop="businessLicenseCardNo">
            <span style="text-align: left;display: block;padding-left: 30px">{{ruleForm.businessLicenseCardNo}}</span>
          </el-form-item>
          <div v-if="ruleForm.isCopy === 0">
            <div v-if="$store.getters.authUser.enterpriseStatus === 1">
              <span class="title" style=" border-left: 2px solid #00a0e9;">联系人信息</span>
              <el-form-item label=" 姓名 : ">
                <el-input placeholder="请输入姓名" style="margin-left: 30px" v-model="ruleForm.user.name" :disabled="editFlag"></el-input>
              </el-form-item>
              <el-form-item label=" 移动电话 : ">
                <el-input placeholder="请输入移动电话" style="margin-left: 30px" v-model="ruleForm.user.cellphone" :disabled="editFlag"></el-input>
              </el-form-item>
              <el-form-item label=" 电子邮箱 : ">
                <el-input placeholder="请输入电子邮箱" style="margin-left: 30px" v-model="ruleForm.user.email" :disabled="editFlag"></el-input>
              </el-form-item>
            </div>
            <div v-else>
              <span class="title" style=" border-left: 2px solid #00a0e9;">联系人信息</span>
              <el-form-item label=" 姓名 : " prop="user.name">
                <el-input placeholder="请输入姓名" style="margin-left: 30px" v-model="ruleForm.user.name" :disabled="editFlag"></el-input>
              </el-form-item>
              <el-form-item label=" 移动电话 : " prop="user.cellphone">
                <el-input placeholder="请输入移动电话" style="margin-left: 30px" v-model="ruleForm.user.cellphone" :disabled="editFlag"></el-input>
              </el-form-item>
              <el-form-item label=" 电子邮箱 : " prop="user.email">
                <el-input placeholder="请输入电子邮箱" style="margin-left: 30px" v-model="ruleForm.user.email" :disabled="editFlag"></el-input>
              </el-form-item>
              <el-form-item class="submit-radio">
                <el-button type="primary" size="medium " class="btn" @click="submit('ruleForm')" v-if="!editFlag">保存</el-button>
              </el-form-item>
            </div>
          </div>
          <div v-if="ruleForm.isCopy === 1 && $store.getters.authUser.enterpriseStatus !== 1">
            <span class="title" style=" border-left: 2px solid #00a0e9;">联系人信息</span>
            <el-form-item label=" 姓名 : " >
              <el-input placeholder="请输入姓名" style="margin-left: 30px" v-model="ruleForm.user.name" :disabled="editFlag"></el-input>
            </el-form-item>
            <el-form-item label=" 移动电话 : " >
              <el-input placeholder="请输入移动电话" style="margin-left: 30px" v-model="ruleForm.user.cellphone" :disabled="editFlag"></el-input>
            </el-form-item>
            <el-form-item label=" 电子邮箱 : " >
              <el-input placeholder="请输入电子邮箱" style="margin-left: 30px" v-model="ruleForm.user.email" :disabled="editFlag"></el-input>
            </el-form-item>
            <el-form-item class="submit-radio">
              <el-button type="primary" size="medium " class="btn" @click="submit('ruleForm')" v-if="!editFlag">保存</el-button>
            </el-form-item>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>
<script>
import {isvalidPhone} from '@/assets/js/validate'
import {enterprise, adminUser} from '@/api'
import * as region from '@/assets/js/regionThird'
import * as industry from '@/assets/js/industry'
export default {
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
        }
      },
      addressArr: region.regionInfo,
      industryArr: industry.industry,
      editFlag: false,
      rule: {
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
      }
    }
  },
  methods: {
    detail () {
      enterprise.detail(this.$store.getters.authUser.enterpriseId).then((res) => {
        this.ruleForm = res.data.enterprise
        // 注册地址
        this.ruleForm.registerAddress = ''
        this.addressArr.map((item) => {
          if (item.value === this.ruleForm.provinceId) {
            this.ruleForm.registerAddress += item.label
            item.children.map((ite) => {
              if (ite.value === this.ruleForm.cityId) {
                this.ruleForm.registerAddress += ite.label
                ite.children.map((it) => {
                  if (it.value === this.ruleForm.countryId) {
                    this.ruleForm.registerAddress += it.label
                  }
                })
              }
            })
          }
        })
        // 行业
        this.ruleForm.industry = ''
        this.industryArr.map((item) => {
          if (item.value === this.ruleForm.industryTypeFirst) {
            this.ruleForm.industry += item.label
            item.children.map((ite) => {
              if (ite.value === this.ruleForm.industryTypeSecond) {
                this.ruleForm.industry += ite.label
              }
            })
          }
        })
      })
    },
    change () {
      this.$router.push({path: '/index/enterprise', query: {changeFlag: true}})
    },
    submit (formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          adminUser.update(this.ruleForm.user).then((res) => {
            this.detail()
          })
        } else {
          return false
        }
      })
    }
  },
  mounted () {
    this.detail()
    this.$store.getters.authUser.enterpriseStatus === 1 ? this.editFlag = true : this.editFlag = false
  }
}
</script>
<style lang="less">
  #enterpriseDetail{
    .el-input{
      width: 500px;
    }
    .el-button--primary{
      margin-left: 210px;
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
      color: #ff8a00;
    }
    .checktipwarning{
      background: #fee8e7;
      border: 1px solid #ff4440;
    }
    .checktipwarning span{
      color: #ff4440;
    }
    .maindetail{
      float: left;
      overflow: hidden;
    }
  }
</style>
