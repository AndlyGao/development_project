<template>
  <div class="cloudcontent" id="cloud_processtype">
    <div class="box">
      <div class="topmain">
        招标邀请书
        <!--按钮-->
        <el-button class="addbutton" @click="handleAdd" v-if="operationFlag">
          <span> + 添加</span>
        </el-button>
        <!--按钮-->
      </div>
      <el-table
        :data="tableData"
        border>
        <el-table-column
          type="index"
          label="序号"
          width="50">
        </el-table-column>
        <el-table-column
          prop="title"
          label="邀请公告名称"
          width="180">
        </el-table-column>
        <el-table-column
          prop="auditStatus"
          label="提交状态"
          width="180"
          :formatter="filterAuditStatus">
        </el-table-column>
        <el-table-column
          prop="submitTime"
          label="提交时间">
        </el-table-column>
        <el-table-column
          label="操作" align="center" width="380">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="handleDetail(scope)">查看</el-button>
            <el-button type="text" size="small" v-if="(scope.row.auditStatus === 0 || scope.row.auditStatus === 3) && operationFlag" @click="handleEdit(scope)">编辑</el-button>
            <el-button type="text" size="small" v-if="(scope.row.auditStatus === 0 || scope.row.auditStatus === 3) && operationFlag" @click="handleDel(scope)">删除</el-button>
            <el-button type="text" size="small" v-if="(scope.row.auditStatus === 2 || scope.row.auditStatus === 4) && operationFlag" @click="handleTender(scope)">投标人确认情况</el-button>
            <el-button type="text" size="small" v-if="(scope.row.auditStatus === 2 || scope.row.auditStatus === 4) && scope.row.type === 4 && operationFlag" @click="addNoticeBtn(scope)">添加更正邀请公告</el-button>
          </template>
        </el-table-column>
      </el-table>
      <!--分页-->
      <el-pagination
        background
        layout="prev, pager, next"
        :total="total"
        :page-size='pageSize'
        :current-page.sync="currentPage"
        @current-change="handleCurrentChange">
      </el-pagination>
      <!--分页-->
    </div>
  </div>
</template>
<script>
import {bulletinInfo} from '@/api/cloudplatform/index'
export default {
  components: {
  },
  props: ['tenderSystemCode', 'sectionSystemCode', 'operationFlag', 'chooseSectionSystemCode'],
  data () {
    return {
      tableData: [],
      // 当前页
      currentPage: 1,
      pageNo: 0,
      total: 100, // 总条数
      pageSize: 10 // 每页展示条数
    }
  },
  methods: {
    /** 审批状态(0.未提交；1.待审批；2.已审批；3.已驳回；4.已提交（不需要审批）)  */
    filterAuditStatus (row) {
      if (row.auditStatus === 0) {
        return '已保存'
      } else if (row.auditStatus === 1) {
        return '待审批'
      } else if (row.auditStatus === 2) {
        return '已审批'
      } else if (row.auditStatus === 3) {
        return '已驳回'
      } else if (row.auditStatus === 4) {
        return '已提交'
      }
    },
    /**
     * 增加邀请公告
     */
    addNoticeBtn (scope) {
      this.$router.push({path: `/processManage/projectProcess/invite/add_notice`, query: {bulletinCode: scope.row.code}})
    },
    handleNoticeEdit (scope) {
      this.$router.push({path: `/processManage/projectProcess/invite/update_notice`, query: {code: scope.row.code}})
    },
    handleNoticeDetail (scope) {
      this.$router.push({path: `/processManage/projectProcess/invite/notice_detail/${scope.row.code}`})
    },
    // 编辑
    handleEdit (scope) {
      let type = scope.row.type
      switch (type) {
        case 3:
          this.handleNoticeEdit(scope)
          break
        case 4:
          this.$router.push({path: `/processManage/projectProcess/invite/update`, query: {code: scope.row.code}})
          break
      }
    },
    handleAdd () {
      this.$router.push({path: `/processManage/projectProcess/invite/add`, query: {tenderSystemCode: this.tenderSystemCode, sectionSystemCode: this.sectionSystemCode}})
    },
    handleDetail (scope) {
      let type = scope.row.type
      switch (type) {
        case 3:
          this.handleNoticeDetail(scope)
          break
        case 4:
          this.$router.push({path: `/processManage/projectProcess/invite/detail/${scope.row.code}`})
          break
      }
    },
    handleDel (scope) {
      this.$confirm('确认删除吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'error'
      }).then(() => {
        // **
        bulletinInfo.delete(scope.row.objectId).then(() => {
          this.getTableData()
        })
      }).catch(() => {
        return false
      })
    },
    handleTender (scope) {
      this.$router.push({path: `/processManage/projectProcess/invite/tender_confirm`, query: {code: scope.row.code}})
    },
    handleCurrentChange (nowNum) {
      this.currentPage = nowNum
      this.pageNo = (nowNum - 1) * this.pageSize
      this.getTableData(parseInt(this.pageNo), parseInt(this.pageSize))
    },
    /** 获取列表数据 */
    getTableData () {
      bulletinInfo.getByRelaSection({
        pageNo: this.pageNo,
        pageSize: this.pageSize,
        enterpriseId: this.$store.getters.authUser.enterpriseId,
        tenderSystemCode: this.tenderSystemCode,
        sectionSystemCode: this.chooseSectionSystemCode ? this.chooseSectionSystemCode : this.sectionSystemCode,
        typeList: '4,3'
      }).then((res) => {
        let infos = res.data.bulletinInfoList
        if (infos && infos.list) {
          this.tableData = infos.list
          this.total = infos.total
        }
      })
    }
  },
  watch: {
    '$route': 'getTableData'
  },
  mounted () {
    this.getTableData()
  }
}
</script>
<style lang="less">
  #cloud_processtype{
  }
</style>
