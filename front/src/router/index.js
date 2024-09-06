import { createRouter, createWebHistory } from 'vue-router'
import BasicLayout from "@/layouts/BasicLayout.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: BasicLayout,
      children: [
        {
          path: "",
          name: "HomePage",
          component: () => import("@/views/home/IndexPage.vue"),
          meta: { name: "首页概览页面" }
        },
        {
          path: "uploadAndDownload",
          name: "UploadAndDownload",
          component: () => import("@/views/file/IndexPage.vue"),
          meta: { name: "文件上传与下载", desc: "" }
        },
        {
          path: "fetchLearn",
          name: "FetchLearn",
          component: () => import("@/views/fetch/fetchIndex.vue"),
          meta: { name: "fetch 学习", desc: "" }
        },
      ]
    }
  ]
})

export default router
