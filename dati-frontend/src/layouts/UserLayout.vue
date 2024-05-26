<template>
  <div id="UserLayout">
    <a-layout style="height: 100vh;">
      <a-layout-header class="header">
        <a-space>
          <img class="logo" src="../assets/logo.png" />
          <div class="title">达题圈 AI 答题应用平台</div>
        </a-space>
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

</script>

<style scoped>
#UserLayout {
  text-align: center;
  background: url("https://gw.alipayobjects.com/zos/rmsportal/FfdJeJRQWjEeGTpqgBKj.png") 0% 0% / 100% 100%;

}

.logo {
  height: 48px;
}

.header {
  margin-top: 16px;
  font-size: 21px;
}

.content {
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

}
</style>