<template>
  <div id="regis_bannerbox">
    <div class="logobigbox">
      <div class="logobox">
        <div class="logoboximg fl">
          <img src="../../../static/images/login_logo.png" alt="">
        </div>
        <div class="logoboxlink fr">
          <el-link href="" :underline="false">首页</el-link>
          <el-link href="" :underline="false">直采商城</el-link>
          <el-link href="" :underline="false">采购招标</el-link>
          <el-link href="" :underline="false">供应商库</el-link>
          <el-link href="" :underline="false">园区直采</el-link>
          <el-link href="" :underline="false">询价信息</el-link>
          <el-link href="" :underline="false">网站导航</el-link>
        </div>
      </div>
    </div>
    <div class="login_bannerimg">
      <el-carousel height="100%">
        <el-carousel-item v-for="item in picloop" :key="item.index">
          <img :src="item.src"/>
          <div class="text"><img :src="item.textsrc"/></div>
        </el-carousel-item>
      </el-carousel>
      <div class="login_intbox">
        <div class="login_titlebox">注册
          <span>已有账号？
            <router-link :to="{ path: '/login' }">
              马上登录
            </router-link>
          </span>
        </div>
        <el-form :model="registerForm" :rules="rules" ref="registerForm" :validate-on-rule-change="true" label-width="120px">
          <el-row>
            <el-col :span="24">
              <el-form-item label="公司名称"  prop="enterpriseName">
                <el-input v-model="registerForm.enterpriseName" placeholder="请输入您的公司名称"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <!--<el-row>-->
          <!--<el-col :span="24">-->
          <!--<el-form-item label="电子邮箱："  prop="email">-->
          <!--<el-input v-model="registerForm.email" placeholder="请输入您的电子邮箱"></el-input>-->
          <!--</el-form-item>-->
          <!--</el-col>-->
          <!--</el-row>-->
          <!--<el-row>-->
          <!--<el-col :span="14">-->
          <!--<el-form-item label="联系人："  prop="userName">-->
          <!--<el-input v-model="registerForm.userName" placeholder="请输入联系人" class="cont"></el-input>-->
          <!--</el-form-item>-->
          <!--</el-col>-->
          <!--<el-col :span="10" class="userName">-->
          <!--<el-form-item  prop="job">-->
          <!--<el-input v-model="registerForm.job" placeholder="请输入您的职位"></el-input>-->
          <!--</el-form-item>-->
          <!--</el-col>-->
          <!--</el-row>-->
          <el-row>
            <el-col :span="24">
              <el-form-item label="联系人"  prop="userName">
                <el-input v-model="registerForm.userName" placeholder="请输入联系人" class="cont"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="24">
              <el-form-item label="手机号码"  prop="cellPhone">
                <el-input v-model="registerForm.cellPhone" placeholder="请输入11位手机号码"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="24">
              <el-form-item label="短信验证码"  prop="smsCheckCode">
                <el-input v-model="registerForm.smsCheckCode" placeholder="请输入正确的验证码"></el-input>
                <el-button type="primary" class="getSmsCheckCode" v-show="show" @click="getCode('registerForm')">获取验证码</el-button>
                <el-button type="primary" class="getSmsCheckCode" v-show="!show">{{count}} 秒后重新发送</el-button>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="24">
              <el-form-item label="密码" prop="password">
                <el-input v-if="pwdHide" type="password" v-model="registerForm.password" placeholder="请填写6至20位密码"></el-input>
                <el-input v-else  type="text" v-model="registerForm.password" placeholder="请填写6至20位密码"></el-input>
                <em @click="pwdShowHide()" :class="pwdHide ? 'pwdHideEye' : 'pwdShowEye'"></em>
              </el-form-item>
            </el-col>
          </el-row>
          <div class="loginbtn">
            <el-checkbox v-model="checked">我已接受并同意网站<span @click="centerDialogVisible = true" style="color: #009688;">《用户服务条款》</span> </el-checkbox>
            <el-button type="primary" @click="register('registerForm')">马上加入注册</el-button>
          </div>
        </el-form>
      </div>
    </div>
    <div class="bottomlink">
      <el-link href="" :underline="false">比比电子交易平台</el-link> |
      <el-link href="" :underline="false">比比建材</el-link> |
      <el-link href="" :underline="false">比比金融</el-link> |
      <el-link href="" :underline="false">比比招标采购</el-link>
    </div>
    <p class="banquan"> <img src="../../../static/images/bq.png"/>晋公网安备 14019202000115号比比网络 版权所有 晋ICP备14002897号-1</p>
    <!-- 协议 -->
    <el-dialog
      title="招管家服务条款"
      :visible.sync="centerDialogVisible"
      width="70%"
      center>
      <div class="dialogContent">
        <div class="clause layui-layer-wrap" style="">
          <p>下列服务条款适用于招管家会员，若您使用招管家提供的服务，您必须同意接受此服务条款。</p>
          <p class="font-wei">一、服务条款的确认和接纳</p>
          <p>本服务条款适用于招管家用户，您在申请注册流程中点击同意本服务条款之前，应当认真阅读本服务条款。请您务必审慎阅读、充分理解各条款内容，当您按照注册页面提示填写信息、阅读并同意本服务条款且完成全部注册程序后，即表示您已充分阅读、理解并接受本服务条款的全部内容，并与招管家达成一致，成为招管家平台用户。阅读本服务条款的过程中，如果您不同意本服务条款或其中任何条款约定，您应立即停止注册程序。</p>
          <p>招管家服务的所有权和运作权归中采国信（北京）科技发展有限公司所有。所提供的服务必须按照其发布的公司章程，服务条款和操作规则严格执行。用户通过完成注册程序并点击“我已经阅读并接受《招管家服务条款》中的各项内容”，这表示用户与中采国信（北京）科技发展有限公司达成协议并接受所有的服务条款。</p>
          <p class="font-wei">二、服务简介</p>
          <p>中采国信（北京）科技发展有限公司运用自己的操作系统通过国际互联网络为用户提供各项服务，用户（包括普通个人用户，免费企业会员，收费企业会员）注册时，必须同意：</p>
          <p>1、提供真实、准确、即时、完整的个人/企业资料。同时，应根据情况变化维护并及时更新注册信息，以确保其真实、准确、即时、完整性。</p>
          <p>2、一旦中采国信（北京）科技发展有限公司发现用户资料含有不准确甚至是虚假内容，中采国信（北京）科技发展有限公司有权利中止对该用户的服务。</p>
          <p class="font-wei">三、用户的帐号、密码和安全性</p>
          <p>1、招管家的注册帐号可以是英文、数字、字母、中文，或者是它们的组合。用户可以根据自己的需要进行选择，但是用户注册的帐号或者填写的昵称需要符合下列规定：</p>
          <p>（1）不得使用党和国家机构的名称或者是它们的缩写。</p>
          <p>（2）不得使用党和国家领导人或者其他知名人士的真实姓名、笔名、艺名或者是他们的缩写。</p>
          <p>（3）不得使用不健康、不文明或者带有侮辱性、攻击性的用户名和昵称。</p>
          <p>（4）您一旦注册成功，就成为中采国信（北京）科技发展有限公司的合法用户，您将得到一个密码和用户名。并同意接受中采国信（北京）科技发展有限公司提供的各项服务。如果您未保管好自己的用户名和密码，而对您、中采国信或第三方造成的损害，您将负全部责任。另外，每个用户都要对其用户名中的所有活动和事件负全责。 您可随时改变您的密码和图标，也可以结束旧的用户名重开一个新用户名。</p>
          <p>（5）为避免用户的合法权利受到侵害，用户若发现任何非法使用用户名或存在安全漏洞的情况，请立即通告中采国信（北京）科技发展有限公司。</p>
          <p class="font-wei">四、服务条款的修改及修订</p>
          <p>中采国信（北京）科技发展有限公司有权在必要时修改服务条款，中采国信（北京）科技发展有限公司服务条款一旦发生变动，公司将会在用户进入下一步使用前的页面提示修改内容。如果您同意改动，则再一次激活“同意服务条款 提交注册信息”按钮，视为接受本服务条款的变动。如果用户不接受。中采国信（北京）科技发展有限公司则保留随时修改或中断服务而不需通知用户的权利。用户接受中采国信（北京）科技发展有限公司行使修改或中断服务的权利，中采国信（北京）科技发展有限公司不需对用户或第三方负责。</p>
          <p>本服务条款任一条款被视为废止、无效或不可执行，该条应视为可分的且并不影响本服务条款其余条款的有效性及可执行性。</p>
          <p class="font-wei">五、用户隐私制度</p>
          <p>中采国信（北京）科技发展有限公司非常重视用户信息的保护，在使用招管家的所有产品和服务前，请您务必仔细阅读并透彻理解本声明。一旦您选择使用，即表示您已经同意我们按照本隐私声明来使用和披露您的个人信息;，并接受本条款现有内容及其可能随时更新的内容。</p>
          <p>1、本注册条款所涉及的隐私是指：</p>
          <p>在会员注册招管家网站帐户时，使用其它招管家网站产品或服务，访问招管家网页, 或参加任何形式的会员活动、培训活动时，招管家会收集的会员的个人身份识别资料，包括会员的姓名、昵称、电邮地址、出生日期、性别、职业、所在行业、工龄，真实头像，籍贯，公司名称，QQ号码，公司地址，公司的产品以及服务简介，公司固话传真，公司主页，公司主要做的项目等。</p>
          <p>招管家网站自动接收并记录会员的浏览器和服务器日志上的信息，包括但不限于会员的IP 地址、在线、无线信息、信件等资料。</p>
          <p>2、 招管家收集上述信息将用于：</p>
          <p>提供网站服务、改进网页内容，满足会员对某种产品、活动的需求、通知会员最新产品、活动信息、或根据法律法规要求的用途、给会员带来更多商业机会等。</p>
          <p>3、 我们网站有相应的安全措施来确保我们掌握的信息不丢失，不被滥用和变造。这些安全措施包括向其它服务器备份数据和对用户密码加密。尽管我们有这些安全措施，但请注意在因特网上不存在“完善的安全措施”。</p>
          <p>4、 招管家可能利用工具，为合作伙伴的网站进行数据搜集工作，有关数据也会作统计用途。网站会将所记录的招管家会员数据整合起来，以综合数据形式供合作伙伴参考。综合数据会包括人数统计和使用情况等资料，但不会包含任何可以识别个人身份的数据。</p>
          <p>5、信息的披露和使用:</p>
          <p>我们不会向任何无关第三方提供，出售，出租，分享和交易用户的个人信息，但为方便您使用招管家服务及招管家关联公司或其他组织的服务（以下称其他服务），您同意并授权招管家将您的个人信息传递给您同时接受其他服务的招管家关联公司或其他组织，或从为您提供其他服务的招管家关联公司或其他组织获取您的个人信息。包括但不限于：</p>
          <p>您同意我们可批露或使用您的个人信息以用于识别和（或）确认您的身份，或解决争议，或有助于确保网站安全、限制欺诈、非法或其他刑事犯罪活动，以执行我们的服务协议。</p>
          <p>您同意我们可批露或使用您的个人信息以保护您的生命、财产之安全或为防止严重侵害他人之合法权益或为公共利益之需要。</p>
          <p>您同意我们可批露或使用您的个人信息以改进我们的服务，并使我们的服务更能符合您的要求，从而使您在使用我们服务时得到更好的使用体验。</p>
          <p>您同意我们利用您的个人信息与您联络，并向您提供您感兴趣的信息，如：产品信息。您接受“服务协议”和本隐私声明即为明示同意收取这些资料。</p>
          <p>您同意，您的个人信息可以被搜索引擎搜索，并在搜索结果中显示，由此给您带来更多的合作机会。</p>
          <p>您同意并授权招管家将您的个人信息传递给招管家，招管家关联公司、招管家合作伙伴、招管家会员，以助于招管家给您带来更多的商业机会、其他服务、合作伙伴等。</p>
          <p>法律规定的其他需披露您个人信息的情况。</p>
          <p>6、关于会员在招管家的上传或张贴的内容</p>
          <p>（1）会员在招管家上传或张贴的内容（包括照片、文字、附件、帖子、招投标信息、个人合作名片、公司名录和黄页、工程信息等），视为会员授予招管家免费、非独家的使用权，招管家有权为展示、传播及推广、促使合作等前述张贴内容的目的，对上述内容进行复制、修改、出版等。该使用权持续至会员书面通知招管家不得继续使用，且招管家实际收到该等书面通知时止。招管家网站、招管家合作伙伴、招管家关联公司均可使用。</p>
          <p>（2）因会员进行上述上传或张贴，而导致任何第三方提出侵权或索赔要求的，会员承担全部责任。</p>
          <p>（3）任何第三方对于会员在招管家的公开使用区域张贴的内容进行复制、修改、编辑、传播等行为的，该行为产生的法律后果和责任均由行为人承担，与招管家无关。</p>
          <p>7、不可抗力</p>
          <p>（1） “不可抗力”是指招管家不能合理控制、不可预见或即使预见亦无法避免的事件，该事件妨碍、影响或延误招管家根据本注册条款履行其全部或部分义务。该事件包括但不限于政府行为、自然灾害、战争、黑客袭击、电脑病毒、网络故障等。不可抗力可能导致招管家网站无法访问、访问速度缓慢、存储数据丢失、会员个人信息泄漏等不利后果。</p>
          <p>（2） 遭受不可抗力事件时，招管家可中止履行本注册条款项下的义务直至不可抗力的影响消除为止，并且不因此承担违约责任；但应尽最大努力克服该事件，减轻其负面影响。</p>
          <p class="font-wei">六、拒绝提供担保和免责声明</p>
          <p>用户明确同意使用招管家服务的风险由用户个人承担。服务提供是建立在免费的基础上。中采国信（北京）科技发展有限公司明确表示不提供任何类型的担保，不论是明确的或隐含的，但是对商业性的隐含担保，特定目的和不违反规定的适当担保除外。中采国信（北京）科技发展有限公司不担保服务一定能满足用户的要求，也不担保服务不会受中断，对服务的及时性、安全性、真实性、出错发生都不作担保。中采国信（北京）科技发展有限公司拒绝提供任何担保，包括信息能否准确、及时、顺利地传送。用户理解并接受下载或通过招管家产品服务取得的任何信息资料取决于用户自己，并由其承担系统受损、资料丢失以及其它任何风险。中采国信（北京）科技发展有限公司对在服务网上得到的任何商品购物服务、交易进程、招聘信息，都不作担保。用户不会从中采国信（北京）科技发展有限公司收到口头或书面的意见或信息，招管家也不会在这里作明确担保。</p>
          <p class="font-wei">七、有限责任</p>
          <p>中采国信（北京）科技发展有限公司对任何直接、间接、偶然、特殊及继起的损害或其他一切损害不负责任，这些损害来自：不正当使用产品服务，在网上进行交易，非法使用服务或用户传送的信息有所变动。这些损害会导致中采国信（北京）科技发展有限公司形象受损，所以中采国信（北京）科技发展有限公司早已提出这种损害的可能性。</p>
          <p class="font-wei">八、不提供零售和商业性服务</p>
          <p>用户使用中采国信（北京）科技发展有限公司各项服务的权利是企业的。个人用户只能是一个公司或实体的商业性组织下的所属员工。用户承诺：未经中采国信（北京）科技发展有限公司同意，不得利用中采国信（北京）科技发展有限公司各项服务进行销售或作其他商业用途。</p>
          <p class="font-wei">九、用户管理</p>
          <p>用户单独承担发布内容的责任。用户对服务的使用是根据所有适用于服务的地方法律、国家法律和国际法律标准的。用户承诺：</p>
          <p>1、在招管家的网页上发布信息或者利用招管家的服务时必须符合中国有关法规(部分法规请见附录)，不得在招管家的网页上或者利用招管家的服务制作、复制、发布、传播以下信息：</p>
          <p>（1）反对宪法所确定的基本原则的；</p>
          <p>（2）危害国家安全，泄露国家秘密，颠覆国家政权，破坏国家统一的；</p>
          <p>（3）损害国家荣誉和利益的；</p>
          <p>（4）煽动民族仇恨、民族歧视，破坏民族团结的；</p>
          <p>（5）破坏国家宗教政策，宣扬邪教和封建迷信的；</p>
          <p>（6）散布谣言，扰乱社会秩序，破坏社会稳定的；</p>
          <p>（7）散布淫秽、色情、赌博、暴力、凶杀、恐怖或者教唆犯罪的；</p>
          <p>（8）侮辱或者诽谤他人，侵害他人合法权益的；</p>
          <p>（9）含有法律、行政法规禁止的其他内容的。</p>
          <p>2、在招管家的网页上发布信息或者利用招管家的服务时还必须符合其他有关国家和地区的法律规定以及国际法的有关规定。</p>
          <p>3、不利用招管家的服务从事以下活动：</p>
          <p>（1） 未经允许，进入计算机信息网络或者使用计算机信息网络资源的；</p>
          <p>（2） 未经允许，对计算机信息网络功能进行删除、修改或者增加的；</p>
          <p>（3） 未经允许，对进入计算机信息网络中存储、处理或者传输的数据和应用程序进行删除、修改或者增加的；</p>
          <p>（4） 故意制作、传播计算机病毒等破坏性程序的；</p>
          <p>（5） 其他危害计算机信息网络安全的行为。</p>
          <p>4、不以任何方式干扰招管家的服务。</p>
          <p>5、遵守招管家的所有其他规定和程序。</p>
          <p>用户需对自己在使用招管家服务过程中的行为承担法律责任。用户理解，如果招管家发现其网站传输的信息明显属于上段第1条所列内容之一，依据中国法律，招管家有义务立即停止传输，保存有关记录，向国家有关机关报告，并且删除含有该内容的地址、目录或关闭服务器。</p>
          <p>若用户的行为不符合以上提到的服务条款，中采国信（北京）科技发展有限公司将作出独立判断立即取消用户服务帐号。</p>
          <p class="font-wei">十、保障</p>
          <p>用户同意保障和维护中采国信（北京）科技发展有限公司全体成员的利益，负责支付由用户使用超出服务范围引起的律师费用，违反服务条款的损害补偿费用，其它人使用用户的电脑、帐号和其它知识产权的追索费。</p>
          <p class="font-wei">十一、结束服务</p>
          <p>用户或中采国信（北京）科技发展有限公司可随时根据实际情况中断服务。中采国信（北京）科技发展有限公司不需对任何个人或第三方负责而随时中断服务。用户若反对任何服务条款的建议或对后来的条款修改有异议，或对招管家服务不满，用户只有以下的追索权：</p>
          <p>1、不再使用招管家服务。</p>
          <p>2、结束用户使用招管家服务的资格。</p>
          <p>3、通告中采国信（北京）科技发展有限公司停止该用户的服务。</p>
          <p class="font-wei">十二、通告</p>
          <p>所有发给用户的通告都可通过电子邮件或常规的信件传送。中采国信（北京）科技发展有限公司会通过邮件服务发报消息给用户，告诉他们服务条款的修改、服务变更、或其它重要事情。同时，中采国信（北京）科技发展有限公司保留对本站免费用户投放商业性广告的权利。</p>
          <p class="font-wei">十三、参与广告策划</p>
          <p>在中采国信（北京）科技发展有限公司许可下用户可在他们发表的信息中加入宣传资料或参与广告策划，在招管家各项免费服务上展示他们的产品。任何这类促销方法，包括运输货物、付款、服务、商业条件、担保及与广告有关的描述都只是在相应的用户和广告销售商之间发生。中采国信（北京）科技发展有限公司不承担任何责任，中采国信（北京）科技发展有限公司没有义务为这类广告销售负任何一部分的责任。</p>
          <p class="font-wei">十四、内容的所有权</p>
          <p>招管家对其独立采编的或从第三方获得合法许可的信息内容，内容的定义包括：文字、软件、声音、相片、录象、图表；在广告中的全部内容；全部招管家虚拟社区服务为用户提供的商业信息。所有这些内容均受版权、商标、标签和其它财产所有权法律的保护。所以，用户只能在中采国信（北京）科技发展有限公司和广告商授权下才能使用这些内容，而不能擅自复制、再造这些内容、或创造与内容有关的派生产品。</p>
          <p>在本站发表、转载的文章仅代表作者本人观点，本站没有义务查实文章或图片、音频、视频文件的出处及其真实性。</p>
          <p>如果您是文章、图片等资料的版权所有人，请与我们联系并说明具体文章标题，招管家会及时加上版权信息，如果您反对招管家的使用，在收到身份证明、版权证明和删除要求后我们会立即删除有版权问题的内容。</p>
          <p class="font-wei">十五、法律</p>
          <p>用户和中采国信（北京）科技发展有限公司一致同意有关本协议以及使用招管家的服务产生的争议交由仲裁解决，但是中采国信（北京）科技发展有限公司有权选择采取诉讼方式，并有权选择受理该诉讼的有管辖权的法院。若有任何服务条款与法律相抵触，那这些条款将按尽可能接近的方法重新解析，而其它条款则保持对用户产生法律效力和影响。</p>
          <p class="font-wei">十六、招管家会员帐号所含服务的信息储存及安全</p>
          <p>招管家对用户帐号上所有服务将尽力维护其安全性及方便性，但对服务中出现信息删除或储存失败不承担任何负责。另外我们保留判定用户的行为是否符合招管家服务条款的要求的权利，如果用户违背了用户服务条款的规定，将会中断其用户服务的帐号。</p>
          <p class="font-wei">十七、青少年用户特别提示</p>
          <p>青少年用户必须遵守全国青少年网络文明公约：</p>
          <p>要善于网上学习，不浏览不良信息；要诚实友好交流，不侮辱欺诈他人；要增强自护意识，不随意约会网友；要维护网络安全，不破坏网络秩序；要有益身心健康，不沉溺虚拟时空。</p>
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
          <el-button type="primary" @click="centerDialogVisible = false">确 定</el-button>
        </span>
    </el-dialog>
    <!-- -->
  </div>
