<template>
  <div class="group-discuss" id="group-discuss">
    <el-row :gutter="15" style="margin-left:0;margin-right:0;">
      <el-col :span="14" style="padding-left:0;">
        <div class="chat-room">
          <div class="title-box">
            <p>聊天室</p>
          </div>
          <div class="chat-cont" ref="chatCont" id="chatCont">
            <div :class="{'dialog-box': showClass, 'dialog-self': String(item.userId) === String(currentUser.userId)}" v-for="(item, index) in chatRecord" :key="index">
              <span class="name-span name-span-other" v-if="String(item.userId) !== String(currentUser.userId)">
                {{item.userName | filterName}}
              </span>
              <label class="dialog-cont">
                <span class="self-name-span" v-if="String(item.userId) === String(currentUser.userId)">{{item.userName}}</span>
                <span class="other-name-span" v-if="String(item.userId) !== String(currentUser.userId)">{{item.userName}}</span>
                <span class="chat-details" v-html="item.message.replace(/\#[\u4E00-\u9FA5]{1,3}\;/gi, emotion)"></span>
                <span :class="{'triangle': showClass}" v-if="String(item.userId) !== String(currentUser.userId)">
                  <i class="el-icon-arrow-left"></i>
                </span>
                <span
                  :class="{'triangle': showClass, 'triangle-right': showClass}"
                  v-else-if="String(item.userId) === String(currentUser.userId)">
                  <i class="el-icon-arrow-right"></i>
                </span>
              </label>
              <span class="name-span" v-if="String(item.userId) === String(currentUser.userId)">
                {{item.userName | filterName}}
              </span>
            </div>
          </div>
          <!-- 聊天记录 -->
          <div :class="{'char-history': true, 'chat-cont': true, 'show-chat': showChatsCont}">
            <div :class="{'dialog-box': showClass, 'dialog-self': String(item.creator) === String(currentUser.userId)}" v-for="(item, index) in chatHistoryRecord" :key="index">
              <span class="name-span name-span-other" v-if="String(item.creator) !== String(currentUser.userId)">
                {{item.creatorName | filterName}}
              </span>
              <label class="dialog-cont">
                <span class="self-name-span" v-if="String(item.userId) === String(currentUser.userId)">{{item.userName}}</span>
                <span class="other-name-span" v-if="String(item.userId) !== String(currentUser.userId)">{{item.userName}}</span>
                <span class="chat-details" v-html="recordReplace(item.message)"></span>
                <span :class="{'triangle': showClass}" v-if="String(item.creator) !== String(currentUser.userId)">
                  <i class="el-icon-arrow-left" style="background:#eee;"></i>
                </span>
                <span
                  :class="{'triangle': showClass, 'triangle-right': showClass}"
                  v-else-if="String(item.creator) === String(currentUser.userId)">
                  <i class="el-icon-arrow-right" style="background:#eee;"></i>
                </span>
              </label>
              <span class="name-span" v-if="String(item.creator) === String(currentUser.userId)">
                {{item.creatorName | filterName}}
              </span>
            </div>
            <!-- 分页 -->
            <el-pagination
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
              :current-page.sync="chartCurrentPage"
              :page-size="20"
              layout="total, prev, pager, next"
              :total="total"
              style="margin-top: 10px;">
            </el-pagination>
          </div>
          <div class="chat-handle">
            <div class="char-handle-btns">
              <div :class="{'emotion-warp': true, 'emotion-display':emotionShow}">
                <emotion @emotion="handleEmotion" :height="200" ></emotion>
              </div>
              <div style="width: 60px;float: left;" @click="hendleEmotionBtn">
                <img src="./images/biaoqing.png" alt="表情包" title="显示/隐藏表情包" class="emotion-btn">
              </div>
              <!-- <img src="./images/tupian.png" alt="上传图片">
              <img src="./images/jiandao.png" alt="截图"> -->
              <span @click="handleCharRecord(showHide)" title="显示/隐藏聊天记录"><i class="el-icon-time"></i>聊天记录</span>
            </div>
            <el-input
              type="textarea"
              :rows="4"
              placeholder="请输入内容"
              v-model="textarea"
              @keyup.enter.native="sendOutInfo"
              :disabled="(isSurveillance || disabledBtn)">
            </el-input>
            <div class="collective">
              <el-button class="btn-blue-bg" size="small" @click="sendOutInfo" :disabled="(isSurveillance || disabledBtn)">发 送</el-button>
            </div>
          </div>
        </div>
      </el-col>
      <el-col :span="10" style="padding-right:0;">
        <div class="bid-cand">
          <div class="title-box">
            <p>定标候选人</p>
          </div>
          <el-table
            :data="bidCandTableData"
            border
            height="190"
            :show-header="false"
            style="width: 100%">
            <el-table-column
              type="index"
              label="序号"
              align="center"
              :index="indexMethod"
              width="55">
            </el-table-column>
            <el-table-column
              prop="userName"
              label="名称">
            </el-table-column>
          </el-table>
        </div>
        <div class="bid-cand" style="height:289px;">
          <div class="title-box">
            <p>排序</p>
          </div>
          <el-table
            :data="sortTableData"
            border
            height="171"
            :show-header="false"
            style="width: 100%"
            v-if="!queryType">
            <el-table-column
              type="index"
              label="序号"
              align="center"
              :index="indexMethod"
              width="55">
            </el-table-column>
            <el-table-column
              prop="nameCode"
              label="名称">
              <template slot-scope="scope">
                <el-select v-model="scope.row.nameCode" placeholder="请选择" :disabled="currentUser.roleIds[0] !== 1 || (isSurveillance || disabledSelect)">
                  <el-option
                    v-for="item in voteUnitList"
                    :key="item.objectId"
                    :label="item.userName"
                    :value="item.objectId+'/'+item.userName">
                  </el-option>
                </el-select>
              </template>
            </el-table-column>
          </el-table>
          <el-table
            :data="sortDisTableData"
            border
            height="171"
            :show-header="false"
            style="width: 100%"
            v-if="queryType">
            <el-table-column
              type="index"
              label="序号"
              align="center"
              :index="indexMethod"
              width="55">
            </el-table-column>
            <el-table-column
              prop="resultName"
              label="名称">
              <template slot-scope="scope">
                <el-select v-model="scope.row.resultName" placeholder="请选择" :disabled="currentUser.roleIds[0] !== 1 || (isSurveillance || disabledSelect)">
                  <el-option
                    v-for="item in voteUnitList"
                    :key="item.objectId"
                    :label="item.userName"
                    :value="item.objectId">
                  </el-option>
                </el-select>
              </template>
            </el-table-column>
          </el-table>
          <div class="submit-btns" v-if="!isSurveillance">
            <el-button class="btn-blue-bg" size="small" @click="submitSort" :disabled="disabledBtn" v-if="currentUser.roleIds[0]===1">
              {{(changeBtnType)? '提 交': '撤 回'}}
            </el-button>
            <el-button type="primary" size="small" @click="confirmSubmit" :disabled="disabledBtn" v-if="currentUser.roleIds[0] === 1 && !changeBtnType">确认提交</el-button>
          </div>
        </div>
      </el-col>
    </el-row>
    <div class="foot-btn">
      <!--底部-->
      <footers></footers>
      <!--底部-->
    </div>
  </div>
