<template>
  <div id="subresultsindexbox"  class="maincontent">
    <div class="headertitle">
      <!--面包屑-->
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/mr/index?roledsType=1&showStatus=false' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>成交确认</el-breadcrumb-item>
        <el-breadcrumb-item>提交成交结果公示</el-breadcrumb-item>
      </el-breadcrumb>
      <!--面包屑-->
    </div>
    <div class="contentbigbox">
      <template>
        <!--添加按钮-->
        <el-row>
          <el-button type="success" class="addbutton" @click="addBtn"> + 添加</el-button>
          <div class="seacher_box">
            <el-select v-model="selectCondition" class="select" @change="selectSeacher">
              <el-option
                v-for="item in options"
                :key="item.value"
                :label="item.label"
                :value="item.value">
              </el-option>
            </el-select>
            <el-input class="search" style="vertical-align: top"  :placeholder="'请输入' + seacherName" v-model="seacher_input">
              <el-button  slot="append" icon="el-icon-search" @click="search"></el-button>
            </el-input>
          </div>
        </el-row>
        <!--添加按钮-->
        <!--主要内容 table-->
        <el-table
          :data="tranResTableData"
          border
          header-cell-class-name="tabletitles">
          <el-table-column
            prop="publicityName"
            label="公示名称"
            align="left"
            width="350"
            show-overflow-tooltip>
          </el-table-column>
          <el-table-column
            prop="sectionNumber"
            label="项目编号"
            align="left"
            width="200"
            show-overflow-tooltip>
            <template slot-scope="scope">
              <span class="jfbdbox" v-for="(item,index) in scope.row.refTransResuList" :key="index">
                <span class="xmbh_box">{{item.section.sectionNumber}}</span>
              </span>
            </template>
          </el-table-column>
          <el-table-column
            prop="sectionName"
            label="项目名称"
            align="left"
            width="350"
            show-overflow-tooltip>
            <template slot-scope="scope">
              <span class="jfbdbox" v-for="(item,index) in scope.row.refTransResuList" :key="index">
                <span class="xmbh_box">{{item.section.sectionName}}</span>
              </span>
            </template>
          </el-table-column>
          <el-table-column
            prop="mineralType"
            label="出让方式"
            align="left">
            <template slot-scope="scope">
              <div class="jfbdbox" v-for="(item,index) in scope.row.refTransResuList" :key="index">
                <div class="statusbigbox" v-if="item.section.landUse === 1">
                  <span class="statusbox">拍卖</span>
                </div>
                <div class="statusbigbox" v-if="item.section.landUse === 2">
                  <span class="statusbox">挂牌</span>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column
            prop="status"
            label="公告状态"
            align="left">
            <template slot-scope="scope">
              <div class="statusbigbox" v-if="scope.row.status === 4">
                <span class="statusbox">已发布</span>
              </div>
              <div class="statusbigbox" v-if="scope.row.status === 3">
                <span class="statusbox">审批不通过</span>
              </div>
              <div class="statusbigbox" v-if="scope.row.status === 2">
                <span class="statusbox">审批通过</span>
              </div>
              <div class="statusbigbox" v-if="scope.row.status === 1">
                <span class="statusbox">备案审批中</span>
              </div>
              <div class="statusbigbox" v-if="scope.row.status === 0">
                <span class="statusbox">未提交</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column
            label="操作" width="140" align="center">
            <template slot-scope="scope">
              <div class="statusbigbox" v-if="scope.row.status === 0">
                <el-button type="text" size="small" @click="updateBtn(scope)">
                  修改
                </el-button>
                <el-button type="text" size="small" @click="deleteBtn(scope)">
                  删除
                </el-button>
                <el-button type="text" size="small" @click="lookBtn(scope)">
                  查看
                </el-button>
              </div>
              <div class="statusbigbox" v-if="scope.row.status === 1">
                <el-button type="text" size="small" @click="lookBtn(scope)">
                  查看
                </el-button>
              </div>
              <div class="statusbigbox" v-if="scope.row.status === 2">
                <el-button type="text" size="small" @click="releaseBtn(scope)">
                  发布
                </el-button>
                <el-button type="text" size="small" @click="updateBtn(scope)">
                  变更
                </el-button>
                <el-button type="text" size="small" @click="lookBtn(scope)">
                  查看
                </el-button>
              </div>
              <div class="statusbigbox" v-if="scope.row.status === 3">
                <el-button type="text" size="small" @click="updateBtn(scope)">
                  修改
                </el-button>
                <el-button type="text" size="small" @click="lookBtn(scope)">
                  查看
                </el-button>
              </div>
              <div class="statusbigbox" v-if="scope.row.status === 4">
                <el-button type="text" size="small" @click="lookBtn(scope)">
                  查看
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
        <!--主要内容 table-->
        <!--分页-->
        <el-pagination
          class="pagebox"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page.sync="currentPage"
          :total="total"
          :page-size='pageSize'
          layout="prev, pager, next, jumper">
        </el-pagination>
        <!--分页-->
      </template>
    </div>
  </div>
