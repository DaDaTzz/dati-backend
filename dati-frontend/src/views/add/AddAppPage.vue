<template>
  <div id="userLoginPage">
    <h2 style="margin-bottom: 32px">创建应用</h2>
    <a-form :model="form" :style="{ width: '480px'}" @submit="handleSubmit" label-align="left" auto-label-width>
      <a-form-item field="appName" label="应用名称">
        <a-input
          v-model="form.appName"
          placeholder="请输入应用名称..."
        />
      </a-form-item>
      <a-form-item field="appDesc" label="应用描述">
        <a-input
          v-model="form.appDesc"
          placeholder="请输入应用描述..."
        />
      </a-form-item>
      <a-form-item field="appIcon" label="应用图标">
        <a-input
          v-model="form.appIcon"
          placeholder="请输入应用描述..."
        />
      </a-form-item>
      <a-form-item field="appType" label="应用类型">
        <a-select v-model="form.appType" :style="{width:'320px'}" placeholder="请选择应用类型...">
          <a-option v-for="(value, key) of APP_TYPE_MAP" :value="Number(key)" :label="value" />
        </a-select>
      </a-form-item>
      <a-form-item field="appType" label="评分策略">
        <a-select v-model="form.scoringStrategy" :style="{width:'320px'}" placeholder="请选择评分策略...">
          <a-option v-for="(value, key) of APP_SCORING_STRATEGY_MAP" :value="Number(key)" :label="value" />
        </a-select>
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit" style="width: 120px">提交</a-button>
      </a-form-item>
    </a-form>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import { Message } from "@arco-design/web-vue";
import { useRouter } from "vue-router";
import { addAppUsingPost, editAppUsingPost, getAppVoByIdUsingGet, updateAppUsingPost } from "@/api/appController";
import { APP_SCORING_STRATEGY_MAP, APP_TYPE_MAP } from "@/constant/app";

const router = useRouter()

const form = ref({
  appDesc: "",
  appIcon: "",
  appName: "",
} as API.AppAddRequest);


interface Props {
  id: string;
}

const props = withDefaults(defineProps<Props>(), {
  id: () => {
    return "";
  },
});

const oldApp = ref<API.AppVO>()

/**
 * 提交
 */
const handleSubmit = async () =>{
  let res : any;
  // 判断是修改操作还是新增操作
  // 如果是修改
  if(props.id){
    res = await editAppUsingPost({
      id:props.id as any,
      ...form.value
    });
    // 如果是新增
  }else{
    res = await addAppUsingPost(form.value);
  }
  if(res.data.code === 0) {
    Message.success("操作成功，即将跳转到应用详情");
    setTimeout(()=>{
      router.push(`/app/detail/${props.id ?? res.data.data}`)
    },3000)
  }else {
    Message.error("操作失败，" + res.data.message)
  }
}

/**
 * 加载数据
 */
const loadData = async () =>{
  if(!props.id){
    return;
  }
  const res = await getAppVoByIdUsingGet({
    id:props.id as any,
  })
  if(res.data.code === 0 && res.data.data){
    oldApp.value = res.data.data
    form.value = oldApp.value
  }else {
    Message.error("获取数据失败，" + res.data.message)
  }
}

onMounted(() => {
  loadData();
});
</script>

<style scoped>
#userLoginPage {

}

</style>