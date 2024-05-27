<template>
  <a-form :model="formSearchParams" :style="{ marginBottom: '20px'}" @submit="doSearch"  label-align="left" layout="inline" auto-label-width >
    <a-form-item field="userAccount" label="用户名">
      <a-input
        v-model="formSearchParams.userName"
        placeholder="请输入用户名..."
      />
    </a-form-item>
    <a-form-item field="userProfile" label="用户简介">
      <a-input
        v-model="formSearchParams.userProfile"
        placeholder="请输入用户简介..."
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
    <template #userAvatar="{ record }">
      <a-image width="64" :src="record.userAvatar"/>
    </template>
    <template #optional="{ record }">
      <a-button status="danger" @click="doDelete(record)">删除</a-button>
    </template>
  </a-table>
</template>

<script setup lang="ts">
import { ref, watchEffect } from "vue";
import { deleteUserUsingPost, listUserByPageUsingPost } from "@/api/userController";
import { Message } from "@arco-design/web-vue";

// 初始化搜索条件
const initSearchParams = {
  current: 1,
  pageSize: 10,
}

const searchParams = ref<API.UserQueryRequest>({
  ...initSearchParams,
})

const formSearchParams =  ref<API.UserQueryRequest>({})

const dataList = ref<API.User[]>([])
const total = ref<number>(0)

const loadData = async () =>{
  const res = await listUserByPageUsingPost(searchParams.value)
  if(res.data.code === 0){
    dataList.value = res.data.data?.records || [];
    total.value = res.data.data?.total || 0;
  }else {
    Message.error("获取数据失败，" + res.data.message)
  }
}

/**
 * 删除用户
 * @param record
 */
const doDelete = async (record:API.User) =>{
  const res = await deleteUserUsingPost({
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
    title: '用户账号',
    dataIndex: 'userAccount',
  },
  {
    title: '用户昵称',
    dataIndex: 'userName',
  },
  {
    title: '用户头像',
    dataIndex: 'userAvatar',
    slotName: 'userAvatar',
  },
  {
    title: '用户简介',
    dataIndex: 'userProfile',
  },
  {
    title: '用户角色',
    dataIndex: 'userRole',
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
  },
  {
    title: '更新时间',
    dataIndex: 'updateTime',
  },
  {
    title: '操作',
    dataIndex: 'optional',
    slotName: 'optional',
  },
];
</script>


<style scoped>
#adminUserPage {

}

</style>