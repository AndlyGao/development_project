<template>
  <div id="bidwinadd" class="smaincontent">
    <div class="headertitle">
      <!--面包屑-->
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/main' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>公告管理</el-breadcrumb-item>
        <el-breadcrumb-item :to="{ path: '/annomanage/bidwin' }">中标候选人公示</el-breadcrumb-item>
        <el-breadcrumb-item>添加中标候选人公示</el-breadcrumb-item>
      </el-breadcrumb>
      <!--面包屑-->
      <div class="returnboxbig">
        <template>
          <el-button @click="close">返回</el-button>
        </template>
      </div>
    </div>
    <div class="contentbigbox">
      <template>
        <el-form :model="winBidForm" :rules="rules" ref="winBidForm">
          <table class="detailtable" style="table-layout:fixed">
            <tr>
              <td><i class="red">*</i>公示名称：</td>
              <td  colspan="2">
                <template>
                  <el-form-item prop="publicityName">
                    <el-input v-model="winBidForm.publicityName" placeholder="请输入公示名称"></el-input>
                  </el-form-item>
                </template>
              </td>
              <td><i class="red">*</i>招标项目编号：</td>
              <td  colspan="2">
                <el-form-item prop="tenderProjectCode">
                  <el-input  v-model="winBidForm.tenderProjectCode" placeholder="请输入招标项目编号" v-bind:disabled="disableFlag"></el-input>
                </el-form-item>
              </td>
            </tr>
            <tr>
              <td><i class="red">*</i>招标项目名称：</td>
              <td  colspan="2">
                <el-form-item prop="tenderExpandsInfo.tenderProjectName">
                  <el-input  v-model="winBidForm.tenderExpandsInfo.tenderProjectName" placeholder="请输入招标项目名称" v-bind:disabled="disableFlag"></el-input>
                </el-form-item>
              </td>
              <td><i class="red">*</i>标段（包）编号：</td>
              <td  colspan="2">
                <el-form-item prop="bidSectionCodes">
                  <el-input  v-model="winBidForm.bidSectionCodes" placeholder="请输入标段（包）编号" v-bind:disabled="disableFlag"></el-input>
                </el-form-item>
              </td>
            </tr>
            <tr>
              <td><i class="red">*</i>项目行业：</td>
              <td  colspan="2">
                <el-form-item prop="tenderExpandsInfo.industryTypeFirst">
                  <el-select v-model="winBidForm.tenderExpandsInfo.industryTypeFirst" class="select" v-bind:disabled="disableFlag">
                    <el-option
                      v-for="item in options"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value">
                    </el-option>
                  </el-select>
                </el-form-item>
              </td>
              <td><i class="red">*</i>行政区域：</td>
              <td  colspan="2">
                <el-form-item prop="tenderExpandsInfo.provinceId">
                  <el-cascader
                    :options="addressOptions"
                    expand-trigger="hover"
                    v-model="provinceIdArray"
                    @change="handlerCityChange"
                    v-bind:disabled="disableFlag">
                  </el-cascader>
                </el-form-item>
              </td>
            </tr>
            <tr>
              <td><i class="red">*</i>公示开始时间：</td>
              <td colspan="2">
                <el-form-item prop="publicityStartTime">
                  <el-date-picker
                    v-model="winBidForm.publicityStartTime"
                    type="datetime"
                    placeholder="请输入公示开始时间"
                    value-format="yyyy-MM-dd HH:mm:ss">
                  </el-date-picker>
                </el-form-item>
              </td>
              <td><i class="red">*</i>公示结束时间：</td>
              <td colspan="2">
                <el-form-item prop="publicityEndTime">
                  <el-date-picker
                    v-model="winBidForm.publicityEndTime"
                    type="datetime"
                    placeholder="请输入公示结束时间"
                    value-format="yyyy-MM-dd HH:mm:ss">
                  </el-date-picker>
                </el-form-item>
              </td>
            </tr>
            <tr>
              <td><i class="red">*</i>公示内容：</td>
              <td  colspan="5">
                <template >
                  <editor ref="ueditor" class="ueditor"></editor>
                </template>
              </td>
            </tr>
            <tr>
              <td>公示附件：</td>
              <td  colspan="5">
                <upload-file @uploadSuccess="uploadOtherSuccess" @deleteSuccess="deleteSuccess" :fileArrays="winBidForm.fileInformations" fileType="9" isImage="0"></upload-file>
              </td>
            </tr>
          </table>
          <el-form-item class="submit-radio">
            <el-button type="primary" @click="submitForm('winBidForm', 1)" :loading="isSubmiting">提交</el-button>
            <el-button type="primary" class="save" @click="submitForm('winBidForm', 0)" :loading="isSubmiting">保存</el-button>
            <el-button class="cancel" @click="close">取消</el-button>
          </el-form-item>
        </el-form>
      </template>
    </div>
  </div>
