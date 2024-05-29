<template>
  <div id="appDetailPage">
  <a-card>
    <a-row style="margin-bottom: 16px">
      <a-col flex="auto" class="content-wrapper">
        <h2>{{ data.appName }}</h2>
        <p>{{ data.appDesc }}</p>
        <p>应用类型：{{ APP_TYPE_MAP[data.appType] }}</p>
        <p>评分策略：{{ APP_SCORING_STRATEGY_MAP[data.scoringStrategy] }}</p>
        <p>
          <a-space>
            作者：
            <div :style="{ display: 'flex', alignItems: 'center' }">
              <a-avatar
                :size="24"
                :image-url="data.user?.userAvatar"
                :style="{ marginRight: '8px' }"
              />
              <a-typography-text
              >{{ data.user?.userName ?? "无名" }}
              </a-typography-text>
            </div>
          </a-space>
        </p>
        <p>
          创建时间：{{ dayjs(data.createTime).format("YYYY-MM-DD HH:mm:ss") }}
        </p>
        <a-space size="medium">
          <a-button v-if="data.reviewStatus === REVIEW_STATUS_ENUM.PASS" type="primary" :href="`/answer/do/${id}`"
          >开始答题</a-button
          >
          <a-button>分享应用</a-button>
          <a-button v-if="isMy" :href="`/add/question/${id}`"
          >设置题目
          </a-button>
          <a-button v-if="isMy" :href="`/add/scoring_result/${id}`"
          >设置评分
          </a-button>
          <a-button v-if="isMy" :href="`/add/app/${id}`">修改应用</a-button>
        </a-space>
      </a-col>
      <a-col flex="320px">
        <a-image width="100%" :src="data.appIcon" />
      </a-col>
    </a-row>
  </a-card>
</div>

</template>

<script setup lang="ts">

import { computed, onMounted, ref } from "vue";
import { Message } from "@arco-design/web-vue";
import { getAppVoByIdUsingGet } from "@/api/appController";
import { APP_SCORING_STRATEGY_MAP, APP_TYPE_MAP, REVIEW_STATUS_ENUM, REVIEW_STATUS_MAP } from "../../constant/app";
import dayjs from "dayjs";
import { useLoginUserStore } from "@/store/userStore";

interface Props {
  id: string;
}

const props = withDefaults(defineProps<Props>(), {
  id: () => {
    return "";
  },
});

const data = ref<API.AppVO>({})

// 获取登录用户
const loginUserStore = useLoginUserStore();
let loginUserId = loginUserStore.loginUser?.id;
// 是否为本人创建
const isMy = computed(() => {
  return loginUserId && loginUserId === data.value.userId;
});

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
  if(res.data.code === 0){
    data.value = res.data.data || {};
  }else {
    Message.error("获取数据失败，" + res.data.message)
  }
}

onMounted(() => {
  loadData();
});

</script>


<style scoped>
#appDetailPage{

}

#appDetailPage .content-wrapper > * {
  margin-bottom: 24px;
}

</style>