</template>
<script>
import $ from 'jquery'
import {validateMobilePhone} from '@/assets/js/validate'
import {base} from '@/api/base'
export default {
  data () {
    // 电话
    let validPhone = (rule, value, callback) => {
      if (value && !validateMobilePhone(value)) {
        callback(new Error('请输入正确的手机号码'))
      } else {
        callback()
      }
    }
    return {
      // 轮播图
      picloop: [
        {
          src: '../../../static/images/banner/banner1.png',
          textsrc: '../../../static/images/banner/text1.png'
        },
        {
          src: '../../../static/images/banner/banner2.png',
          textsrc: '../../../static/images/banner/text2.png'
        },
        {
          src: '../../../static/images/banner/banner3.png',
          textsrc: '../../../static/images/banner/text3.png'
        }
      ],
      registerForm: {
        enterpriseName: '',
        email: '',
        userName: '',
        job: '',
        cellPhone: '',
        smsCheckCode: '',
        password: ''
      },
      // 验证
      rules: {
        enterpriseName: [
          { required: true, message: '公司名称不能为空', trigger: 'blur' }
        ],
        email: [
          { required: true, message: '电子邮箱不能为空', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur'] }
        ],
        userName: [
          { required: true, message: '联系人不能为空', trigger: 'blur' }
        ],
        job: [
          { required: true, message: '职位不能为空', trigger: 'blur' }
        ],
        cellPhone: [
          { required: true, message: '手机号码不能为空', trigger: 'blur' },
          { validator: validPhone, trigger: ['blur'] }
        ],
        smsCheckCode: [
          { required: true, message: '验证码不能为空', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '密码不能为空', trigger: 'blur' },
          { min: 6, max: 20, message: '长度在 6到 20 个字符', trigger: ['blur'] }
        ]
      },
      // 倒计时
      show: true,
      count: '',
      timer: null,
      // 比比网协议
      centerDialogVisible: false,
      checked: '',
      pwdHide: true
    }
  },
  created () {},
  methods: {
    mousedownFn: function (e) {
      this.mouseMoveStata = true
      this.beginClientX = e.clientX
    },
    // 验证码倒计时
    getCode (formName) {
      if (!this.registerForm.cellPhone) {
        this.$message({
          type: 'warning',
          message: '请输入手机号'
        })
        return false
      } else {
        this.$refs[formName].validateField('cellPhone', (errMsg) => {
          if (errMsg) {
            this.$message({
              type: 'warning',
              message: '请输入正确的手机号'
            })
          } else {
            let submitForm = {
              cellPhone: this.registerForm.cellPhone,
              type: 1
            }
            base.sendVerCode(submitForm).then((res) => {
              if (!res.data.data) {
                this.$message({
                  type: 'warning',
                  message: '该手机号已被注册！',
                  duration: 5000
                })
              } else {
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
              }
            })
          }
        })
      }
    },
    // 注册
    register (formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          if (this.checked) {
            this.registerForm.expends = {
              smsCode: this.registerForm.smsCheckCode
            }
            base.regist(this.registerForm).then(res => {
              if (res.data.resCode === '0000') {
                // 保存系统默认的字典信息
                this.$store.dispatch('SaveDictionaryDetail')
                // 保存直接登录信息
                this.$store.dispatch('SaveLoginInfo', res.data)
                // 企业用户直接跳转企业信息
                this.$router.push({path: '/system/enterprise'})
              }
            })
          } else {
            this.$message({
              type: 'warning',
              message: '请先勾选平台《用户服务条款》！',
              duration: 5000
            })
            return false
          }
        } else {
          return false
        }
      })
    },
    pwdShowHide () {
      this.pwdHide = !this.pwdHide
    }
  },
  mounted () {
    $('body').on('mousemove', (e) => {
      if (this.mouseMoveStata) {
        var width = e.clientX - this.beginClientX
        if (width > 0 && width <= this.maxwidth) {
          $('.handler').css({'left': width})
          $('.drag_bg').css({'width': width})
        } else if (width > this.maxwidth) {
          this.successFunction()
        }
      }
    })
    $('body').on('mouseup', (e) => {
      this.mouseMoveStata = false
      var width = e.clientX - this.beginClientX
      if (width < this.maxwidth) {
        $('.handler').css({'left': 0})
        $('.drag_bg').css({'width': 0})
      }
    })
  }
}
</script>
<style lang="less">
  #regis_bannerbox {
    .login_banner {
      width: 100%;
    }
    .logobigbox {
      background: #fff;
      height: 56px;
      width: 100%;
    }
    .logobox {
      width: 1220px;
      height: 56px;
      margin: 0 auto;
    }
    .login_bannerimg {
      width: 100%;
      height: 740px;
      position: relative;
      /*background: url("../../../static/images/regis_banner.jpg");*/
    }
    .login_bannerimg .el-carousel{
      width: 100%;
      height: 100%;
    }
    .login_bannerimg .el-carousel img{
      width:100%;
      height: 100%;
    }
    .login_bannerimg .el-carousel .text{
      position: absolute;
      left: 10%;
      top: 50%;
      z-index: 9999;
      margin-top: -130px;
    }
    .logoboximg {
      width: 120px;
      height: 100%;
      position: relative;
    }
    .logoboximg > img {
      width: 120px;
      height: 43px;
      position: absolute;
      top: 0;
      bottom: 0;
      left: 0;
      margin: auto;
    }
    .logoboxlink{
      margin-top:7px;
    }
    .logoboxlink a{
      display:inline-block;
      margin: 0 10px;
      height:46px;
      line-height:46px;
      border-bottom:2px solid #ffffff;
    }
    .logoboxlink a:hover{
      color:#009688;
      border-bottom:2px solid #009688;
    }
    .login_inputbox {
      width: 1220px;
      height: 100%;
      position: absolute;
      left: 50%;
      top: 0;
      margin-left: -660px;
      z-index: 9999;
    }
    .login_intbox {
      width: 450px;
      height: 550px;
      background: #fff;
      position: absolute;
      right: 10%;
      top: 0;
      bottom: 0;
      margin: auto;
      border-radius: 3px;
      z-index: 9;
    }
    @media screen and (max-width: 1400px) {
      .login_bannerimg .el-carousel.el-carousel--horizontal .text{
        left: 3%;
      }
      .login_bannerimg .el-carousel.el-carousel--horizontal .text img{
        max-width: 78%;
      }
      .login_intbox {
        right: 3%;
      }
    }
    .login_titlebox {
      width: 100%;
      height: 100px;
      color: #666666;
      font-size: 28px;
      line-height: 100px;
      padding: 0 35px;
      box-sizing: border-box;
    }
    .login_titlebox span{
      float: right;
      font-size: 14px;
    }
    .login_titlebox span a{
      color: #009688;
    }
    .el-form{
      width: 90%;
      margin: 10px auto 20px auto;
      overflow: hidden;
    }
    .el-form .el-form-item__label{
      width: 106px;
    }
    .el-form  .el-form-item__content{
      margin-left: 106px;
    }
    .getSmsCheckCode{
      position: absolute;
      right: 0px;
      top: 0px;
      background: #009688;
      border-color: #009688;
    }
    .loginbtn{
      width: 90%;
      margin: 0 auto;
      text-align: center;
    }
    .loginbtn .el-button--primary{
      width: 100%;
      margin: 20px auto;
      background: #009688;
      border-color: #009688;
    }
    .userName .el-form-item__content{
      margin-left: 10px;
    }

    .el-dialog{
      height: 70%;
      overflow-y: auto;
    }
    .clause{
      padding: 10px 15px;
      font-size:14px;
      text-indent: 28px;
      text-align: left;
    }
    .clause .font-wei{
      font-weight: 600;
    }
    .banquan{
      text-align: center;
      line-height: 36px;
      font-size: 14px;
      margin:0;
      padding:0;
      color:#999999
    }
    .banquan img{
      vertical-align: middle;
      margin-right: 5px;
    }
    .bottomlink{
      margin-top:10px;
      overflow:hidden;
      text-align:center;
      line-height: 24px;
      color:#999999
    }
    .el-link.el-link--default{
      color:#999999
    }
    /*修改input 高度*/
    .el-input__inner{
      height: 40px;
      line-height: 40px;
    }
    .el-input__icon{
      line-height: 40px;
    }
    .el-button{
      padding: 12px 20px;
    }
    .el-form-item__label{
      line-height: 40px;
    }
    .el-form-item__content{
      line-height: 40px;
    }
    .el-form-item{
      margin-bottom: 22px;
    }
    .el-form-item__error{
      padding-top: 4px;
    }
    .el-input__inner:focus{
      border-color: #009688;
    }
    em.pwdHideEye{
      position: absolute;
      right: 0;
      top: 0;
      width: 34px;
      height: 34px;
      background: url("../../../static/images/pwd_hide.png") center center no-repeat;
    }
    em.pwdShowEye{
      position: absolute;
      right: 0;
      top: 0;
      width: 34px;
      height: 34px;
      background: url("../../../static/images/pwd_show.png") center center no-repeat;
    }
  }
</style>
