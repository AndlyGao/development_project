<template>
  <el-row>
    <!--<el-col :span="3" class="approve_label">审批：</el-col>-->
    <el-col :span="24">
      <el-form :model="approveForm" :rules="rules" ref="approveForm" label-width="140px" class="approve_form">
        <el-form-item label="审批：">
          <el-form-item label="审批是否通过：" prop="auditStatus">
            <el-radio-group v-model="approveForm.auditStatus">
              <el-radio :label="1">通过</el-radio>
              <el-radio :label="0">不通过</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="审批意见：" :prop="(approveForm.auditStatus === 0)?'opinion':''" class="approve_opinion">
            <el-input type="textarea" v-model="approveForm.opinion"></el-input>
          </el-form-item>
        </el-form-item>
      </el-form>
    </el-col>
  </el-row>
</template>
<script>
export default {
  data () {
    return {
      approveForm: {
        auditStatus: 1,
        opinion: ''
      },
      radioAcceptResults: [
        {
          label: 1,
          name: '通过'
        },
        {
          label: 0,
          name: '不通过'
        }
      ],
      rules: {
        auditStatus: [
          { required: true, message: '请选择审批结果', trigger: 'change' }
        ],
        opinion: [
          {required: true, message: '请输入审批意见', trigger: ['blur', 'change']}
        ]
      }
    }
  },
  watch: {
    approveForm: function (newV, oldV) {
      this.handleValidate()
      this.$emit('approveForm', this.approveForm)
    },
    deep: true
  },
  methods: {
    handleValidate () {
      this.$refs.approveForm.validate((valid) => {
        if (valid) {
          this.$emit('valid', true)
          this.$emit('approveForm', this.approveForm)
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

<style lang="less">
  .approve_label {
    text-align: end;
    color: #606266;
  }
  .approve_form {
    width: 100%!important;
  }
  .approve_opinion {
    margin-top: 16px;
  }
</style>
