<template>
    <div class="">
      <div class="contentbigbox">
        <template>
          <el-table
            :data="indextableData"
            border
            header-cell-class-name="tabletitles">
            <el-table-column
              prop="annoNumber"
              label="公告（示）名称"
              align="center">
            </el-table-column>
            <el-table-column
              prop="transferorName"
              label="出让人"
              align="center">
            </el-table-column>
            <el-table-column
              prop="ggStatus"
              label="公告（示）状态"
              width="190"
              align="center">
              <template slot-scope="scope">
                <div class="statusbigbox" v-if="scope.row.ggStatus === 0">
                  <span class="statusbox">未提交</span>
                  <el-button type="text" size="small" @click="ggSubmit(scope)">
                    提交
                  </el-button>
                </div>
                <div class="statusbigbox" v-if="scope.row.ggStatus === 1">
                  <span class="statusbox">备案审批中</span>
                  <el-button type="text" size="small" @click="ggLookBtn(scope)">
                    查看
                  </el-button>
                </div>
                <div class="statusbigbox" v-if="scope.row.ggStatus === 2">
                  <span class="statusbox">审批通过</span>
                  <el-button type="text" size="small" @click="ggReleaseBtn(scope)">
                    发布
                  </el-button>
                </div>
                <div class="statusbigbox" v-if="scope.row.ggStatus === 3">
                  <span class="statusbox">审批不通过</span>
                  <el-button type="text" size="small" @click="ggSubmit(scope)">
                    修改
                  </el-button>
                </div>
                <div class="statusbigbox" v-if="scope.row.ggStatus === 4">
                  <span class="statusbox">已发布</span>
                  <el-button type="text" size="small" @click="ggLookBtn(scope)">
                    查看
                  </el-button>
                </div>
              </template>
            </el-table-column>
            <el-table-column
              prop="sectionName"
              label="项目"
              align="center">
            </el-table-column>
            <el-table-column
              prop="applyStatus"
              label="报名"
              align="center"
              width="120">
              <template slot-scope="scope">
                <div class="statusbigbox" v-if="scope.row.applyStatus === '未开始'">
                  <span class="statusbox">未开始</span>
                </div>
                <div class="statusbigbox" v-if="scope.row.applyStatus === '已开始'">
                  <span class="statusbox">已开始</span>
                  <el-button type="text" size="small" @click="bmAcceptanceBtn(scope)">
                    受理
                  </el-button>
                </div>
                <div class="statusbigbox" v-if="scope.row.applyStatus === '已结束'">
                  <span class="statusbox">已结束</span>
                  <el-button type="text" size="small" @click="bmDetailBtn(scope)">
                    查看
                  </el-button>
                </div>
              </template>
            </el-table-column>
            <el-table-column
              prop="crwjStatus"
              label="出让文件"
              align="center"
              width="120">
              <template slot-scope="scope">
                <div class="statusbigbox" v-if="scope.row.crwjStatus === null">
                  <span class="statusbox">-----------</span>
                </div>
                <div class="statusbigbox" v-if="scope.row.crwjStatus === 0">
                  <span class="statusbox">未提交</span>
                </div>
                <div class="statusbigbox" v-if="scope.row.crwjStatus === 1">
                  <span class="statusbox">已提交</span>
                  <el-button type="text" size="small" @click="crwjReleaseBtn(scope)">
                    发布
                  </el-button>
                </div>
                <div class="statusbigbox" v-if="scope.row.crwjStatus === 2">
                  <span class="statusbox">已发布</span>
                  <el-button type="text" size="small" @click="crwjLookBtn(scope)">
                    查看
                  </el-button>
                </div>
              </template>
            </el-table-column>
            <el-table-column
              prop="cqwjStatus"
              label="澄清"
              align="center"
              width="120">
              <template slot-scope="scope">
                <div class="statusbigbox" v-if="scope.row.cqwjStatus === 0">
                  <span class="statusbox">未提交</span>
                </div>
                <div class="statusbigbox" v-if="scope.row.cqwjStatus === 1">
                  <span class="statusbox">已提交</span>
                  <el-button type="text" size="small" @click="cqwjReleaseBtn(scope)">
                    发布
                  </el-button>
                </div>
                <div class="statusbigbox" v-if="scope.row.cqwjStatus === 2">
                  <span class="statusbox">已发布</span>
                  <el-button type="text" size="small" @click="cqwjLookBtn(scope)">
                    查看
                  </el-button>
                </div>
                <div class="statusbigbox" v-if="scope.row.cqwjStatus === null">
                  <span class="statusbox">-----------</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column
              prop="priceStatus"
              label="竞价"
              align="center"
              width="120">
              <template slot-scope="scope">
                <div class="statusbigbox" v-if="scope.row.priceStatus === 0">
                  <span class="statusbox">未开始</span>
                </div>
                <div class="statusbigbox" v-if="scope.row.priceStatus === 1 && scope.row.gglandUse === 1">
                  <span class="statusbox">拍卖竞价室</span>
                  <el-button type="text" size="small" @click="biddingBtn(scope)">
                    竞价室
                  </el-button>
                </div>
                <div class="statusbigbox" v-if="scope.row.priceStatus === 1 && scope.row.gglandUse === 2">
                  <span class="statusbox">挂牌竞价室</span>
                  <el-button type="text" size="small" @click="gpBiddingBtn(scope)">
                    竞价室
                  </el-button>
                </div>
                <div class="statusbigbox" v-if="scope.row.priceStatus === 2 && scope.row.gglandUse === 1">
                  <span class="statusbox">拍卖结束</span>
                  <el-button type="text" size="small" @click="pmDetailBtn(scope)">
                    竞价记录
                  </el-button>
                </div>
                <div class="statusbigbox" v-if="scope.row.priceStatus === 2 && scope.row.gglandUse === 2">
                  <span class="statusbox">挂牌结束</span>
                  <el-button type="text" size="small" @click="gpDetailBtn(scope)">
                    竞价记录
                  </el-button>
                </div>
                <div class="statusbigbox" v-if="scope.row.priceStatus === 3 && scope.row.gglandUse === 2">
                  <span class="statusbox">限时竞价开始</span>
                  <el-button type="text" size="small" @click="gpBiddingBtn(scope)">
                    竞价室
                  </el-button>
                </div>
                <div class="statusbigbox" v-if="scope.row.priceStatus === 4 && scope.row.gglandUse === 2">
                  <span class="statusbox">限时竞价中</span>
                  <el-button type="text" size="small" @click="gpBiddingBtn(scope)">
                    竞价室
                  </el-button>
                </div>
                <div class="statusbigbox" v-if="scope.row.priceStatus === 5 && scope.row.gglandUse === 2">
                  <span class="statusbox">限时竞价结束</span>
                  <el-button type="text" size="small" @click="gpDetailBtn(scope)">
                    竞价记录
                  </el-button>
                </div>
              </template>
            </el-table-column>
            <el-table-column
              prop="isSend"
              label="成交确认"
              align="center"
              width="120">
              <template slot-scope="scope">
                <div class="statusbigbox" v-if="(scope.row.isCommit === null && scope.row.isSend === null && scope.row.isAffirm === null) || scope.row.isAffirmPerson === 0">
                  <span class="statusbox">-----------</span>
                </div>
                <div class="statusbigbox" v-if="scope.row.isCommit === 0 && scope.row.isSend === 0 && scope.row.isAffirm === 0 && scope.row.isAffirmPerson !== 0">
                  <span class="statusbox">未开始</span>
                </div>
                <div class="statusbigbox" v-if="scope.row.isCommit === 1 && scope.row.isSend === 0">
                  <span class="statusbox">未发送</span>
                  <el-button type="text" size="small" @click="sendOutBtn(scope)">
                    发送
                  </el-button>
                </div>
                <div class="statusbigbox" v-if="scope.row.isCommit === 1 && scope.row.isSend === 1 && scope.row.isAffirm === 0">
                  <span class="statusbox">已发送</span>
                </div>
                <div class="statusbigbox" v-if="scope.row.isCommit === 1 && scope.row.isSend === 1 && scope.row.isAffirm === 1">
                  <span class="statusbox">已结束</span>
                  <el-button type="text" size="small" @click="detailBtn(scope)">
                    查看
                  </el-button>
                </div>
              </template>
            </el-table-column>
            <el-table-column
              prop="cjjgStatus"
              label="结果公示"
              align="center"
              width="120">
              <template slot-scope="scope">
                <div class="statusbigbox" v-if="scope.row.cjjgStatus === null">
                  <span class="statusbox">-----------</span>
                </div>
                <div class="statusbigbox" v-if="scope.row.cjjgStatus === 0">
                  <span class="statusbox">未提交</span>
                  <el-button type="text" size="small" @click="cjjgSubmit(scope)">
                    提交
                  </el-button>
                </div>
                <div class="statusbigbox" v-if="scope.row.cjjgStatus === 1">
                  <span class="statusbox">备案审批中</span>
                  <el-button type="text" size="small" @click="cjjgLookBtn(scope)">
                    查看
                  </el-button>
                </div>
                <div class="statusbigbox" v-if="scope.row.cjjgStatus === 2">
                  <span class="statusbox">审批通过</span>
                  <el-button type="text" size="small" @click="cjjgReleaseBtn(scope)">
                    发布
                  </el-button>
                </div>
                <div class="statusbigbox" v-if="scope.row.cjjgStatus === 3">
                  <span class="statusbox">审批不通过</span>
                  <el-button type="text" size="small" @click="cjjgSubmit(scope)">
                    修改
                  </el-button>
                </div>
                <div class="statusbigbox" v-if="scope.row.cjjgStatus === 4">
                  <span class="statusbox">已发布</span>
                  <el-button type="text" size="small" @click="cjjgLookBtn(scope)">
                    查看
                  </el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination
            class="pagebox"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :current-page.sync="currentPage"
            :total="total"
            :page-size='pageSize'
            layout="prev, pager, next, jumper">
          </el-pagination>
        </template>
      </div>
    </div>
