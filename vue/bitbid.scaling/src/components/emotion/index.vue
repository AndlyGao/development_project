<template>
  <div>
    <div class="emotion-box" :style="{height: height + 'px'}">
      <div class="emotiom-box-line" v-for="(line, i) in list" :key="i" style="width: 210px;">
        <emotion class="emotion-item" v-for="(item, i) in line" :key="i" @click.native="clickHandler(item)">{{item}}</emotion>
      </div>
    </div>
  </div>
</template>
<script>
import Emotion from './Emotion'
export default {
  props: {
    height: {
      type: Number,
      default: 200
    }
  },
  data () {
    return {
      list: [
        ['微笑', '撇嘴', '色', '发呆', '得意', '流泪', '害羞', '闭嘴'],
        ['睡', '大哭', '尴尬', '发怒', '调皮', '呲牙', '惊讶', '难过'],
        ['酷', '冷汗', '抓狂', '吐', '偷笑', '可爱', '白眼', '傲慢'],
        ['饥饿', '困', '惊恐', '流汗', '憨笑', '大兵', '奋斗', '咒骂'],
        ['疑问', '嘘', '晕', '折磨', '衰', '骷髅', '敲打', '再见'],
        ['擦汗', '抠鼻', '鼓掌', '糗大了', '坏笑', '左哼哼', '右哼哼', '哈欠'],
        ['鄙视', '委屈', '快哭了', '阴险', '亲亲', '吓', '可怜', '菜刀'],
        ['西瓜', '啤酒', '篮球', '乒乓', '咖啡', '饭', '猪头', '玫瑰'],
        ['凋谢', '示爱', '爱心', '心碎', '蛋糕', '闪电', '炸弹', '刀'],
        ['足球', '瓢虫', '便便', '月亮', '太阳', '礼物', '拥抱', '强'],
        ['弱', '握手', '胜利', '抱拳', '勾引', '拳头', '差劲', '爱你'],
        ['NO', 'OK', '爱情', '飞吻', '跳跳', '发抖', '怄火', '转圈'],
        ['磕头', '回头', '跳绳', '挥手', '激动', '街舞', '左太极', '右太极']
      ]
    }
  },
  methods: {
    clickHandler (val) {
      let emotion = `#${val};`
      this.$emit('emotion', emotion)
    }
  },
  components: {
    Emotion
  }
}
</script>
<style lang="scss">
.emotion-box {
  margin: 0 auto;
  width: 100%;
  box-sizing: border-box;
  padding: 5px;
  border: 1px solid #b4b4b4;
  overflow: hidden;
  overflow-y: auto;
}
.emotion-box-line {
  display: flex;
}
.emotion-item {
  flex: 1;
  text-align: center;
  cursor: pointer;
}
</style>
