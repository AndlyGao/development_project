<template>
  <div id="negRevSum" class="content_bgibox">
    <!--谈判纪要-->
    <div class="setup_bigbox">
      <div class="proinfobox">
        <span class="line_bigbox"></span>
        <span class="title_bigbox">谈判纪要</span>
      </div>
      <div class="score_bigbox" style="padding: 20px;border: none">
        <el-form :model="ruleForm" ref="ruleForm" label-width="240px" class="demo-ruleForm">
          <el-form-item label="谈判纪要：" prop="content">
            <div class="editor">
              <editor ref="ueditor" class="ueditor" :content="briefContent" :editread="true"></editor>
            </div>
          </el-form-item>
          <el-form-item label="附件：" prop="content">
            <fileDownload :oldFileName="oldFileName" :filePath="briefFilePath"></fileDownload>
          </el-form-item>
          <el-form-item label="发布状态:" prop="content">
            <span>{{ruleForm.briefStatusName}}</span>
          </el-form-item>
          <el-form-item label="上传澄清与承诺文件截止时间：">
            <span>{{ruleForm.submitTime}}</span>
          </el-form-item>
          <el-form-item label="驳回理由：" v-if="ruleForm.briefReason">
            <span>{{ruleForm.briefReason}}</span>
          </el-form-item>
        </el-form>
      </div>
      <!--按钮-->
      <div class="sub_btnbigbox" style="padding-bottom: 50px" v-if="ruleForm.briefStatus === 1 && headmanStatus">
        <el-button type="primary" class="confirm_btn" @click="publish">确认并发布</el-button>
        <el-button type="primary" class="withdraw_btn" @click="back">驳 回</el-button>
      </div>
      <!--按钮-->
      <!--驳回理由-->
      <el-dialog title="驳回理由" :visible.sync="dialogFormVisible" width="40%" :before-close="cancelBtn">
        <el-form :model="rejectedFrom" label-width="120px" ref="rejectedFrom" :rules="rules">
          <el-form-item label="驳回理由：" prop="briefReason">
            <el-input type="textarea" v-model="rejectedFrom.briefReason"></el-input>
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button type="primary" @click="determineBtn('rejectedFrom')">确 定</el-button>
        </div>
      </el-dialog>
      <!--驳回理由-->
    </div>
    <!--谈判纪要-->
  </div>
</template>
<script>
import fileDownload from '@/components/common/file-download'
import editor from '@/components/common/ueditor/ueditor.vue'
import { negotiatingPoints } from '@/api'
export default {
  components: {
    fileDownload,
    editor
  },
  data () {
    return {
      // 谈判纪要数据
      ruleForm: {},
      rules: {
        briefReason: [
          { required: true, message: '请输入驳回理由', trigger: ['blur', 'change'] },
          {min: 1, max: 500, message: '长度在 1 到 500 个字符', trigger: ['blur', 'change']}
        ]
      },
      // 富文本编辑
      dialogFormVisible: false, // 驳回弹框
      rejectedFrom: {}, // 驳回表单
      briefContent: ' ',
      headmanStatus: false, // 是否是组长
      oldFileName: '', // 附件名字
      briefFilePath: '', // 附件路径
      oldFileOtherName: '', // 采购文件名字
      briefOtherFilePath: '' // 采购文件路径
    }
  },
  methods: {
    // 谈判纪要
    negotiationBriefLists () {
      negotiatingPoints.negotiationBriefList(this.$route.params.num).then((res) => {
        if (res.data.BidOpenNegotiatingBrief.briefStatus) {
          this.ruleForm = res.data.BidOpenNegotiatingBrief
          this.briefContent = res.data.BidOpenNegotiatingBrief.briefContent // 获取富文本内容
          this.$refs.ueditor.setContent(this.briefContent) // 进行赋值
          if (this.ruleForm.briefFile) { // 附件
            this.oldFileName = this.ruleForm.briefFile
            this.briefFilePath = this.ruleForm.briefFilePath
          }
        }
      })
    },
    // 发布
    publish () {
      negotiatingPoints.publish(this.$route.params.num).then((res) => {
        this.negotiationBriefLists()
      })
    },
    // 驳回
    back () {
      this.dialogFormVisible = true
    },
    // 确定驳回
    determineBtn (formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          let reasonParameter = {
            briefReason: this.rejectedFrom.briefReason
          }
          negotiatingPoints.backNeoBrief(this.$route.params.num, reasonParameter).then((res) => {
            this.negotiationBriefLists()
            this.dialogFormVisible = false
            this.$refs['rejectedFrom'].resetFields()
          })
        } else {
          return false
        }
      })
    },
    // 关闭内容
    cancelBtn () {
      this.dialogFormVisible = false
      this.$refs['rejectedFrom'].resetFields()
    }
  },
  watch: {
    '$route': 'negotiationBriefLists'
  },
  mounted () {
    this.$store.getters.authUser.roleIds.map((its) => {
      if (its === 2 || its === 9) {
        this.headmanStatus = true
      }
    })
    this.negotiationBriefLists()
  }
}
</script>
<style lang="less">
  #negRevSum{
    margin-top: 20px;
    .tableDataSupplier_sonbox{
      width: 80%;
      padding-bottom: 30px;
    }
    .project_summary_bigbox{
      margin-top: 20px;
    }
    .project_summary_box{
      width: 100%;
      min-height: 100px;
      box-sizing: border-box;
      box-shadow: 0 0 10px rgba(0,0,0,0.2);
    }
    .el-table .warning-row {
      background: #eceffe;
    }
    // 表格hover事件
    .el-table--enable-row-hover .el-table__body tr:hover>td{
      background-color: transparent !important;
    }
  }
</style>
