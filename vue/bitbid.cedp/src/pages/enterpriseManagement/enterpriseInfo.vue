<template>
  <div class="enterInfo" id="enterpriseInfo">
    <p class="tips" v-if="isEdit">
      <span>编辑状态下，离开此标签未保存的信息将不会被保存</span>
      <el-button class="handleBtn" type="primary"  @click="saveBtn">保存</el-button>
    </p>
    <p class="tips" v-else>
      <span>完善企业介绍信息，提升公司形象赢得更多商机</span>
      <el-button class="handleBtn" type="primary"  @click="editBtn">编辑</el-button>
    </p>
    <el-form :model="dataForm" ref="dataForm" label-width="120px" :validate-on-rule-change="true">
      <div class="mainContent" style="width: 98%; padding: 1%;">
        <div :class="isEdit ? 'show' : 'hidden'">
          <editor :hidden="!isEdit && !this.dataForm.companyProfile" ref="ueditor" class="ueditor" :height= '530' :editread="true"></editor>
        </div>
        <div class="nodata" v-if="!isEdit && !this.dataForm.companyProfile">
          <img src="../../../static/images/enterprise/nointroduction.png"/>
          <p>暂无企业介绍</p>
        </div>
      </div>
    </el-form>
  </div>
</template>

<script>
import editor from '@/components/ueditor/ueditor.vue'
import {attachinfo} from '@/api/system'
export default {
  name: 'enterpriseInfo',
  components: {
    editor
  },
  data () {
    return {
      // 判断编辑、查看状态 ；默认应为查看状态
      isEdit: false,
      dataForm: {
        enterpriseId: this.$store.getters.authUser.enterpriseId
      },
      nodata: true
    }
  },
  methods: {
    saveBtn () {
      if (this.$refs.ueditor.getContent().length !== 0) {
        this.dataForm.companyProfile = this.$refs.ueditor.getContent()
      } else {
        this.dataForm.companyProfile = ''
      }
      attachinfo.update(this.dataForm).then((res) => {
        if (res.data.resCode === '0000') {
          this.isEdit = false
          this.$store.commit('SET_ENTERPRISE_SAVE', new Date().getTime())
        }
        this.$refs['ueditor'].setEnabled(this.isEdit)
      })
    },
    editBtn () {
      this.isEdit = true
      this.$refs['ueditor'].setEnabled(this.isEdit)
    },
    detail () {
      let url = {
        enterpriseId: this.$store.getters.authUser.enterpriseId
      }
      attachinfo.queryOne(url).then((res) => {
        this.$set(this.dataForm, 'objectId', res.data.enterpriseExpand.objectId)
        this.$set(this.dataForm, 'companyProfile', res.data.enterpriseExpand.companyProfile)
        this.$refs.ueditor.setContent(this.dataForm.companyProfile)
      })
    }
  },
  mounted () {
    this.detail()
  }
}
</script>

<style lang="less">
  #enterpriseInfo{
    .show .edui-editor-toolbarbox{
      display: block;
    }
    .hidden .edui-editor-toolbarbox{
      display: none;
    }
  }
</style>
