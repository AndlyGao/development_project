<template>
  <div class="noPermission" id="noPermission">
    <p>您没有该功能的查看或操作权限，请联系系统管理员授权！</p>
    <a href="javascript:void(0);" @click="returnPrev">返回</a>
  </div>
</template>
<script>
export default {
  name: 'headTop',
  data () {
    return {}
  },
  methods: {
    returnPrev () {
      this.$router.go(-1)
    }
  }
}
</script>

<style lang='less'>
  #noPermission{
    text-align: center;
    img{
      margin-top: 100px;
    }
    p{
      line-height: 32px;
      color: #333333;
    }
    a{
      width: 102px;
      height: 34px;
      line-height: 34px;
      display: block;
      background: #498ce9;
      text-align: center;
      color: #fff;
      margin: 0 auto;
      text-decoration: none;
      font-size: 14px;
      -webkit-border-radius: 20px;
      -moz-border-radius: 20px;
      border-radius: 20px;
    }
  }
</style>
