<template>
  <div class="offerDetail details blueCon" id="offerDetail">
    <el-breadcrumb separator-class="el-icon-arrow-right">
      <el-breadcrumb-item :to="{ path: '/'}">首页</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/'}">项目管理</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/projectExecute'}">项目执行</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/projectExecute/directPurchase/offer'}">直采报价</el-breadcrumb-item>
      <el-breadcrumb-item>直采详情</el-breadcrumb-item>
    </el-breadcrumb>
    <el-form :model="dataForm" label-width="160px">
      <div class="mainContent">
        <h3 class="title">项目信息</h3>
        <el-row class="marginTop">
          <el-col :span="24">
            <el-form-item label="项目名称">
              <span>{{dataForm.name}}</span>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="联系人">
              <span>{{dataForm.name}}</span>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="联系方式">
              <span>{{dataForm.name}}</span>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="联系信息">
              <span>{{dataForm.name}}</span>
            </el-form-item>
          </el-col>
        </el-row>
      </div>
      <div class="mainContent">
        <h3 class="title">报价信息</h3>
        <el-row class="marginTop ">
          <el-col :span="24">
            <el-form-item label="报价公司名称">
              <span>{{dataForm.name}}</span>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="联系人">
              <span>{{dataForm.name}}</span>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="联系方式">
              <span>{{dataForm.name}}</span>
            </el-form-item>
          </el-col>
        </el-row>
      </div>
      <div class="mainContent">
        <h3 class="title">产品信息</h3>
        <p class="handle handle-none">
          <span>共4种物料，采购总金额：<i>￥50000.00</i>（已报价4种物料，报价总金额：<i>￥50000.00</i>）</span>
        </p>
        <div class="tablemain product-table">
          <el-table
            :data="tableData"
            border
            header-cell-class-name="table_header"
            style="width: 100%">
            <el-table-column
              type="index"
              label="序号"
              width="50"
              align="center"
              :index="indexMethod">
            </el-table-column>
            <el-table-column
              prop="date"
              label="物料号">
            </el-table-column>
            <el-table-column
              prop="number"
              label="物料类目">
            </el-table-column>
            <el-table-column
              prop="date"
              label="物料名称">
            </el-table-column>
            <el-table-column
              prop="number"
              label="品牌">
            </el-table-column>
            <el-table-column
              prop="date"
              label="型号">
            </el-table-column>
            <el-table-column
              prop="number"
              label="物料描述">
            </el-table-column>
            <el-table-column
              prop="date"
              label="采购量">
            </el-table-column>
            <el-table-column
              prop="number"
              label="预算总额">
            </el-table-column>
            <el-table-column
              prop="money"
              label="报价总额">
              <template slot-scope="scope">
                <span v-if="scope.row.money" class="moneyOffer">{{scope.row.money}}</span>
                <span v-else>未报价</span>
              </template>
            </el-table-column>
            <el-table-column
              prop="date"
              align="center"
              label="备注及附件">
              <template slot-scope="scope">
                <el-button type="text" size="small" @click="handleCheck(scope, '备注及附件')">查看</el-button>
              </template>
            </el-table-column>
            <el-table-column
              prop="number"
              align="center"
              label="报价及附件">
              <template slot-scope="scope">
                <el-button type="text" size="small" class="operation-btn" v-if="scope.row.money" @click="handleCheck(scope, '报价及附件')">查看</el-button>
                <span v-else>-----</span>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
      <div class="mainContent">
        <h3 class="title">供货信息</h3>
        <div class="prompt-title">
          <img src="../../../../../static/images/gth.png" alt="">
          <span>请仔细确认供应商供货信息</span>
        </div>
        <el-row class="marginTop">
          <el-col :span="24">
            <el-form-item label="生产备货周期">
              <span>{{dataForm.name}}</span>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="交货期">
              <span>{{dataForm.name}}</span>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="预估运费">
              <span>{{dataForm.name}}</span>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="供货地址">
              <span>{{dataForm.name}}</span>
              <br>
              <span>{{dataForm.name}}</span>
            </el-form-item>
          </el-col>
        </el-row>
      </div>
      <div class="mainContent">
        <h3 class="title">采购信息</h3>
        <el-row class="marginTop">
          <el-col :span="24">
            <el-form-item label="报价截止日期">
              <span>{{dataForm.name}}</span>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="期望收货日期">
              <span>{{dataForm.name}}</span>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="收货地址">
              <span>{{dataForm.name}}</span>
              <br>
              <span>{{dataForm.name}}</span>
            </el-form-item>
          </el-col>
        </el-row>
      </div>
      <div class="mainContent">
        <h3 class="title">供应商要求</h3>
        <el-row class="marginTop ">
          <el-col :span="24">
            <el-form-item label="报价要求">
              <span>{{dataForm.name}}</span>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="支付方式">
              <span>{{dataForm.name}}</span>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="交易方式">
              <span>{{dataForm.name}}</span>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="账期结算日">
              <span>{{dataForm.name}}</span>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="发票要求">
              <span>{{dataForm.name}}</span>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="经营地址">
              <span>{{dataForm.name}}</span>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="注册资金">
              <span>{{dataForm.name}}</span>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="证照要求">
              <span>{{dataForm.name}}</span>
            </el-form-item>
          </el-col>
        </el-row>
      </div>
      <div class="mainContent">
        <h3 class="title">补充说明</h3>
        <el-row class="marginTop">
          <el-col :span="24">
            <el-form-item label="补充说明" class="ueditor-el">
              <editor ref="ueditor" class="ueditor" :editread="true" :content = "content"></editor>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="添加附件" class="fileDownload executeFile">
              <file-download :fileArrays="fileInformations" fileType="7"></file-download>
            </el-form-item>
          </el-col>
        </el-row>
      </div>
      <div class="footerDetailBtn">
        <el-button class="detailBtn returnBtn" @click="$router.go(-1)">返回</el-button>
        <el-button class="detailBtn" type="primary">退回报价</el-button>
        <el-button class="detailBtn" type="primary">选择报价</el-button>
      </div>
      <el-dialog :title="dialogLookName" width="668px" :visible.sync="dialogFormVisible">
        <el-form-item label="报价" label-width="60px" v-if="dialogLookName === '报价及附件'">
          <span>5000（报价含税）</span>
        </el-form-item>
        <el-form-item label="备注" label-width="60px">
          <el-input v-model="dataForm.name" type="textarea" autocomplete="off" readonly></el-input>
        </el-form-item>
        <el-form-item label="附件" label-width="60px" class="fileDownload executeFile">
          <file-download :fileArrays="fileInformations" fileType="7"></file-download>
        </el-form-item>
        <div slot="footer" class="dialog-footer">
          <el-button @click="dialogFormVisible = false">取 消</el-button>
        </div>
      </el-dialog>
    </el-form>
  </div>
