<template>
  <div class="cloudcontent" id="cloud_enterprise">
    <!--面包屑-->
    <el-breadcrumb separator-class="el-icon-arrow-right" class="breadcrumb_box">
      <el-breadcrumb-item :to="{ path: '/index' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item class="active_bread">编辑企业信息</el-breadcrumb-item>
    </el-breadcrumb>
    <!--面包屑-->
    <div class="main">
      <p class="title">
        企业信息
        <span>注：完善资料后即可获得更多服务，*号为必填项。</span>
      </p>
      <el-form :model="enterpriseForm" :rules="rules" ref="enterpriseForm" :validate-on-rule-change="true">
        <div class="part">
          <h3>企业基本信息</h3>
          <div class="formmain">
            <el-row>
              <el-col :span="12">
                <el-form-item label="企业名称："  prop="name">
                  <el-input v-model="enterpriseForm.name"></el-input>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="统一信用代码："  prop="uscCode">
                  <el-input v-model="enterpriseForm.uscCode"></el-input>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="12">
                <el-form-item label="法定代表人："  prop="businessLicenseName">
                  <el-input v-model="enterpriseForm.businessLicenseName"></el-input>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="法人身份证号："  prop="businessLicenseCardNo">
                  <el-input v-model="enterpriseForm.businessLicenseCardNo"></el-input>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="24">
                <el-form-item label="所属行业："  prop="industry">
                  <el-cascader class=""
                               expand-trigger="hover"
                               :options="industryOptions"
                               v-model="selectedIndustryOptions"
                               @change="handleIndustryChange">
                  </el-cascader>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="24">
                <el-form-item label="所在地区："  prop="address.privinceId">
                  <el-cascader class=""
                               expand-trigger="hover"
                               :options="addressOptions"
                               v-model="selectedcityOptions"
                               @change="handlecityChange">
                  </el-cascader>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="24">
                <el-form-item label="详细地址："  prop="address.addr">
                  <el-input v-model="enterpriseForm.address.addr"></el-input>
                </el-form-item>
              </el-col>
            </el-row>
          </div>
        </div>
        <div class="part">
          <h3>联系人信息</h3>
          <div class="formmain">
            <el-row>
              <el-col :span="12">
                <el-form-item label="联系人："  prop="contactName">
                  <el-input v-model="enterpriseForm.contactName"></el-input>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="手机号码："  prop="contactCellphone">
                  <el-input v-model="enterpriseForm.contactCellphone"></el-input>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="12">
                <el-form-item label="邮箱："  prop="email">
                  <el-input v-model="enterpriseForm.email"></el-input>
                </el-form-item>
              </el-col>
            </el-row>
          </div>
        </div>
        <div class="part">
          <h3>企业证件</h3>
          <div class="formmain">
            <el-row class="ctype">
              <template>
                <el-radio v-model="ctype" label="1">普通证件</el-radio>
                <el-radio v-model="ctype" label="2">多证合一营业执照</el-radio>
              </template>
            </el-row>
            <el-row>
              <el-col :span="8">
                <el-form-item label="营业执照："  prop="name">
                  <uploadlFile @upLoadFile="upLoadFile" :fileType="fileType" :oldFileName="oldFileName"></uploadlFile>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="组织机构代码证："  prop="name" v-if="Number(this.ctype) === 1">
                  <uploadlFile @upLoadFile="upLoadFile" :fileType="fileType" :oldFileName="oldFileName"></uploadlFile>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="税务登记证："  prop="name" v-if="Number(this.ctype) === 1">
                  <uploadlFile @upLoadFile="upLoadFile" :fileType="fileType" :oldFileName="oldFileName"></uploadlFile>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="8">
                <el-form-item label="法人身份证（正）："  prop="name">
                  <uploadlFile @upLoadFile="upLoadFile" :fileType="fileType" :oldFileName="oldFileName"></uploadlFile>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="法人身份证（反）："  prop="name">
                  <uploadlFile @upLoadFile="upLoadFile" :fileType="fileType" :oldFileName="oldFileName"></uploadlFile>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="8">
                <el-form-item label="开户许可："  prop="name">
                  <uploadlFile @upLoadFile="upLoadFile" :fileType="fileType" :oldFileName="oldFileName"></uploadlFile>
                </el-form-item>
              </el-col>
            </el-row>
          </div>
        </div>
        <el-form-item class="submit-radio">
          <el-button type="primary">提交</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>
