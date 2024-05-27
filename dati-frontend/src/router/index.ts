import { createRouter, createWebHistory, RouteRecordRaw } from "vue-router";
import Hello from "@/components/Hello.vue";
import Hi from "@/components/Hi.vue";
import UserLayout from "@/layouts/UserLayout.vue";
import NoAuthPage from "@/components/NoAuthPage.vue";
import UserLoginPage from "@/views/user/UserLoginPage.vue";
import UserRegisterPage from "@/views/user/UserRegisterPage.vue";
import ACCESS_ENUM from "@/access/accessEnum";
import AdminUserPage from "@/views/admin/AdminUserPage.vue";

const routes: Array<RouteRecordRaw> = [
  {
    path: "/noAuth",
    name: "无权限",
    component: NoAuthPage,
  },
  {
    path: "/admin/user",
    name: "用户管理",
    component: AdminUserPage,
    meta:{
      access:ACCESS_ENUM.ADMIN,
    }
  },
  {
    path: "/hello",
    name: "Hello",
    component: Hello,
  },

  {
    path: "/hi",
    name: "Hi",
    component: Hi,
  },

  {
    path: "/user",
    name: "用户",
    component: UserLayout,
    children: [
      {
        path: "/user/login",
        name: "用户登录",
        component: UserLoginPage,
      },
      {
        path: "/user/register",
        name: "用户注册",
        component: UserRegisterPage,
      },
    ]
  },


];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
});

export default router;
