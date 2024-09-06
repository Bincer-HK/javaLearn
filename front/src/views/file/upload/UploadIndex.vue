<template>
  <div class="py-2 leading-normal text-base">
    <div class="flex items-center">
      文件上传的界面各种各样，原生实现的话，就搞定一个input就行。
      <el-tooltip placement="top">
        <template #content>
          原生的input type="file"本身就能实现拖拽文件。<br/>
          所以你可以借助 input 给它设置opacity：0透明，再使用其他标签样式写丰富的上传样式。
        </template>
        <el-icon color="#7f7f7f" size="14"><InfoFilled /></el-icon>
      </el-tooltip>
    </div>

    <div class="mt-4 border-dashed border border-indigo-500 rounded p-2 min-h-fit">
      <form>
        <div class="text-sm text-indigo-300">
          ★示例：使用原生“input”上传一张图片。本地显示读取图片的数据（使用FileReader）。
        </div>
        <div class="mt-4 flex items-center">
          <label class="block">
            <span class="sr-only">Choose profile photo</span>
            <input type="file"
               class="block w-full text-sm text-slate-500
                file:mr-4 file:py-2 file:px-4
                file:rounded-full file:border-0
                file:text-sm file:font-semibold
                file:bg-violet-50 file:text-violet-700
                hover:file:bg-violet-100
              "
               accept="image/png, image/jpeg"
               @change="fileChange"
            />
          </label>
          <el-link class="ml-8" @click="cancelFileUpload" type="warning" title="默认axios的取消，fetch也支持取消，需查看，请更改上传与取消方法">取消上传</el-link>
        </div>
        <!-- 选择后显示预览图 -->
        <div class="py-4" v-if="imgRes">
          <div class="pb-4">
            <span>当前文件真实上传进度：{{ imgUploadProgress }}% </span>
            <el-tooltip placement="top">
              <template #content>
                因 fetch 对于上传单个文件单个请求的进度监控的不支持，改用 xrh 的请求方式，获取progress。<br/>
                对于不需要进度的，fetch也可以支持上传的。
              </template>
              <el-icon color="#7f7f7f" size="14"><InfoFilled /></el-icon>
            </el-tooltip>
          </div>
          <div>
            <img class="shadow-md" :src="imgRes" alt="上传后的图片">
          </div>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { InfoFilled } from "@element-plus/icons-vue";
import { post } from "@/utils/request/fetch.js";
import axios from "@/utils/request/axios.js";
import {CancelToken} from "axios";

const props = defineProps({
  requestType: {
    type: String,
    default: "fetch"
  }
})

const imgRes = ref(null)
const imgUploadProgress = ref(0)

const fileChange = (e) => {
  // 单个文件
  const file = e.target.files[0];

  // 显示预览图，直接用 FileReader 去读选择的图片文件
  const reader = new FileReader();
  reader.onload = e => {
    imgRes.value = e.target.result;
  }
  reader.readAsDataURL(file); // 异步事件，用onload去监听一下，就能拿到结果。

  // 上传
  if (props.requestType === 'fetch') {
    // uploadUseFetch(file); // 无进度监控，fetch方式，如要看进度，改用下行方法
    uploadUseAxios(file); // axios的上传进度
  } else {
    uploadUseAxios(file);
  }
}

let fetchController;
/**
 * 文件上传【fetch】，不支持进度监控，如无需进度监控可用，否则推荐 xrh的请求方式。
 * @param file 单个文件
 */
// eslint-disable-next-line
function uploadUseFetch (file) {
  fetchController && fetchController.abort();

  // 创建控制器
  fetchController = new AbortController();
  post("/mbapi/v1/files/upload", { file }, false, {
    signal: fetchController.signal
  }).then(res => {
    // 返回结果！
    console.log(res);
  }).catch(error => {
    // 如果不进行捕获，则会报错：DOMException: signal is aborted without reason，请求还是在进行中！！！
    // fetch 本身并不提供直接的取消机制，因此如果不捕获请求，就无法有效地取消它。
    if (error.name === 'AbortError') {
      console.log('请求被取消');
    } else {
      console.error('请求失败:', error);
    }
  });
}

let cancelUploadRequest;
/**
 * 文件上传【XHR】，带上传进度的。
 * 原生XHR就有上传进度 upload.progress 的方法，以及取消请求的 abort 的方法。axios封装了XHR，只是使用起来方便一些。
 * @param file 单个文件
 */
function uploadUseAxios (file) {
  const formData = new FormData();
  formData.append("file", file);

  axios({
    method: "post",
    url: "/mbapi/v1/files/upload",
    data: formData,
    // 上传进度
    onUploadProgress: (progressEvent) => {
      imgUploadProgress.value = Math.floor(progressEvent.loaded / progressEvent.total * 100);
    },
    // 取消请求
    cancelToken: new CancelToken((c) => {
      cancelUploadRequest = c;
    })
  }).then(res => {
    // 返回结果！
    console.log(res);
  })
}

const cancelFileUpload = () => {
  if (props.requestType === 'fetch') {
    // fetchController && fetchController.abort(); // fetch的取消请求
    cancelUploadRequest(); // axios 的取消请求
  } else {
    cancelUploadRequest(); // axios 的取消请求
  }
}

</script>

<style scoped>

</style>