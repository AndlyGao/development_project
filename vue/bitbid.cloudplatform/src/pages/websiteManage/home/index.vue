<template>
  <div class="cloudcontent" id="cloud_webhome">
    <!--面包屑-->
    <el-breadcrumb separator-class="el-icon-arrow-right" class="breadcrumb_box">
      <el-breadcrumb-item :to="{ path: '/index' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item class="active_bread">网站首页管理</el-breadcrumb-item>
    </el-breadcrumb>
    <!--面包屑-->
    <div class="main">
      <ul class="model_list">
        <li>
          <router-link :to="{ path: '/websiteManage/home/homeImage' }">
            <img src="../../../../static/images/webmanage/home1.png"/>
            <span>首页图片</span>
          </router-link>
        </li>
        <li>
          <router-link :to="{ path: '/websiteManage/home/imageNews' }">
            <img src="../../../../static/images/webmanage/home2.png"/>
            <span>图片新闻</span>
          </router-link>
        </li>
        <li>
          <router-link :to="{ path: '/websiteManage/home/adManage' }">
            <img src="../../../../static/images/webmanage/home3.png"/>
            <span>广告管理</span>
          </router-link>
        </li>
        <li>
          <router-link :to="{ path: '/websiteManage/home/enterpriseImage' }">
            <img src="../../../../static/images/webmanage/home4.png"/>
            <span>企业图片</span>
          </router-link>
        </li>
        <li>
          <router-link :to="{ path: '/websiteManage/home/cooperator' }">
            <img src="../../../../static/images/webmanage/home5.png"/>
            <span>合作单位</span>
          </router-link>
        </li>
      </ul>
    </div>
  </div>
</template>
<script>
export default {
  components: {
  },
  data () {
    return {
    }
  },
  methods: {
  }
}
</script>
<style lang="less">
  #cloud_webhome {
    .main ul.model_list{
      margin: 0 auto;
      padding: 20px 0;
    }
    .main ul.model_list:after {
      display: block;
      content: "";
      clear: both;
    }
    .main ul.model_list li{
      width: 130px;
      height: 170px;
      border: 1px solid #cccccc;
      float: left;
      margin: 0 30px;
      text-align: center;
      -webkit-border-radius: 10px;
      -moz-border-radius: 10px;
      border-radius: 10px;
    }
    .main ul.model_list li img{
      margin-top: 30px;
    }
    .main ul.model_list li span{
      width: 100%;
      display: block;
      line-height: 32px;
      overflow: hidden;
      margin-top: 16px;
    }
    .main ul.model_list li:hover{
      border: 1px solid #3f63f6;
      box-shadow:0px 0px  10px 5px #eeeeee;
    }
  }
</style>
