<template>
  <div id="footer">
    <div class="footer_sonbox">
      <el-button type="success" round class="look_btn" @click="lookTenderingFile">查看采购文件</el-button>
      <!--查看招标文件弹框-->
      <el-dialog title="查看采购文件" :visible.sync="tenderingFileVisible" width="30%">
        <!--<template v-if="!isEnquirySys">-->
          <!--<div class="selectbigbox">-->
            <!--<div class="selecttitle"><span class="red">*</span>选择谈判轮数：</div>-->
            <!--<el-select v-model="selectNegoId" placeholder="请选择" @change="handleSelectBid(selectNegoId)">-->
              <!--<el-option-->
                <!--v-for="item in selectNegotiation"-->
                <!--:key="item.objectId"-->
                <!--:label="item.label"-->
                <!--:value="item.objectId">-->
              <!--</el-option>-->
            <!--</el-select>-->
          <!--</div>-->
        <!--</template>-->
        <div class="selectbigbox">
          <div class="selecttitle">采购文件：</div>
          <div class="look_download_btn">
            <el-button type="text" size="small" class="look_download" @click="biddingFileBtn" :disabled="!isVisible">查看</el-button>
            <!--<el-button type="text" size="small" class="look_download" @click="downBidFileBtn" :disabled="!isVisible">下载</el-button>-->
          </div>
        </div>
      </el-dialog>
      <!--查看招标文件弹框-->
      <el-button type="success" round class="look_btn" @click="lookBrowseFile" v-if="isShowBtn">查看响应文件</el-button>
      <!--浏览投标文件弹框-->
      <el-dialog title="查看响应文件" :visible.sync="browseFileVisible" width="30%">
        <!--<template v-if="!isEnquirySys">-->
          <!--<div class="selectbigbox">-->
            <!--<div class="selecttitle"><span class="red">*</span>选择谈判轮数：</div>-->
            <!--<el-select v-model="selectNegoId" placeholder="请选择" @change="handleSelectTender(selectNegoId)">-->
              <!--<el-option-->
                <!--v-for="item in selectNegotiation"-->
                <!--:key="item.objectId"-->
                <!--:label="item.label"-->
                <!--:value="item.objectId">-->
              <!--</el-option>-->
            <!--</el-select>-->
          <!--</div>-->
        <!--</template>-->
        <div class="selectbigbox">
          <el-table
            :data="tableDataBrowse"
            border
            style="width: 100%">
            <el-table-column
              type="index"
              label="序号"
              width="80"
              align="center"
              :index='dataIndex'>
            </el-table-column>
            <el-table-column
              prop="label"
              label="公司名称"
              show-overflow-tooltip>
            </el-table-column>
            <el-table-column
              label="查看"
              width="80"
              align="center">
              <template slot-scope="scope">
                <el-button type="text" size="small" class="look_download" @click="tenderFileBtn(scope)">查看</el-button>
              </template>
            </el-table-column>
            <!--<el-table-column-->
              <!--label="下载"-->
              <!--width="120">-->
              <!--<template slot-scope="scope">-->
                <!--<el-button type="text" size="small" class="look_download" @click="downTenderFileBtn(scope)">下载</el-button>-->
              <!--</template>-->
            <!--</el-table-column>-->
          </el-table>
        </div>
      </el-dialog>
      <!--浏览投标文件弹框-->
      <!--浏览工程量清单弹框-->
      <el-button type="success" round class="look_btn" @click="detailedBrowseFile" v-if="isShowEng && isShowBtn">下载工程量清单</el-button>
      <el-dialog title="下载工程量清单" :visible.sync="detailedFileVisible" width="30%">
        <!--<div class="selectbigbox" v-if="!isEnquirySys">-->
          <!--<div class="selecttitle"><span class="red">*</span>选择谈判轮数：</div>-->
          <!--<el-select v-model="detailedId" placeholder="请选择" @change="engineeringSelect(detailedId)">-->
            <!--<el-option-->
              <!--v-for="item in selectNegotiation"-->
              <!--:key="item.objectId"-->
              <!--:label="item.label"-->
              <!--:value="item.objectId">-->
            <!--</el-option>-->
          <!--</el-select>-->
        <!--</div>-->
        <div class="selectbigbox">
          <el-table
            :data="tableDataDetailed"
            border
            style="width: 100%">
            <el-table-column
              type="index"
              label="序号"
              width="80"
              align="center"
              :index='dataIndex'>
            </el-table-column>
            <el-table-column
              prop="label"
              label="公司名称"
              show-overflow-tooltip>
            </el-table-column>
            <el-table-column
              label="下载"
              width="80"
              align="center">
              <template slot-scope="scope">
                <el-button type="text" size="small" class="look_download" @click="downTenderFileBtn(scope)">下载</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-dialog>
      <!--浏览工程量清单弹框-->
    </div>
    <div class="footer_sonbox">
      <div class="bottom_rightbox">
        <span>下一阶段：</span>
        <span>{{nextStep}}</span>
      </div>
      <div class="bottom_rightbox">
        <span>上一阶段：</span>
        <span>{{lastStep}}</span>
      </div>
    </div>
  </div>