<script>
import uploadlFile from '../../../components/upload/publicFileUpload'
import * as region from '@/assets/js/region'
import * as industry from '@/assets/js/industry_three'
import {isvalidFixedPhone} from '@/assets/js/validate'
export default {
  components: {
    uploadlFile
  },
  data () {
    // 电话号码
    var validPhoneUser = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入联系电话'))
      } else if (!isvalidFixedPhone(value)) {
        callback(new Error('请输入正确的11位手机号码'))
      } else {
        callback()
      }
    }
    return {
      enterpriseForm: {
        name: '',
        uscCode: '',
        address: {
          privinceId: '',
          cityId: '',
          countyId: '',
          addr: ''
        },
        businessLicenseName: '',
        businessLicenseCardNo: '',
        industry: '',
        industrySecond: '',
        industryThird: ''
      },
      // 地址三级联动
      addressOptions: region.CityInfo,
      selectedcityOptions: [],
      // 行业三级联动
      industryOptions: industry.industry,
      selectedIndustryOptions: [],
      fileType: 1,
      fileArry: [],
      oldFileName: '',
      ctype: '1',
      // 表单验证
      rules: {
        name: [
          { required: true, message: '企业名称不能为空', trigger: 'blur' }
        ],
        uscCode: [
          { required: true, message: '统一信用代码不能为空', trigger: 'blur' }
        ],
        businessLicenseName: [
          { required: true, message: '法定代表人不能为空', trigger: 'blur' }
        ],
        businessLicenseCardNo: [
          { required: true, message: '统一信用代码不能为空', trigger: 'blur' }
        ],
        industry: [
          { required: true, message: '所属行业不能为空', trigger: ['blur', 'change'] }
        ],
        'address.privinceId': [
          { required: true, message: '所在地区不能为空', trigger: ['blur', 'change'] }
        ],
        'address.addr': [
          { required: true, message: '详细地址不能为空', trigger: 'blur' }
        ],
        contactName: [
          { required: true, message: '联系人不能为空', trigger: 'blur' }
        ],
        contactCellphone: [
          { required: true, message: '手机号码不能为空', trigger: 'blur' },
          {validator: validPhoneUser, trigger: 'blur'}
        ],
        email: [
          { required: true, message: '邮箱不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    // 将子组件获取到的数据传给父组件
    upLoadFile (file) {
      this.fileArry = file
    },
    // 所属行业三级联动赋值
    handleIndustryChange (value) {
      this.enterpriseForm.industry = value[0]
      this.enterpriseForm.industrySecond = value[1]
      this.enterpriseForm.industryThird = value[2]
    },
    // 所在地区三级联动赋值
    handlecityChange (value) {
      this.enterpriseForm.address.privinceId = value[0]
      this.enterpriseForm.address.cityId = value[1]
      this.enterpriseForm.address.countyId = value[2]
    }
  }
}
</script>
<style lang="less">
  #cloud_enterprise {
    .main p.title{
      padding: 0 20px;
      font-size: 16px;
      font-weight: bold;
      color: #111111;
      line-height: 32px;
    }
    .main p.title span{
      display: inline-block;
      width: 100%;
      line-height: 28px;
      color: #999999;
      font-size: 14px;
    }
    .part{
      overflow: hidden;
      margin: 12px auto;
    }
    .part h3{
      height: 36px;
      line-height: 36px;
      background: #f5f5f5;
      color: #111111;
      font-size: 14px;
      padding: 0 20px;
    }
    .formmain{
      overflow: hidden;
      width: 90%;
      margin: 30px auto;
    }
    .el-cascader{
      width: 41%;
    }
    .ctype{
      margin: 20px 0 30px 60px;
    }
  }
</style>
