<template>
  <div id="portal-credit-eval-page" class="cont-view">
    <!-- 面包屑区域start -->
    <div class="bread-box">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>门户信息管理</el-breadcrumb-item>
        <el-breadcrumb-item>信用评价</el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    <!-- 面包屑区域end -->
    <div class="credit-eval-list">
      <div class="handle-add">
        <el-button type="primary" size="small" @click="addEval">新增评价</el-button>
      </div>
      <el-table
        :data="tableData"
        border
        style="width: 100%">
        <el-table-column
          label="序号"
          type="index"
          align="center"
          :index="computedIndex"
          width="80">
        </el-table-column>
        <el-table-column
          prop="userName"
          align="center"
          :formatter="simpleFormatData"
          label="电商名称">
        </el-table-column>
        <el-table-column
          prop="creditOrder"
          align="center"
          :formatter="simpleFormatData"
          label="排名">
        </el-table-column>
        <el-table-column
          prop="evaluation"
          align="center"
          show-overflow-tooltip
          label="评价内容"
          :formatter="simpleFormatData">
        </el-table-column>
        <el-table-column
          align="center"
          label="操作"
          width="100">
          <template slot-scope="scope">
            <el-button type="text" @click="handleTableBtn(scope.row, 'edit')">编辑</el-button>
            <el-button type="text" @click="handleTableBtn(scope.row, 'del')">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-box">
        <!--分页-->
        <el-pagination
          background
          layout="prev, pager, next"
          :total="page.total"
          :page-size='page.pageSize'
          :current-page.sync="page.currentPage"
          @current-change="handlePage"
          @next-click="handlePage">
        </el-pagination>
        <!--分页-->
      </div>
    </div>
    <!-- 弹窗start -->
    <div class="dialog-box">
      <el-dialog
        title="新增评价"
        :visible.sync="dialogVisible"
        @closed="close"
        width="40%"
        center>
        <el-form :model="ruleForm" :rules="rules" ref="ruleForm" label-width="100px" class="demo-ruleForm">
          <el-form-item label="电商名称：" prop="userId">
            <el-select v-model="ruleForm.userId" placeholder="请选择电商名称">
              <el-option
                v-for="item in ecPlatform"
                :key="item.id"
                :label="item.userName"
                :value="item.id"
                :disabled="handleEcNameDisable(item)">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="排名：" prop="creditOrder">
            <el-select v-model="ruleForm.creditOrder" placeholder="请选择排名">
              <el-option
                v-for="item in orderOptions"
                :key="item.creditOrder"
                :label="item.name"
                :value="item.creditOrder"
                :disabled="handleOrderDisable(item)">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="评语：" prop="evaluation">
            <el-input type="textarea" v-model="ruleForm.evaluation" :rows="3"></el-input>
          </el-form-item>
        </el-form>
        <span slot="footer" class="dialog-footer">
          <el-button @click="dialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="submitForm">确 定</el-button>
        </span>
      </el-dialog>
    </div>
    <!-- 弹窗end -->
  </div>
</template>
<script>
import {credit, complain} from '@/api'
export default {
  name: 'credit-eval',
  data () {
    return {
      ruleForm: {},
      tableData: [],
      ecPlatform: [],
      page: {
        total: 0,
        pageSize: 10,
        pageNo: 0,
        currentPage: 1
      },
      rules: {
        userId: [
          {required: true, message: '请选择电商', trigger: ['blur', 'change']}
        ],
        creditOrder: [
          {required: true, message: '请选择排名', trigger: ['blur', 'change']}
        ],
        evaluation: [
          {required: true, message: '请填写评语', trigger: ['blur', 'change']}
        ]
      },
      dialogTitle: '',
      dialogVisible: false,
      isEidt: false,
      orderOptions: []
    }
  },
  methods: {
    close () {
      this.dialogVisible = false
      this.ruleForm = {}
      this.$refs['ruleForm'].resetFields()
    },
    simpleFormatData (row, col, cellValue) {
      return cellValue || '-----'
    },
    /** 序号计算 */
    computedIndex (index) {
      return index + (this.page.currentPage - 1) * this.page.pageSize + 1
    },
    submitForm () {
      this.$refs['ruleForm'].validate(valid => {
        if (valid) {
          credit.update(this.ruleForm).then(res => {
            // 成功
            if (res.data.resCode === '0000') {
              this.dialogVisible = false
              this.getList()
            }
          })
        } else {
          return false
        }
      })
    },
    /** 新增评价 */
    addEval () {
      this.dialogTitle = '新增评价'
      this.dialogVisible = true
      this.isEidt = false
    },
    handleTableBtn (row, type) {
      switch (type) {
        case 'edit':
          this.dialogTitle = '编辑评价'
          this.dialogVisible = true
          this.isEidt = true
          this.ruleForm = Object.assign({}, row)
          break
        case 'del':
          this.handleDel(row.id)
          break
      }
    },
    handleDel (id) {
      this.$confirm('确认删除吗?', '提示', {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'error'
      }).then(() => {
        // 执行
        credit.delCredit(id).then(res => {
          if (res.data.resCode === '0000') {
            this.getList()
          }
        })
      }).catch(() => {
        return false
      })
    },
    handlePage (nowNum) {
      this.page.currentPage = nowNum
      this.page.pageNo = (nowNum - 1) * this.page.pageSize
      this.getList()
    },
    getList () {
      credit.queryList().then(res => {
        this.tableData = res.data.creditEvaluationPageInfo.list
        this.page.total = res.data.creditEvaluationPageInfo.total
      })
    },
    generateOrderOption (list) {
      list.forEach((item, index) => {
        let obj = {}
        obj.creditOrder = Number(index) + 1
        obj.name = '第' + obj.creditOrder + '名'
        this.orderOptions.push(obj)
      })
    },
    getEcPlatformData () {
      complain.merchantName().then(res => {
        this.ecPlatform = res.data.users
        this.generateOrderOption(this.ecPlatform)
      })
    },
    handleOrderDisable (val) {
      let temp = this.tableData.some(item => item.creditOrder === val.creditOrder)
      return temp
    },
    handleEcNameDisable (val) {
      let temp = this.tableData.some(item => item.userId === val.id)
      return temp
    }
  },
  mounted () {
    this.getEcPlatformData()
    this.getList()
  }
}
</script>
<style lang="less">
#portal-credit-eval-page {
  .handle-add {
    padding-bottom: 15px;
    text-align: right;
  }
}
</style>
