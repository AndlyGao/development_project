<template>
  <div class="cloudcontent" id="cloud_processedit">
    <!--面包屑-->
    <el-breadcrumb separator-class="el-icon-arrow-right" class="breadcrumb_box">
      <el-breadcrumb-item :to="{ path: '/index' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/processManage/projectProcess' }">项目进度管理</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/processManage/projectProcess/project' }">项目管理</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/processManage/projectProcess/project/process' }">流程管理</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/processManage/projectProcess/project/process' }">定标结果</el-breadcrumb-item>
      <el-breadcrumb-item class="active_bread">修改中标候选人公示</el-breadcrumb-item>
    </el-breadcrumb>
    <!--面包屑-->
    <div class="main">
      <el-form :model="updateForm" :rules="rules"  ref="updateForm" :validate-on-rule-change="true">
        <el-row>
          <el-col :span="12">
            <el-form-item label="项目编号：">
              <el-input v-model="updateForm.tenderProjectCode" disabled></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="项目名称：">
              <el-input v-model="updateForm.tenderProjectName" disabled></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="标段编号：">
              <el-input v-model="updateForm.bidSectionCode" disabled></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="标段名称：">
              <el-input v-model="updateForm.bidSectionName" disabled></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="中标候选人："  prop="candidateList">
              <el-button class="addbtn" @click="addCandidateBtn">+ 新增</el-button>
              <el-table
                :data="updateForm.candidateList"
                border
                style="width: 100%" header-cell-class-name="tableheader">
                <el-table-column
                  prop="name"
                  label="中标候选人名称"
                  show-overflow-tooltip>
                  <template slot-scope='scope'>
                    <el-input v-model="scope.row.name"></el-input>
                  </template>
                </el-table-column>
                <el-table-column
                  label="操作" align="center" width="100">
                  <template slot-scope="scope">
                    <el-button type="text" size="small" @click="delCandidateBtn(scope.$index)">删除</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24" class="ueditor_box">
            <el-form-item label="公示内容：" class="bitianicon">
              <editor ref="notice_ueditor" class="ueditor" :editread="editread"></editor>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24" class="ueditor_box">
            <el-form-item label="其他内容：">
              <editor ref="other_ueditor" class="ueditor" :editread="editread"></editor>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="附件：">
              <upload-file @uploadSuccess="uploadOtherSuccess" @deleteSuccess="deleteSuccess"
                           :fileArrays="updateForm.fileInformationList"
                           fileType="9"
                           isImage="0">
              </upload-file>
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
import editor from '@/components/ueditor/ueditor.vue'
import uploadFile from '@/components/upload/publicFileUpload'
import {bulletinInfo} from '@/api/cloudplatform/index'
export default {
  components: {
    editor,
    uploadFile
  },
  data () {
    return {
      isSubmiting: false,
      updateForm: {
        tenderProjectCode: '',
        fileInformationList: [],
        candidateList: []
      },
      rules: {
        title: [
          { required: true, message: '公告名称不能为空', trigger: ['blur', 'change'] }
        ],
        candidateList: [
          { required: true, message: '中标候选人不能为空', trigger: ['blur', 'change'] }
        ]
      },
      // 富文本
      editread: false
    }
  },
  methods: {
    /**
     * 增加中标候选人
     */
    addCandidateBtn () {
      this.updateForm.candidateList.push({})
    },
    delCandidateBtn (index) {
      this.updateForm.candidateList.splice(index, 1)
    },
    // 上传附件
    uploadOtherSuccess (file) {
      this.updateForm.fileInformationList.push(file)
    },
    deleteSuccess (fileId) {
      this.updateForm.fileInformationList = this.updateForm.fileInformationList.filter(item => item.relativePath !== fileId)
    },
    detail () {
      bulletinInfo.getOne(this.$route.query.code).then((res) => {
        if (res.data.resCode === '0000') {
          this.updateForm = res.data.bulletinInfo
          this.$refs.notice_ueditor.setContent(this.updateForm.content ? this.updateForm.content : '')
          this.$refs.other_ueditor.setContent(this.updateForm.otherContent ? this.updateForm.otherContent : '')
          // 候选人信息的拆分
          this.updateForm.candidateList = this.splitCandidateName(this.updateForm.candidateName)
        }
      })
    },
    /** 拆分候选人信息 */
    splitCandidateName (candidateNameStr) {
      let candidateList = []
      let list = candidateNameStr.split(',')
      if (list.length > 0) {
        for (let i = 0; i < list.length; i++) {
          candidateList.push({name: list[i]})
        }
      }
      return candidateList
    },
    // 提交/保存
    submit (form, status) {
      this.$refs[form].validate((valid) => {
        if (valid) {
          let content = this.$refs.notice_ueditor.getContent()
          let otherContent = this.$refs.other_ueditor.getContent()
          if (!content) {
            this.$message({
              type: 'warning',
              message: '请输入公告内容'
            })
            return false
          }
          this.isSubmiting = true
          // 根据type设置当前招标项目的状态
          if (Object.is(status, 1)) {
            this.updateForm.auditStatus = 4 // 暂时不考虑审批
          } else {
            this.updateForm.auditStatus = 0
          }
          this.updateForm.content = content
          if (otherContent) {
            this.updateForm.otherContent = otherContent
          }
          this.updateForm.fileInformationList.map(item => {
            item.objectId = null
          })
          this.updateForm.candidateName = this.mergeCandidateName(this.updateForm.candidateList)
          bulletinInfo.save(this.updateForm).then((res) => {
            if (res.data.resCode === '0000') {
              this.isSubmiting = false
              this.$router.go(-1)
            }
          })
        } else {
          return false
        }
      })
    },
    /** 拼接候选人名称 */
    mergeCandidateName (candidateList) {
      let str = ''
      for (let i = 0; i < candidateList.length; i++) {
        if (i !== candidateList.length - 1) {
          str += candidateList[i].name
          str += ','
        } else {
          str += candidateList[i].name
        }
      }
      return str
    },
    // 取消
    close () {
      this.$router.go(-1)
    },
    init () {
      this.detail()
    }
  },
  mounted () {
    this.init()
  }
}
</script>
<style lang="less">
  #cloud_processedit{
    .addbtn{
      border: 1px solid #3f63f6;
      height: 32px;
      padding: 7px 12px;
      margin-bottom: 12px;
    }
  }
</style>
