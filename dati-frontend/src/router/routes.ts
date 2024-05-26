import {RouteRecordRaw } from "vue-router";
import Hello from "@/components/Hello.vue";
import Hi from "@/components/Hi.vue";


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
    path: "/hide",
    name: "隐藏页面",
    component: Hi,
    meta:{
      hideInMenu: true, // 菜单栏中隐藏
    }
  },

];

