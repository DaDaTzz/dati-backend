<template>
  <a-card class="appCard" hoverable @click="doCardClick">
    <template #actions>
      <span class="icon-hover"> <IconThumbUp /> </span>
      <span class="icon-hover"> <IconShareInternal /> </span>
    </template>
    <template #cover>
      <div
        :style="{
          height: '204px',
          overflow: 'hidden',
        }"
      >
        <img
          :style="{ width: '100%', transform: 'translateY(-20px)' }"
          :alt="app.appName"
          :src="app.appIcon"
        />
      </div>
    </template>
    <a-card-meta :title="app.appName" :description="app.appDesc">
      <template #avatar>
        <div
          :style="{ display: 'flex', alignItems: 'center', color: '#1D2129' }"
        >
          <a-avatar :size="24" :style="{ marginRight: '8px' } ">
            <img
              alt="avatar"
              :src="app.user?.userAvatar"
            />
          </a-avatar>
          <a-typography-text>{{ app.user?.userName ?? '无名' }}</a-typography-text>
        </div>
      </template>
    </a-card-meta>
  </a-card>
</template>

<script setup lang="ts">
import {
  IconThumbUp,
  IconShareInternal,
} from '@arco-design/web-vue/es/icon';
import { useRouter } from "vue-router";

interface props {
  app:API.AppVO,
}

const props = withDefaults(defineProps<props>(), {
  app: () => {
    return {};
  },
})

const router = useRouter()

/**
 * 跳转应用详情页
 */
const doCardClick = ()=>{
  router.push(`/app/detail/${props.app.id}`)
}

</script>
<style scoped>
.appCard {
  cursor: pointer;
}

.icon-hover {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  transition: all 0.1s;
}
.icon-hover:hover {
  background-color: rgb(var(--gray-2));
}
</style>
