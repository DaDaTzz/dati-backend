import {  ref } from "vue";
import { defineStore } from "pinia";
import { getLoginUserUsingGet } from "@/api/userController";

/**
 * 登录用户信息全局状态
 */
export const useLoginUserStore = defineStore("loginUser", () => {
  const loginUser = ref<API.LoginUserVO>({
    userName: "未登录",
  });

  function setLoginUser(newLoginUser: API.LoginUserVO) {
    loginUser.value = newLoginUser;
  }

  async function fetchLoginUser() {
    const res = await getLoginUserUsingGet();
    if (res.data.code === 0 && res.data.data) {
      loginUser.value = res.data.data;
    } else {
      setTimeout(() => {
        loginUser.value = { userName: "测试用户", id:1 };
      }, 3000)
    }
  }

  return { loginUser, setLoginUser, fetchLoginUser };
});