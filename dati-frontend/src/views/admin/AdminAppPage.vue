<template>
  <a-form :model="formSearchParams" :style="{ marginBottom: '20px'}" @submit="doSearch"  label-align="left" layout="inline" auto-label-width >
    <a-form-item field="AppAccount" label="应用名">
      <a-input
        v-model="formSearchParams.appName"
        placeholder="请输入应用名..."
        allow-clear
      />
    </a-form-item>
    <a-form-item field="AppProfile" label="应用简介">
      <a-input
        v-model="formSearchParams.appDesc"
        placeholder="请输入应用简介..."
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
    <template #appIcon="{ record }">
      <a-image width="64" :src="record.appIcon"/>
    </template>
    <template #appType="{ record }">
      {{ APP_TYPE_MAP[record.appType] }}
    </template>
    <template #scoringStrategy="{ record }">
      {{ APP_SCORING_STRATEGY_MAP[record.appType] }}
    </template>
    <template #reviewStatus="{ record }">
      {{  REVIEW_STATUS_MAP[record.reviewStatus]}}
    </template>
    <template #reviewTime ="{ record }">
      {{record.reviewStatus === 1 ? dayjs(record.reviewTime).format("YYYY-MM-DD HH:mm:ss") : ''}}
    </template>
    <template #updateTime="{ record }">
      {{ dayjs(record.updateTime).format("YYYY-MM-DD HH:mm:ss") }}
    </template>
    <template #createTime="{ record }">
      {{ dayjs(record.createTime).format("YYYY-MM-DD HH:mm:ss") }}
    </template>
    <template #optional="{ record }">
      <a-space>
        <a-button v-if="record.reviewStatus !== REVIEW_STATUS_ENUM.PASS" status='success' @click="doReview(record, REVIEW_STATUS_ENUM.PASS, '')">通过</a-button>
        <a-button v-else status='warning' @click="doReview(record, REVIEW_STATUS_ENUM.REJECT, '不符合上架要求') ">拒绝</a-button>
        <a-button status="danger" @click="doDelete(record)">删除</a-button>
      </a-space>
    </template>
  </a-table>
</template>

<script setup lang="ts">
import { ref, watchEffect } from "vue";
import { Message } from "@arco-design/web-vue";
import dayjs from "dayjs";
import {
  deleteAppUsingPost,
  doAppReviewUsingPost,
  listAppByPageUsingPost,
} from "@/api/appController";
import {
  APP_SCORING_STRATEGY_MAP,
  APP_TYPE_MAP, REVIEW_STATUS_ENUM,
  REVIEW_STATUS_MAP
} from "../../constant/app";

// 初始化搜索条件`
const initSearchParams = {
  current: 1,
  pageSize: 10,
}

const searchParams = ref<API.AppQueryRequest>({
  ...initSearchParams,
})

const formSearchParams =  ref<API.AppQueryRequest>({})

const dataList = ref<API.App[]>([])
const total = ref<number>(0)

const loadData = async () =>{
  const res = await listAppByPageUsingPost(searchParams.value)
  if(res.data.code === 0){
    dataList.value = res.data.data?.records || [];
    total.value = res.data.data?.total || 0;
  }else {
    Message.error("获取数据失败，" + res.data.message)
  }
}

/**
 * 删除应用
 * @param record
 */
const doDelete = async (record:API.App) =>{
  const res = await deleteAppUsingPost({
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
 * 审核
 * @param record
 * @param reviewStatus
 * @param reviewMessage
 */
const doReview = async (record:API.App, reviewStatus:number, reviewMessage:string) =>{
  if(!record.id){
    return;
  }
  const res = await doAppReviewUsingPost({
    id:record.id,
    reviewStatus,
    reviewMessage,
  })
  if(res.data.code === 0){
    loadData();
  } else {
    Message.error("审核失败，" + res.data.message)
  }
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
    title: 'id',
    dataIndex: 'id',
  },
  {
    title: '应用名',
    dataIndex: 'appName',
  },
  {
    title: '应用描述',
    dataIndex: 'appDesc',
  },
  {
    title: '应用图标',
    dataIndex: 'appIcon',
    slotName:'appIcon',
  },
  {
    title: '应用类型（0-得分类，1-测评类）',
    dataIndex: 'appType',
    slotName:'appType',
  },
  {
    title: '评分策略（0-自定义，1-AI）',
    dataIndex: 'scoringStrategy',
    slotName:'scoringStrategy',
  },
  {
    title: '审核状态：0-待审核, 1-通过, 2-拒绝',
    dataIndex: 'reviewStatus',
    slotName:'reviewStatus',
  },
  {
    title: '审核信息',
    dataIndex: 'reviewMessage',
  },
  {
    title: '审核人 id',
    dataIndex: 'reviewerId',
  },
  {
    title: '审核时间',
    dataIndex: 'reviewTime',
    slotName:'reviewTime',
  },
  {
    title: '创建用户 id',
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
  {
    title: '操作',
    dataIndex: 'optional',
    slotName: 'optional',
  },
];
</script>


<style scoped>
#adminAppPage {

}

</style>