<template>
  <div id="bidwindetail" class="smaincontent">
    <div class="headertitle">
      <!--面包屑-->
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/main' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>公告管理</el-breadcrumb-item>
        <el-breadcrumb-item :to="{ path: '/annomanage/bidwin' }">中标候选人公示</el-breadcrumb-item>
        <el-breadcrumb-item>查看中标候选人公示</el-breadcrumb-item>
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
        <el-form :model="winBidForm" ref="winBidForm">
          <table class="detailtable">
            <tr>
              <td>公示名称：</td>
              <td  colspan="2">
                <span :title="winBidForm.publicityName">{{winBidForm.publicityName}}</span>
              </td>
              <td>公示性质：</td>
              <td  colspan="2">
                <span v-if="winBidForm.noticeNature === '1'">正常公示</span>
                <span v-if="winBidForm.noticeNature === '2'">变更公示</span>
                <span v-if="winBidForm.noticeNature === '9'">其他</span>
              </td>
            </tr>
            <tr>
              <td>招标项目编号：</td>
              <td  colspan="2">
                <span :title="winBidForm.tenderProjectCode">{{winBidForm.tenderProjectCode}}</span>
              </td>
              <td>招标项目名称：</td>
              <td  colspan="2">
                <span :title="winBidForm.tenderExpandsInfo.tenderProjectName">{{winBidForm.tenderExpandsInfo.tenderProjectName}}</span>
              </td>
            </tr>
            <tr>
              <td>项目行业：</td>
              <td  colspan="2">
                <span>{{industryStr}}</span>
              </td>
              <td>行政区域：</td>
              <td  colspan="2">
                <span>{{regionStr}}</span>
              </td>
            </tr>
            <tr>
              <td>标段（包）编号：</td>
              <td colspan="2">
                <span :title="winBidForm.bidSectionCodes">{{winBidForm.bidSectionCodes}}</span>
              </td>
              <td>公示开始时间：</td>
              <td  colspan="2">
                <span>{{winBidForm.publicityStartTime}}</span>
              </td>
            </tr>
            <tr>
              <td>公示结束时间：</td>
              <td  colspan="2">
                <span>{{winBidForm.publicityEndTime}}</span>
              </td>
              <td>公示发布时间：</td>
              <td  colspan="2">
                <span>{{winBidForm.publicityReferTime}}</span>
              </td>
            </tr>
            <tr v-if="fileObject !== null">
              <td>公示附件：</td>
              <td  colspan="5">
                <file-download :fileArrays="winBidForm.fileInformations" fileType="9"></file-download>
              </td>
            </tr>
            <tr v-if="showNotice">
              <td>公示内容：</td>
              <td  colspan="5">
                <editor ref="ueditor" class="ueditor" :editread="true"></editor>
              </td>
            </tr>
            <tr v-if="winBidForm.noticeNature === '2'">
              <td>变更公示内容：</td>
              <td  colspan="5">
                <editor ref="change_ueditor" class="ueditor" :editread="true"></editor>
              </td>
            </tr>
            <approverecord v-if="winBidForm.status === 2 || winBidForm.status === 3" :code="winBidForm.code"></approverecord>
          </table>
        </el-form>
      </template>
    </div>
  </div>
</template>
<script>
import editor from '@/components/ueditor/ueditor.vue'
import fileDownload from '@/components/file-download'
import approverecord from '@/components/approve-record.vue'
import {bidwin} from '@/api'
import * as industry from '../../../assets/js/industry'
import * as region from '../../../assets/js/region'
export default {
  components: {
    editor,
    fileDownload,
    approverecord
  },
  data () {
    return {
      showNotice: false,
      industryList: industry.industry,
      addressOptions: region.CityInfo,
      regionStr: '',
      industryStr: '',
      fileObject: {},
      winBidForm: {
        noticeName: '',
        noticeNature: '',
        tenderProjectCode: '',
        tenderExpandsInfo: {
          tenderProjectName: ''
        }
      },
      status: null
    }
  },
  methods: {
    detail () {
      bidwin.detail(this.$route.params.objectId).then((res) => {
        // 行业
        this.industryList.map((item) => {
          if (item.value === res.data.record.tenderExpandsInfo.industryTypeFirst) {
            this.industryStr = item.label
          }
        })
        // 行政区域
        this.addressOptions.map((item) => {
          if (item.value === res.data.record.tenderExpandsInfo.provinceId) {
            this.regionStr += item.label
            item.children.map((ite) => {
              if (ite.value === res.data.record.tenderExpandsInfo.cityId) {
                this.regionStr += ite.label
              }
            })
          }
        })
        this.winBidForm = res.data.record
        this.fileObject = res.data.record.fileInformations.length === 0 ? null : res.data.record.fileInformations[0]
        this.showNotice = true
        return this.winBidForm
      }).then((result) => {
        if (result.noticeNature === '2') {
          this.$refs.ueditor.setContent(result.originalBulletinContent)
          this.$refs.change_ueditor.setContent(result.publicityContent)
        } else {
          this.$refs.ueditor.setContent(result.publicityContent)
        }
      })
    },
    close () {
      this.$router.push({path: '/annomanage/bidwin', query: {status: this.status}})
    }
  },
  mounted () {
    this.status = this.$route.query.status ? this.$route.query.status : this.$route.query.status === 0 ? this.$route.query.status : null
    this.detail()
  }
}
</script>
<style lang="less">
  #bidwindetail{
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
