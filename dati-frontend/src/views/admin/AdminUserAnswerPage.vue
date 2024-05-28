<template>
  <a-form :model="formSearchParams" :style="{ marginBottom: '20px'}" @submit="doSearch"  label-align="left" layout="inline" auto-label-width >
    <a-form-item field="UserAnswerAccount" label="结果名称">
      <a-input
        v-model="formSearchParams.resultName"
        placeholder="请输入结果描述..."
        allow-clear
      />
    </a-form-item>
    <a-form-item field="UserAnswerProfile" label="结果描述">
      <a-input
        v-model="formSearchParams.resultDesc"
        placeholder="请输入结果描述..."
        allow-clear
      />
    </a-form-item>
    <a-form-item field="ScoringResultProfile" label="应用id">
      <a-input
        v-model="formSearchParams.appId"
        placeholder="请输入应用id..."
        allow-clear
      />
    </a-form-item>
    <a-form-item field="ScoringResultProfile" label="用户id">
      <a-input
        v-model="formSearchParams.userId"
        placeholder="请输入用户id..."
        allow-clear
      />
    </a-form-item>
    <a-form-item>
        <a-button type="primary" html-type="submit" style="width: 100px">搜索</a-button>
    </a-form-item>
  </a-form>

  <a-table :columns="columns" :data="dataList" :pagination="{
    total,
    pageSize:searchParams.pageSize,
    current:searchParams.current,
    showTotal:true,
  }"
  @page-change="onPageChange"
  >
    <template #resultPicture="{ record }">
      <a-image width="64" :src="record.resultPicture"/>
    </template>
    <template #appType="{ record }">
      {{ APP_TYPE_MAP[record.appType] }}
    </template>
    <template #scoringStrategy="{ record }">
      {{ APP_SCORING_STRATEGY_MAP[record.appType] }}
    </template>
    <template #updateTime="{ record }">
      {{ dayjs(record.updateTime).format("YYYY-MM-DD HH:mm:ss") }}
    </template>
    <template #createTime="{ record }">
      {{ dayjs(record.createTime).format("YYYY-MM-DD HH:mm:ss") }}
    </template>
    <template #optional="{ record }">
      <a-button status="danger" @click="doDelete(record)">删除</a-button>
    </template>
  </a-table>
</template>

<script setup lang="ts">
import { ref, watchEffect } from "vue";
import { deleteUserAnswerUsingPost, listUserAnswerByPageUsingPost } from "@/api/userAnswerController";
import { Message } from "@arco-design/web-vue";
import dayjs from "dayjs";
import { APP_SCORING_STRATEGY_MAP, APP_TYPE_MAP } from "@/constant/app";

// 初始化搜索条件
const initSearchParams = {
  current: 1,
  pageSize: 10,
}

const searchParams = ref<API.UserAnswerQueryRequest>({
  ...initSearchParams,
})

const formSearchParams =  ref<API.UserAnswerQueryRequest>({})

const dataList = ref<API.UserAnswer[]>([])
const total = ref<number>(0)

const loadData = async () =>{
  const res = await listUserAnswerByPageUsingPost(searchParams.value)
  console.log(res)
  if(res.data.code === 0){
    dataList.value = res.data.data?.records || [];
    total.value = res.data.data?.total || 0;
  }else {
    Message.error("获取数据失败，" + res.data.message)
  }
}

/**
 * 删除用户回答
 * @param record
 */
const doDelete = async (record:API.UserAnswer) =>{
  const res = await deleteUserAnswerUsingPost({
    id:record.id,
  })
  if(res.data.code === 0){
    Message.success("删除成功")
    await loadData();
  }else{
    Message.error("删除失败，" + res.data.message)
  }
}

/**
 * 执行搜索
 */
const doSearch = async () =>{
  searchParams.value = {
    ...initSearchParams,
    ...formSearchParams.value,
  };
}



/**
 * 当分页变化时，改变搜索条件，触发数据加载
 * @param page
 */
const onPageChange = (page:number)=>{
  searchParams.value = {
    ...searchParams.value,
    current:page,
  }
}

// 监听 searchParams 变量，改变时触发数据的重新加载
watchEffect(() =>{
  loadData();
})

// 列数据配置
const columns = [
  {
    title: 'ID',
    dataIndex: 'id',
  },
  {
    title: '应用 ID',
    dataIndex: 'appId',
  },
  {
    title: '应用类型（0-得分类，1-角色测评类）',
    dataIndex: 'appType',
    slotName:'appType',
  },
  {
    title: '评分策略（0-自定义，1-AI）',
    dataIndex: 'scoringStrategy',
    slotName:'scoringStrategy',
  },
  {
    title: '用户答案（JSON 数组）',
    dataIndex: 'choices',
  },
  {
    title: '评分结果 id',
    dataIndex: 'resultId',
  },
  {
    title: '结果名称，如物流师',
    dataIndex: 'resultName',
  },
  {
    title: '结果描述',
    dataIndex: 'resultDesc',
  },
  {
    title: '结果图标',
    dataIndex: 'resultPicture',
    slotName:'resultPicture',
  },
  {
    title: '得分',
    dataIndex: 'resultScore',
  },
  {
    title: '用户 id',
    dataIndex: 'userId',
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    slotName:'createTime',
  },
  {
    title: '更新时间',
    dataIndex: 'updateTime',
    slotName:'updateTime',
  },
];

</script>


<style scoped>
#adminUserAnswerPage {

}

</style>