<template>
  <div id="report_box" >
    <!--pdf展示定标报告-->
    <div v-if="currentUser.roleIds[0] === 1">
<!--      <iframe id="iframe_box" :src="pdfLink"-->
<!--              style="width: 100%;height: 86vh;margin: 0 auto"-->
<!--              frameborder="0">-->
<!--      </iframe>-->
      <div class="confirm">
        <el-button type="success" class="btn" size="mini" @click="confirmReportBtn" :disabled="isHide">确认</el-button>
      </div>
      <div id="iframe_bigbox" v-loading="loading" element-loading-text="生成中，请等待" element-loading-spinner="el-icon-loading">
        <pdf ref="pdfInit" :objectId="objectIdQuery" :pdfData="pdfData"></pdf>
      </div>
      <!--底部-->
      <footers v-if="isShowFooter"></footers>
      <!--底部-->
    </div>
    <!--pdf展示定标报告-->
    <!--其他方式定标报告-->
    <div v-else>
      <!--项目信息-->
      <project :projectInfoForm="projectInfoForm"></project>
      <!--项目信息-->
      <div class="scaling-box">
        <div class="title-box">
          <p>定标结果</p>
        </div>
        <div class="formmian_projectbox">
          <el-form label-width="150px" class="project_box">
            <el-row>
              <el-col :span="12">
                <el-form-item label="中标人：">
                  <span>{{showBidwinner?bidWinner:''}}</span>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="12">
                <el-form-item label="定标报告：">
                  <fileDownload :fileObject="fileObject"></fileDownload>
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </div>
      </div>
    </div>
    <!--其他方式定标报告-->
  </div>
</template>

<script>
// 状态
import footers from '@/views/admin/footer/footer.vue'
import project from '@/components/project.vue'
import fileDownload from '@/components/file-download'
import {projectInfo, report} from '@/api'
// import {pdfUrl} from '@/assets/js/common'
import pdf from '@/components/report-pdf.vue'
export default {
  name: '',
  components: {
    footers,
    project,
    fileDownload,
    pdf
  },
  data () {
    return {
      currentUser: this.$store.getters.authUser,
      // basePdfUrl: pdfUrl,
      pdfWidth: '',
      pdfHeight: '',
      loading: true,
      projectId: this.$store.getters.projectId,
      // userType等于1表示代理
      // roleType: this.$store.getters.authUser.userType,
      fileObject: {},
      projectInfoForm: {},
      users: [],
      bidWinner: '', // 中标人
      showBidwinner: false,
      objectIdQuery: '',
      pdfData: {},
      isHide: false,
      isShowFooter: false
    }
  },
  methods: {
    detail () {
      if (!this.projectId) {
        return
      }
      projectInfo.getScalingReport(this.projectId).then((res) => {
        // console.log(res)
        if (res.data.resCode === '0000') {
          this.projectInfoForm = res.data.ProjectView.project
          if (res.data.ProjectView.users.length > 0) {
            this.bidWinner = res.data.ProjectView.users[0].userName
          }
          let report = res.data.report
          if (report && (report.fileId || report.fileName)) {
            this.showBidwinner = true
            this.$set(this.fileObject, 'fileName', report.fileName)
            this.$set(this.fileObject, 'fileUrl', report.fileId)
          }
        }
      })
    },
    report () {
      if (!this.projectId) {
        return
      }
      report.getReport(this.projectId).then((res) => {
        if (res.data.resCode === '0000') {
          let result = res.data.Report
          if (result && result.fileId) {
            this.objectIdQuery = result.objectId
            let url = {
              doc: result.fileId,
              objectId: result.objectId
            }
            report.pdfDetail(url).then((res) => {
              this.pdfData = res.data
              this.$refs.pdfInit.init(res.data.fileName, res.data.filePath)
            })
            this.loading = false
          } else {
            report.createReport(this.projectId).then((res) => {
              if (res.data.resCode === '0000') {
                this.report()
              }
            })
          }
        } else if (res.data.resCode === '4444') {
          this.loading = false
          return false
        }
      })
    },
    // 查询是否生成报告
    viewReport () {
      report.getReport(this.projectId).then(res => {
        let temp = res.data.Report
        this.isHide = res.data.isHide === '1'
        if (temp && (temp.fileId || temp.fileName)) {
          this.showBidwinner = true
          this.$set(this.fileObject, 'fileName', temp.fileName)
          this.$set(this.fileObject, 'fileUrl', temp.fileId)
        }
      })
    },
    confirmReportBtn () {
      report.confirmReport(this.projectId, this.objectIdQuery).then((res) => {
        if (res.data.resCode === '0000') {
          this.isHide = 1
        }
      })
    }
  },
  mounted () {
    if (this.currentUser.roleIds[0] !== 1) {
      this.isShowFooter = true
      this.detail()
    } else {
      this.isShowFooter = false
      this.viewReport()
      this.report()
    }
  }
}
</script>

<style lang="scss">
  #report_box{
    width: 100%;
    height: 100%;
    #iframe_bigbox{
      width: 100%;
      height: 100%;
    }
    .scaling-box{
      background: #fff;
      margin-top: 15px;
      padding: 20px;
    }
    .confirm {
      margin: 24px 24px;
      text-align: left;
    }
    .confirm .btn{
      /*width: 103px;*/
      font-size: 14px;
    }
  }
</style>
