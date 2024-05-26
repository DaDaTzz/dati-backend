<template>
  <div id="app">
    <template v-if="route.path.startsWith('/user')">
      <router-view />
    </template>
    <template v-else>
      <BasicLayout />
    </template>
  </div>
</template>

<style scoped></style>

<script setup lang="ts">
import BasicLayout from "@/layouts/BasicLayout.vue";
import { useRoute } from "vue-router";
import { getLoginUserUsingGet } from "@/api/userController";
import { useLoginUserStore } from "@/store/userStore";

const route = useRoute();

const loginUserStore = useLoginUserStore();
loginUserStore.fetchLoginUser();



getLoginUserUsingGet().then((response) => {
  console.log(response)
})
</script>
