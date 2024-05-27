import { createApp } from "vue";
import App from "./App.vue";
import ArcoVue from "@arco-design/web-vue";
import "@arco-design/web-vue/dist/arco.css";
import 'bytemd/dist/index.css'
import router from "@/router";
import { createPinia } from 'pinia'
import "@/access";

const pinia = createPinia()
createApp(App).use(router).use(ArcoVue).use(pinia).mount("#app");
