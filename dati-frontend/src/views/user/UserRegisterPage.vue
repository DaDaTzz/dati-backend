<template>
  <div id="userRegisterPage">
    <h2 style="margin-bottom: 16px">注册</h2>
    <a-form :model="form" :style="{ width: '480px', margin:'0 auto' }" @submit="handleSubmit" label-align="left" auto-label-width>
      <a-form-item field="userAccount" label="账号">
        <a-input
          v-model="form.userAccount"
          placeholder="请输入账号..."
        />
      </a-form-item>
      <a-form-item field="userPassword" tooltip="密码不小于 8 位" label="密码">
        <a-input-password v-model="form.userPassword" placeholder="请输入密码..." />
      </a-form-item>
      <a-form-item field="checkPassword" tooltip="两次输入的密码必须相同" label="确认密码">
        <a-input-password v-model="form.checkPassword" placeholder="请确认密码..." />
      </a-form-item>
      <a-form-item>
        <div style="display: flex;width: 100%; align-items: center; justify-content: space-between">
          <a-button type="primary" html-type="submit" style="width: 120px">注册</a-button>
          <a-link @click="() => router.push('/user/login')">老用户登录</a-link>
        </div>
      </a-form-item>
    </a-form>
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue';
import { userLoginUsingPost, userRegisterUsingPost } from "@/api/userController";
import { useLoginUserStore } from "@/store/userStore";
import { Message } from "@arco-design/web-vue";
import { useRouter } from "vue-router";

const loginUserStore = useLoginUserStore()
const router = useRouter()

const form = reactive({
  userAccount: '',
  userPassword: '',
  checkPassword: '',
} as API.UserRegisterRequest);

/**
 * 提交
 */
const handleSubmit = async () =>{
  const res = await userRegisterUsingPost(form);
  if(res.data.code === 0) {
    Message.success("注册成功");
    router.push({
      path:'/user/login',
      replace: true,
    })
  }else {
    Message.error("注册失败，" + res.data.message)
  }
}
</script>

<style scoped>
#userLoginPage {

}

</style>