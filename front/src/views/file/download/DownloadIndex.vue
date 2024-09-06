<template>
  <div class="py-2 leading-normal text-base relative">
    <div class="absolute top-0 right-0 bg-white shadow-grey-200 shadow-md p-4 z-10" v-if="state.isDownload">
      <div class="font-bold">使用{{ props.requestType }}下载</div>
      <div class="text-blue-500 mt-2">进度: {{ state.isDownloadProgress }}%</div>
    </div>
    <div class="text-sm">
      涉及文件下载的，可以同源优先考虑a标签去下载文件，优点是由系统去存储文件，点击即可看到下载。而xhr,fetch需要接口完成后，才触发下载，大文件就会超长等待。
    </div>
    <div class="mt-4 flex">
      <el-table :data="state.tableData" border stripe class="w-full">
        <el-table-column type="index" label="序号" width="80" align="center"></el-table-column>
        <el-table-column label="类型" prop="type" width="90">
          <template #default="scope">
            {{ scope.row.type }}
          </template>
        </el-table-column>
        <el-table-column label="文件名" prop="name"></el-table-column>
        <el-table-column label="操作" width="210">
          <template #default="scope">
            <div class="flex items-center">
              <a
                class="cursor-pointer"
                style="color: var(--el-color-primary)"
                :href="`/mbapi/v1/files/download/${scope.row.fileId}`"
                :download="`${scope.row.name}.${scope.row.extension}`"
                title="同源请求，优先使用a标签下载文件"
              >
                a标签下载
              </a>
              <el-link
                class="ml-4"
                type="primary"
                :title="`使用${props.requestType}方式下载`"
                @click="downloadFile(scope.row)"
                :disabled="state.isDownload"
              >
                使用{{ props.requestType }}下载
              </el-link>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { reactive, onMounted } from "vue";
import { get } from "@/utils/request/fetch.js";
import {useALabelToDownload} from "@/utils/request/useALabelToDownload.js";
import axios from "@/utils/request/axios.js";

const props = defineProps({
  requestType: {
    type: String,
    default: "fetch"
  }
})
const state = reactive({
  tableData: [],
  isDownload: false,
  isDownloadProgress: 0
})

const getFileList = () => {
  get('/mbapi/v1/files/getFileList', {
    page: 1,
    size: 10
  }).then(res => {
    state.tableData = res.data.list;
  })
}

// 使用接口方式下载
const downloadFile = (row) => {
  state.isDownload = true;
  if (props.requestType === 'fetch') {
    get(`/mbapi/v1/files/download/${row.fileId}`, {}, {
      // 传递下载进度配置项
      downloadProgress: (val) => {
        state.isDownloadProgress = val;

        if (val === 100) {
          // 自动关闭下载进度框 5s 关闭
          setTimeout(() => {
            state.isDownload = false;
          }, 5000);
        }
      }
    } , "blob").then(res => {
      useALabelToDownload(res, `${row.name}.${row.extension}`)
    })
  } else {
    // xhr 下载进度，axios，已实现的下载配置
    axios({
      method: "get",
      url: `/mbapi/v1/files/download/${row.fileId}`,
      responseType: "blob",
      onDownloadProgress: (progressEvent) => {
        state.isDownloadProgress = Math.floor(progressEvent.loaded / progressEvent.total * 100);

        if (state.isDownloadProgress === 100) {
          // 自动关闭下载进度框 5s 关闭
          setTimeout(() => {
            state.isDownload = false;
          }, 5000);
        }
      }
    }).then(res => {
      useALabelToDownload(res, `${row.name}.${row.extension}`)
    })
  }
}

onMounted(() => {
  getFileList()
})
</script>

<style scoped>

</style>
