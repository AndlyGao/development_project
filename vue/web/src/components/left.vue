<template>
  <el-col :span="3" style="height: 100%;background-color: rgb(25, 35, 60);">
    <div class="logobigbox logo">
      <img src="../../static/images/logo.png" alt="">
    </div>
    <el-scrollbar style="height: 100%;">
    <el-menu
      :default-active="activeNav"
      class="el-menu-vertical-demo sidebox"
      background-color="#19233c"
      text-color="#cccccc"
      :router="true"
      :unique-opened="true"
      @close="handleClose"
      @open="handleOpen">
      <el-menu-item index="/main" class="indexs">
        <span class="iconimg">
          <img src="../../static/images/public/shouye.png" alt="">
        </span>
        <span slot="title" class="titlesbox" :title="'首页'" v-if="[1, 2, 3].includes($store.getters.authUser.userType)">首页</span>
      </el-menu-item>
      <el-submenu index="2" v-if="[1].includes($store.getters.authUser.userType)">
        <template slot="title">
          <span class="iconimg">
            <img src="../../static/images/public/systemuser.png" alt="">
          </span>
          <span class="titlesbox" :title="'管理员管理'" v-if="[1].includes($store.getters.authUser.userType)">管理员管理</span>
        </template>
        <el-menu-item index="/index/adminuser" v-if="[1].includes($store.getters.authUser.userType)">管理员管理</el-menu-item>
      </el-submenu>
      <el-submenu index="3" v-if="[1, 2].includes($store.getters.authUser.userType)">
        <template slot="title">
          <span class="iconimg">
            <img src="../../static/images/public/commonuser.png" alt="">
          </span>
          <span class="titlesbox" :title="'会员管理'" v-if="[1, 2].includes($store.getters.authUser.userType)">会员管理</span>
        </template>
        <el-menu-item index="/index/commonuser" v-if="[1, 2].includes($store.getters.authUser.userType)">会员管理</el-menu-item>
      </el-submenu>
      <el-submenu index="4" v-if="[1, 2, 3].includes($store.getters.authUser.userType)">
        <template slot="title">
          <span class="iconimg">
            <img src="../../static/images/public/bidmanage.png" alt="">
          </span>
          <span class="titlesbox" :title="'公告管理'" v-if="[1, 2, 3].includes($store.getters.authUser.userType)">公告管理</span>
        </template>
        <el-menu-item index="/annomanage/bidanno" v-if="[1, 2, 3].includes($store.getters.authUser.userType)">招标公告</el-menu-item>
        <el-menu-item index="/annomanage/bidwin" v-if="[1, 2, 3].includes($store.getters.authUser.userType)">中标候选人公示</el-menu-item>
        <el-menu-item index="/annomanage/resultanno" v-if="[1, 2, 3].includes($store.getters.authUser.userType)">中标公告</el-menu-item>
      </el-submenu>
      <el-submenu index="5" v-if="[1, 2].includes($store.getters.authUser.userType)">
        <template slot="title">
          <span class="iconimg">
            <img src="../../static/images/public/bidapprove.png" alt="">
          </span>
          <span class="titlesbox" :title="'公告审核'" v-if="[1, 2].includes($store.getters.authUser.userType)">公告审核</span>
        </template>
        <el-menu-item index="/annoapprove/ckbidanno" v-if="[1, 2].includes($store.getters.authUser.userType)">招标公告审核</el-menu-item>
        <el-menu-item index="/annoapprove/ckbidwin" v-if="[1, 2].includes($store.getters.authUser.userType)">中标候选人公示审核</el-menu-item>
        <el-menu-item index="/annoapprove/ckresultanno" v-if="[1, 2].includes($store.getters.authUser.userType)">中标公告审核</el-menu-item>
      </el-submenu>
      <el-submenu index="6" v-if="[1, 2].includes($store.getters.authUser.userType)">
        <template slot="title">
          <span class="iconimg">
            <img src="../../static/images/public/indexmanage.png" alt="">
          </span>
          <span class="titlesbox" :title="'首页管理'" v-if="[1, 2].includes($store.getters.authUser.userType)">首页管理</span>
        </template>
        <el-menu-item index="/index/home-manage/rotateinfo" v-if="[1, 2].includes($store.getters.authUser.userType)">轮换信息</el-menu-item>
        <el-menu-item index="/index/home-manage/dynamic-info" v-if="[1, 2].includes($store.getters.authUser.userType)">动态信息</el-menu-item>
        <el-menu-item index="/index/home-manage/admanage" v-if="[1, 2].includes($store.getters.authUser.userType)">广告管理</el-menu-item>
        <el-menu-item index="/index/home-manage/cooperation" v-if="[1, 2].includes($store.getters.authUser.userType)">会员单位</el-menu-item>
        <el-menu-item index="/index/home-manage/friendship-link" v-if="[1, 2].includes($store.getters.authUser.userType)">友情链接</el-menu-item>
        <el-menu-item index="/index/home-manage/notice" v-if="[1, 2].includes($store.getters.authUser.userType)">通知管理</el-menu-item>
      </el-submenu>
      <el-submenu index="7" v-if="[1, 2].includes($store.getters.authUser.userType)">
        <template slot="title">
          <span class="iconimg">
            <img src="../../static/images/public/infomanage.png" alt="">
          </span>
          <span class="titlesbox" :title="'信息管理'" v-if="[1, 2].includes($store.getters.authUser.userType)">信息管理</span>
        </template>
        <el-menu-item index="/index/info-manage/bidstate" v-if="[1, 2].includes($store.getters.authUser.userType)">招标声明</el-menu-item>
        <el-menu-item index="/index/info-manage/violation" v-if="[1, 2].includes($store.getters.authUser.userType)">违规公示</el-menu-item>
        <el-menu-item index="/index/info-manage/industry-supervision" v-if="[1, 2].includes($store.getters.authUser.userType)">行业监督</el-menu-item>
        <el-menu-item index="/index/info-manage/download" v-if="[1, 2].includes($store.getters.authUser.userType)">下载中心</el-menu-item>
        <el-menu-item index="/index/info-manage/service-guide" v-if="[1, 2].includes($store.getters.authUser.userType)">服务指南</el-menu-item>
      </el-submenu>
    </el-menu>
    </el-scrollbar>
  </el-col>
