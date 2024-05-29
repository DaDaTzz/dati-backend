<template>
  <div id="userLoginPage">
    <h2 style="margin-bottom: 16px">创建题目</h2>
    <a-form :model="questionContent" :style="{ width: '480px'}" @submit="handleSubmit" label-align="left" auto-label-width>
      <a-form-item label="应用 id">
        {{ props.appId }}
      </a-form-item>
      <a-form-item label="题目列表" :content-flex="false" :merge-props="false">
        <a-space size="medium">
          <a-button @click="addQuestion(questionContent.length)">
            底部添加题目
          </a-button>
          <!-- AI 生成抽屉 -->
          <AiGenerateQuestionDrawer :appId="appId" :onSuccess="onAiGenerateSuccess"/>
        </a-space>
        <!-- 遍历每到题目 -->
        <div v-for="(question, questionIndex) in questionContent" :key="questionIndex">
          <a-space size="large">
            <h3>题目 {{ questionIndex + 1 }}</h3>
            <a-button size="small" @click="addQuestion(questionIndex + 1)">
              添加题目
            </a-button>
            <a-button size="small" status="danger" @click="deleteQuestion(questionIndex)">
              删除题目
            </a-button>
          </a-space>
          <a-form-item field="title" :label="`题目 ${questionIndex + 1} 标题`">
            <a-input v-model="question.title" placeholder="请输入标题..." />
          </a-form-item>
          <!-- 题目选项 -->
          <a-space size="mini">
            <h4>题目 {{ questionIndex + 1 }} 选项列表</h4>
            <a-button size="small" @click="addQuestionOption(question, question.options.length)">
              底部添加选项
            </a-button>
          </a-space>
          <a-form-item v-for="(option, optionIndex) in question.options" :key="optionIndex" :label="`选项${optionIndex + 1}`" :content-flex="false" :merge-props="false">
            <a-form-item label="选项 key">
              <a-input v-model="option.key" placeholder="请输入选项key..." />
            </a-form-item>
            <a-form-item label="选项 value">
              <a-input v-model="option.value" placeholder="请输入选项value..." />
            </a-form-item>
            <a-form-item label="选项结果">
              <a-input v-model="option.result" placeholder="请输入选项结果..." />
            </a-form-item>
            <a-form-item label="选项得分">
              <a-input v-model="option.score" placeholder="请输入选项得分..." />
            </a-form-item>
            <a-button size="small" @click="addQuestionOption(question,optionIndex + 1)">
              添加选项
            </a-button>
            <a-button size="small" status="danger" @click="deleteQuestionOption(question, optionIndex)">
              删除选项
            </a-button>
          </a-form-item>
        </div>
        <a-form-item>
          <a-button v-if="questionContent.length > 0 " type="primary" html-type="submit" style="width: 120px">提交</a-button>
        </a-form-item>
      </a-form-item>
    </a-form>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, watchEffect } from "vue";
import { useRouter } from "vue-router";
import { Message } from "@arco-design/web-vue";
import { addQuestionUsingPost, editQuestionUsingPost, listQuestionVoByPageUsingPost } from "@/api/questionController";
import AiGenerateQuestionDrawer from "@/views/add/components/AiGenerateQuestionDrawer.vue";
import message from "@arco-design/web-vue/es/message";

interface Props {
  appId: string;
}

const props = withDefaults(defineProps<Props>(), {
  appId: () => {
    return "";
  },
});

const router = useRouter();

// 题目内容结构（题目列表）
const questionContent = ref<API.QuestionContentDTO[]>([])

/**
 * 添加题目
 * @param index
 */
const addQuestion = (index:number) => {
  questionContent.value.splice(index, 0, {
    title:'',
    options:[],
  })
};

/**
 * 删除题目
 * @param index
 */
const deleteQuestion = (index:number) => {
  questionContent.value.splice(index, 1);
}

/**
 * 添加题目选项
 * @param index
 */
const addQuestionOption = (question:API.QuestionContentDTO, index:number) => {
  if(!question.options){
    return question.options = []
  }
  question.options?.splice(index, 0, {
    key:"",
    value:"",
  })
};

const oldQuestion = ref<API.QuestionVO>()


/**
 * 删除题目选项
 * @param index
 */
const deleteQuestionOption = (question:API.QuestionContentDTO,index:number) => {
  if(!question.options){
    return question.options = []
  }
  question.options?.splice(index, 1);
}



/**
 * 加载数据
 */
const loadData = async () =>{
  if(!props.appId){
    return;
  }
  const res = await listQuestionVoByPageUsingPost({
    appId:props.appId as any,
    current: 1,
    pageSize: 1,
    sortField: "createTime",
    sortOrder: "descend",
  })
  if(res.data.code === 0 && res.data.data?.records){
    oldQuestion.value = res.data.data.records[0]
    if(oldQuestion.value){
      questionContent.value = oldQuestion.value?.questionContent ?? []
    }
  }else {
    Message.error("获取数据失败，" + res.data.message)
  }
}

// 获取旧数据
onMounted(() => {
  loadData();
});

/**
 * 提交
 */
const handleSubmit = async () =>{
  if(!props.appId || !questionContent.value){
    return;
  }
  let res :any;
  // 如果是修改
  if(oldQuestion.value?.id){
    res = await editQuestionUsingPost({
      id:oldQuestion.value?.id,
      questionContent:questionContent.value,
    });
    // 如果是新增
  }else{
    res = await addQuestionUsingPost({
      appId:props.appId,
      questionContent:questionContent.value
    });
  }
  if(res.data.code === 0) {
    Message.success("操作成功，即将跳转到应用详情");
    setTimeout(()=>{
      router.push(`/app/detail/${props.appId}`)
    },3000)
  }else {
    Message.error("操作失败，" + res.data.message)
  }
}

/**
 * AI 生成题目成功后执行
 */
const onAiGenerateSuccess = (result: API.QuestionContentDTO[]) =>{
  message.success(`AI 生成${result.length}道题目成功！`)
  questionContent.value = [...questionContent.value, ...result];
}

</script>

<style scoped>
#userLoginPage {

}

</style>