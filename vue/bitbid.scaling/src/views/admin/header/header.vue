<template>
  <div class="header-warp">
    <el-row>
      <el-col :span="3">
        <div class="logo-box">
          <img src="./images/logo.png" alt="">
          <!-- <span>{{titleName | filterRole}}</span> -->
        </div>
      </el-col>
      <el-col :span="21">
        <div class="up-box">
          <span>{{userInfo.userName}}</span>
          <img src="./images/tuichu.png" alt="" @click="logout">
        </div>
      </el-col>
    </el-row>
  </div>
</template>
<script>
// import {WebPushSocket} from '../../../assets/js/websocket.js'
import {auth} from '@/api'
export default {
  name: 'head-name',
  data () {
    return {
      // webPushSocket: new WebPushSocket(),
      userInfo: {},
      titleName: ''
      // num: this.$route.query.num || 1,
      // projectId: this.$store.getters.projectId,
      // methodType: this.$route.query.methodType
    }
  },
  created () {
    this.getUserInfo()
  },
  filters: {
    filterRole (val) {
      if (val === 1) {
        return '委员会组长'
      } else if (val === 2) {
        return '委员会组员'
      } else if (val === 3) {
        return '代理机构'
      } else if (val === 4) {
        return '监标人'
      } else if (val === 5) {
        return '投标人'
      }
    }
  },
  methods: {
    // 获得用户信息
    getUserInfo () {
      auth.getLoginInfo().then(res => {
        // console.log(res)
        this.userInfo = res.data.loginUserInfo
      })
    },
    logout () {
      this.$store.dispatch('Logout').then(() => {
        this.$router.push({path: '/login'})
      })
    }
    // 初始化websocket
    // initWebSocket () {
    //   this.webPushSocket.setSettings(this.$store.getters.projectId, this.$store.getters.authUser.userId, this.$store.getters.authUser.userName, 'All', this.methodType, this.num)
    //   this.webPushSocket.init()
    // }
  },
  // 实例创建完成
  mounted () {
    // this.initWebSocket()
  }
}
</script>
<style lang="scss">
.header-warp {
  width: 100%;
  height: 60px;
  position: fixed;
  top: 0;
  z-index: 1;
  box-shadow: 0 2px 0 #ddd;
  background: rgba(255,255,255, .9);
  .logo-box {
    height: 60px;
    line-height: 60px;
    background: #393C4F;
    margin: 0 auto;
    img {
      width: 145px;
      height: 43px;
      vertical-align: middle;
    }
  }
  .up-box {
    height: 60px;
    line-height: 60px;
    text-align: right;
    padding-right: 20px;
    /*padding-top: 12px;*/
    /*padding-bottom: 12px;*/
    /*border-bottom: 1px solid #ddd;*/
    img {
      vertical-align: middle;
      cursor: pointer;
    }
  }
  .down-box {
    text-align: left;
    padding-left: 10px;
    padding-top: 4px;
    span, img {
      cursor: pointer;
    }
  }
}
</style>
