<template>
  <div class="cloudcontent" id="cloud_projectProcess">
    <!--面包屑-->
    <el-breadcrumb separator-class="el-icon-arrow-right" class="breadcrumb_box">
      <el-breadcrumb-item :to="{ path: '/index' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/processManage/projectProcess' }">项目进度管理</el-breadcrumb-item>
      <el-breadcrumb-item class="active_bread">修改</el-breadcrumb-item>
    </el-breadcrumb>
    <!--面包屑-->
    <div class="main">
      <el-form :model="updateForm" :rules="rules" ref="updateForm" :validate-on-rule-change="true">
        <el-row>
          <el-col :span="12">
            <el-form-item label="项目名称："  prop="tenderProjectName">
              <el-input v-model="updateForm.tenderProjectName"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="项目编号："  prop="tenderProjectCode">
              <el-input v-model="updateForm.tenderProjectCode"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="项目类型："  prop="tenderProjectType">
              <el-select v-model="updateForm.tenderProjectType" placeholder="请选择">
                <el-option
                  v-for="item in options"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="项目预算（元）："  prop="tenderProjectBudget">
              <el-input v-model="updateForm.tenderProjectBudget"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item :label="index === 0 ? '资金来源：': ''"
                          v-for="(item, index) in updateForm.fundSourceList"
                          :key="item.key"
                          :prop="'fundSourceList.' + index + '.value'"
                          :rules="rules.value">
             <!-- <div class="zjbox" v-for="(item, index) in fundSourceList" :key="item.value">-->
                <el-input placeholder="请输入内容" v-model="item.value" class="input-with-select" >
                  <el-select v-model="item.sourceOfFunds" slot="prepend" placeholder="请选择" @visible-change="checkChooseStatus" @change="checkSourceOfFunds(index)">
                    <el-option
                      v-for="item in fundSourceOptions"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value"
                      :disabled="item.disabled"></el-option>
                  </el-select>
                  <el-button slot="append">%</el-button>
                </el-input>
                <el-button class="addbutton" v-if="index === 0" @click="addItem">添加</el-button>
                <el-button class="addbutton" v-if="index !== 0" @click="delItem(index)">删除</el-button>
                <div class="radiobox" v-if="Number(item.sourceOfFunds) === 1">
                  <div>
                    国有资金控股：
                    <el-radio-group v-model="stateOwnedCapitalHolding" @change="checkHolding(index)">
                      <el-radio :label="1" value="1">是</el-radio>
                      <el-radio :label="0" value="0">否</el-radio>
                    </el-radio-group>
                  </div>
                  <div>
                    国有资金占主导地位：
                    <el-radio-group v-model="stateOwnedFundsPredominate" @change="checkStateOwnedFunds(index)">
                      <el-radio :label="1" value="1">是</el-radio>
                      <el-radio :label="0" value="0">否</el-radio>
                    </el-radio-group>
                  </div>
                </div>
              <!--</div>-->
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="批准文号："  prop="approvalNumber">
              <el-input v-model="updateForm.approvalNumber"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="招标方式："  prop="tenderMode">
              <el-select v-model="updateForm.tenderMode" placeholder="请选择">
                <el-option
                  v-for="item in typeOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="是否资格预审："  prop="isPreBid">
              <el-select v-model="updateForm.isPreBid" placeholder="请选择">
                <el-option
                  v-for="item in isCheckOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="是否允许联合体投标：">
              <el-select v-model="updateForm.syndicatedFlag" placeholder="请选择">
                <el-option
                  v-for="item in isAllowOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="招标人："  prop="tendererName">
              <el-input v-model="updateForm.tendererName"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="监督部门名称：">
              <el-input v-model="updateForm.supervisionDepName"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24" class="ueditor_box">
            <el-form-item label="招标内容与范围及招标方案说明：">
              <editor ref="ueditor" class="ueditor" :editread="editread" :content="content"></editor>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="附件：">
              <upload-file @uploadSuccess="uploadOtherSuccess" @deleteSuccess="deleteSuccess"
                           :fileArrays="updateForm.fileInformationList"
                           fileType="9"
                           isImage="0">
              </upload-file>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item class="submit-radio">
          <el-button type="primary" @click="handleClickEvent('updateForm','submit')">提交</el-button>
          <el-button type="primary" @click="handleClickEvent('updateForm','save')">保存</el-button>
          <el-button class="cancal" @click="handleClickEvent('updateForm','cancel')">取消</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>