</template>
<script>
import editor from '@/components/ueditor/ueditor.vue'
import uploadFile from '@/components/upload/publicFileUpload'
import {bidwin, bidanno} from '@/api'
import * as industry from '../../../assets/js/industry'
import * as region from '../../../assets/js/region'
export default {
  components: {
    editor,
    uploadFile
  },
  data () {
    // 电话号码
    return {
      // 禁用标记
      disableFlag: false,
      value: 1,
      // 项目行业
      options: industry.industry,
      // 行政区域
      addressOptions: region.CityInfo,
      // 行政区域绑定数组
      provinceIdArray: [],
      winBidForm: {
        publicityName: '',
        publicityType: '',
        tenderProjectCode: '',
        bidSectionCodes: '',
        publicityStartTime: '',
        publicityEndTime: '',
        publicityReferTime: '',
        tenderExpandsInfo: {
          tenderProjectName: '',
          provinceId: '',
          industryTypeFirst: ''
        },
        sectionExpandsInfos: [],
        fileInformations: []
      },
      rules: {
        publicityName: [
          {required: true, message: '请输入公示名称', trigger: 'blur'},
          {min: 1, max: 600, message: '长度在 1~600个字符', trigger: ['blur', 'change']}
        ],
        tenderProjectCode: [
          {required: true, message: '请输入招标项目编号', trigger: 'blur'},
          {min: 1, max: 20, message: '长度在 1~20字符', trigger: ['blur', 'change']}
        ],
        bidSectionCodes: [
          {required: true, message: '请输入标段编号', trigger: 'blur'},
          {min: 1, max: 200, message: '长度在 1~200字符', trigger: ['blur', 'change']}
        ],
        'tenderExpandsInfo.tenderProjectName': [
          {required: true, message: '请输入招标项目名称', trigger: 'blur'},
          {min: 1, max: 256, message: '长度在 1~256个字符', trigger: ['blur', 'change']}
        ],
        publicityStartTime: [
          {required: true, message: '请选择日期', trigger: 'change'}
        ],
        publicityEndTime: [
          {required: true, message: '请选择日期', trigger: 'change'}
        ],
        'tenderExpandsInfo.industryTypeFirst': [
          {required: true, message: '请选择项目行业', trigger: 'change'}
        ],
        'tenderExpandsInfo.provinceId': [
          {required: true, message: '请选择行政区域', trigger: 'change'}
        ]
      },
      status: null,
      isSubmiting: false
    }
  },
  methods: {
    // 省市两级联动
    handlerCityChange (value) {
      this.winBidForm.tenderExpandsInfo.provinceId = value[0]
      this.winBidForm.tenderExpandsInfo.cityId = value[1]
    },
    // 上传附件
    uploadOtherSuccess (file) {
      this.winBidForm.fileInformations.push(file)
    },
    deleteSuccess (fileId) {
      this.winBidForm.fileInformations = this.winBidForm.fileInformations.filter(item => item.relativePath !== fileId)
    },
    // 提交数据
    submitForm (name, status) {
      this.$refs[name].validate((valid) => {
        if (valid) {
          if (!this.$refs.ueditor.getContent()) {
            this.$message({
              type: 'warning',
              message: '请输入公示内容'
            })
            return false
          }
          this.winBidForm.publicityContent = this.$refs.ueditor.getContent()
          this.winBidForm.status = status
          this.winBidForm.enterpriseId = this.$store.getters.authUser.enterpriseId
          this.isSubmiting = true
          bidwin.save(this.winBidForm).then((res) => {
            if (res.data.resCode === '0000') {
              this.$router.push({path: '/annomanage/bidwin', query: {status: this.status}})
            } else {
              this.isSubmiting = false
              return false
            }
          })
        } else {
          this.$message({
            type: 'warning',
            message: '请检查必填项是否填写完毕'
          })
          return false
        }
      })
    },
    bidAnnoDetail () {
      bidanno.detail(this.$route.params.objectId).then((res) => {
        this.disableFlag = true
        this.winBidForm.relatedCode = res.data.tenderBulletin.code
        this.winBidForm.tenderProjectCode = res.data.tenderBulletin.tenderProjectCode
        this.winBidForm.bidSectionCodes = res.data.tenderBulletin.bidSectionCodes
        this.winBidForm.tenderExpandsInfo = res.data.tenderBulletin.tenderExpandsInfo
        this.provinceIdArray.push(res.data.tenderBulletin.tenderExpandsInfo.provinceId)
        this.provinceIdArray.push(res.data.tenderBulletin.tenderExpandsInfo.cityId)
        this.winBidForm.tenderExpandsInfo.objectId = null
      })
    },
    close () {
      this.$router.push({path: '/annomanage/bidwin', query: {status: this.status}})
    }
  },
  mounted () {
    this.status = this.$route.query.status ? this.$route.query.status : this.$route.query.status === 0 ? this.$route.query.status : null
    if (Number(this.$route.params.objectId) !== 0) {
      this.bidAnnoDetail()
    }
  }
}
</script>
<style lang="less">
  #bidwinadd{
    .select {
      float: left;
      width: 100%;
    }
    .el-cascader{
      float: left;
      width: 100%;
    }
    .el-date-editor{
      float: left;
      width: 100%;
    }
    .el-date-editor.el-input, .el-date-editor.el-input__inner{
      width: 100%;
    }
    .el-form-item{
      margin-bottom: 0px;
    }
    .expandtable{
      margin-bottom: 10px;
    }
    .expandtable  .cell{
      height: 40px;
      line-height: 40px;
    }
    .expandtable  .cell span{
      color: #66b1ff;
    }
  }
</style>
