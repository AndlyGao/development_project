<template>
  <div class="greenCon" id="dynamic">
    <el-breadcrumb separator-class="el-icon-arrow-right">
      <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/' }">项目管理</el-breadcrumb-item>
      <el-breadcrumb-item>项目执行</el-breadcrumb-item>
    </el-breadcrumb>
    <el-tabs v-model="activeTab">
      <el-tab-pane label="直接采购" name="1">
        <directPurchase></directPurchase>
      </el-tab-pane>
      <el-tab-pane label="招标采购" name="2">
      </el-tab-pane>
      <el-tab-pane label="询价采购" name="3">
      </el-tab-pane>
      <el-tab-pane label="竞价采购" name="4">
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
export default {
  components: {
    directPurchase: resolve => require(['./directPurchase/index.vue'], resolve)
  },
  data () {
    return {
      activeTab: '1'
    }
  }
}
</script>

<style lang="less">
  #dynamic {
    .el-tabs__header {
      margin: 10px 0 0 0;
      background: #ffffff;
    }
    .el-tabs__nav-scroll{
      padding-left: 20px;
    }
  }
</style>