</template>
<script>
export default {
  data () {
    return {
      activeNav: ''
      // activesubNav: ''
    }
  },
  name: 'left',
  methods: {
    handleClose (key, keyPath) {
    },
    handleOpen (key, keyPath) {
    },
    setLeftMenu () {
      // 获得屏幕的高度
      let screenH = document.body.clientHeight
      // 获得左侧ul
      let leftUl = document.getElementsByClassName('el-menu-vertical-demo')
      // 设置左侧ul的高度
      leftUl[0].style.minHeight = (screenH - 50) + 'px'
    },
    setActiveNav () {
      if (this.$route.meta) {
        this.activeNav = this.$route.meta.active
      }
    }
  },
  watch: {
    '$route': 'setActiveNav'
  },
  mounted () {
    this.setLeftMenu()
    this.setActiveNav()
  }
}
</script>
<style lang="less">
  .el-scrollbar__wrap {
    overflow-x: hidden;
    overflow-y: auto;
  }
  /*公共侧边导航和头部开始*/
  .el-col-3{
    position: fixed;
  }
  .logo>img{
    width: 80% !important;
  }
  .el-submenu__title{
    text-align: left;
    height: 50px;
    line-height: 50px;
  }
  .iconimg{
    display: inline-block;
    width: 20px;
    height: 100%;
    /*background: red;*/
    position: relative;
    vertical-align: middle;
  }
  .iconimg>img{
    width: 90%;
    position: absolute;
    left: 0;
    right: 0;
    top:0;
    bottom: 0;
    margin: auto;
  }
  .titlesbox{
    display: inline-block;
    width: 115px;
    height: 100%;
    padding: 0 5px;
    overflow: hidden;
    text-overflow:ellipsis;
    white-space: nowrap;
    box-sizing: border-box;
    vertical-align: middle;
    height: 40px;
    line-height: 40px;
  }
  .el-aside{
    background:#19233c;
  }
  .el-menu-item.is-active{
    background: rgb(25, 35, 60)!important;
  }
  .el-menu-item{
    text-align: left;
  }
  /*定义滚动条高宽及背景 高宽分别对应横竖滚动条的尺寸*/
  ::-webkit-scrollbar
  {
    width: 8px;
    height: 8px;
    background-color: #F5F5F5;
  }

  /*定义滚动条轨道 内阴影+圆角*/
  ::-webkit-scrollbar-track
  {
    -webkit-box-shadow: inset 0 0 6px rgba(0,0,0,0.3);
    border-radius: 10px;
    background-color: #F5F5F5;
  }

  /*定义滑块 内阴影+圆角*/
  ::-webkit-scrollbar-thumb
  {
    border-radius: 10px;
    -webkit-box-shadow: inset 0 0 6px rgba(0,0,0,.3);
    background-color: #cccccc}

</style>
