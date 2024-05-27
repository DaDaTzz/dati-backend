import {RouteRecordRaw } from "vue-router";
import Hello from "@/components/Hello.vue";
import Hi from "@/components/Hi.vue";
import ACCESS_ENUM from "@/access/accessEnum";
import AdminPage from "@/components/AdminPage.vue";
import NoAuthPage from "@/components/NoAuthPage.vue";
import UserLayout from "@/layouts/UserLayout.vue";
import UserLoginPage from "@/views/user/UserLoginPage.vue";
import UserRegisterPage from "@/views/user/UserRegisterPage.vue";
import AdminUserPage from "@/views/admin/AdminUserPage.vue";


export const routes: Array<RouteRecordRaw> = [
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
    path: "/noAuth",
    name: "无权限",
    component: NoAuthPage,
    meta:{
      hideInMenu: true, // 菜单栏中隐藏
    }
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
    path: "/hide",
    name: "隐藏页面",
    component: Hi,
    meta:{
      hideInMenu: true, // 菜单栏中隐藏
    }
  },

  {
    path: "/user",
    name: "用户",
    component: UserLayout,
    meta:{
      hideInMenu: true, // 菜单栏中隐藏
    },
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

