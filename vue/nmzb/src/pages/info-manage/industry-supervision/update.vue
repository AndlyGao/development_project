<template>
  <div id="rotate-add" class="smaincontent">
    <div class="headertitle">
      <!--面包屑-->
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/main' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>信息管理</el-breadcrumb-item>
        <el-breadcrumb-item :to="{ path: '/index/info-manage/industry-supervision' }">行业监督</el-breadcrumb-item>
        <el-breadcrumb-item>编辑行业监督</el-breadcrumb-item>
      </el-breadcrumb>
      <div class="returnboxbig">
        <template>
          <el-button @click="$router.go(-1)">返回</el-button>
        </template>
      </div>
      <!--面包屑-->
    </div>
    <div class="contentbigbox">
      <el-form :model="ruleForm" :rules="rules" ref="ruleForm" label-width="100px" class="demo-ruleForm">
        <el-form-item label="标题:" prop="title">
          <el-input v-model="ruleForm.title" style="width:300px"></el-input>
        </el-form-item>
        <el-form-item label="类别:" prop="type">
          <el-select v-model="ruleForm.type" class="select" style="width:300px">
            <el-option label="水利" value="6"></el-option>
            <el-option label="交通" value="7"></el-option>
            <el-option label="铁路" value="8"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="内容:" class="form-ueditor">
          <ueditor ref="content"></ueditor>
        </el-form-item>
        <el-form-item class="submit-radio">
          <el-button type="primary" @click="submitForm('ruleForm')">确 定</el-button>
          <el-button @click="$router.go(-1)">取 消</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>
<script>
import ueditor from '@/components/ueditor/ueditor'
import {notice} from '@/api'
export default {
  data () {
    return {
      ruleForm: {},
      rules: {
        title: [
          { required: true, message: '请填写标题', trigger: 'blur' },
          { min: 1, max: 256, message: '长度在 1到 256 个字符', trigger: 'blur' }
        ],
        type: [
          { required: true, message: '请选择类型', trigger: 'change' }
        ]
      }
    }
  },
  components: {
    ueditor
  },
  methods: {
    detail () {
      notice.detail(this.$route.query.objectId).then((res) => {
        this.ruleForm = res.data.notice
        this.ruleForm.type = res.data.notice.type.toString()
        this.$refs.content.setContent(res.data.notice.content)
      })
    },
    submitForm (name) {
      this.$refs[name].validate((valid) => {
        if (valid) {
          this.ruleForm.content = this.$refs.content.getContent()
          notice.update(this.ruleForm).then((res) => {
            this.$router.push({path: '/index/info-manage/industry-supervision'})
          })
        } else {
          return false
        }
      })
    },
    upLoadFile () {}
  },
  mounted () {
    this.detail()
  }
}
</script>
<style lang="less">
  #rotate-add {
    .contentbigbox .demo-ruleForm {
      .el-form-item__content {
        text-align: left;
      }
      .ueditor-wrap {
        padding: 0;
      }
      .btns {
        margin-top: 0;
      }
    }
    .form-ueditor {
      .el-form-item__content {
        line-height: 0;
      }
    }
  }
</style>
