<template>
  <div id="contasscontractdetailebox"  class="maincontent">
    <div class="headertitle">
      <!--面包屑-->
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/mr/index?roledsType=1&showStatus=false' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>标后业务</el-breadcrumb-item>
        <el-breadcrumb-item :to="{ path: '/mr/transferor/post-bid-business/contract-view?roledsType=1'}">合同提交</el-breadcrumb-item>
        <el-breadcrumb-item>查看合同</el-breadcrumb-item>
      </el-breadcrumb>
      <!--面包屑-->
    </div>
    <div class="contentbigbox detailcontentbigbox">
      <el-form :model="contractForm" ref="contractForm" label-width="100px" class="demo-ruleForm">
        <!--项目信息-->
        <div class="proinfobox">
          <span class="title">项目信息</span>
          <div class="formmian">
            <el-row>
              <el-col :span="8">
                <el-form-item label="项目名称：" prop="sectionName">
                  <span>{{sectionName}}</span>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="项目编号：" prop="sectionNumber">
                  <span>{{sectionNumber}}</span>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="公告名称：" prop="unifiedTransCode">
                  <span>{{annoNumber}}</span>
                </el-form-item>
              </el-col>
            </el-row>
          </div>
        </div>
        <!--项目信息-->
        <!--合同信息-->
        <div class="proinfobox">
          <span class="title" style="border-left: 3px solid #2d9631">合同信息</span>
          <div class="formmain">
            <el-row>
              <el-col :span="8">
                <el-form-item label="转让方名称：" prop="transferorName">
                  <span>{{contractForm.transferorName}}</span>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="受让方名称：" prop="publicityName">
                  <span>{{contractForm.publicityName}}</span>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="合同金额(万元)：" prop="contractMoney">
                  <span>{{contractForm.contractMoney}}</span>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="合同签署时间：" prop="contractDate">
                  <span>{{contractForm.contractDate}}</span>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="合同附件" prop="" class="filebigbox">
                  <commonFileDownload :fileObjecit="fileObjecit"></commonFileDownload>
                </el-form-item>
              </el-col>
              <el-col :span="24">
                <el-form-item label="说明：" prop="content">
                  <span>{{contractForm.content}}</span>
                </el-form-item>
              </el-col>
            </el-row>
          </div>
        </div>
        <!--合同信息-->
        <!--审批记录-->
        <div class="proinfobox">
          <span class="title" style="border-left: 3px solid #fe8900">审批记录</span>
          <el-table
            :data="approvaltableData"
            border>
            <el-table-column
              prop="approvrResult"
              label="审批结果"
              align="left">
              <template slot-scope="scope">
                <div v-if="parseInt(scope.row.approvrResult) === 1">
                  <span class="statusbox">审批通过</span>
                </div>
                <div v-if="parseInt(scope.row.approvrResult) === 0">
                  <span class="statusbox">审批不通过</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column
              prop="approverName"
              label="操作人"
              align="left">
            </el-table-column>
            <el-table-column
              prop="approvrDate"
              label="操作时间"
              align="left">
            </el-table-column>
            <el-table-column
              prop="approvrOpinion"
              label="审批意见"
              align="left" width="700">
            </el-table-column>
          </el-table>
        </div>
        <!--审批记录-->
      </el-form>
    </div>
  </div>
</template>
<script>
import { contract } from '@/api'
import commonFileDownload from '@/components/public-resource/mineral-rights/common/file-download.vue'
export default {
  components: {
    commonFileDownload
  },
  data () {
    return {
      contractForm: {},
      sectionName: {},
      sectionNumber: {},
      annoNumber: {},
      payTypes: [
        {
          value: 0,
          label: '在线缴纳'
        },
        {
          value: 1,
          label: '保函'
        },
        {
          value: 2,
          label: '其他'
        },
        {
          value: 3,
          label: '支票'
        },
        {
          value: 4,
          label: '线下汇款'
        }
      ],
      payTypesvalue: '',
      approvaltableData: [],
      fileObjecit: {}
    }
  },
  methods: {
    list () {
      contract.queryOne(this.$route.params.objectId).then((res) => {
        if (res.data.contractOneInfo.fileInformations) {
          this.fileObjecit = res.data.contractOneInfo.fileInformations
        }
        this.contractForm = res.data.contractOneInfo
        this.sectionName = res.data.contractOneInfo.section.sectionName
        this.sectionNumber = res.data.contractOneInfo.section.sectionNumber
        this.annoNumber = res.data.contractOneInfo.section.announcement.annoNumber
        this.approvaltableData = res.data.contractOneInfo.approveRecords
      })
    }
  },
  computed: {
    IEVersion () {
      let browser = navigator.appName
      let bversion = navigator.appVersion
      let version = bversion.split(';')
      let trimVersion = version[1].replace(/[ ]/g, '')
      if (browser === 'Microsoft Internet Explorer' && trimVersion === 'MSIE9.0') {
        return 1
      }
    }
  },
  created () {},
  mounted () {
    this.list()
  }
}
</script>
<style lang="less">
  @import '../../../../../assets/css/mineral/common.css';
</style>
