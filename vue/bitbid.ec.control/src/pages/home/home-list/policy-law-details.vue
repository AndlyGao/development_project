<template>
  <div id="policylaw-details-page" class="policy-law-details">
    <HomeHeader></HomeHeader>
    <div class="banner">
      <img src="../../../../static/images/home/banner.png" alt="">
    </div>
    <div class="policy-law-details-warp">
      <p class="bread-title">当前位置：政策法规 > 详情</p>
      <div class="policy-law-cont">
        <h2>{{titleName}}</h2>
        <div class="contents" v-html="formConts">
          <!-- <ueditor></ueditor> -->
        </div>
        <div class="file-warp">
          <el-form ref="ruleForm" label-width="100px" class="demo-ruleForm">
            <el-form-item label="附件:" v-if="fileUrl.relativePath">
              <fileDownload
                :fileObject="fileUrl">
              </fileDownload>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </div>
    <HomeFooter></HomeFooter>
  </div>
</template>
<script>
import HomeHeader from '../header/header'
import HomeFooter from '../footer/footer'
import {home} from '@/api'
import fileDownload from '@/components/file-download'
export default {
  name: '',
  data () {
    return {
      id: this.$route.params.objectId,
      formConts: '',
      titleName: '',
      fileUrl: {}
    }
  },
  components: {
    HomeHeader,
    HomeFooter,
    fileDownload
  },
  methods: {
    getLawsOne () {
      home.queryLawsOne(this.id).then(res => {
        if (res.data.news) {
          this.titleName = res.data.news.title
          this.formConts = res.data.news.content
          this.fileUrl = res.data.news
          this.fileUrl.relativePath = res.data.news.fileUrl
        }
      })
    }
  },
  mounted () {
    this.getLawsOne()
  }
}
</script>
<style lang="less">
#policylaw-details-page {
  background: #F2F6F9;
  padding-bottom: 1px;
  .policy-law-details-warp .bread-title {
    text-align: left;
    width: 1200px;
    margin: 0 auto;
    padding-top: 12px;
    padding-bottom: 8px;
  }
  .policy-law-cont {
    width: 1200px;
    margin: 0 auto;
    box-sizing: border-box;
    padding: 15px 20px;
    margin-top: 10px;
    margin-bottom: 40px;
    background: #fff;
    .contents {
      min-height: 300px;
      padding: 15px 20px;
      border: 1px solid #e1e1e1;
    }
    .file-warp {
      text-align: left;
      padding-left: 10px;
      border: 1px solid #e1e1e1;
      border-top: none;
      .el-form-item {
        margin-bottom: 0;
      }
    }
  }
}
</style>
