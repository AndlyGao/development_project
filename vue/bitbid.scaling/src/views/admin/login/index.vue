<template>
    <div id="login_bigbox">
      <div class="login_con_bigbox">
        <div class="login_conboxfather">
          <div class="login_conboxtop">
            <div class="login_conbox_son" v-for="(item,index) in items" :key="index" :class="{active:isTrue == index}" @click="handleChange(index)">{{item.name}}</div>
          </div>
          <div class="login_conboxcon">
            <el-form  :model="logonForm" :rules="rules" ref="logonForm"  class="demo-ruleForm login_conbox">
              <el-form-item prop="sectionStr" v-if="isTrue !== 0">
                <el-input v-model="logonForm.sectionStr" suffix-icon="iconfont"  placeholder="标段识别号" class="icon_imgbox_loginfather">
                  <i slot="suffix" class="icon_imgbox_login">
                    <img src="../../../../static/img/shibie.png" alt="">
                  </i>
                </el-input>
              </el-form-item>
              <el-form-item prop="userAccount">
                <el-input v-model="logonForm.userAccount" suffix-icon="iconfont" placeholder="用户名" class="icon_imgbox_loginfather">
                  <i slot="suffix" class="icon_imgbox_login">
                    <img src="../../../../static/img/yonghuming.png" alt="">
                  </i>
                </el-input>
              </el-form-item>
              <el-form-item prop="userPassWord">
                <el-input type="password" v-model="logonForm.userPassWord" suffix-icon="iconfont" placeholder="密码" class="icon_imgbox_loginfather" @keyup.enter.native="logonFormbtn('logonForm')">
                  <i slot="suffix" class="icon_imgbox_login">
                    <img src="../../../../static/img/mima.png" alt="">
                  </i>
                </el-input>
              </el-form-item>
            </el-form>
          </div>
          <div class="login_bottombox" style="text-align: center">
            <el-button type="primary" class="login_btn" @click="logonFormbtn('logonForm')">登 录</el-button>
          </div>
        </div>
      </div>
      <div class="login_bottom_bigbox">
        <span class="login_bottom">Copyright©2014-2017 技术支持：广东比比信息技术服务有限公司 版权所有 bibenet.com 晋ICP备14006571号-7</span>
      </div>
    </div>
</template>
<script>
export default {
  data () {
    return {
      // 提交时的数据
      logonForm: {
        sectionStr: this.$route.query.biaoDuanId
      },
      // 登录方式
      items: [
        {name: '代理机构'},
        {name: '其他角色'}
      ],
      isTrue: this.$route.query.biaoDuanId ? 1 : 0, // 默认登录方式
      rules: {
        sectionStr: [
          { required: true, message: '标段识别号不能为空', trigger: ['blur', 'change'] }
        ],
        userAccount: [
          { required: true, message: '账号不能为空', trigger: ['blur', 'change'] }
        ],
        userPassWord: [
          { required: true, message: '密码不能为空', trigger: ['blur', 'change'] }
        ]
      }
    }
  },
  methods: {
    handleChange (index) {
      this.isTrue = index
      this.logonForm = {}
      this.$refs['logonForm'].resetFields()
    },
    logonFormbtn (formName) {
      // if (document.cookie.includes('saclingsystem-token')) {
      //   this.$message({
      //     type: 'error',
      //     message: '同一个浏览器只能登录一个用户!'
      //   })
      //   return false
      // }
      if (this.isTrue === 1) {
        this.logonForm.flag = 1
      } else {
        // 代理机构
        this.logonForm.flag = 0
      }
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.$store.dispatch('Login', this.logonForm).then(userType => {
            this.$router.push({path: '/admin'})
          })
        } else {
          return false
        }
      })
    }
  },
  mounted () {
  }
}
</script>
<style lang="scss">
  #login_bigbox{
    /*登录页面样式*/
    .logo_imgbox{
      height: 80%;
      position: absolute;
      top:0;
      bottom: 0;
      margin: auto;
    }
    .logo_imgbox>img{
      height: 100%;
    }
    .login_con_bigbox{
      width: 100%;
      height: 90vh;
      background: url("../../../../static/img/beijign.png")no-repeat;
      background-size: 100% auto;
    }
    .login_bottom_bigbox{
      width: 100%;
      height: 10vh;
      font-size: 12px;
      color:#a9a9a9;
      text-align: center;
      position: relative;
    }
    .login_bottom{
      display: inline-block;
      height: 20px;
      line-height: 20px;
      position: absolute;
      top:0;
      bottom: 0;
      left: 0;
      right: 0;
      margin: auto;
    }
    .login_conboxfather{
      width: 440px;
      height: 375px;
      background:#fff;
      position: absolute;
      left: 0;
      right: 0;
      top:0;
      bottom: 0;
      margin: auto;
    }
    .login_conboxtop{
      width: 100%;
      height: 50px;
      line-height: 50px;
      text-align: center;
      font-size: 18px;
      font-weight: bold;
      color:#333333;
      background-size: 100% auto;
    }
    .login_conbox_son{
      display: inline-block;
      width: 50%;
      height: 100%;
      float: left;
      background: #efefef;
      font-size: 16px;
      font-weight: bold;
      color: #333333;
      cursor: pointer;
    }
    .active{
      background: #fff;
      background: url("../../../../static/img/jianbian.png") bottom center no-repeat;
    }
    .login_conboxcon{
      width: 80%;
      margin: 0 auto;
      margin-top: 40px;
    }
    .login_conboxcon .el-input__inner{
      font-size: 12px;
      color:#afabab;
    }
    /*登录按钮样式*/
    .login_btn{
      width: 350px;
      height: 35px;
      border-radius: 3px;
      border-color: #3f63f6;
      padding: 0;
      font-size: 13px;
      background:#3f63f6;
    }
    .icon_imgbox_login{
      display: inline-block;
      width: 15px;
      height: 15px;
      vertical-align: middle;
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
    }
    .icon_imgbox_loginfather>.el-input__inner{
      padding-left: 30px;
    }
    .icon_imgbox_loginfather>.el-input__suffix{
      left: 10px;
      text-align: left;
    }
    /*必填效果*/
    .bitianicon .el-form-item__label::before{
      content:"*";
      color:#f66c6c;
      margin-right: 5px;
    }
    .login_bottombox{
      margin-top: 50px;
    }
  }
</style>
