<template>
    <div id="login_bannerbox">
      <div class="logobigbox">
        <div class="logobox">
          <div class="logoboximg fl">
            <img src="../../../static/images/login_logo.png" alt="">
          </div>
          <div class="logoboxlink fr">
            <el-link href="" :underline="false">首页</el-link>
            <el-link href="" :underline="false">直采商城</el-link>
            <el-link href="" :underline="false">采购招标</el-link>
            <el-link href="" :underline="false">供应商库</el-link>
            <el-link href="" :underline="false">园区直采</el-link>
            <el-link href="" :underline="false">询价信息</el-link>
            <el-link href="" :underline="false">网站导航</el-link>
          </div>
        </div>
      </div>
      <div class="login_bannerimg">
        <el-carousel height="100%">
          <el-carousel-item v-for="item in picloop" :key="item.index">
            <img :src="item.src"/>
            <div class="text"><img :src="item.textsrc"/></div>
          </el-carousel-item>
        </el-carousel>
        <div class="login_intbox">
          <div class="login_titlebox">登 录</div>
          <el-form  :model="loginForm" :rules="rules" ref="loginForm"  class="demo-ruleForm login_conbox">
            <el-form-item prop="account">
              <el-input v-model="loginForm.account"  placeholder="账号">
                <i slot="suffix" class="icon_imgbox_login">
                  <img src="../../../static/images/phone.png" alt="">
                </i>
              </el-input>
            </el-form-item>
            <el-form-item prop="password">
              <el-input v-if="pwdHide" type="password" v-model="loginForm.password" placeholder="密码" @keyup.enter.native="loginFormbtn('loginForm')">
                <i slot="suffix" class="icon_imgbox_login">
                  <img src="../../../static/images/pwd.png" alt="">
                </i>
              </el-input>
              <el-input v-else type="text" v-model="loginForm.password" placeholder="密码" @keyup.enter.native="loginFormbtn('loginForm')">
                <i slot="suffix" class="icon_imgbox_login">
                  <img src="../../../static/images/pwd.png" alt="">
                </i>
              </el-input>
              <em @click="pwdShowHide()" :class="pwdHide ? 'pwdHideEye' : 'pwdShowEye'"></em>
            </el-form-item>
            <div class="dragbox" v-show="errorStatus">
              <div class="drag" >
                <div class="drag_bg"></div>
                <div class="drag_text">{{confirmWords}}</div>
                <div @mousedown="mousedownFn($event)" class="handler handler_bg"></div>
              </div>
            </div>
          </el-form>
          <div class="loginbtn">
            <el-button type="primary" @click="loginFormbtn('loginForm')">登 录</el-button>
          </div>
          <div class="function_bigbox">
            <div class="function_box loginleft">
              <router-link :to="{ path: '/register' }">
                立即注册
              </router-link>
            </div>
            <div class="function_box loginright">
              <router-link :to="{ path: '/forgetPwd' }">
                找回密码
              </router-link>
            </div>
          </div>
        </div>
      </div>
      <div class="bottomlink">
        <el-link href="" :underline="false">比比电子交易平台</el-link> |
        <el-link href="" :underline="false">比比建材</el-link> |
        <el-link href="" :underline="false">比比金融</el-link> |
        <el-link href="" :underline="false">比比招标采购</el-link>
      </div>
      <p class="banquan"> <img src="../../../static/images/bq.png"/>晋公网安备 14019202000115号比比网络 版权所有 晋ICP备14002897号-1</p>
    </div>
