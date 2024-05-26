import { createRouter, createWebHistory, RouteRecordRaw } from "vue-router";
import Hello from "@/components/Hello.vue";
import Hi from "@/components/Hi.vue";
import UserLayout from "@/layouts/UserLayout.vue";

const routes: Array<RouteRecordRaw> = [
  {
    path: "/hello",
    name: "Hello",
    component: Hello,
  },

  {
    path: "/hi",
    name: "Hi",
    component: Hi,
  },{
    path: "/user",
    name: "用户",
    component: UserLayout,
    children: [
      {
        path: "/user/login",
        name: "用户登录",
        component: Hi,
      },
      {
        path: "/user/register",
        name: "用户注册",
        component: Hi,
      },
    ]
  },


];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
});

export default router;
