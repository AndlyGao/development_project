<template>
  <div id="register">
    <div class="login-top">
      <div class="topmain"><a href=""><img src="../../../static/images/user/logo.png"/> </a></div>
    </div>
    <div class="maincontain">
      <div class="regmain">
        <div class="title register_title">
          <p>已有账号？ <a @click="toLogin">马上登录</a></p>
        </div>
        <ul class="step">
          <li :class="(active===1)? 'active' :'prev'">
            <i>1</i>
            <span>账号信息</span>
            <em></em>
          </li>
          <li :class="(active===2)? 'active' : (active===3)?'prev':''">
            <i>2</i>
            <span>企业信息</span>
            <em></em>
          </li>
          <li :class="(active===3)? 'active':''">
            <i>3</i>
            <span>联系方式</span>
          </li>
        </ul>
        <el-form :model="stepFirstForm" status-icon :rules="rules1" ref="stepFirstForm" class="registerForm">
          <el-form-item label="用户名：" prop="account" v-if="this.active === 1" label-width="100px">
            <el-input v-model="stepFirstForm.account" placeholder="请输入用户名"></el-input>
          </el-form-item>
          <el-form-item label="密码：" prop="password" v-if="this.active === 1" label-width="100px">
            <el-input type="password" v-model="stepFirstForm.password" placeholder="请输入密码"></el-input>
          </el-form-item>
          <el-form-item label="确认密码：" prop="passwordConfirm" v-if="this.active === 1" label-width="100px">
            <el-input type="password" v-model="stepFirstForm.passwordConfirm" placeholder="请重新输入密码"></el-input>
          </el-form-item>
        </el-form>
        <el-form :model="stepSecondForm" status-icon :rules="rules2" ref="stepSecondForm" class="registerForm">
          <el-form-item label="公司类型：" prop="enterprise.enterType" v-if="this.active === 2" label-width="150px">
            <el-radio-group v-model="stepSecondForm.enterprise.enterType">
              <el-radio label="1">招标人</el-radio>
              <el-radio label="2">投标人</el-radio>
              <el-radio label="3">代理机构</el-radio>
              <el-radio label="4">其他</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="企业名称：" prop="enterprise.name" v-if="this.active === 2" label-width="150px">
            <el-input  v-model="stepSecondForm.enterprise.name" placeholder="请输入企业名称"></el-input>
          </el-form-item>
          <el-form-item label="统一社会信用代码：" prop="enterprise.useCode" v-if="this.active === 2" label-width="150px">
            <el-input  v-model="stepSecondForm.enterprise.useCode" placeholder="请输入统一社会信用代码"></el-input>
          </el-form-item>
          <el-form-item label="所属地区：" prop="enterprise.provinceId" v-if="this.active === 2" label-width="150px">
            <el-cascader
              :options="addressoptions"
              expand-trigger="hover"
              v-model="addressArray"
              @change="handleCityChange">
            </el-cascader>
          </el-form-item>
          <el-form-item label="企业地址：" prop="enterprise.address"  label-width="150px" v-if="this.active === 2">
            <el-input  v-model="stepSecondForm.enterprise.address" placeholder="请输入企业地址"></el-input>
          </el-form-item>
          <el-form-item label="邮政编码：" prop="enterprise.zipCode"  label-width="150px" v-if="this.active === 2">
            <el-input  v-model="stepSecondForm.enterprise.zipCode" placeholder="请输入邮政编码"></el-input>
          </el-form-item>
          <el-form-item label="所属行业：" prop="enterprise.industryTypeFirst" v-if="this.active === 2" label-width="150px">
            <el-select v-model="stepSecondForm.enterprise.industryTypeFirst" placeholder="请选择所属行业">
              <el-option
                v-for="item in industryType"
                :key="item.value"
                :label="item.label"
                :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="资料上传：" v-if="this.active === 2" label-width="150px" class="bitian">
            <div class="file"><span>营业执照</span>
              <upload-file @uploadSuccess="uploadSuccess" @deleteSuccess="deleteSuccess" :fileArrays="stepSecondForm.enterprise.fileInformations" fileType="1" isImage="1"></upload-file>
            </div>
            <div class="file"><span>银行开户证明</span>
              <upload-file @uploadSuccess="uploadSuccess" @deleteSuccess="deleteSuccess" :fileArrays="stepSecondForm.enterprise.fileInformations" fileType="2" isImage="1"></upload-file>
            </div>
          </el-form-item>
        </el-form>
        <el-form :model="stepThirdForm" status-icon :rules="rules3" ref="stepThirdForm" class="registerForm">
          <el-form-item label=" 联系人姓名 : " prop="name"  v-if="this.active === 3" label-width="110px">
            <el-input placeholder="请输入联系人姓名" v-model="stepThirdForm.name"></el-input>
          </el-form-item>
          <el-form-item label=" 电子邮箱 : " prop="email"  v-if="this.active === 3" label-width="110px">
            <el-input placeholder="请输入电子邮箱" v-model="stepThirdForm.email"></el-input>
          </el-form-item>
          <el-form-item label=" 注册手机 : "  prop="registeredCellPhone" v-if="this.active === 3" label-width="110px">
            <el-input placeholder="请输入注册手机" class="phoneInput" v-model="stepThirdForm.registeredCellPhone"></el-input>
            <!--<span id="btn" @click="settime">{{btnText}}</span>-->
            <span class="btn" v-show="show" @click="getCode">获取验证码</span>
            <span class="btn" v-show="!show">{{count}} 秒后重新发送</span>
          </el-form-item>
          <el-form-item label=" 验证码 : "  prop="smsCode" v-if="this.active === 3" label-width="110px">
            <el-input placeholder="请输入验证码" v-model="stepThirdForm.smsCode"></el-input>
          </el-form-item>
          <el-form-item label=" 固定电话 : " prop="fixedTelephone" v-if="this.active === 3" label-width="110px">
            <el-input placeholder="请输入固定电话" v-model="stepThirdForm.fixedTelephone"></el-input>
          </el-form-item>
          <el-form-item label=" 网址 : "  v-if="this.active === 3" label-width="110px">
            <el-input placeholder="请输入网址" v-model="stepThirdForm.url"></el-input>
          </el-form-item>
        </el-form>
        <div class="loginbtn">
          <el-checkbox v-model="checked"  v-if="this.active === 1">我已阅读并同意<span @click="centerDialogVisible = true">《内蒙古招标投标平台用户服务协议》</span> </el-checkbox>
          <el-button type="primary" @click="nextStep()">{{steptext}}</el-button>
        </div>
      </div>
    </div>
    <!--登录注册 底部 -->
    <div class="login-bottom">
      内蒙古招标投标协会&nbsp;&nbsp;&nbsp;&nbsp;版权所有&nbsp;&nbsp;&nbsp;&nbsp;蒙ICP备07000600号-1
    </div>
    <!--登录注册 底部 -->
    <!-- 协议 -->
    <el-dialog
      title="内蒙古招标投标平台用户服务协议"
      :visible.sync="centerDialogVisible"
      width="70%"
      center>
        <div class="dialogContent">
          <p class="headline">
            <span class="alertfont"><span>请仔细阅读下面的协议，只有接受协议才能继续进行注册。</span> </span>
          </p>
          <p class="headline"><span class="alertfont"><span class="title-content">1．服务条款的确认和接纳</span></span></p>
          <p class="headline">
            <span class="alertfont">
              <span>内蒙古招标投标协会网用户服务的所有权和运作权归内蒙古招标投标协会网拥有。内蒙古招标投标协会网所提供的服务将按照有关章程、服务条款和操作规则严格执行。用户通过注册程序点击“我同意” 按钮，即表示用户与内蒙古招标投标协会网达成协议并接受所有的服务条款。 内蒙古招标投标协会受内蒙古自治区发展和改革委员会委托，管理运营内蒙古招标投标协会网，对本服务条款负责制定和适时修正。</span>
            </span>
          </p>
          <p class="headline"><span class="alertfont"><span class="title-content">2． 内蒙古招标投标协会网服务简介</span></span></p>
          <p class="headline">
            <span class="alertfont">
              “内蒙古招标投标协会网”是内蒙古自治区发展和改革委员会组织建设的专业性网站。网站的一切业务均在内蒙古自治区发展和改革委员会的领导和指导下进行。网站的基本功能是：发布招标公告，招标人或代理机构提供网上服务。
            </span>
          </p>
          <p class="headline">
            <span class="alertfont">
              由自治区政府授权，自治区发展和改革委员会指定本网站为全自治区发布招标公告的唯一的网络媒介。
            </span>
          </p>
          <p class="headline">
            <span class="alertfont">
              按照有关规定，凡在内蒙古自治区内依法进行的所有公开招标，其招标公告必须在本网站上发布（在指定报纸发布的同时必须抄送本网站）。
            </span>
          </p>
          <p class="headline">
            <span class="alertfont">
              本网站是政府对招投标活动提供服务、进行监管的工具。本着“减少财政负担，力求收支平衡”的原则，本网站将通过提供多样化的、高质量的、便捷的网络服务，在提供免费服务的同时推出有偿服务项目。
            </span>
          </p>
          <p class="headline">
            <span class="alertfont"><span>用户必须：</span></span>
          </p>
          <p class="headline"><span class="alertfont">1)购置设备，包括个人电脑一台、调制解调器一个及配备上网装置。 </span></p>
          <p class="headline"><span class="alertfont">2)个人上网和支付与此服务有关的电话费用、网络费用。</span></p>
          <p class="headline"><span class="alertfont"><span>用户同意：</span> </span></p>
          <p class="headline"><span class="alertfont">1)提供及时、详尽及准确的个人资料。 </span></p>
          <p class="headline"><span class="alertfont">2)不断更新注册资料，符合及时、详尽、准确的要求。所有原始键入的资料将引用为注册资料。 </span></p>
          <p class="headline">
            <span class="alertfont">3)用户同意遵守《中华人民共和国保守国家秘密法》、《中华人民共和国计算机信息系统安全保护条例》、《计算机软件保护条例》等有关计算机及互联网规定的法律和法规、实施办法。在任何情况下，内蒙古招标投标协会网网站合理地认为用户的行为可能违反上述法律、法规，内蒙古招标投标协会网网站可以在任何时候，不经事先通知终止向该用户提供服务。用户应了解国际互联网的无国界性，应特别注意遵守当地所有有关的法律和法规。</span>
          </p>
          <p class="headline"><span class="alertfont"><span class="title-content">3． 服务条款的修改</span></span></p>
          <p class="headline">
            <span class="alertfont"><span>内蒙古招标投标协会网会不定时地修改服务条款，服务条款一旦发生变动，将会在相关页面上提示修改内容。如果您同意改动，则再一次点击“我同意”按钮。 如果您不接受，则及时取消您的用户使用服务资格。</span></span>
          </p>
          <p class="headline"><span class="alertfont">&nbsp;<span class="title-content">4． 服务修订</span></span></p>
          <p class="headline">
            <span class="alertfont">
              内蒙古招标投标协会网保留随时修改或中断服务而不需告知用户的权利。内蒙古招标投标协会网行使修改或中断服务的权利，不需对用户或第三方负责。
            </span>
          </p>
          <p class="headline"><span class="alertfont">&nbsp;<span class="title-content">5． 用户隐私制度</span></span></p>
          <p class="headline">
            <span class="alertfont">
              <span>尊重用户个人隐私是内蒙古招标投标协会网的基本原则。内蒙古招标投标协会网不会公开、编辑或透露用户的注册信息，除非有法律许可要求，或内蒙古招标投标协会网在诚信的基础上认为透露这些信息在以下三种情况是必要的：</span>
            </span>
          </p>
          <p class="headline"><span class="alertfont">1)遵守有关法律规定，遵从合法服务程序。 </span></p>
          <p class="headline"><span class="alertfont">2)保持维护内蒙古招标投标协会网的商标所有权。 </span></p>
          <p class="headline"><span class="alertfont">3)在紧急情况下竭力维护用户个人和社会大众的隐私安全。 </span></p>
          <p class="headline"><span class="alertfont">4)符合其他相关的要求。 </span></p>
          <p class="headline"><span class="alertfont">&nbsp;<span class="title-content">6．用户的帐号，密码和安全性</span></span></p>
          <p class="headline">
            <span class="alertfont">
              <span>一旦注册成功成为内蒙古招标投标协会网用户，您将得到一个密码和帐号。如果您不保管好自己的帐号和密码安全，将对因此产生的后果负全部责任。另外，每个用户都要对其帐户中的所有活动和事件负全责。您可随时根据提示改变您的密码。用户同意若发现任何非法使用用户帐号或安全漏洞的情况，立即通知内蒙古招标投标协会网。</span>
            </span>
          </p>
          <p class="headline"><span class="alertfont">&nbsp;<span class="title-content">7． 免责条款</span></span></p>
          <p style="text-indent:13px">
            <span class="alertfont floatleft"><span>&nbsp;&nbsp;&nbsp;&nbsp;用户明确同意网站服务的使用由用户个人承担风险。</span></span>
          </p>
          <p class="headline">
            <span class="alertfont">
              <span>内蒙古招标投标协会网不作任何类型的担保，不担保服务一定能满足用户的要求，也不担保服务不会受中断，对服务的及时性，安全性，出错发生都不作担保。用户理解并接受：任何通过内蒙古招标投标协会网服务取得的信息资料的可靠性取决于用户自己，用户自己承担所有风险和责任。</span>
            </span>
          </p>
          <p class="headline"><span class="alertfont">&nbsp;<span class="title-content">8．有限责任</span></span></p>
          <p class="headline">
            <span class="alertfont">
              <span>内蒙古招标投标协会网对任何直接、间接、偶然、特殊及继起的损害不负责任。</span>
            </span>
          </p>
          <p class="headline"><span class="alertfont">&nbsp;<span class="title-content">9． 不提供零售和商业性服务</span></span></p>
          <p class="headline">
            <span class="alertfont">
              <span>用户承诺不经内蒙古招标投标协会网同意，不能利用网站服务进行销售或其他商业用途。</span>
            </span>
          </p>
          <p class="headline"><span class="alertfont">&nbsp;<span class="title-content">10．用户责任</span></span></p>
          <p class="alert_p"><span class="alertfont floatleft"><span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;用户单独承担传输内容的责任。用户必须遵循：</span></span>
          </p>
          <p class="alert_p"><span class="alertfont floatleft">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;1)从中国境内向外传输技术性资料时必须符合中国有关法规。 </span></p>
          <p class="alert_p"><span class="alertfont floatleft">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;2)使用网站服务不作非法用途。</span></p>
          <p class="alert_p"><span class="alertfont floatleft">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;3)不干扰或混乱网络服务。 </span></p>
          <p class="alert_p"><span class="alertfont floatleft">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;4)不发表任何与政治相关的信息。 </span></p>
          <p class="alert_p"><span class="alertfont floatleft">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;5)遵守所有使用网站服务的网络协议、规定、程序和惯例。</span></p>
          <p class="alert_p"><span class="alertfont floatleft">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;6)不得利用本站危害国家安全、泄露国家秘密，不得侵犯国家社会集体的和公民的合法权益。</span></p>
          <p class="alert_p"><span class="alertfont floatleft">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;7)不得利用本站制作、复制和传播下列信息： </span></p>
          <p class="alert_p"><span class="alertfont floatleft">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;1、煽动抗拒、破坏宪法和法律、行政法规实施的；</span></p>
          <p class="alert_p"><span class="alertfont floatleft">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;2、煽动颠覆国家政权，推翻社会主义制度的；</span></p>
          <p class="alert_p"><span class="alertfont floatleft">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;3、煽动分裂国家、破坏国家统一的；</span></p>
          <p class="alert_p"><span class="alertfont floatleft">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;4、煽动民族仇恨、民族歧视，破坏民族团结的；</span></p>
          <p class="alert_p"><span class="alertfont floatleft">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;5、捏造或者歪曲事实，散布谣言，扰乱社会秩序的；</span></p>
          <p class="alert_p"><span class="alertfont floatleft">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;6、宣扬封建迷信、淫秽、色情、赌博、暴力、凶杀、恐怖、教唆犯罪的；</span></p>
          <p class="alert_p"><span class="alertfont floatleft">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;7、公然侮辱他人或者捏造事实诽谤他人的，或者进行其他恶意攻击的；</span></p>
          <p class="alert_p"><span class="alertfont floatleft">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;8、损害国家机关信誉的；</span></p>
          <p class="alert_p"><span class="alertfont floatleft">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;9、其他违反宪法和法律行政法规的；</span></p>
          <p class="alert_p"><span class="alertfont floatleft">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;10、进行商业广告行为的。</span></p>
          <p style="text-indent:16px">
            <span class="alertfont floatleft">
              &nbsp;&nbsp;&nbsp;&nbsp;用户不能传输任何教唆他人构成犯罪行为的资料；不能传输长国内不利条件和涉及国家安全的资料；不能传输任何不符合当地法规、国家法律和国际法 律的资料。未经许可而非法进入其它电脑系统是禁止的。若用户的行为不符合以上的条款，内蒙古招标投标协会网将取消用户服务帐号。
            </span>
          </p>
          <p class="headline"><span class="alertfont">&nbsp;<span class="title-content">11．网站内容的所有权</span></span></p>
          <p class="headline">
            <span class="alertfont">
              <span>&nbsp;内蒙古招标投标协会网定义的内容包括：文字、软件、声音、相片、录象、图表；在广告中全部内容；电子邮件的全部内容；内蒙古招标投标协会网为用户提供的商业信息。所有这些内容受版权、商标、标签和其它财产所有权法律的保护。所以，用户只能在内蒙古招标投标协会网和广告商授权下才能使用这些内容，而不能擅自复制、篡改这些内容、或创造与内容有关的派生产品。</span>
            </span>
          </p>
          <p class="headline"><span class="alertfont">&nbsp;<span class="title-content">12．附加信息服务</span></span></p>
          <p class="headline">
            <span class="alertfont">
              <span>用户在享用内蒙古招标投标协会网提供的免费服务的同时，同意接受内蒙古招标投标协会网提供的各类附加信息服务。</span>
            </span>
          </p>
          <p class="headline"><span class="alertfont">&nbsp;<span class="title-content">13．解释权</span></span></p>
          <p class="headline">
            <span class="alertfont">
              <span>本注册协议的解释权归内蒙古招标投标协会所有。如果其中有任何条款与国家的有关法律相抵触，则以国家法律的明文规定为准。</span>
            </span>
          </p>
        </div>
        <span slot="footer" class="dialog-footer">
          <el-button type="primary" @click="centerDialogVisible = false">确 定</el-button>
        </span>
    </el-dialog>
    <!-- -->
  </div>