</template>

<script>
import editor from '@/components/ueditor/ueditor.vue'
import fileDownload from '@/components/download/file-download'
export default {
  components: {
    editor,
    fileDownload
  },
  data () {
    return {
      dataForm: {
        name: '山西众泰集团有限公司'
      },
      indexMethod: 1, // 序号
      content: '',
      tableData: [
        {
          date: '2019-11-11',
          number: '测试项目',
          money: '4000'
        },
        {
          date: '2019-11-11',
          number: '测试项目',
          money: ''
        },
        {
          date: '2019-11-11',
          number: '测试项目',
          money: '4000'
        }
      ],
      fileInformations: [
        {
          fileName: '营业执照.jpg',
          businessType: '7'
        },
        {
          fileName: '连锁执照.jpg',
          businessType: '7'
        }
      ],
      dialogLookName: '', // 弹框名字
      dialogFormVisible: false
    }
  },
  methods: {
    handleCheck (scope, name) {
      this.dialogLookName = name
      this.dialogFormVisible = true
    }
  },
  mounted () {
  }
}
</script>

<style lang="less">
  #offerDetail{
    .prompt-title{
      width: 98%;
      height: 26px;
      background: #fff6f0;
      padding: 0 12px;
      border: 1px solid #ff8c40;
      box-sizing: border-box;
      color: #ff8c40;
      margin: 10px auto;
    }
    .prompt-title>img{
      display: inline-block;
      vertical-align: middle;
    }
    .prompt-title>span{
      display: inline-block;
      vertical-align: middle;
    }
    .handle-none{
      border-bottom: none;
    }
    .tablemain .el-table td {
      border-bottom: 1px solid #e6e6e6;
      padding: 6px 0 6px 0;
      color: #333333;
    }
    .marginTop{
      margin-top: 15px;
    }
    .product-table{
      padding-bottom: 15px;
    }
    .ueditor{
      width: 99%;
    }
    .ueditor-el .edui-editor-toolbarboxouter{
      display: none;
    }
    .fileDownload .el-form-item__label{
      line-height: 40px;
    }

  }
</style>