<script>
import editor from '@/components/ueditor/ueditor.vue'
import {tenderProject} from '@/api/cloudplatform/index'
import uploadFile from '@/components/upload/publicFileUpload'
import {sumType, isValidPercentage} from '@/assets/js/validate'
export default {
  components: {
    editor,
    uploadFile
  },
  data () {
    // 项目预算
    let validBudget = (rule, value, callback) => {
      if (!value) {
        callback()
      } else if (!sumType(value)) {
        callback(new Error('请输入数字且最多两位小数！'))
      } else {
        callback()
      }
    }
    // 百分比
    let ValidPercentage = (rule, value, callback) => {
      if (!value) {
        callback()
      } else if (!isValidPercentage(value)) {
        callback(new Error('请输入1-100数字且最多两位小数！'))
      } else {
        callback()
      }
    }
    return {
      updateForm: {
        tenderProjectName: '',
        fundSourceList: [
          {key: 0, sourceOfFunds: '', value: ''}
        ],
        fileInformationList: []
      },
      rules: {
        tenderProjectName: [
          { required: true, message: '项目名称不能为空', trigger: ['blur', 'change'] }
        ],
        tenderProjectCode: [
          { required: true, message: '项目编号不能为空', trigger: ['blur', 'change'] }
        ],
        tenderProjectType: [
          { required: true, message: '请选择项目类型', trigger: ['blur', 'change'] }
        ],
        tenderProjectBudget: [
          { required: true, message: '项目预算不能为空', trigger: ['blur', 'change'] },
          {validator: validBudget, trigger: ['blur', 'change']}
        ],
        approvalNumber: [
          { required: true, message: '批准文号不能为空', trigger: ['blur', 'change'] }
        ],
        tenderMode: [
          { required: true, message: '请选择招标方式', trigger: ['blur', 'change'] }
        ],
        isPreBid: [
          { required: true, message: '请选择是否资格预审', trigger: ['blur', 'change'] }
        ],
        tendererName: [
          { required: true, message: '招标人名称不能为空', trigger: ['blur', 'change'] }
        ],
        value: [
          { required: true, message: '请输入资金比例', trigger: ['blur', 'change'] },
          { validator: ValidPercentage, trigger: ['blur', 'change'] }
        ]
      },
      // 项目类型
      options: [{
        value: '1',
        label: '工程'
      }, {
        value: '2',
        label: '货物'
      }, {
        value: '3',
        label: '服务'
      }],
      value: '',
      // 招标方式
      typeOptions: [{
        value: 1,
        label: '公开招标'
      }, {
        value: 2,
        label: '邀请招标'
      }, {
        value: 3,
        label: '竞争性谈判'
      }, {
        value: 4,
        label: '单一来源采购'
      }, {
        value: 5,
        label: '询价采购'
      }, {
        value: 6,
        label: '竞争性磋商'
      }],
      typeValue: '',
      // 是否资格预审
      isCheckOptions: [{
        value: 1,
        label: '是'
      }, {
        value: 0,
        label: '否'
      }],
      isCheckValue: '',
      // 是否允许联合体投标
      isAllowOptions: [{
        value: '1',
        label: '是'
      }, {
        value: '0',
        label: '否'
      }],
      fundSourceOptions: [{
        value: 1,
        label: '自有资金'
      }, {
        value: 2,
        label: '吸收资金'
      }, {
        value: 3,
        label: '专项资金'
      }, {
        value: 4,
        label: '其他资金'
      }],
      isAllowValue: '',
      // 富文本
      editread: false,
      content: '',
      stateOwnedCapitalHolding: 0,
      stateOwnedFundsPredominate: 0
    }
  },
  created () {
    this.getChooseTenderProjectInfo()
  },
  methods: {
    /** 获取当前招标项目信息 */
    getChooseTenderProjectInfo () {
      tenderProject.getBaseByCode(this.$route.query.code).then((res) => {
        if (res.data.tenderProject) {
          if (this.$route.query.operation === 'edit') {
            this.updateForm = res.data.tenderProject
            if (res.data.fundSourceList) {
              this.updateForm.fundSourceList = res.data.fundSourceList
              this.updateForm.fundSourceList.forEach(item => {
                if (item.sourceOfFunds === 1) {
                  this.stateOwnedCapitalHolding = item.stateOwnedCapitalHolding
                  this.stateOwnedFundsPredominate = item.stateOwnedFundsPredominate
                }
              })
            }
            // 获取文本编辑器的内容
            this.$refs.ueditor.setContent(this.content = this.updateForm.tenderContent)
            // 设置附件信息
            if (this.updateForm.fileInformationList.length > 0) {
              this.updateForm.fileInformationList.forEach(item => {
                item.objectId = null
              })
            }
          } else {
            this.updateForm.objectId = res.data.tenderProject.objectId
            this.updateForm.code = res.data.tenderProject.code
            this.updateForm.tenderProjectName = res.data.tenderProject.tenderProjectName
            this.updateForm.tenderProjectCode = res.data.tenderProject.tenderProjectCode
            this.updateForm.tendererName = res.data.tenderProject.tendererName
          }
        }
      })
    },
    /** 判断资金来源是否可选 */
    checkChooseStatus () {
      if (this.updateForm.fundSourceList.length > 0) {
        for (let i = 0; i < this.updateForm.fundSourceList.length; i++) {
          for (let j = 0; j < this.fundSourceOptions.length; j++) {
            if (Number(this.updateForm.fundSourceList[i].sourceOfFunds) === this.fundSourceOptions[j].value) {
              this.fundSourceOptions[j].disabled = true
              continue
            }
          }
        }
      }
    },
    /** 判断选择的资金来源类型是否为自有资金时为其设置默认值 */
    checkSourceOfFunds (index) {
      if (Number(this.updateForm.fundSourceList[index].sourceOfFunds) === 1) {
        this.updateForm.fundSourceList[index].stateOwnedCapitalHolding = this.stateOwnedCapitalHolding
        this.updateForm.fundSourceList[index].stateOwnedFundsPredominate = this.stateOwnedFundsPredominate
      }
    },
    /* 按钮点击事件：提交、保存、取消 */
    handleClickEvent (formName, type) {
      if (Object.is(type, 'submit') || Object.is(type, 'save')) {
        this.$refs[formName].validate((valid) => {
          if (valid) {
            // 获取文本编辑器中的内容
            if (this.$refs.ueditor) {
              this.updateForm.tenderContent = this.$refs.ueditor.getContent()
            }
            // 根据type设置当前招标项目的状态
            if (Object.is(type, 'submit')) {
              this.updateForm.status = 9
            } else {
              this.updateForm.status = 5
            }
            tenderProject.save(this.updateForm).then((res) => {
              if (res.data.resCode === '0000') {
                this.$router.push({path: '/processManage/projectProcess'})
              }
            })
          } else {
            return false
          }
        })
      } else if (Object.is(type, 'cancel')) {
        this.$router.push({path: '/processManage/projectProcess'})
      }
    },
    addItem () {
      this.updateForm.fundSourceList.push(
        {key: this.updateForm.fundSourceList.length, sourceOfFunds: '', value: ''})
    },
    // 上传附件
    uploadOtherSuccess (file) {
      this.updateForm.fileInformationList.push(file)
    },
    deleteSuccess (fileId) {
      this.updateForm.fileInformationList = this.updateForm.fileInformationList.filter(item => item.relativePath !== fileId)
    },
    delItem (index) {
      if (this.updateForm.fundSourceList[index].sourceOfFunds !== '') {
        this.fundSourceOptions[Number(this.updateForm.fundSourceList[index].sourceOfFunds) - 1].disabled = false
      }
      this.updateForm.fundSourceList.splice(index, 1)
    },
    /** 处理资金来源中的信息 */
    checkHolding (index) {
      this.updateForm.fundSourceList[index].stateOwnedCapitalHolding = this.stateOwnedCapitalHolding
    },
    checkStateOwnedFunds (index) {
      this.updateForm.fundSourceList[index].stateOwnedFundsPredominate = this.stateOwnedFundsPredominate
    }
  }
}
</script>
<style lang="less">
  #cloud_projectProcess {
    .el-select{
      width: 100%;
    }
    .zjbox{
      margin-bottom: 20px;
    }
    .input-with-select{
      width: 320px;
    }
    .input-with-select .el-input-group__prepend,.el-input-group__append{
      background-color: #fff;
    }
    .input-with-select .el-input-group__prepend .el-select{
      width: 130px;
    }
    .addbutton{
      background: #6582f8;
      border-color: #6582f8;
      color: #ffffff;
      margin-left: 10px;
    }
    .addbutton span{
      color: #ffffff;
    }
  }
</style>
