<template>
  <div id="transelbmissionindexbox"  class="maincontent">
    <div class="headertitle">
      <!--面包屑-->
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/mr/index?roledsType=1&showStatus=false' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>出让文件</el-breadcrumb-item>
        <el-breadcrumb-item :to="{ path: '/mr/transferor/transfer-documents/sub-transfer-documents?roledsType=1'}">提交出让文件</el-breadcrumb-item>
        <el-breadcrumb-item>选择出让公告</el-breadcrumb-item>
      </el-breadcrumb>
      <!--面包屑-->
    </div>
    <div class="contentbigbox">
      <template>
        <!--添加按钮-->
        <el-row>
          <div class="seacher_box">
            <el-select v-model="selectStatus" class="select" @change="selectSeacher">
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
          :data="projecttableData"
          border
          header-cell-class-name="tabletitles">
          <el-table-column
            prop="noticeName"
            label=""
            align="center"
            width="50">
            <template slot-scope="scope">
              <i class="morebtn el-icon-arrow-right" @click="btnOpen($event)"></i>
            </template>
          </el-table-column>
          <el-table-column
            prop="annoNumber"
            label="公告名称"
            align="left"
            show-overflow-tooltip>
          </el-table-column>
          <el-table-column
            prop="sectionNumber"
            label="项目编号"
            align="left"
            width="200"
            show-overflow-tooltip>
            <template slot-scope="scope">
              <span class="jfbdbox" v-for="(item,index) in scope.row.sections" :key="index">
                <span class="xmbh_box" :title="item.sectionNumber">{{item.sectionNumber}}</span>
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
              <span class="jfbdbox" v-for="(item,index) in scope.row.sections" :key="index">
                <span class="xmbh_box" :title="item.sectionName">{{item.sectionName}}</span>
              </span>
            </template>
          </el-table-column>
          <el-table-column
            prop="landUse"
            label="出让方式"
            align="left"
            show-overflow-tooltip>
            <template slot-scope="scope">
              <div class="statusbigbox" v-if="scope.row.landUse === 1">
                <span class="statusbox">拍卖</span>
              </div>
              <div class="statusbigbox" v-if="scope.row.landUse === 2">
                <span class="statusbox">挂牌</span>
              </div>
              <div class="statusbigbox" v-if="scope.row.landUse === 3">
                <span class="statusbox">其他</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column
            label="操作" width="140" align="center">
            <template slot-scope="scope">
              <el-button type="text" size="small" @click="addFile(scope)">
                添加出让文件
              </el-button>
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
import $ from 'jquery'
import {anno, tdocuments} from '@/api'
export default {
  data () {
    return {
      seacher_input: '',
      // 选择不同的搜索方式切换不同的提示
      seacherName: '公告名称',
      options: [{
        value: 1,
        label: '公告名称'
      }, {
        value: 2,
        label: '项目名称'
      }, {
        value: 3,
        label: '项目编号'
      }],
      selectStatus: 1,
      projecttableData: [],
      // 当前页
      currentPage: 1,
      total: 0, // 总条数
      pageNo: 0,
      pageSize: 10, // 每页展示条数
      searchIs: false,
      // 添加状态
      addStatus: false
    }
  },
  methods: {
    // 列表
    queryList () {
      let url = null
      if (this.searchIs) {
        url = {
          pageNo: this.pageNo,
          pageSize: this.pageSize,
          isDelete: 0,
          isSupplement: 0,
          status: 4
        }
        if (this.selectStatus === 1) {
          url.annoNumbers = this.seacher_input
        } else if (this.selectStatus === 2) {
          url.sectionName = this.seacher_input
        } else if (this.selectStatus === 3) {
          url.sectionNumber = this.seacher_input
        }
      } else {
        url = {
          pageNo: this.pageNo,
          pageSize: this.pageSize,
          isSupplement: 0,
          isDelete: 0,
          status: 4
        }
      }
      anno.announcementList(url).then((res) => {
        this.projecttableData = res.data.AnnouncementList.list
        this.total = res.data.AnnouncementList.total
      })
    },
    search () {
      this.currentPage = 1
      this.pageNo = 0
      let searchs = {
        pageNo: this.pageNo,
        pageSize: this.pageSize,
        isDelete: 0,
        isSupplement: 0,
        status: 4
      }
      // 通过不同的查询进行参数赋值
      if (this.seacherName == '') {
        this.$message({
          type: 'warning',
          message: '请选择查询方式'
        })
        return false
      } else if (this.selectStatus === 1) {
        searchs.annoNumbers = this.seacher_input
      } else if (this.selectStatus === 2) {
        searchs.sectionName = this.seacher_input
      } else if (this.selectStatus === 3) {
        searchs.sectionNumber = this.seacher_input
      }
      anno.announcementList(searchs).then((res) => {
        this.projecttableData = res.data.AnnouncementList.list
        this.total = res.data.AnnouncementList.total
        this.searchIs = true
      })
    },
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
        this.seacherName = '公告名称'
        this.selectStatus = 1
      } else if (val === 2) {
        this.seacherName = '项目名称'
        this.selectStatus = 2
      } else {
        this.seacherName = '项目编号'
        this.selectStatus = 3
      }
    },
    // 展开隐藏信息
    btnOpen (event) {
      if ($(event.currentTarget).hasClass('el-icon-arrow-down')) {
        $(event.currentTarget).removeClass('el-icon-arrow-down').addClass('el-icon-arrow-right')
        $(event.currentTarget).parents('tr.el-table__row').find('.cell').css('height', '30px')
      } else {
        $(event.currentTarget).parents('tr.el-table__row').find('.cell').css('height', 'auto')
        $(event.currentTarget).removeClass('el-icon-arrow-right').addClass('el-icon-arrow-down')
      }
    },
    // 添加
    addFile (scope) {
      let url = {
        annoId: scope.row.objectId,
        isChecked: 0
      }
      tdocuments.selectStatus(url).then((res) => {
        this.addStatus = res.data.status
        if (this.addStatus) {
          this.$message({
            type: 'warning',
            message: '没有要添加出让文件的标段'
          })
          return false
        } else {
          this.$router.push({path: '/mr/transferor/transfer-documents/sub-transfer-documents/add', query: {objectId: scope.row.objectId, code: scope.row.code, roledsType: 1}})
        }
      })
    }
  },
  mounted () {
    this.queryList()
  }
}
</script>
<style lang="less">
  @import '../../../../../assets/css/mineral/common.css';
  #transelbmissionindexbox{
    .el-table .cell {
      height: 30px;
    }
    .cellopen{
      box-sizing: border-box;
      word-break: break-all;
      line-height: 23px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
    .el-tooltip{
      padding: 0 20px;
    }
    .xmbh_box{
      display: block;
      margin-bottom: 10px;
      overflow: hidden;
      text-overflow:ellipsis;
      white-space: nowrap;
    }
    .el-icon-arrow-right{
      cursor: pointer;
    }
  }
</style>