</template>
<script>
import $ from 'jquery'
export default {
  name: '',
  props: {},
  data () {
    return {
      // 轮播图
      picloop: [
        {
          src: '../../../static/images/banner/banner1.png',
          textsrc: '../../../static/images/banner/text1.png'
        },
        {
          src: '../../../static/images/banner/banner2.png',
          textsrc: '../../../static/images/banner/text2.png'
        },
        {
          src: '../../../static/images/banner/banner3.png',
          textsrc: '../../../static/images/banner/text3.png'
        }
      ],
      // 提交时的数据
      loginForm: {
      },
      // 验证
      rules: {
        account: [
          { required: true, message: '账号不能为空', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '密码不能为空', trigger: 'blur' }
        ]
      },
      errorStatus: false,
      errorNumber: 0,
      beginClientX: 0,
      mouseMoveStata: false,
      maxwidth: 258,
      confirmWords: '拖动滑块验证',
      confirmSuccess: false,
      pwdHide: true
    }
  },
  created () {},
  methods: {
    loginFormbtn (formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          if (this.errorNumber >= 2) {
            this.errorStatus = true
            this.$message({
              message: '请先进行验证,再登录',
              type: 'warning'
            })
            return false
          }
          this.$store.dispatch('Login', this.loginForm).then(userType => {
            // 根据用户类型判断跳转
            if (userType === 1) {
              // 初始化企业默认模块
              this.$store.dispatch('InitEnterpriseModels')
              this.$router.push({path: '/purchaser'})
            } else if (userType === 2) {
              this.$router.push({path: '/purchaser'})
            }
          }).catch(() => {
            this.errorNumber += 1
          })
        } else {
          return false
        }
      })
    },
    mousedownFn: function (e) {
      this.mouseMoveStata = true
      this.beginClientX = e.clientX
    },
    successFunction () {
      $('.handler').removeClass('handler_bg').addClass('handler_ok_bg')
      this.confirmWords = '验证通过'
      $('.drag').css({'color': '#fff'})
      $('.handler').css({'left': this.maxwidth})
      $('.drag_bg').css({'width': this.maxwidth})
      $('body').unbind('mousemove')
      $('body').unbind('mouseup')
      this.confirmSuccess = true
      if (this.confirmSuccess === true) {
        this.$message({
          message: '验证已通过,请重新输入',
          type: 'success'
        })
        $('.handler').css({'left': 0})
        $('.drag_bg').css({'width': 0})
        this.errorStatus = false
        this.errorNumber = 0
        this.beginClientX = 0
        this.mouseMoveStata = false
        this.maxwidth = 258
        this.confirmWords = '拖动滑块验证'
        this.confirmSuccess = false
        this.loginForm = {}
        $('.handler').addClass('handler_bg').removeClass('handler_ok_bg')
        $('body').on('mousemove', (e) => {
          if (this.mouseMoveStata) {
            var width = e.clientX - this.beginClientX
            if (width > 0 && width <= this.maxwidth) {
              $('.handler').css({'left': width})
              $('.drag_bg').css({'width': width})
            } else if (width > this.maxwidth) {
              this.successFunction()
            }
          }
        })
      }
    },
    pwdShowHide () {
      this.pwdHide = !this.pwdHide
    }
  },
  mounted () {
    $('body').on('mousemove', (e) => {
      if (this.mouseMoveStata) {
        var width = e.clientX - this.beginClientX
        if (width > 0 && width <= this.maxwidth) {
          $('.handler').css({'left': width})
          $('.drag_bg').css({'width': width})
        } else if (width > this.maxwidth) {
          this.successFunction()
        }
      }
    })
    $('body').on('mouseup', (e) => {
      this.mouseMoveStata = false
      var width = e.clientX - this.beginClientX
      if (width < this.maxwidth) {
        $('.handler').css({'left': 0})
        $('.drag_bg').css({'width': 0})
      }
    })
  }
}
</script>
<style lang="less">
  #login_bannerbox {
    .login_banner {
      width: 100%;
    }
    .logobigbox {
      background: #fff;
      height: 56px;
      width: 100%;
    }
    .logobox {
      width: 1220px;
      height: 56px;
      margin: 0 auto;
    }
    .login_bannerimg {
      width: 100%;
      height: 650px;
      position: relative;
      /*background: url("../../../static/images/loginbanner.png");*/
    }
    .login_bannerimg .el-carousel{
      width: 100%;
      height: 100%;
      position: relative;
    }
    .login_bannerimg .el-carousel img{
      width:100%;
      height: 100%;
    }
    .login_bannerimg .el-carousel .text{
      position: absolute;
      left: 10%;
      top: 50%;
      z-index: 9999;
      margin-top: -130px;
    }
    .logoboximg {
      width: 120px;
      height: 100%;
      position: relative;
    }
    .logoboximg > img {
      width: 120px;
      height: 43px;
      position: absolute;
      top: 0;
      bottom: 0;
      left: 0;
      margin: auto;
    }
    .logoboxlink{
      margin-top:7px;
    }
    .logoboxlink a{
      display:inline-block;
      margin: 0 10px;
      height:46px;
      line-height:46px;
      border-bottom:2px solid #ffffff;
    }
    .logoboxlink a:hover{
      color:#009688;
      border-bottom:2px solid #009688;
    }
    .login_inputbox {
      width: 1220px;
      height: 100%;
      position: absolute;
      left: 50%;
      top: 0;
      margin-left: -660px;
      z-index: 9999;
    }
    .login_intbox {
      width: 450px;
      height: 380px;
      background: #fff;
      position: absolute;
      right: 10%;
      top: 0;
      bottom: 0;
      margin: auto;
      border-radius: 3px;
      z-index: 9999;
    }
    @media screen and (max-width: 1400px) {
      .login_bannerimg .el-carousel.el-carousel--horizontal .text{
        left: 3%;
      }
      .login_bannerimg .el-carousel.el-carousel--horizontal .text img{
        max-width: 78%;
      }
      .login_intbox {
        right: 3%;
      }
    }
    .login_titlebox {
      width: 100%;
      height: 100px;
      color: #666666;
      font-size: 28px;
      line-height: 100px;
      padding: 0 35px;
      box-sizing: border-box;
    }
    .login_conbox {
      width: 390px;
      margin: 0 auto;
    }
    .el-input__prefix, .el-input__suffix {
      position: absolute;
      top: 0;
      left: 10px;
      -webkit-transition: all .3s;
      height: 100%;
      color: #c0c4cc;
      text-align: center;
      font-size: 21px;
      right: auto;
    }
    .iconfont {
      font-family: "iconfont" !important;
      font-size: 20px;
      font-style: normal;
      -webkit-font-smoothing: antialiased;
      -moz-osx-font-smoothing: grayscale;
      color: #75b6f3;
    }
    .el-input--suffix .el-input__inner {
      padding-right: 35px;
      padding-left: 35px;
    }
    .el-form-item {
      margin-bottom: 30px;
    }
    .yanzhengma {
      width: 50%;
      display: inline-block;
      vertical-align: top;
    }
    .yan_imgbox {
      width: 40%;
      height: 42px;
      display: inline-block;
      vertical-align: top;
      margin-left: 8%;
      cursor: pointer;
      position: relative;
    }
    .yan_imgbox > img {
      width: 60%;
      position: absolute;
      left: 0;
      right: 0;
      top: 0;
      bottom: 0;
      margin: auto;
    }
    .loginbtn {
      width: 390px;
      margin: 0 auto;
      margin-top:35px;
      text-align: center;
    }
    .loginbtn .el-button--primary {
      width: 100%;
      color: #fff;
      background-color: #009688;
      border-color: #009688;
    }
    .dragbox{
      width: 300px;
      height: 34px;
    }
    .drag {
      position: relative;
      background-color: #e8e8e8;
      width: 300px;
      height: 34px;
      margin-left: 30px;
      margin-top: 20px;
      line-height: 34px;
      text-align: center;
    }
    .handler {
      position: absolute;
      top: 0px;
      left: 0px;
      width: 40px;
      height: 32px;
      border: 1px solid #ccc;
      cursor: move;
    }
    /*.handler_bg {*/
      /*background: #fff url("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAA3hpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuNS1jMDIxIDc5LjE1NTc3MiwgMjAxNC8wMS8xMy0xOTo0NDowMCAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wTU09Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9tbS8iIHhtbG5zOnN0UmVmPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvc1R5cGUvUmVzb3VyY2VSZWYjIiB4bWxuczp4bXA9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC8iIHhtcE1NOk9yaWdpbmFsRG9jdW1lbnRJRD0ieG1wLmRpZDo0ZDhlNWY5My05NmI0LTRlNWQtOGFjYi03ZTY4OGYyMTU2ZTYiIHhtcE1NOkRvY3VtZW50SUQ9InhtcC5kaWQ6NTEyNTVEMURGMkVFMTFFNEI5NDBCMjQ2M0ExMDQ1OUYiIHhtcE1NOkluc3RhbmNlSUQ9InhtcC5paWQ6NTEyNTVEMUNGMkVFMTFFNEI5NDBCMjQ2M0ExMDQ1OUYiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENDIDIwMTQgKE1hY2ludG9zaCkiPiA8eG1wTU06RGVyaXZlZEZyb20gc3RSZWY6aW5zdGFuY2VJRD0ieG1wLmlpZDo2MTc5NzNmZS02OTQxLTQyOTYtYTIwNi02NDI2YTNkOWU5YmUiIHN0UmVmOmRvY3VtZW50SUQ9InhtcC5kaWQ6NGQ4ZTVmOTMtOTZiNC00ZTVkLThhY2ItN2U2ODhmMjE1NmU2Ii8+IDwvcmRmOkRlc2NyaXB0aW9uPiA8L3JkZjpSREY+IDwveDp4bXBtZXRhPiA8P3hwYWNrZXQgZW5kPSJyIj8+YiRG4AAAALFJREFUeNpi/P//PwMlgImBQkA9A+bOnfsIiBOxKcInh+yCaCDuByoswaIOpxwjciACFegBqZ1AvBSIS5OTk/8TkmNEjwWgQiUgtQuIjwAxUF3yX3xyGIEIFLwHpKyAWB+I1xGSwxULIGf9A7mQkBwTlhBXAFLHgPgqEAcTkmNCU6AL9d8WII4HOvk3ITkWJAXWUMlOoGQHmsE45ViQ2KuBuASoYC4Wf+OUYxz6mQkgwAAN9mIrUReCXgAAAABJRU5ErkJggg==") no-repeat center;*/
    /*}*/
    /*.handler_ok_bg {*/
      /*background: #fff url("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAA3hpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuNS1jMDIxIDc5LjE1NTc3MiwgMjAxNC8wMS8xMy0xOTo0NDowMCAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wTU09Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9tbS8iIHhtbG5zOnN0UmVmPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvc1R5cGUvUmVzb3VyY2VSZWYjIiB4bWxuczp4bXA9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC8iIHhtcE1NOk9yaWdpbmFsRG9jdW1lbnRJRD0ieG1wLmRpZDo0ZDhlNWY5My05NmI0LTRlNWQtOGFjYi03ZTY4OGYyMTU2ZTYiIHhtcE1NOkRvY3VtZW50SUQ9InhtcC5kaWQ6NDlBRDI3NjVGMkQ2MTFFNEI5NDBCMjQ2M0ExMDQ1OUYiIHhtcE1NOkluc3RhbmNlSUQ9InhtcC5paWQ6NDlBRDI3NjRGMkQ2MTFFNEI5NDBCMjQ2M0ExMDQ1OUYiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENDIDIwMTQgKE1hY2ludG9zaCkiPiA8eG1wTU06RGVyaXZlZEZyb20gc3RSZWY6aW5zdGFuY2VJRD0ieG1wLmlpZDphNWEzMWNhMC1hYmViLTQxNWEtYTEwZS04Y2U5NzRlN2Q4YTEiIHN0UmVmOmRvY3VtZW50SUQ9InhtcC5kaWQ6NGQ4ZTVmOTMtOTZiNC00ZTVkLThhY2ItN2U2ODhmMjE1NmU2Ii8+IDwvcmRmOkRlc2NyaXB0aW9uPiA8L3JkZjpSREY+IDwveDp4bXBtZXRhPiA8P3hwYWNrZXQgZW5kPSJyIj8+k+sHwwAAASZJREFUeNpi/P//PwMyKD8uZw+kUoDYEYgloMIvgHg/EM/ptHx0EFk9I8wAoEZ+IDUPiIMY8IN1QJwENOgj3ACo5gNAbMBAHLgAxA4gQ5igAnNJ0MwAVTsX7IKyY7L2UNuJAf+AmAmJ78AEDTBiwGYg5gbifCSxFCZoaBMCy4A4GOjnH0D6DpK4IxNSVIHAfSDOAeLraJrjgJp/AwPbHMhejiQnwYRmUzNQ4VQgDQqXK0ia/0I17wJiPmQNTNBEAgMlQIWiQA2vgWw7QppBekGxsAjIiEUSBNnsBDWEAY9mEFgMMgBk00E0iZtA7AHEctDQ58MRuA6wlLgGFMoMpIG1QFeGwAIxGZo8GUhIysmwQGSAZgwHaEZhICIzOaBkJkqyM0CAAQDGx279Jf50AAAAAABJRU5ErkJggg==") no-repeat center;*/
    /*}*/
    .drag_bg {
      background-color: #7ac23c;
      height: 34px;
      width: 0px;
    }
    .drag_text {
      position: absolute;
      top: 0px;
      width: 300px;
      -moz-user-select: none;
      -webkit-user-select: none;
      user-select: none;
      -o-user-select: none;
      -ms-user-select: none;
    }
    .function_bigbox{
      width: 85%;
      height: 35px;
      margin: 0 auto;
      margin-top:15px;
      padding: 0 5px;
      box-sizing: border-box;
    }
    .function_box{
      width: 50%;
      height: 100%;
      line-height: 30px;
      float: left;
      font-size: 14px;
    }
    .function_box>a{
      text-decoration:none;
      color: #009688;
    }
    .loginleft{
      text-align: left;
    }
    .loginright{
      text-align: right;
    }
    .icon_imgbox_login{
      display: inline-block;
      width: 15px;
      height: 15px;
      position: relative;
    }
    .icon_imgbox_login>img{
      vertical-align: top;
      position: absolute;
      left: 0;
      right: 0;
      top:0;
      bottom: 0;
      margin: auto;
      width: 80%;
    }
    .banquan{
      text-align: center;
      line-height: 36px;
      font-size: 14px;
      margin:0;
      padding:0;
      color:#999999
    }
    .banquan img{
      vertical-align: middle;
      margin-right: 5px;
    }
    .bottomlink{
      margin-top:10px;
      overflow:hidden;
      text-align:center;
      line-height: 24px;
      color:#999999
    }
    .el-link.el-link--default{
      color:#999999
    }
    /*修改input 高度*/
    .el-input__inner{
      height: 40px;
      line-height: 40px;
    }
    .el-input__icon{
      line-height: 40px;
    }
    .el-button{
      padding: 12px 20px;
    }
    .el-form-item__label{
      line-height: 40px;
    }
    .el-form-item__content{
      line-height: 40px;
    }
    .el-form-item{
      margin-bottom: 22px;
    }
    .el-form-item__error{
      padding-top: 4px;
    }
    .el-input__inner:focus{
      border-color: #009688;
    }
    em.pwdHideEye{
      position: absolute;
      right: 0;
      top: 0;
      width: 34px;
      height: 34px;
      background: url("../../../static/images/pwd_hide.png") center center no-repeat;
    }
    em.pwdShowEye{
      position: absolute;
      right: 0;
      top: 0;
      width: 34px;
      height: 34px;
      background: url("../../../static/images/pwd_show.png") center center no-repeat;
    }
  }
</style>
