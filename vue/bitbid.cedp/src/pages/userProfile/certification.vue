<template>
    <div id="certification">
      <div class="logobigbox">
        <div class="logobox">
          <div class="logoboximg fl">
            <img src="../../../static/images/login_logo.png" alt="">
          </div>
          <div class="logoboxlink fr">
            <el-link href="" :underline="false">首页</el-link>
            <el-link href="" :underline="false">直采商城</el-link>
            <el-link href="" :underline="false">采购招标</el-link>
            <el-link href="" :underline="false">供应商库</el-link>
            <el-link href="" :underline="false">园区直采</el-link>
            <el-link href="" :underline="false">询价信息</el-link>
            <el-link href="" :underline="false">网站导航</el-link>
          </div>
        </div>
      </div>
      <div class="content">
        <h3>企业认证</h3>
        <el-form :model="enterpriseForm" :rules="rules" ref="enterpriseForm" label-width="120px" :validate-on-rule-change="true">
          <div class="part">
            <p class="title"><span>基本信息</span></p>
            <div class="formmain">
              <el-row :gutter="60">
                <el-col :span="12">
                  <el-form-item label="企业名称" prop="name">
                    <el-input v-model="enterpriseForm.name" placeholder="请输入企业名称"></el-input>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="统一信用代码" prop="uscCode">
                    <el-input v-model="enterpriseForm.uscCode" clearable placeholder="请输入统一信用代码"></el-input>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row :gutter="60">
                <el-col :span="12">
                  <el-form-item label="法定代表人" prop="businessLicenseName">
                    <el-input v-model="enterpriseForm.businessLicenseName" placeholder="请输入法定代表人"></el-input>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="法人身份证号" prop="businessLicenseCardNo">
                    <el-input v-model="enterpriseForm.businessLicenseCardNo" clearable placeholder="请输入法人身份证号"></el-input>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row :gutter="60">
                <el-col :span="12">
                  <el-form-item label="企业性质" prop="characterId">
                    <el-select v-model="enterpriseForm.characterId" placeholder="请选择企业性质">
                      <el-option
                        v-for="item in characterIdOptions"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value">
                      </el-option>
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="所属行业" prop="industry" >
                    <el-cascader
                      expand-trigger="hover"
                      :options="industryOptions"
                      v-model="selectedIndustryOptions"
                      @change="handleIndustryChange"
                      clearable>
                    </el-cascader>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row :gutter="60">
                <el-col :span="12">
                  <el-form-item label="所在地区" prop="address.privinceId" clearable>
                    <el-cascader expand-trigger="hover"
                                 :options="addressOptions"
                                 v-model="selectedcityOptions"
                                 @change="handlecityChange"
                                 clearable>
                    </el-cascader>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="详细地址" prop="address.addr">
                    <el-input v-model="enterpriseForm.address.addr" placeholder="请输入详细地址"></el-input>
                  </el-form-item>
                </el-col>
              </el-row>
<!--              <el-row :gutter="60">-->
<!--                <el-col :span="12">-->
<!--                  <el-form-item label="供货类型" prop="supplyType">-->
<!--                    <el-select v-model="enterpriseForm.supplyType" placeholder="请选择供货类型" clearable>-->
<!--                      <el-option label="代理商" value="1"></el-option>-->
<!--                      <el-option label="制造商" value="2"></el-option>-->
<!--                    </el-select>-->
<!--                  </el-form-item>-->
<!--                </el-col>-->
<!--              </el-row>-->
            </div>
          </div>
          <div class="part">
            <p class="title"><span>企业证件</span></p>
            <div class="formmain">
              <el-row>
                <el-col :span="24">
                  <el-form-item label="营业执照" prop="fileValue1" class="certificate bitianicon">
                    <img :src="imgSrc"/>
                    <upload-file @uploadSuccess="uploadSuccess" @deleteSuccess="deleteSuccess" fileType="1" :fileArrays="fileInformations" isImage="1"></upload-file>
                    <span>提示：请上传JPG/PNG/GIF格式图片，要求内容清晰</span>
                  </el-form-item>
                </el-col>
              </el-row>
            </div>
          </div>
          <div class="part">
            <p class="title"><span>联系人信息</span></p>
            <div class="formmain">
              <el-row :gutter="60">
                <el-col :span="12">
                  <el-form-item label="联系人名称" prop="contactName">
                    <el-input v-model="enterpriseForm.contactName" clearable placeholder="请输入联系人名称"></el-input>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="手机号码" prop="contactCellphone">
                    <el-input v-model="enterpriseForm.contactCellphone" clearable placeholder="请输入手机号码"></el-input>
                  </el-form-item>
                </el-col>
              </el-row>
            </div>
            <p class="btn">
              <el-button type="primary" @click="saveEnterprise('enterpriseForm')" :loading="loadingFlag">提交审核</el-button>
              <el-button @click="skipCertification()">下次再说</el-button>
            </p>
          </div>
        </el-form>
      </div>
      <div class="bottomlink">
        <el-link href="" :underline="false">比比电子交易平台</el-link> |
        <el-link href="" :underline="false">比比建材</el-link> |
        <el-link href="" :underline="false">比比金融</el-link> |
        <el-link href="" :underline="false">比比招标采购</el-link>
      </div>
      <p class="banquan"> <img src="../../../static/images/bq.png"/>晋公网安备 14019202000115号比比网络 版权所有 晋ICP备14002897号-1</p>
    </div>
