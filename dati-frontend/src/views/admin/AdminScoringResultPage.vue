<template>
  <a-form :model="formSearchParams" :style="{ marginBottom: '20px'}" @submit="doSearch"  label-align="left" layout="inline" auto-label-width >
    <a-form-item field="ScoringResultAccount" label="结果名称">
      <a-input
        v-model="formSearchParams.resultName"
        placeholder="请输入结果名称..."
        allow-clear
      />
    </a-form-item>
    <a-form-item field="ScoringResultProfile" label="结果描述">
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
    <template #updateTime="{ record }">
      {{ dayjs(record.updateTime).format("YYYY-MM-DD HH:mm:ss") }}
    </template>
    <template #createTime="{ record }">
      {{ dayjs(record.createTime).format("YYYY-MM-DD HH:mm:ss") }}
    </template>
    <template #optional="{ record }">
      <a-space>
        <a-button status="danger" @click="doDelete(record)">删除</a-button>
      </a-space>
    </template>
  </a-table>
</template>

<script setup lang="ts">
import { ref, watchEffect } from "vue";
import { Message } from "@arco-design/web-vue";
import dayjs from "dayjs";
import { deleteScoringResultUsingPost, listScoringResultByPageUsingPost } from "@/api/scoringResultController";


// 初始化搜索条件`
const initSearchParams = {
  current: 1,
  pageSize: 10,
}

const searchParams = ref<API.ScoringResultQueryRequest>({
  ...initSearchParams,
})

const formSearchParams =  ref<API.ScoringResultQueryRequest>({})

const dataList = ref<API.ScoringResult[]>([])
const total = ref<number>(0)

const loadData = async () =>{
  const res = await listScoringResultByPageUsingPost(searchParams.value)
  if(res.data.code === 0){
    dataList.value = res.data.data?.records || [];
    total.value = res.data.data?.total || 0;
  }else {
    Message.error("获取数据失败，" + res.data.message)
  }
}

/**
 * 删除评分
 * @param record
 */
const doDelete = async (record:API.ScoringResult) =>{
  const res = await deleteScoringResultUsingPost({
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
      title: "id",
      dataIndex: "id"
    },
    {
      title: "结果名称，如物流师",
      dataIndex: "resultName"
    },
    {
      title: "结果描述",
      dataIndex: "resultDesc"
    },
    {
      title: "结果图片",
      dataIndex: "resultPicture",
      slotName: 'resultPicture',
    },
    {
      title: "结果属性集合 JSON，如 [I,S,T,J]",
      dataIndex: "resultProp"
    },
    {
      title: "结果得分范围，如 80，表示 80及以上的分数命中此结果",
      dataIndex: "resultScoreRange"
    },
    {
      title: "应用 id",
      dataIndex: "appId"
    },
    {
      title: "创建用户 id",
      dataIndex: "userId"
    },
    {
      title: "创建时间",
      dataIndex: "createTime",
      slotName: 'updateTime',
    },
    {
      title: "更新时间",
      dataIndex: "updateTime",
      slotName: 'updateTime',
    },
  {
    title: '操作',
    dataIndex: 'optional',
    slotName: 'optional',
  },

]
</script>


<style scoped>
#adminScoringResultPage {

}

</style>