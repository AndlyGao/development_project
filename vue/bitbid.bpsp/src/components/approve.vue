<template>
    <tr class="tr_examine">
      <td class="examine">审批：</td>
      <td colspan="5">
        <el-form :model="approveForm" :rules="rules" ref="approveForm" label-width="150px">
          <el-form-item label="审批是否通过：" prop="auditStatus">
            <el-radio
              v-model="approveForm.auditStatus"
              :label="acceptResultsRadio.label"
              v-for="(acceptResultsRadio,index) in radioAcceptResults"
              :key="index"
              @change="changeRadio">
              {{acceptResultsRadio.name}}
            </el-radio>
          </el-form-item>
          <!--<el-form-item label="审批意见：" v-if="approveForm.auditStatus === 1">-->
            <!--<el-input type="textarea" v-model="approveForm.opinion"></el-input>-->
          <!--</el-form-item>-->
          <el-form-item label="审批意见：" :prop="(approveForm.auditStatus === 0)?'opinion':''">
            <el-input type="textarea" v-model="approveForm.opinion"></el-input>
          </el-form-item>
        </el-form>
      </td>
    </tr>
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
    changeRadio (val) {
      this.$set(this.approveForm, 'auditStatus', val)
      if (val) {
        this.$refs['approveForm'].clearValidate()
      }
    },
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
</style>