</template>
<script>
import uploadFile from '@/components/upload/publicFileUpload'
import * as region from '@/assets/js/region'
import * as industry from '@/assets/js/industry_three'
import {validateMobilePhone, validateIdCard} from '@/assets/js/validate'
import {enterprise} from '@/api/system'
import {fileReviewUrl} from '@/assets/js/common'

export default {
  name: '',
  props: {},
  components: {
    uploadFile
  },
  data () {
    // 电话号码
    let validPhoneUser = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入联系电话'))
      } else if (!validateMobilePhone(value)) {
        callback(new Error('请输入正确的11位手机号码'))
      } else {
        callback()
      }
    }
    // 身份证号
    let validIdCard = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入法人身份证号'))
      } else if (!validateIdCard(value)) {
        callback(new Error('请输入正确的法人身份证号'))
      } else {
        callback()
      }
    }
    // 文件
    let validateFile = (rule, value, callback) => {
      let fileValue = this.enterpriseForm[rule.field]
      if (!fileValue) {
        callback(new Error('文件不能为空'))
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
        characterId: null,
        supplyType: '',
        industry: '',
        industrySecond: '',
        industryThird: '',
        contactName: '',
        contactCellphone: ''
      },
      characterIdOptions: [
        {
          value: 1,
          label: '国有企业'
        },
        {
          value: 2,
          label: '集体企业'
        },
        {
          value: 3,
          label: '有限责任公司'
        },
        {
          value: 4,
          label: '股份有限公司'
        },
        {
          value: 5,
          label: '私营企业'
        },
        {
          value: 6,
          label: '中外合资企业'
        },
        {
          value: 7,
          label: '外商投资企业'
        },
        {
          value: 8,
          label: '其他'
        }
      ],
      imgSrc: '../../../static/images/enterprise/add.png',
      // 地址三级联动
      addressOptions: region.CityInfo,
      selectedcityOptions: [],
      // 行业三级联动
      industryOptions: industry.industry,
      selectedIndustryOptions: [],
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
          { required: true, message: '法人身份证号不能为空', trigger: 'blur' },
          {validator: validIdCard, trigger: 'blur'}
        ],
        characterId: [
          { required: true, message: '企业性质不能为空', trigger: ['blur', 'change'] }
        ],
        industry: [
          { required: true, message: '所属行业不能为空', trigger: ['blur', 'change'] }
        ],
        'address.privinceId': [
          { required: true, message: '所在地区不能为空', trigger: ['blur', 'change'] }
        ],
        contactName: [
          { required: true, message: '联系人不能为空', trigger: 'blur' }
        ],
        contactCellphone: [
          { required: true, message: '手机号码不能为空', trigger: 'blur' },
          {validator: validPhoneUser, trigger: 'blur'}
        ],
        fileValue1: [
          {validator: validateFile, trigger: 'change'}
        ]
      },
      fileInformations: [],
      loadingFlag: false
    }
  },
  created () {},
  methods: {
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
    },
    // 上传文件
    uploadSuccess (file, fileType) {
      this.fileInformations = this.fileInformations.filter(item => Number(item.businessType) !== Number(fileType))
      this.fileInformations.push(file)
      this.$set(this.enterpriseForm, `fileValue${fileType}`, fileType)
      this.$refs['enterpriseForm'].validateField(`fileValue${fileType}`)
      this.imgSrc = fileReviewUrl + file.relativePath
    },
    // 删除文件
    deleteSuccess (fileId, fileType) {
      this.fileInformations = this.fileInformations.filter(item => item.relativePath !== fileId)
      this.$set(this.enterpriseForm, `fileValue${fileType}`, '')
      this.$refs['enterpriseForm'].validateField(`fileValue${fileType}`)
      this.imgSrc = '../../../static/images/enterprise/add.png'
    },
    // 提交企业认证
    saveEnterprise (formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.$confirm('确认要提交吗?', '提示', {
            confirmButtonText: '确认',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(() => {
            this.loadingFlag = true
            this.enterpriseForm.status = 1
            this.enterpriseForm.fileInformations = this.fileInformations
            enterprise.update(this.enterpriseForm).then((res) => {
              this.loadingFlag = false
              this.$router.push({path: '/purchaser'})
            })
          }).catch(() => {
            this.loadingFlag = false
            return false
          })
        }
      })
    },
    // 下次再说
    skipCertification () {
      this.$store.dispatch('SkipCertification').then(() => {
        this.$router.push({path: '/purchaser'})
      })
    },
    detail () {
      let enterpriseId = this.$store.getters.authUser.enterpriseId
      enterprise.queryOne(enterpriseId).then((res) => {
        this.enterpriseForm = res.data.enterprise
        this.fileInformations = res.data.fileInformations || []
        if (this.fileInformations.length) {
          this.fileInformations.forEach(item => { this.enterpriseForm[`fileValue${item.businessType}`] = item.businessType })
          this.imgSrc = fileReviewUrl + this.fileInformations[0].relativePath
        }
        if (this.enterpriseForm.address && this.enterpriseForm.address.privinceId) {
          this.selectedcityOptions.push(this.enterpriseForm.address.privinceId)
          this.selectedcityOptions.push(this.enterpriseForm.address.cityId)
          this.selectedcityOptions.push(this.enterpriseForm.address.countyId)
        }
        if (this.enterpriseForm.industry) {
          this.selectedIndustryOptions.push(this.enterpriseForm.industry)
          this.selectedIndustryOptions.push(this.enterpriseForm.industrySecond)
          this.selectedIndustryOptions.push(this.enterpriseForm.industryThird)
        }
      })
    }
  },
  mounted () {
    this.detail()
  }
}
</script>
<style lang="less">
  #certification {
    .login_banner {
      width: 100%;
    }
    .logobigbox {
      background: #fff;
      height: 56px;
      width: 100%;
    }
    .logobox {
      width: 1200px;
      height: 56px;
      margin: 0 auto;
    }
    .logoboximg > img {
      width: 120px;
      height: 43px;
      margin: 5px 0 0 0;
    }
    .logoboxlink{
      margin-top:7px;
    }
    .logoboxlink a{
      display:inline-block;
      margin: 0 10px;
      height:46px;
      line-height:46px;
      border-bottom:2px solid #ffffff;
    }
    .logoboxlink a:hover{
      color:#009688;
      border-bottom:2px solid #009688;
    }
    .formmain{
      width: 80%;
      margin: 20px auto;
    }
    .banquan{
      text-align: center;
      line-height: 36px;
      font-size: 14px;
      margin:0;
      padding:0;
      color:#999999
    }
    .banquan img{
      vertical-align: middle;
      margin-right: 5px;
    }
    .bottomlink{
      margin-top:10px;
      overflow:hidden;
      text-align:center;
      line-height: 24px;
      color:#999999
    }
    .el-link.el-link--default{
      color:#999999
    }
    /*修改input 高度*/
    .el-input__inner{
      height: 40px;
      line-height: 40px;
    }
    .el-input__icon{
      line-height: 40px;
    }
    .el-button{
      padding: 12px 20px;
    }
    .el-form-item__label{
      line-height: 40px;
    }
    .el-form-item__content{
      line-height: 40px;
    }
    .el-form-item{
      margin-bottom: 22px;
    }
    .el-form-item__error{
      padding-top: 4px;
    }
    .el-input__inner:focus{
      border-color: #009688;
    }
    .content{
      width: 1200px;
      overflow:hidden;
      margin: 20px auto;
      background: #ffffff;
      min-height: 200px;
    }
    .content h3{
      text-align: center;
      padding: 20px;
      box-sizing: border-box;
    }
    .content .part{
      /*overflow: hidden;*/
    }
    .content .part .title{
      width: 100%;
      height: 20px;
      border-top: 1px solid #eeeeee;
      position: relative;
    }
    .content .part .title span{
      height: 32px;
      line-height: 32px;
      font-size: 14px;
      position: absolute;
      top: -16px;
      left: 0;
      background: #ffffff url("../../../static/images/point.png") 10px center no-repeat;
      padding:0 20px 0 30px;
      font-weight: bold;
    }
    .certificate img{
      float: left;
      margin-right: 20px;
    }
    .certificate .webuploader-container{
      margin: 100px 10px 0 0;
      float: left;
    }
    .certificate span{
      float: left;
      margin: 100px 10px 0 0;
      color: #999999;
    }
    .certificate .webuploader-pick{
      width: 72px;
      height: 32px;
      line-height: 32px;
      background: #009688 url("../../../static/images/enterprise/upload.png") 10px center no-repeat;
      padding-left: 20px;
    }
    .btn{
      width: 100%;
      text-align: center;
      margin-bottom: 50px;
    }
    .btn .el-button{
      width: 73px;
      height: 30px;
      line-height: 30px;
      padding: 0;
    }
    .btn .el-button.el-button--primary{
      background-color: #009688;
      border-color: #009688;
    }
    .btn .el-button.el-button--primary:hover{
      background-color: #01a192;
    }
    .btn .el-button.el-button--default{
      background-color: #ffffff;
      border-color: #bbbbbb;
    }
    .btn .el-button.el-button--default:hover{
      color: #009688;
    }
  }
</style>
