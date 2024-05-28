<template>
  <a-form :model="formSearchParams" :style="{ marginBottom: '20px'}" @submit="doSearch"  label-align="left" layout="inline" auto-label-width >
    <a-form-item field="questionContent" label="题目内容">
      <a-input
        v-model="formSearchParams.questionContent"
        placeholder="请输入题目内容..."
        allow-clear
      />
    </a-form-item>
    <a-form-item field="QuestionProfile" label="应用id">
      <a-input
        v-model="formSearchParams.appId"
        placeholder="请输入题目id..."
        allow-clear
      />
    </a-form-item>
      <a-form-item field="QuestionProfile" label="用户id">
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
    <template #questionContent="{ record }">
      <div v-for="question in JSON.parse(record.questionContent)" :key="record.id">
        {{ question }}
      </div>
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
import { deleteQuestionUsingPost, listQuestionByPageUsingPost } from "@/api/questionController";


// 初始化搜索条件`
const initSearchParams = {
  current: 1,
  pageSize: 10,
}

const searchParams = ref<API.QuestionQueryRequest>({
  ...initSearchParams,
})

const formSearchParams =  ref<API.QuestionQueryRequest>({})

const dataList = ref<API.Question[]>([])
const total = ref<number>(0)

const loadData = async () =>{
  const res = await listQuestionByPageUsingPost(searchParams.value)
  if(res.data.code === 0){
    dataList.value = res.data.data?.records || [];
    total.value = res.data.data?.total || 0;
  }else {
    Message.error("获取数据失败，" + res.data.message)
  }
}

/**
 * 删除题目
 * @param record
 */
const doDelete = async (record:API.Question) =>{
  const res = await deleteQuestionUsingPost({
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
    title: "题目内容（json格式）",
    dataIndex: "questionContent",
    slotName: "questionContent",
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
    slotName: 'createTime',
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
];
</script>


<style scoped>
#adminQuestionPage {

}

</style>