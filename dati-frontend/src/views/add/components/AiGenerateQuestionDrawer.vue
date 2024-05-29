<template>
  <a-button type="outline" @click="handleClick">AI 生成题目</a-button>
  <a-drawer :width="340" :visible="visible" @ok="handleOk" @cancel="handleCancel" unmountOnClose>
    <template #title>
      AI 生成题目
    </template>
    <div>
      <a-form :model="form"  @submit="handleSubmit" label-align="left" auto-label-width>
        <a-form-item label="应用 id">
          {{ appId }}
        </a-form-item>
        <a-form-item field="questionNumber" label="题目数量">
          <a-input-number min="0" max="20"
            v-model="form.questionNumber"
            placeholder="请输入生成题目数..."
          />
        </a-form-item>
        <a-form-item field="optionNumber" label="选项数量">
          <a-input-number min="0" max="6"
            v-model="form.optionNumber"
            placeholder="请输入每道题选项数量..."
          />
        </a-form-item>
        <a-form-item>
          <a-button :loading="submitting" type="primary" html-type="submit" style="width: 120px">{{ submitting ? "题目生成中" : "一键生成" }}</a-button>
        </a-form-item>
      </a-form>
    </div>
  </a-drawer>
</template>

<script setup lang="ts">
import { reactive, ref } from "vue";
import { generatorQuestionByAiUsingPost } from "@/api/questionController";
import message from "@arco-design/web-vue/es/message";

interface Props {
  appId: string;
  onSuccess?: (result: API.QuestionContentDTO[]) => void;
}

const props = withDefaults(defineProps<Props>(), {
  appId: () => {
    return "";
  },
});

const form = reactive({
  optionNumber: 2,
  questionNumber: 10,
} as API.AiGeneratorQuestionRequest);

/**
 * 提交
 */
const handleSubmit = async () =>{
  if(!props.appId){
    return;
  }
  submitting.value = true;
  const res = await generatorQuestionByAiUsingPost({
    appId: props.appId,
    ...form,
  });

  if(res.data.code === 0 && res.data.data.length > 0){
    if(props.onSuccess){
      props.onSuccess(res.data.data)
    }else{
      message.success(" AI 生成题目成功");
    }
    // 关闭抽屉
    handleCancel();
  }else{
    message.error(" AI 生成题目失败，" + res.data.message);
  }
  submitting.value = false;
}


  const visible = ref(false);
  const submitting = ref(false);

  const handleClick = () => {
    visible.value = true;
  };
  const handleOk = () => {
    visible.value = false;
  };
  const handleCancel = () => {
    visible.value = false;
  }


</script>
