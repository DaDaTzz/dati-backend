<template>
  <div id="basicLayout">
    <a-layout style="height: 100vh;">
      <a-layout-header class="header">
        <GlobalHeader />
      </a-layout-header>
      <a-layout-content class="content">
        <router-view />
      </a-layout-content>
      <a-layout-footer class="footer">
        <a href="https://github.com/DaDaTzz" target="_blank" >欢迎关注程序员 Da 的日常！</a>
      </a-layout-footer>
    </a-layout>
  </div>
</template>

<script setup lang="ts">

import GlobalHeader from "@/components/GlobalHeader.vue";
</script>

<style scoped>
#basicLayout {

}

.header {
  margin-bottom: 16px;
  box-shadow: #eee 1px 1px 5px;
}

.content {
  background: linear-gradient(to right, #fefefe, #fff);
  margin-bottom: 28px;
  padding: 20px;
}

.footer {
  background: #efefef;
  padding: 16px;
  text-align: center;

  a {
    text-decoration: none;
  }

  .menu-demo {
    box-sizing: border-box;
    width: 100%;
    padding: 40px;
    background-color: var(--color-neutral-2);
  }
}
</style>