</template>
<script>
import {transResAnno} from '@/api'
export default {
  data () {
    return {
      seacher_input: '',
      // 选择不同的搜索方式切换不同的提示
      seacherName: '公示名称',
      options: [{
        value: 1,
        label: '公示名称'
      }, {
        value: 2,
        label: '项目名称'
      }, {
        value: 3,
        label: '项目编号'
      }],
      // 当前页
      pageSize: 10,
      pageNo: 0,
      total: 0, // 总条数
      currentPage: 1,
      selectCondition: 1,
      tranResTableData: [],
      searchIs: false
    }
  },
  methods: {
    handleSizeChange (nowNum) {
      this.pageNo = (nowNum - 1) * this.pageSize
      this.queryList(this.searchIs)
    },
    handleCurrentChange (nowNum) {
      this.pageNo = (nowNum - 1) * this.pageSize
      this.queryList(this.searchIs)
    },
    // 搜索选择
    selectSeacher (val) {
      if (val === 1) {
        this.seacherName = '公示名称'
        this.selectCondition = 1
      } else if (val === 2) {
        this.seacherName = '项目名称'
        this.selectCondition = 2
      } else {
        this.seacherName = '项目编号'
        this.selectCondition = 3
      }
    },
    deleteBtn (scope) {
      this.$confirm('确认删除吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'error'
      }).then(() => {
        transResAnno.delete(scope.row.objectId).then((res) => {
          this.queryList()
        })
      }).catch(() => {
        return false
      })
    },
    // 添加
    addBtn () {
      this.$router.push({path: '/mr/transferor/business-confirmation/sub-results-transaction/select-publicity?roledsType=1'})
    },
    // 修改
    updateBtn (scope) {
      this.$router.push({path: '/mr/transferor/business-confirmation/sub-results-transaction/update', query: {objectId: scope.row.objectId, annoId: scope.row.annoId, roledsType: 1}})
    },
    // 发布
    releaseBtn (scope) {
      this.$router.push({path: '/mr/transferor/business-confirmation/sub-results-transaction/release', query: {objectId: scope.row.objectId, roledsType: 1}})
    },
    // 查看
    lookBtn (scope) {
      this.$router.push({path: `/mr/transferor/business-confirmation/sub-results-transaction/detail/${scope.row.objectId}`, query: {roledsType: 1}})
    },
    // 列表显示
    queryList () {
      let url = null
      if (this.searchIs) {
        url = {
          pageNo: this.pageNo,
          pageSize: this.pageSize,
          isDelete: 0
        }
        if (this.selectCondition === 1) {
          url.publicityNameLike = this.seacher_input
        } else if (this.selectCondition === 2) {
          url.sectionName = this.seacher_input
        } else if (this.selectCondition === 3) {
          url.sectionNumber = this.seacher_input
        }
      } else {
        url = {
          pageNo: this.pageNo,
          pageSize: this.pageSize,
          isDelete: 0
        }
      }
      transResAnno.queryList(url).then((res) => {
        this.tranResTableData = res.data.transResultsAnnoList.list
        this.total = res.data.transResultsAnnoList.total
      })
    },
    search () {
      this.currentPage = 1
      this.pageNo = 0
      let searchs = {
        pageNo: this.pageNo,
        pageSize: this.pageSize,
        isDelete: 0
      }
      if (this.selectCondition === 1) {
        searchs.publicityNameLike = this.seacher_input
      } else if (this.selectCondition === 2) {
        searchs.sectionName = this.seacher_input
      } else if (this.selectCondition === 3) {
        searchs.sectionNumber = this.seacher_input
      }
      transResAnno.queryList(searchs).then((res) => {
        this.tranResTableData = res.data.transResultsAnnoList.list
        this.total = res.data.transResultsAnnoList.total
      })
      this.currentPage = 1
      this.pageNo = (this.currentPage - 1) * this.pageSize
      this.searchIs = true
    }
  },
  mounted () {
    this.queryList()
  }
}
</script>
<style lang="less">
  @import '../../../../../assets/css/mineral/common.css';
  #subresultsindexbox{
   .jfbdbox{
     display: block;
   }
  }
</style>
