<template>
  <div id="rotate-add" class="smaincontent">
    <div class="headertitle">
      <!--面包屑-->
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/main' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>信息管理</el-breadcrumb-item>
        <el-breadcrumb-item :to="{ path: '/index/info-manage/download' }">下载中心</el-breadcrumb-item>
        <el-breadcrumb-item>添加下载</el-breadcrumb-item>
      </el-breadcrumb>
      <div class="returnboxbig">
        <template>
          <el-button @click="$router.go(-1)">返回</el-button>
        </template>
      </div>
      <!--面包屑-->
    </div>
    <div class="contentbigbox">
      <el-form :model="ruleForm" :rules="rules" ref="ruleForm" label-width="100px" class="demo-ruleForm">
        <el-form-item label="标题:" prop="title">
          <el-input v-model="ruleForm.title" style="width:300px"></el-input>
        </el-form-item>
        <el-form-item label="类型:" prop="type">
          <el-select v-model="ruleForm.type" class="select" style="width:300px">
            <el-option label="协会" value="1"></el-option>
            <el-option label="专家库" value="2"></el-option>
            <el-option label="交易平台" value="3"></el-option>
            <el-option label="其他" value="4"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="文件:" prop="fileInformations">
          <upload-file @uploadSuccess="uploadSuccess" @deleteSuccess="deleteSuccess" :fileArrays="ruleForm.fileInformations" fileType="9" isImage="0" fileMaxNum="5" ></upload-file>
        </el-form-item>
        <el-form-item label="备注:" prop="remark">
          <el-input v-model="ruleForm.remark" style="width:300px"></el-input>
        </el-form-item>
        <el-form-item class="submit-radio">
          <el-button type="primary" @click="submitForm('ruleForm')">确 定</el-button>
          <el-button @click="$router.go(-1)">取 消</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>
<script>
import uploadFile from '@/components/upload/publicFileUpload'
import {download} from '@/api'
export default {
  data () {
    return {
      ruleForm: {
        fileInformations: []
      },
      rules: {
        title: [
          { required: true, message: '请填写标题', trigger: 'blur' },
          { min: 1, max: 256, message: '长度在 1到 256 个字符', trigger: 'blur' }
        ],
        type: [
          { required: true, message: '请选择下载类型', trigger: 'change' }
        ],
        fileInformations: [
          { required: true, message: '请上传文件', trigger: 'change' }
        ]
      }
    }
  },
  components: {
    uploadFile
  },
  methods: {
    submitForm (name) {
      this.$refs[name].validate((valid) => {
        if (valid) {
          download.save(this.ruleForm).then((res) => {
            this.$router.push({path: '/index/info-manage/download'})
          })
        } else {
          return false
        }
      })
    },
    uploadSuccess (file) {
      this.ruleForm.fileInformations.splice(0, 1, file)
    },
    deleteSuccess (fileId) {
      this.ruleForm.fileInformations = this.ruleForm.fileInformations.filter(item => item.relativePath !== fileId)
    }
  },
  mounted () {
  }
}
</script>
<style lang="less">
  #rotate-add {
    .contentbigbox .demo-ruleForm {
      .el-form-item__content {
        text-align: left;
      }
      .ueditor-wrap {
        padding: 0;
      }
      .btns {
        margin-top: 0;
      }
    }
    .form-ueditor {
      .el-form-item__content {
        line-height: 0;
      }
    }
  }
</style>