</template>
<script>
// 状态
import {mapState} from 'vuex'
import footers from '@/views/admin/footer/footer.vue'
import {WebPushSocket} from '../../../assets/js/websocket-inst.js'
import {projectInfo, scalingEnd} from '@/api'
// 表情包
import Emotion from '@/components/emotion/index'
// import { setTimeout } from 'timers';
export default {
  name: 'chat-room',
  components: {
    footers,
    Emotion
  },
  data () {
    return {
      currentUser: this.$store.getters.authUser,
      disabledBtn: false,
      num: this.$route.query.num || 1,
      total: 0,
      chartCurrentPage: 1,
      methodType: this.$route.query.methodType,
      showClass: true,
      textarea: '',
      currentPage: 1,
      bidCandTableData: [], // 定标候选人
      sortTableData: [], // 无排序结果
      sortDisTableData: [], // 有排序结果
      disabledSelect: false, // 禁用下拉选择
      queryType: false, // 判断执行那个查询
      voteUnitList: [],
      webPushSocket: null,
      projectId: this.$store.getters.projectId, // 项目id
      userId: this.$store.getters.authUser.userId,
      // 是否监标人
      isSurveillance: this.$store.getters.authUser.roleIds[0] === 4,
      chatRecord: [], // 聊天内容
      changeBtnType: true,
      showHide: 1,
      showChatsCont: false,
      emotionShow: false,
      emotionBtn: 1,
      chatHistoryRecord: [] // 聊天历史记录
    }
  },
  computed: {
    // 获取状态
    ...mapState({
      chatMessages: (state) => {
        return state.messages
      }
    })
  },
  watch: {
    chatMessages: function (newV, oldV) {
      if (newV) {
        if (newV.message !== '排序完成' && newV.message !== '撤销排序' && String(newV.projectId) === String(this.$store.getters.projectId) && newV.messageType === 4) {
          this.chatRecord.push(newV)
          this.$nextTick(function () {
            this.scroolButtom()
          })
        }
        if (newV.message === '排序完成') {
          // console.log('排序完成')
          window.setTimeout(this.getOrderResult, 100)
          // this.getOrderResult()
          // this.getBidCandidate()
        }
        if (newV.message === '撤销排序') {
          this.recallOerderResult()
        }
        if (newV.message === '下达开始命令') {
          this.getChatRoomPage()
        }
      }
    },
    queryType: function (newV, oldV) {
      if (newV) {
        this.getBidCandidate()
      }
    }
  },
  filters: {
    filterName (name) {
      let result = name || ''
      result = result.substring(0, 1)
      return result
    }
  },
  methods: {
    // 滚动到底部
    scroolButtom () {
      // 获得div
      let $div = document.getElementById('chatCont')
      $div.scrollTop = $div.scrollHeight
    },
    indexMethod (index) {
      return index + (this.currentPage - 1) * 10 + 1
    },
    initWebsocket () {
      let userName = this.$store.getters.authUser.userName
      this.webPushSocket = WebPushSocket.getInstance(this.projectId, this.userId, userName, 'All', this.methodType, this.num)
      this.webPushSocket.init()
    },
    handleEmotion (icon) {
      this.textarea = icon
    },
    // 分页
    handleSizeChange (val) {
    },
    handleCurrentChange (val) {
      this.getChatRecord(val, 20)
    },
    recordReplace (val) {
      let str = val + ''
      return str.replace(/#[\u4E00-\u9FA5]{1,3};/gi, this.emotion)
    },
    // 将匹配结果替换表情图片
    emotion (res) {
      if (typeof res !== 'string') return
      let exp = /#|;/gi
      let word = res.replace(exp, '')
      const list = ['微笑', '撇嘴', '色', '发呆', '得意', '流泪', '害羞', '闭嘴', '睡', '大哭', '尴尬', '发怒', '调皮', '呲牙', '惊讶', '难过', '酷', '冷汗', '抓狂', '吐', '偷笑', '可爱', '白眼', '傲慢', '饥饿', '困', '惊恐', '流汗', '憨笑', '大兵', '奋斗', '咒骂', '疑问', '嘘', '晕', '折磨', '衰', '骷髅', '敲打', '再见', '擦汗', '抠鼻', '鼓掌', '糗大了', '坏笑', '左哼哼', '右哼哼', '哈欠', '鄙视', '委屈', '快哭了', '阴险', '亲亲', '吓', '可怜', '菜刀', '西瓜', '啤酒', '篮球', '乒乓', '咖啡', '饭', '猪头', '玫瑰', '凋谢', '示爱', '爱心', '心碎', '蛋糕', '闪电', '炸弹', '刀', '足球', '瓢虫', '便便', '月亮', '太阳', '礼物', '拥抱', '强', '弱', '握手', '胜利', '抱拳', '勾引', '拳头', '差劲', '爱你', 'NO', 'OK', '爱情', '飞吻', '跳跳', '发抖', '怄火', '转圈', '磕头', '回头', '跳绳', '挥手', '激动', '街舞', '献吻', '左太极', '右太极']
      let index = list.indexOf(word)
      return `<img src="https://res.wx.qq.com/mpres/htmledition/images/icon/emotion/${index}.gif" align="middle">`
    },
    // 点击表情按钮
    hendleEmotionBtn () {
      if (this.emotionBtn === 1) {
        this.emotionBtn++
        this.emotionShow = true
      } else {
        this.emotionBtn--
        this.emotionShow = false
      }
    },
    // 发送消息
    sendOutInfo () {
      let megs = this.textarea
      if (megs && megs.trim() !== '') {
        this.webPushSocket.sendMsg(megs)
        this.showChatCont()
      } else {
        this.$message.warning('聊天内容不能为空')
      }
      this.textarea = ''
    },
    // 展示消息内容
    showChatCont () {
      this.webPushSocket.websocket.onmessage = this.webPushSocket.websocket.onmessage || function (event) {
      }
    },
    // 提交-排序结果
    submitSort () {
      if (this.changeBtnType) {
        // 推送消息
        this.webPushSocket.sendMsg('排序完成')
        this.submitOrderResult()
      } else {
        // 推送消息
        this.webPushSocket.sendMsg('撤销排序')
        this.recallOerderResult()
      }
    },
    // 获取定标候选人
    getBidCandidate () {
      let params = {
        methodType: this.methodType,
        num: this.$route.query.num
      }
      projectInfo.scalingEndInfo(this.projectId, params).then(res => {
        let tempData = res.data.ProjectView.users
        let tempBidDate = tempData.sort(function (a, b) {
          let x = a['initUserOrder']
          let y = b['initUserOrder']
          return (x > y ? 1 : (x < y ? -1 : 0))
        })
        this.bidCandTableData = tempBidDate
        // 排序数据
        this.voteUnitList = tempData
        this.sortTableData = tempData
      })
    },
    // 提交数据-排序结果
    submitOrderResult () {
      let data = this.sortTableData.map((item, index) => {
        if (item.nameCode) {
          return {
            sort: index + 1,
            projectId: item.projectId,
            resultId: item.nameCode.split('/')[0] || null,
            resultName: item.nameCode.split('/')[1] || null
          }
        } else {
          this.$message({
            message: '请选择排序',
            type: 'warning'
          })
          return false
        }
      })
      // 判断排序表格是否有重复选择
      let result = []
      if (data[data.length - 1]) {
        for (let x = 0; x < data.length; x++) {
          if (data[x] !== undefined || data[x] !== false) {
            result.push(data[x].resultId)
          } else {
            this.$message({
              message: '请选择排序',
              type: 'warning'
            })
            return false
          }
        }
        // 过滤重复
        let setData = Array.from(new Set(result))
        let setDataLen = setData.length
        if (setDataLen !== data.length && data.length > 0) {
          this.$message({
            message: '排序不可重复，请重新排序',
            type: 'warning'
          })
        } else {
          let params = {
            methodType: this.methodType,
            projectId: this.projectId
          }
          scalingEnd.submitOrderResult(data, params).then(res => {
            if (res.data.resCode === '0000') {
              this.changeBtnType = false
              // 禁用下拉
              this.disabledSelect = true
            }
          })
        }
      }
    },
    // 处理确认提交
    handleConfirmSummit () {
      let obj = {
        methodType: this.methodType,
        num: this.num
      }
      // 调用确认提交-接口
      scalingEnd.confirmResult(this.projectId, obj).then(res => {
        if (res.data.resCode === '0000') {
          this.disabledBtn = true
        }
        // // 禁用下拉
        // this.disabledSelect = true
      })
    },
    // 获得排序结果
    getOrderResult () {
      let params = {
        methodType: this.methodType,
        num: this.num,
        userId: 0
      }
      scalingEnd.getOrderResult(this.projectId, params).then((res) => {
        let tempResult = res.data.ResultList
        // console.log(tempResult)
        // if (!tempResult.length) {
        //   this.getOrderResult()
        // }
        if (tempResult.length) {
          this.queryType = true
          // 禁用下拉
          this.disabledSelect = true
          this.changeBtnType = false
          this.sortDisTableData = tempResult
          // console.log(this.sortDisTableData)
        }
      })
    },
    // 撤回
    recallOerderResult () {
      let params = {
        methodType: this.methodType,
        num: this.num
      }
      scalingEnd.cancelOrderResult(this.projectId, params).then(res => {
        if (res.data.resCode) {
          this.getBidCandidate()
          this.voteTableData = []
          this.queryType = false
          this.changeBtnType = true
          this.disabledSelect = false
        }
      })
    },
    // 点击-聊天记录
    handleCharRecord (val) {
      if (val === 1) {
        this.showHide++
        this.showChatsCont = true
      } else if (val === 2) {
        this.showHide--
        this.showChatsCont = false
      }
      this.getChatRecord()
    },
    // 获得聊天记录
    getChatRecord (pageNo = 1, pageSize = 20) {
      let obj = {
        projectId: this.projectId,
        pageNo: pageNo,
        pageSize: pageSize,
        messagetype: 4,
        methodType: this.methodType,
        num: this.num
      }
      scalingEnd.getChatRecord(obj).then(res => {
        // console.log(res)
        this.chatHistoryRecord = res.data.websocketLogList
        this.total = res.data.total
      })
    },
    // 集体议事-流程控制
    getChatRoomPage () {
      let obj = {
        methodType: this.methodType,
        num: this.num
      }
      scalingEnd.getChatRoomPage(this.projectId, obj).then(res => {
        // console.log(res)
        if (res.data.isHide === '1') {
          this.disabledBtn = true
        } else if (res.data.resCode === '4444') {
          this.disabledBtn = true
        } else {
          this.disabledBtn = false
        }
      })
    },
    // 确认并且提交-弹窗
    confirmSubmit () {
      this.$confirm('确认提交后不能撤回?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.handleConfirmSummit()
      })
    }
  },
  mounted () {
    this.$nextTick(function () {
      this.scroolButtom()
    })
    // console.log(this.currentUser)
    this.getBidCandidate()
    this.getOrderResult()
    this.initWebsocket()
    // 流程
    this.getChatRoomPage()
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
$color: #FEA842;
#group-discuss {
  .chat-room,
  .bid-cand {
    margin-top: 15px;
    background: #fff;
    padding: 10px;
  }
  .chat-room {
    position: relative;
  }
  .chat-room .chat-cont {
    height: 328px;
    overflow: auto;
    border: 1px solid #ddd;
  }
  /*对话内容样式*/
  .chat-cont {
    position: relative;
    .dialog-box {
      margin: 20px;
      text-align: left;
      label {
        position: relative;
        max-width: 80%;
        display: inline-block;
        border: 1px solid #ddd;
        padding: 0 10px;
        margin-left: 30px;
        border-radius: 5px;
      }
      .self-name-span {
        position: absolute;
        top: -20px;
        right:0;
        display:inline-block;
        width: 200px;
        overflow: hidden;
      }
      .other-name-span {
        position: absolute;
        top: -20px;
        left:0;
        display:inline-block;
        width: 200px;
        overflow: hidden;
      }
      .name-span {
        background: #83C284;
        color: #fff;
        border: 1px solid #eee;
        border-radius: 70px;
        width: 40px;
        height: 40px;
        line-height: 40px;
        margin: 0 auto;
        text-align: center;
        font-size: 22px;
        display: inline-block;
        overflow: hidden;
        white-space: nowrap;
        text-overflow: ellipsis;
      }
      .name-span-other {
        background: $color;
      }
      .triangle {
        position: absolute;
        top: 4px;
        left: -14px;
        color: #ddd;
        font-size: 20px;
        i {
          display: inline-block;
          background: #fff;
        }
      }
      .chat-details {
        display: inline-block;
        line-height: 20px;
        height: auto;
        text-align: left;
        padding: 5px 0;
      }
    }
    .dialog-self {
      text-align: right;
      .dialog-cont {
        margin-right: 20px;
      }
      .triangle-right {
        right: -14px;
      }
    }
  }
  .chat-room .char-handle-btns  {
    position: relative;
    height: 50px;
    line-height: 50px;
    text-align: left;
    border-left: 1px solid #ddd;
    border-right: 1px solid #ddd;
    // 表情包盒子
    .emotion-warp {
      position: absolute;
      bottom: 51px;
      left: 0;
      display: none;
      background: wheat;
    }
    .emotion-display {
      display: block;
    }
    .emotion-btn {
      vertical-align: middle;
      margin: 0 30px;
      &:hover {
        cursor: pointer;
      }
    }
    span {
      display: inline-block;
      float: right;
      margin-right: 20px;
      cursor: pointer;
    }
  }
  .chat-handle {
    .collective {
      padding-top: 8px;
      text-align: right;
    }
  }
  .submit-btns {
    margin: 20px 0;
  }
  /*聊天记录*/
  .char-history {
    position: absolute;
    right: 0;
    top: 59px;
    width: 0;
    height: 326px;
    overflow: auto;
    border: 1px solid #ddd;
    display: none;
    background: #eee;
    z-index: 9999;
  }
  .show-chat {
    display: block;
    transition: width 10s;
    width: 80%;
  }
  .el-tooltip__popper {
    // transform-origin: right center 0px; z-index: 2017; position: absolute; top: 136px; left: 536px;
  }
}
</style>
