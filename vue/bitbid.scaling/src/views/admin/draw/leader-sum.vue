<template>
  <div class="leader-sum" id="leader-sum">
    <div class="proinfo-info-box">
      <div class="title-box">
        <p>投票汇总</p>
      </div>
      <el-table
        :data="voteSumTableData"
        border
        :show-header="false"
        style="width: 50%">
        <el-table-column
          type="index"
          label="序号"
          align="center"
          width="55">
        </el-table-column>
        <el-table-column
          prop="userName"
          label="名称">
        </el-table-column>
      </el-table>
      <div class="tips_data" v-if="isShowTips">
        注：{{tips}}
      </div>
      <div class="confirm-btn" v-if="isShowed">
        <el-button
          type="primary"
          @click="confirmClick"
          size="small"
          :disabled="isDisabled"
          v-if="currentUser.roleIds[0] === 1 && voteSumTableData.length > 0"
          :loading="isLoading">
          确 认
        </el-button>
        <el-button
          type="primary"
          @click="handleAddVote"
          size="small"
          :disabled="isDisabled"
          v-if="currentUser.roleIds[0] === 1 && isShowAddVote"
          :loading="isAddVoteLoading">
          加轮
        </el-button>
      </div>
    </div>
    <div class="bid-report-box">
      <div class="title-box">
        <p>定标人投票情况</p>
      </div>
      <el-row :gutter="20">
        <div class="group-box" v-for="(item, index) in bidVoteTableData" :key="index">
          <el-col :span="2" class="bid-title">{{index}}:</el-col>
          <el-col :span="10" style="margin-bottom: 20px;">
            <el-table
              :data="item"
              border
              :show-header="false"
              style="width: 100%">
              <el-table-column
                type="index"
                label="序号"
                align="center"
                width="55">
              </el-table-column>
              <el-table-column
                prop="resultName"
                label="名称">
              </el-table-column>
            </el-table>
          </el-col>
        </div>
        <div class="no-data" v-if="bidVoteTableData && Object.keys(bidVoteTableData).length===0">
          <p>暂无投票结果</p>
        </div>
      </el-row>
    </div>
    <div class="foot-btn">
      <!--底部-->
      <footers></footers>
      <!--底部-->
    </div>
  </div>
</template>
<script>
import footers from '@/views/admin/footer/footer.vue'
import {scalingEnd} from '@/api'
import {WebPushSocket} from '../../../assets/js/websocket-inst.js'
// 状态
import {mapState} from 'vuex'
export default {
  name: 'leader-sum',
  components: {
    footers
  },
  data () {
    return {
      currentUser: this.$store.getters.authUser,
      isDisabled: false,
      projectId: this.$store.getters.projectId,
      methodType: this.$route.query.methodType,
      num: this.$route.query.num || 1,
      voteSumTableData: [], // 投票汇总
      bidVoteTableData: {}, // 定标人投票情况
      webPushSocket: null,
      isLoading: false,
      isShowed: false, // 默认不显示确认按钮
      isShowAddVote: false, // 是否显示加轮按钮（投票相同时）
      isAddVoteLoading: false,
      isShowTips: false, // 是否显示提示
      tips: ''
    }
  },
  computed: {
    // 获取状态
    ...mapState({
      removeMessages: (state) => {
        return state.messages
      }
    })
  },
  methods: {
    // 定标人投票情况
    getVoteResult () {
      let params = {
        methodType: this.methodType,
        num: this.num
      }
      scalingEnd.getScallingVoteResult(this.projectId, params).then(res => {
        this.bidVoteTableData = res.data.GroupMap
      })
    },
    // 投票汇总
    getVoteReport () {
      let params = {
        methodType: this.methodType,
        num: this.num
      }
      scalingEnd.getScallingVoteReport(this.projectId, params).then((res) => {
        this.voteSumTableData = []
        let tempResult = res.data.userList
        if (tempResult && tempResult.length) {
          this.voteSumTableData = tempResult
        }
        this.isShowTips = !!res.data.tips
        this.tips = this.isShowTips ? res.data.tips.toString() : ''
        if (this.isShowTips && res.data.isEnd) {
          this.isShowAddVote = true
        } else {
          this.isShowAddVote = false
        }
      })
    },
    // 点击加轮
    handleAddVote () {
      this.isAddVoteLoading = true
      let obj = {
        methodType: this.methodType,
        num: this.num
      }
      scalingEnd.addVote(this.projectId, obj).then((res) => {
        this.isAddVoteLoading = false
        if (res.data.resCode === '0000') {
          this.isShowAddVote = false
          this.$store.commit('SET_IS_REFRESH_MENU', true)
          // 加轮成功后，执行确认
          this.isDisabled = true
          this.webPushSocket.sendMsg('确认提交')
          this.getVoteRoomPage()
          let obj1 = {
            methodType: this.methodType,
            num: this.num,
            noActivity: 1
          }
          scalingEnd.confirmResult(this.projectId, obj1).then(res => {
          })
        }
      })
    },
    // 确认-投票结果
    confirmClick () {
      this.isLoading = true
      let obj = {
        methodType: this.methodType,
        num: this.num
      }
      scalingEnd.confirmResult(this.projectId, obj).then(res => {
        this.isLoading = false
        if (res.data.resCode === '0000') {
          this.getVoteRoomPage()
          this.webPushSocket.sendMsg('确认提交')
        }
      })
    },
    // 流程控制
    getVoteRoomPage () {
      scalingEnd.getVoteRoomPage(this.projectId, {methodType: this.methodType, num: this.num}).then(res => {
        // 1已经执行-不可操作
        // 0未执行-可操作
        this.isDisabled = res.data.isHide === '1'
        this.isShowed = true
      })
    },
    initWebsocket () {
      this.webPushSocket = WebPushSocket.getInstance(this.$store.getters.projectId, this.$store.getters.authUser.userId, this.$store.getters.authUser.userName, 'All', this.methodType, this.num)
      this.webPushSocket.init()
    },
    init () {
      this.getVoteRoomPage()
      this.getVoteReport()
      this.getVoteResult()
      this.initWebsocket()
    }
  },
  watch: {
    $route: function () {
      this.num = this.$route.query.num || 1
      this.methodType = this.$route.query.methodType
      this.init()
    },
    removeMessages: function (newV, oldV) {
      // console.log(newV)
      if (newV.message === '撤销提交' || newV.message === '提交数据') {
        this.getVoteReport()
        this.getVoteResult()
      }
    }
  },
  mounted () {
    this.init()
  },
  destroyed () {
    if (this.webPushSocket) {
      this.webPushSocket.close()
      this.webPushSocket = null
    }
  }
}
</script>
<style lang="scss">
#leader-sum {
  .proinfo-info-box,
  .bid-report-box {
    padding: 10px;
    margin-top: 15px;
    background: #fff;
    .pro-form {
      .el-form-item {
        margin-bottom: 0;
      }
      .el-form-item__content {
        text-align: left;
      }
    }
    .confirm-btn {
      margin: 15px;
    }
  }
  .bid-report-box {
    .bid-title {
      color: #2E76EC;
    }
  }
  .tips_data {
    margin-top: 20px;
    color: #e8ab4e;
    background-color: #fdf6ec;
    size: 12px;
    width: 50%;
    text-align: left;
    padding:  6px 15px;
    box-sizing: border-box;
  }
}
</style>
