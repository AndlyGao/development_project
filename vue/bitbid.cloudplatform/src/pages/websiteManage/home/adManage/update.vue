<template>
  <div class="cloudcontent" id="cloud_webhome">
    <!--面包屑-->
    <el-breadcrumb separator-class="el-icon-arrow-right" class="breadcrumb_box">
      <el-breadcrumb-item :to="{ path: '/index' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/websiteManage/home' }">网站首页管理</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/websiteManage/home/adManage' }">广告管理</el-breadcrumb-item>
      <el-breadcrumb-item class="active_bread">修改广告管理</el-breadcrumb-item>
    </el-breadcrumb>
    <!--面包屑-->
    <div class="main">
      <el-form :model="updateForm" :rules="rules" ref="updateForm" :validate-on-rule-change="true">
        <el-row>
          <el-col :span="12">
            <el-form-item label="标题："  prop="agencyContractCode">
              <el-input v-model="updateForm.agencyContractCode"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="URL地址："  prop="agencyContractCode">
              <el-input v-model="updateForm.agencyContractCode"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="图片："  prop="agencyContractName">
              <upload-file @uploadSuccess="uploadOtherSuccess" @deleteSuccess="deleteSuccess" :fileArrays="updateForm.fileInformations" fileType="9" isImage="1"></upload-file>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-form-item label="">
            <el-col style="color: #ff0000;">注：建议图片尺寸：宽290px，高111px</el-col>
          </el-form-item>
        </el-row>
        <el-form-item class="submit-radio">
          <el-button type="primary" @click="submitBtn('updateForm')">提交</el-button>
          <el-button type="primary" @click="saveBtn('updateForm')">保存</el-button>
          <el-button class="cancal" @click="$router.go(-1)">取消</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>
<script>
import uploadFile from '@/components/upload/publicFileUpload'
export default {
  components: {
    uploadFile
  },
  data () {
    return {
      updateForm: {
        fileInformations: {}
      }
    }
  },
  methods: {
    // 上传附件
    uploadOtherSuccess (file) {
      this.updateForm.fileInformations.push(file)
    },
    deleteSuccess (fileId) {
      this.updateForm.fileInformations = this.tenderBulletin.fileInformations.filter(item => item.relativePath !== fileId)
    }
  }
}
</script>
<style lang="less">
  #cloud_webhome {
  }
</style>