</template>
<script>
import { home } from '@/api'
export default {
  data () {
    return {
      indextableData: [],
      // 当前页
      currentPage: 1,
      total: 0, // 总条数
      pageSize: 10, // 每页展示条数
      pageNo: 0,
      searchIs: false
    }
  },
  methods: {
    initDates () {
      let url = null
      if (this.searchIs) {
        url = {
          pageNo: this.pageNo,
          pageSize: this.pageSize,
          // 企业id
          enterpriseId: this.$store.getters.authUser.enterpriseId
        }
      } else {
        url = {
          pageNo: this.pageNo,
          pageSize: this.pageSize,
          enterpriseId: this.$store.getters.authUser.enterpriseId
        }
      }
      home.homeList(url).then((res) => {
        this.indextableData = res.data.sectionHomeInfoPageInfo.list
        this.total = res.data.sectionHomeInfoPageInfo.total
        this.searchIs = true
      })
    },
    // 公告查看
    ggLookBtn (scope) {
      this.$router.push({path: `/mr/transferor/submission/subminssion-notice/detail/${scope.row.annoObjectId}`, query: {ObjectId: scope.row.annoObjectId, landUse: scope.row.gglandUse, roledsType: 1}})
    },
    // 公告提交
    ggSubmit (scope) {
      this.$router.push({path: '/mr/transferor/submission/subminssion-notice/update', query: {ObjectId: scope.row.annoObjectId, landUse: scope.row.gglandUse, roledsType: 1}})
    },
    // 公告发布
    ggReleaseBtn (scope) {
      this.$router.push({path: '/mr/transferor/submission/subminssion-notice/release', query: {ObjectId: scope.row.annoObjectId, landUse: scope.row.gglandUse, roledsType: 1}})
    },
    // 报名受理
    bmAcceptanceBtn (scope) {
      this.$router.push({path: '/mr/transferor/online-registration/enrolment/situation', query: {ObjectId: scope.row.sectionObjectId, roledsType: 1}})
    },
    // 报名查看
    bmDetailBtn (scope) {
      this.$router.push({path: `/mr/transferor/online-registration/look-registration/detail/${scope.row.sectionObjectId}`, query: {ObjectId: scope.row.sectionObjectId, roledsType: 1}})
    },
    // 出让文件查看
    crwjLookBtn (scope) {
      this.$router.push({path: `/mr/transferor/transfer-documents/sub-transfer-documents/detail/${scope.row.crwjObjectId}`, query: {roledsType: 1}})
    },
    // 出让文件发布
    crwjReleaseBtn (scope) {
      this.$router.push({path: '/mr/transferor/transfer-documents/sub-transfer-documents/release', query: {objectId: scope.row.crwjObjectId, roledsType: 1}})
    },
    // 澄清文件发布
    cqwjReleaseBtn (scope) {
      this.$router.push({path: '/mr/transferor/transfer-documents/sub-clarify-documents/release', query: {objectId: scope.row.cqwjObjectId, roledsType: 1}})
    },
    // 澄清文件查看
    cqwjLookBtn (scope) {
      this.$router.push({path: `/mr/transferor/transfer-documents/sub-clarify-documents/detail/${scope.row.cqwjObjectId}`, query: {roledsType: 1}})
    },
    // 发送成交确认书
    sendOutBtn (scope) {
      let times = new Date().getFullYear() + '-' + (new Date().getMonth() + 1) + '-' + new Date().getDate() + ' ' + new Date().getHours() + ':' + new Date().getMinutes() + ':' + new Date().getSeconds()
      this.$router.push({path: `/mr/transferor/business-confirmation/issue-sales-confirmation/send-out/${scope.row.cjqrObjectId}`, query: {times: times, roledsType: 1}})
    },
    // 查看成交确认书
    detailBtn (scope) {
      this.$router.push({path: `/mr/transferor/business-confirmation/issue-sales-confirmation/detail/${scope.row.cjqrObjectId}`, query: {roledsType: 1}})
    },
    // 提交成交结果公示
    cjjgSubmit (scope) {
      this.$router.push({path: '/mr/transferor/business-confirmation/sub-results-transaction/update', query: {objectId: scope.row.cjjgObjectId, annoId: scope.row.annoObjectId, roledsType: 1}})
    },
    // 查看成交结果公示
    cjjgLookBtn (scope) {
      this.$router.push({path: `/mr/transferor/business-confirmation/sub-results-transaction/detail/${scope.row.cjjgObjectId}`, query: {roledsType: 1}})
    },
    // 发布成交结果公示
    cjjgReleaseBtn (scope) {
      this.$router.push({path: '/mr/transferor/business-confirmation/sub-results-transaction/release', query: {objectId: scope.row.cjjgObjectId, roledsType: 1}})
    },
    // 进入拍卖竞价室
    biddingBtn (scope) {
      this.$router.push({path: '/mr/transferor/auction/check-auction-bids/bidding-room', query: {startTime: scope.row.saleStartTime, objectId: scope.row.sectionObjectId, roomId: scope.row.roomId, code: scope.row.xmCode, roledsType: 1}})
    },
    // 查看拍卖竞价记录
    pmDetailBtn (scope) {
      this.$router.push({path: '/mr/transferor/auction/check-auction-bids/detail', query: {sectionName: scope.row.sectionName, sectionNumber: scope.row.sectionNumber, annoNumber: scope.row.annoNumber, roomId: scope.row.roomId, roledsType: 1}})
    },
    // 进入挂牌竞价室
    gpBiddingBtn (scope) {
      this.$router.push({path: '/mr/transferor/quotation/check-listed-bids/bidding-room', query: {objectId: scope.row.sectionObjectId, roledsType: 1}})
    },
    // 查看挂牌竞价记录
    gpDetailBtn (scope) {
      this.$router.push({path: '/mr/transferor/quotation/check-listed-bids/detail', query: {sectionName: scope.row.sectionName, sectionNumber: scope.row.sectionNumber, annoNumber: scope.row.annoNumber, roomId: scope.row.roomId, roledsType: 1}})
    },
    handleSizeChange (nowNum) {
      this.pageNo = (nowNum - 1) * this.pageSize
      this.initDates(this.searchIs)
    },
    handleCurrentChange (nowNum) {
      this.pageNo = (nowNum - 1) * this.pageSize
      this.initDates(this.searchIs)
    }
  },
  mounted () {
    let that = this
    if (that.$store.getters.authUser.userType !== 1) {
      if (that.$store.getters.authUser.subjectIds !== null && that.$store.getters.authUser.subjectIds !== undefined) {
        that.$store.getters.authUser.subjectIds.map(item => {
          if (item === 10009) {
            that.initDates()
          }
        })
      }
    }
    if (Number(this.$route.query.roledsType) === 1) {
      that.initDates()
      return false
    }
  }
}
</script>
