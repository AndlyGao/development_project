<template>
  <div id="login">
    <div class="login-top">
      <div class="topmain"><a href="http://www.sdbidding.org.cn/home"><img src="../../../static/images/user/logo.png"/> </a></div>
    </div>
    <div class="logmain">
      <div class="fl">
        <img src="../../../static/images/user/user_img.png"/>
      </div>
      <div class="fr">
        <div class="title">
            <span>
                用户登录
            </span>
          <a @click="toRegister">用户注册</a>
        </div>
        <el-form :model="loginForm" status-icon :rules="rules" ref="loginForm" class="loginForm">
          <el-form-item prop="account" :class="IEVersion === 1? 'ie9box':''">
            <span v-if="IEVersion === 1" class="formtip">用户名：</span>
            <el-input :class="IEVersion === 1? 'shortinput':''" v-model="loginForm.account" placeholder="用户名"></el-input>
            <i v-if="IEVersion !== 1" class="icon_img"><img src="../../../static/images/user/user.png"/> </i>
          </el-form-item>
          <el-form-item prop="password" :class="IEVersion === 1? 'ie9box':''">
            <span v-if="IEVersion === 1" class="formtip">密码：</span>
            <el-input :class="IEVersion === 1? 'shortinput':''" type="password" v-model="loginForm.password" placeholder="密码" @keyup.enter.native="loginFormbtn('loginForm')"></el-input>
            <i v-if="IEVersion !== 1" class="icon_img"><img src="../../../static/images/user/password.png"/> </i>
          </el-form-item>
        </el-form>
        <div class="loginbtn">
          <!--<el-checkbox v-model="checked">记住用户名</el-checkbox>-->
          <el-button type="primary" @click="loginFormbtn('loginForm')">登 录</el-button>
        </div>
      </div>
    </div>
    <!--登录注册 底部 -->
    <!--<div class="login-bottom">-->
      <!--Copyright © 2005-2017 In800.com. All Rights Reserved. 山东省采购与招标网-->
    <!--</div>-->
    <!--登录注册 底部 -->
  </div>
</template>
<script>
export default {
  data () {
    return {
      checked: false,
      loginForm: {
        account: '',
        password: ''
      },
      rules: {
        account: [
          { required: true, message: '请输入用户名', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, max: 20, message: '长度在 6到 20 个字符', trigger: ['blur', 'change'] }
        ]
      }
    }
  },
  methods: {
    loginFormbtn (formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.$store.dispatch('Login', this.loginForm).then((res) => {
            if (res.userType === 3) {
              this.$router.push(res.enterpriseStatus === 2 ? {path: '/main'} : res.enterpriseStatus === 1 ? {path: '/index/enterprise/detail'} : {path: '/index/enterprise'})
            } else {
              this.$router.push({path: '/main'})
            }
          })
        } else {
          return false
        }
      })
    },
    toRegister () {
      this.$router.push({path: '/register'})
    }
  },
  computed: {
    IEVersion () {
      let browser = navigator.appName
      let bversion = navigator.appVersion
      let version = bversion.split(';')
      if (version[1] !== undefined) { // ie浏览器上使用
        let trimVersion = version[1].replace(/[ ]/g, '')
        if (browser === 'Microsoft Internet Explorer' && trimVersion === 'MSIE9.0') {
          return 1
        }
      }
    }
  },
  mounted () {
  }
}
</script>
<style lang="less">
  body, html{
    background: #ffffff;
  }
  #login {
    .login-top{
      width: 100%;
      min-width: 1200px;
      height: 103px;
      overflow: hidden;
      background: url("../../../static/images/user/top_bg.png") repeat-x;
    }
    .login-top .topmain{
      width: 1200px;
      overflow: hidden;
      margin: 0 auto;
      text-align: left;
    }
    .login-top .topmain a{
      margin-top: 25px;
      display: block;
      border: 0px;
    }
    .login-top .topmain a img{
      border:none;
    }
    .logmain{
      width: 1200px;
      overflow: hidden;
      margin: 30px auto;
    }
    .logmain .fl{
      float: left;
    }
    .logmain .fr{
      width: 350px;
      height: 427px;
      float: right;
      border: 1px solid #eeeeee;
      -webkit-box-sizing: border-box;
      -moz-box-sizing: border-box;
      box-sizing: border-box;
    }
    .logmain .fr .title{
      height: 72px;
      border: 1px solid #eeeeee;
      padding: 0px;
      margin: 0px;
    }
    .logmain .fr .title span{
      float: left;
      margin: 27px 0 0 27px;
      display: inline-block;
      line-height: 40px;
      font-size: 16px;
    }
    .logmain .fr .title a{
      float: right;
      margin: 27px 27px 0 0;
      display: inline-block;
      line-height: 40px;
      font-size: 16px;
      padding-left: 20px;
      color: #5a7dd6;
      font-size: 12px;
      cursor: pointer;
      background: url("../../../static/images/user/iconlink.png") left center no-repeat;
    }
    .loginForm{
      width: 90%;
      margin: 30px auto;
    }
    .login .el-form-item__content{
      margin-left: 0px!important;
      position: relative;
    }
    .el-input__inner{
      padding: 0 30px;
    }
    .icon_img{
      position: absolute;
      left: 5px;
      top: 0px;
      display: inline-block;
      height: 40px;
      line-height: 40px;
    }
    .icon_img img{
      vertical-align: middle;
    }
    .loginbtn {
      width: 90%;
      margin: 80px auto 0 auto;
    }
    .el-checkbox{
      float: left;
      margin-bottom: 13px;
    }
    .loginbtn .el-button--primary {
      width: 100%;
      color: #fff;
      background-color: #5a7dd6;
      border-color: #5a7dd6;
    }
    .formtip{
      width: 24%;
      display: inline-block;
      float: left;
      color: #2c3e50;
      float: left;
      text-align: right;
    }
    .shortinput{
      width: 72%;
      float: left;
      margin-left: 2%;
      padding-left: 2%;
    }
    .shortinput .el-input__inner{
      padding-left: 10px;
    }
    .ie9box .el-form-item__error{
      left: 28%;
    }
  }
</style>