</template>
<script>
import { isvalidAccount, Zipcode, isvalidFixedPhone } from '@/assets/js/validate'
import * as region from '@/assets/js/region'
import * as industry from '@/assets/js/industry'
import uploadFile from '@/components/upload/publicFileUpload'
import {login} from '@/api'
export default {
  components: {
    uploadFile
  },
  data () {
    // 密码
    let validatePwd = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入密码'))
      } else if (this.stepFirstForm.passwordConfirm !== '') {
        this.$refs.stepFirstForm.validateField('passwordConfirm')
        callback()
      }
    }
    // 确认密码
    let validateConfirmPwd = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请再次输入密码'))
      } else if (value !== this.stepFirstForm.password) {
        callback(new Error('两次输入密码不一致!'))
      } else {
        callback()
      }
    }
    // 用户名
    let validateAccount = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入用户名'))
      } else if (!isvalidAccount(value)) {
        callback(new Error('请输入4-16位字母或数字'))
      } else {
        callback()
      }
    }
    // 邮政编码
    let isZipcode = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入邮政编码'))
      } else if (!Zipcode(value)) {
        callback(new Error('请输入正确的邮政编码'))
      } else {
        callback()
      }
    }
    // 电话
    let validPhone = (rule, value, callback) => {
      if (value && !isvalidFixedPhone(value)) {
        callback(new Error('请输入正确的固定电话号码'))
      } else {
        callback()
      }
    }
    return {
      checked: false,
      steptext: '下一步',
      stepFirstForm: {
        account: '',
        password: '',
        passwordConfirm: ''
      },
      stepSecondForm: {
        enterprise: {
          enterType: '',
          name: '',
          useCode: '',
          provinceId: '',
          cityId: '',
          address: '',
          zipCode: '',
          industryTypeFirst: '',
          fileInformations: []
        }
      },
      stepThirdForm: {
        name: '',
        fixedTelephone: '',
        email: '',
        url: '',
        registeredCellPhone: '',
        smsCode: ''
      },
      addressArray: [],
      centerDialogVisible: false,
      rules1: {
        account: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 4, max: 16, message: '长度在 4到 16 个字符', trigger: 'blur' },
          {validator: validateAccount, trigger: ['blur', 'change']}
        ],
        password: [
          { required: true, message: '请输入密码', trigger: ['blur', 'change'] },
          {min: 6, max: 20, message: '长度在 6到 20 个字符', trigger: ['blur']},
          { validator: validatePwd, trigger: ['blur', 'change'] }
        ],
        passwordConfirm: [
          { required: true, message: '请重新输入密码', trigger: ['blur', 'change'] },
          {min: 6, max: 20, message: '长度在 6到 20 个字符', trigger: ['blur']},
          { validator: validateConfirmPwd, trigger: ['blur', 'change'] }
        ]
      },
      rules2: {
        'enterprise.enterType': [
          { required: true, message: '请选择公司类型', trigger: ['blur', 'change'] }
        ],
        'enterprise.name': [
          { required: true, message: '请输入企业名称', trigger: ['blur', 'change'] },
          { min: 1, max: 100, message: '企业名称不能大于100个字符', trigger: ['blur', 'change'] }
        ],
        'enterprise.useCode': [
          { required: true, message: '请输入统一社会信用代码', trigger: ['blur'] },
          { min: 18, max: 18, message: '请输入18位统一社会信用代码', trigger: ['blur', 'change'] }
        ],
        'enterprise.provinceId': [
          { required: true, message: '请选择所属地区', trigger: ['blur', 'change'] }
        ],
        'enterprise.address': [
          { required: true, message: '请输入企业地址', trigger: ['blur', 'change'] },
          { min: 1, max: 100, message: '企业地址不能大于100个字符', trigger: ['blur', 'change'] }
        ],
        'enterprise.zipCode': [
          { validator: isZipcode, trigger: ['blur', 'change'] },
          { required: true, message: '请输入邮政编码', trigger: ['blur', 'change'] }
        ],
        'enterprise.industryTypeFirst': [
          { required: true, message: '请选择所属行业', trigger: ['blur', 'change'] }
        ]
      },
      rules3: {
        name: [
          { required: true, message: '请输入姓名', trigger: ['blur', 'change'] }
        ],
        email: [
          { required: true, message: '请输入邮箱地址', trigger: ['blur', 'change'] },
          { type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }
        ],
        registeredCellPhone: [
          { required: true, message: '请输入手机号', trigger: ['blur', 'change'] },
          { validator: validPhone, trigger: ['blur', 'change'] }
        ],
        fixedTelephone: [
          { validator: validPhone, message: '请输入正确固话', trigger: ['blur', 'change'] }
        ],
        smsCode: [
          { required: true, message: '请输入验证码', trigger: ['blur', 'change'] }
        ]
      },
      // 步骤条
      active: 1,
      // 所属地区
      addressoptions: region.CityInfo,
      // 所属地区绑定数组
      industryType: industry.industry,
      // 倒计时
      show: true,
      count: '',
      timer: null
    }
  },
  methods: {
    toLogin () {
      this.$router.push({path: '/login'})
    },
    // 所选地区
    handleCityChange (value) {
      if (value.length === 0) {
        return false
      }
      this.stepSecondForm.enterprise.provinceId = value[0]
      this.stepSecondForm.enterprise.cityId = value[1]
    },
    // 上传文件
    uploadSuccess (file, fileType) {
      this.stepSecondForm.enterprise.fileInformations = this.stepSecondForm.enterprise.fileInformations.filter(item => item.businessType !== fileType)
      this.stepSecondForm.enterprise.fileInformations.push(file)
    },
    deleteSuccess (fileId) {
      this.stepSecondForm.enterprise.fileInformations = this.stepSecondForm.enterprise.fileInformations.filter(item => item.relativePath !== fileId)
    },
    // 验证码倒计时
    getCode () {
      if (!this.stepThirdForm.registeredCellPhone) {
        this.$message({
          type: 'warning',
          message: '请输入手机号'
        })
        return false
      } else {
        login.getVCode(this.stepThirdForm.registeredCellPhone).then((res) => {
          const TIME_COUNT = 60
          if (!this.timer) {
            this.count = TIME_COUNT
            this.show = false
            this.timer = setInterval(() => {
              if (this.count > 0 && this.count <= TIME_COUNT) {
                this.count--
              } else {
                this.show = true
                clearInterval(this.timer)
                this.timer = null
              }
            }, 1000)
          }
        })
      }
    },
    // 下一步
    nextStep () {
      let formName
      if (this.active === 1) {
        formName = 'stepFirstForm'
      } else if (this.active === 2) {
        formName = 'stepSecondForm'
      } else {
        formName = 'stepThirdForm'
      }
      this.$refs[formName].validate((valid) => {
        if (valid) {
          if (!this.checked) {
            this.$message({
              type: 'warning',
              message: '请阅读并同意内蒙古招标投标平台用户服务协议！'
            })
            return false
          }
          if (this.active === 2) {
            if (this.stepSecondForm.enterprise.fileInformations.length < 2) {
              this.$message({
                type: 'warning',
                message: '请确认文件是否上传完毕！'
              })
              return false
            } else {
              this.steptext = '注册'
            }
          }
          if (this.active !== 3) {
            this.active++
          } else {
            // 提交表单
            let totalForm = {}
            Object.assign(totalForm, this.stepSecondForm, this.stepThirdForm)
            totalForm.account = this.stepFirstForm.account
            totalForm.password = this.stepFirstForm.password
            login.register(totalForm).then((res) => {
              if (res.data.resCode === '0000') {
                this.$router.push({path: '/login'})
              } else {
                return false
              }
            })
          }
        } else {
          return false
        }
      })
    }
  },
  mounted () {
    this.active = 1
  }
}
</script>
<style lang="less">
  body, html{
    background: #ffffff;
  }
  #register {
    text-align: left;
    .login-top{
      width: 100%;
      min-width: 1200px;
      height: 103px;
      overflow: hidden;
      background: #0084ff;
    }
    .login-top .topmain{
      width: 1200px;
      overflow: hidden;
      margin: 0 auto;
      text-align: left;
    }
    .login-top .topmain a{
      margin-top: 25px;
      display: block;
    }
    .login-top .topmain a img{
      border:none;
    }
    .maincontain{
      width: 100%;
      min-width: 1200px;
      overflow: hidden;
      background: url("../../../static/images/user/maincontain.jpg") no-repeat;
    }
    .regmain{
      width: 861px;
      box-shadow: #dddddd 0px 0px 10px;
      overflow: hidden;
      margin: 50px auto;
      background: #ffffff;
    }
    .regmain  .title{
      overflow: hidden;
      padding: 0px;
      margin: 0px;
    }
    .regmain  .title p{
      padding: 0 26px;
      margin: 20px 0;
      text-align: right;
      line-height: 24px;
      font-size: 12px;
    }
    .regmain .title p a{
      float: right;
      padding: 0 13px;
      margin-left: 5px;
      -webkit-border-radius: 15px;
      -moz-border-radius: 15px;
      border-radius: 15px;
      display: inline-block;
      line-height: 22px;
      font-size: 12px;
      color: #666666;
      border: 1px solid #999999;
      cursor: pointer;
    }
    .registerForm{
      width: 60%;
      margin: 50px auto 20px auto;
    }
    .loginbtn {
      width: 60%;
      margin: 50px auto 30px auto;
    }
    .el-checkbox{
      float: left;
    }
    .el-cascader,.el-select{
      width: 367px;
    }
    .loginbtn .el-button--primary {
      width: 100%;
      color: #fff;
      -webkit-border-radius: 20px;
      -moz-border-radius: 20px;
      border-radius: 20px;
      background-color: #0084ff;
      border-color: #0084ff;
    }
    .el-dialog{
      height: 70%;
      overflow: auto;
    }
    .el-checkbox__label{
      margin: 10px 0 20px 0;
      font-size: 12px;
    }
    .headline {
      text-indent:32px;
      float: left;
      width: 100%;
      text-align: left;
    }
    .alertfont {
      font-size:16px;
      text-align: left;
    }
    .floatleft{
      width:100%;
      float: left;
      line-height: 30px;
    }
    .alert_p {
      text-indent:0
    }
    .title-content {
      font-weight: bold;
    }
    .formtip{
      width: 24%;
      display: inline-block;
      float: left;
      color: #2c3e50;
      text-align: right;
    }
    .shortinput{
      width: 72%;
      float: left;
      margin-left: 2%;
      padding-left: 2%;
    }
    .shortinput .el-input__inner{
      padding-left: 10px;
    }
    // 步骤条
    ul.step{
      list-style: none;
      width: 70%;
      height: 38px;
      cursor: pointer;
      overflow: hidden;
      margin: 0 auto;
      text-align: center;
    }
    ul.step li{
      float: left;
      height: 38px;
      margin-right: 150px;
      position: relative;
    }
    ul.step li i{
      display: inline-block;
      width: 22px;
      height: 22px;
      line-height: 22px;
      -webkit-border-radius: 50%;
      -moz-border-radius: 50%;
      border-radius: 50%;
      background: #bbbbbb;
      color: #ffffff;
      font-style: normal;
    }
    ul.step li span{
      display: inline-block;
      line-height: 38px;
      margin-left: 10px;
      font-size: 14px;
    }
    ul.step li:last-child{
      margin-right: 0px;
    }
    ul.step li em{
      position: absolute;
      width: 104px;
      height: 2px;
      display: block;
      left: 120px;
      top: 18px;
      background: #eeeeee;
    }
    ul.step li.active i{
      width: 36px;
      height: 36px;
      line-height: 36px;
      background: #0084ff;
    }
    ul.step li.active span{
      color: #333333;
    }
    ul.step li.prev i{
      background: #0084ff;
    }
    .login-bottom{
      width: 1200px;
      margin: 30px auto;
      font-size: 12px;
      color: #999999;
      line-height: 30px;
      text-align: center;
    }
    .file{
      overflow: hidden;
      margin-bottom: 20px;
    }
    .file>span{
      width: 120px;
      display: inline-block;
      float: left;
    }
    .phoneInput{
      width: 270px;
    }
    span.btn{
      display: inline-block;
      width: 120px;
      color: #ffffff;
      text-align: center;
      height: 40px;
      background: #0084ff;
      margin-left: 12px;
      border-radius: 5px;
      cursor: pointer;
    }
    .bitian .el-form-item__label::before{
      content:"*";
      color:#f66c6c;
      margin-right: 5px;
    }
  }
</style>
