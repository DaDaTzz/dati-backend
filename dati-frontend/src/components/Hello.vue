<script setup lang="ts">
import { useLoginUserStore } from "@/store/userStore";

const loginUserStore = useLoginUserStore();

</script>

<template>
  <div>
    <h1>Hello</h1>
    {{ JSON.stringify(loginUserStore.loginUser) }}
  </div>
</template>

<style scoped>

</style>