</template>
<script>
import { reviewControl } from '@/api'
import {downloadFile, pdfUrl} from '@/assets/js/common'
import { handlePdfDisplay } from '@/utils/common'
export default {
  data () {
    return {
      tenderingFileVisible: false, // 查看招标文件
      browseFileVisible: false, // 浏览投标文件
      detailedFileVisible: false, // 浏览工程量清单
      selectNegotiation: [], // 查看招标文件
      tableDataBrowse: [], // 浏览投标文件
      tableDataDetailed: [], // 浏览工程量清单
      // 自定义序号
      dataIndex: 1,
      selectNegoId: '',
      bidFileId: '',
      detailedId: '',
      bidFileName: '',
      routeList: [],
      lastStep: '',
      nextStep: '',
      winHeight: 0,
      winWidth: 0,
      isEnquirySys: false,
      isVisible: true,
      isShowEng: false,
      isShowBtn: true
    }
  },
  methods: {
    // 查看招标文件
    lookTenderingFile () {
      this.tenderingFileVisible = true
    },
    // 招标文件
    biddingFileBtn () {
      let url = pdfUrl + '?doc=' + this.bidFileId +
        '&height=' + this.winHeight + '&width=' + this.winWidth
      window.open(url)
    },
    // 浏览投标文件
    lookBrowseFile () {
      this.browseFileVisible = true
    },
    tenderFileBtn (scope) {
      let url = pdfUrl + '?doc=' + scope.row.objectId +
      '&height=' + this.winHeight + '&width=' + this.winWidth
      window.open(url)
    },
    // 浏览工程量清单
    detailedBrowseFile () {
      this.detailedFileVisible = true
    },
    downTenderFileBtn (scope) {
      window.open(scope.row.objectId)
    },
    downBidFileBtn () {
      let index1 = this.bidFileId.lastIndexOf('.')
      let index2 = this.bidFileId.length
      let suffixName = this.bidFileId.substring(index1, index2)
      downloadFile(this.bidFileName + suffixName, this.bidFileId)
    },
    roundsNumberList () {
      if (this.isEnquirySys) {
        this.handleSelectBid(1)
        this.handleSelectTender(1)
        this.engineeringSelect(1)
        return
      }
      let url = ''
      if (this.isShowEng) {
        url = {
          selectType: 'gclqd'
        }
      }
      reviewControl.getBidNumList(url).then((res) => {
        this.selectNegotiation = res.data.ItemList
        if (this.selectNegotiation) {
          this.selectNegoId = this.selectNegotiation[0].objectId
          this.detailedId = this.selectNegotiation[0].objectId
          this.handleSelectBid(this.selectNegoId)
          this.engineeringSelect(this.detailedId)
        }
      })
    },
    handleSelectBid (id) {
      reviewControl.getBidFile(id).then((res) => {
        this.bidFileName = res.data.BuyerFile.label
        this.bidFileId = res.data.BuyerFile.objectId
        if (this.bidFileName || this.bidFileId) {
          this.handleSelectTender(id)
          this.engineeringSelect(id)
        }
      })
    },
    handleSelectTender (id) {
      reviewControl.getTenderFile(id).then((res) => {
        this.tableDataBrowse = res.data.TenderFileList
        this.isVisible = this.tableDataBrowse.length > 0
      })
    },
    handleStep () {
      this.isEnquirySys = this.$route.meta.layout === 'EnquirySystem'
      this.routeList = this.$store.getters.routeList
      let index = this.$store.getters.curIndex
      let length = this.routeList.length - 1
      this.lastStep = index ? this.routeList[index - 1] : '暂无' + '  '
      this.nextStep = index < length ? this.routeList[index + 1] : '暂无' + ' '
    },
    getWinWH () {
      let body = document.body
      this.winHeight = (body.clientHeight || body.offsetHeight) - 20
      this.winWidth = body.clientWidth || body.offsetWidth
    },
    // 是否显示工程量清单
    isEngineering () {
      reviewControl.isHideEngineering().then((res) => {
        if (res.data.isShow) {
          this.isShowEng = true
        } else {
          this.isShowEng = false
        }
        this.roundsNumberList()
      })
    },
    // 工程量清单数据
    engineeringSelect (id) {
      reviewControl.engineeringList(id).then((res) => {
        this.tableDataDetailed = res.data.fileList
      })
    },
    gainUserInfo () {
      this.$store.getters.authUser.roleIds.map((its) => {
        if (its === 6) {
          this.isShowBtn = false
        }
      })
    }
  },
  watch: {
    '$route': 'handleStep',
    tenderingFileVisible: function (value) {
      handlePdfDisplay(value)
    },
    browseFileVisible: function (value) {
      handlePdfDisplay(value)
    },
    detailedFileVisible: function (value) {
      handlePdfDisplay(value)
    },
    isEnquirySys: function (value) {
      if (value) {
        this.roundsNumberList()
      }
    }
  },
  mounted () {
    this.getWinWH()
    this.roundsNumberList()
    this.handleStep()
    this.isEngineering()
    this.gainUserInfo()
  }
}
</script>
