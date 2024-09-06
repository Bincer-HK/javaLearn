<template>
  <div class="p-4">
    <div class="flex items-center">
      <div class="mr-4" title="点击，去了解fetch相关信息">
        <el-link type="info" @click="toFetchPage">请求类型：</el-link>
      </div>
      <div>
        <el-button :type="requestType === 'fetch' ? 'primary' : 'default'" @click="changeReqType('fetch')">使用fetch方式</el-button>
        <el-button :type="requestType === 'xhr' ? 'primary' : 'default'" @click="changeReqType('xhr')">使用XHR方式的axios</el-button>
      </div>
    </div>
    <div class="flex mt-2 p-2 bg-gray-50">
      <div class="w-1/2 pr-2">
        <div class="font-bold text-lg">上传单个文件</div>
        <upload-index :requestType="requestType" class="mt-2" />

        <div class="font-bold text-lg mt-4 border-t-4 border-indigo-500 pt-2">上传大文件分片</div>
        <upload-large-index class="mt-2" />
      </div>

      <div class="w-1/2 pl-2">
        <div class="font-bold text-lg">下载单个文件</div>
        <download-index :requestType="requestType" class="mt-2" />

        <div class="font-bold text-lg mt-4 border-t-4 border-indigo-500 pt-2">上传大文件分片</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import UploadIndex from "@/views/file/upload/UploadIndex.vue";
import DownloadIndex from "@/views/file/download/DownloadIndex.vue";
import UploadLargeIndex from "@/views/file/upload/UploadLargeIndex.vue";

const router = useRouter();

const requestType = ref('fetch');
const changeReqType = (str) => {
  requestType.value = str;
}

const toFetchPage = () => {
  router.push({
    path: "/fetchLearn"
  })
}
</script>

<style lang="scss" scoped>

</style>