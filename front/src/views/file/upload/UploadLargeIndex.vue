<template>
  <div>
    <div class="text-sm">大文件上传，主要在于前端怎么把文件切分成小文件与计算文件md5。文件切片用 <span class="text-red-300 font-bold">file.slice()</span>，唯一标识用spark-md5库计算hash值。后端只是暂存分片与合并分片而已。</div>
    <div class="mt-2">
      <el-upload
        action="#"
        :http-request="upload"
        :before-upload="beforeUpload"
        :show-file-list="false"
      >
        <el-button type="primary">选择上传文件</el-button>
      </el-upload>
      <el-divider content-position="left">上传列表</el-divider>

      <!-- 正在上传的文件列表 -->
      <div class="uploading" v-for="uploadFile in uploadFileList" :key="uploadFile.name">
        <span class="fileName">{{ uploadFile.name }}</span>
        <span class="fileSize">{{ formatSize(uploadFile.size) }}</span>
        <div class="parse">
          <span>解析进度：</span>
          <el-progress :text-inside="true" :stroke-width="16" :percentage="uploadFile.parsePercentage"></el-progress>
        </div>
        <div class="progress">
          <span>上传进度：</span>
          <el-progress :text-inside="true" :stroke-width="16" :percentage="uploadFile.uploadPercentage"></el-progress>
          <span v-if="(uploadFile.uploadPercentage > 0) && (uploadFile.uploadPercentage < 100)">
              <span class="uploadSpeed">{{ uploadFile.uploadSpeed }}</span>
              <el-button circle link @click="changeUploadingStop(uploadFile)">
                <el-icon v-if="uploadFile.uploadingStop === false" size="20" title="暂停上传">
                  <VideoPause/>
                </el-icon>
                <el-icon v-else size="20" title="继续上传">
                  <VideoPlay/>
                </el-icon>
              </el-button>
            </span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import SparkMD5 from "spark-md5";
import { ElMessage } from "element-plus";
import { checkFileIF, uploadChunkIF } from "@/api/file/index.js";
import {VideoPause, VideoPlay} from "@element-plus/icons-vue";

// 待上传的多个文件列表信息
const uploadFileList = ref([]);

/**
 * 计算文件的MD5值,对于大文件一次性读取比较慢，而且容易造成浏览器崩溃，因此这里采用分片读取的方式计算MD5。
 *
 * @param {file} file - 文件
 * @param uploadFile - 文件待上传的附加信息
 * @return md5码
 */
const computeMd5 = (file, uploadFile) => {
  return new Promise((resolve) => {
    const chunkTotal = 100; // 分片数
    const chunkSize = Math.ceil(file.size / chunkTotal);
    const fileReader = new FileReader();
    const md5 = new SparkMD5();
    let index = 0;
    const loadFile = (uploadFile) => {
      uploadFile.parsePercentage.value = parseInt((index / file.size) * 100);
      const slice = file.slice(index, index + chunkSize);

      fileReader.readAsArrayBuffer(slice);
    }
    loadFile(uploadFile);
    fileReader.onload = (e) => {
      md5.appendBinary(e.target.result);
      if (index < file.size) {
        index += chunkSize;
        loadFile(uploadFile);
      } else {
        // md5.end() 就是文件md5码
        resolve(md5.end());
      }
    }
  });
}
/**
 * 数字转文件大小字符（带单位）
 * @param {number} size 文件大小
 * @param pointLength 保留几位小数
 * @returns {string} 转换后的大小
 */
const formatSize = (size, pointLength = 2) => {
  let unit = null;
  const units = [" B", " K", " M", " G"];
  while ((unit = units.shift()) && size > 1024) {
    size = size / 1024;
  }
  return ((unit === "B" ? size : size.toFixed(pointLength === undefined ? 2 : pointLength)) + unit);
}

// 1. 文件上传前， el-upload自动触发
const beforeUpload = async (file) => {
  const uploadFile = {};
  uploadFile.name = file.name;
  uploadFile.size = file.size;
  uploadFile.parsePercentage = ref(0);
  uploadFile.uploadPercentage = ref(0);
  uploadFile.uploadSpeed = "0 M/s";
  uploadFile.chunkList = [];
  uploadFile.file = file;
  uploadFile.uploadingStop = false;

  // ①计算文件的md5值
  const md5 = await computeMd5(file, uploadFile);
  uploadFile.md5 = md5;

  // ②上传服务器检查，以确认是否秒传
  const res = await checkFileIF({md5: md5});
  const data = res.data;

  if(!data.isUploaded) {
    uploadFile.chunkList = data.chunkList || []
    uploadFile.needUpload = true
  } else {
    uploadFile.needUpload = false
    uploadFile.uploadPercentage.value = 100
    console.log("文件已秒传！")
    ElMessage.warning("文件已秒传！")
  }

  uploadFileList.value.push(uploadFile);
}

// 2.文件上传，替换 el-upload 的 action，自己实现上传
const upload = (xhrData) => {
  let uploadFile = null;

  for(let i = 0; i < uploadFileList.value.length; i++) {
    if ((xhrData.file.name === uploadFileList.value[i].name) && (xhrData.file.size === uploadFileList.value[i].size)) {
      uploadFile = uploadFileList.value[i];
      break
    }
  }

  if (uploadFile.needUpload) {
    console.log("========  Begin: 上传文件 =========")
    // 开始从第一片分片，上传文件
    uploadChunk(xhrData.file, 1, uploadFile);
  }
}

const defaultSplitSize = 4;
/**
 * 上传文件分片, 默认分片大小为 4 MB
 * @param file 真实文件
 * @param index 下标
 * @param uploadFile 上传的文件信息
 */
const uploadChunk = (file, index, uploadFile) => {
  const chunkSize = 1024 * 1024 * defaultSplitSize; // 4MB
  const chunkTotal = Math.ceil(file.size / chunkSize);
  if(index <= chunkTotal) {
    let startTime = new Date().valueOf();
    // ①检查当前分片，是否存在
    let exit = uploadFile.chunkList.includes(index);

    if (!exit) {
      // ②不存在，且没有点击“暂停”，继续分片上传。
      if(!uploadFile.uploadingStop) {
        const form = new FormData();
        let start = (index - 1) * chunkSize;
        let end = index * chunkSize >= file.size ? file.size : index * chunkSize;
        let chunk = file.slice(start, end);

        form.append("chunk", chunk);
        form.append("index", index);
        form.append("chunkTotal", chunkTotal);
        form.append("chunkSize", chunkSize);
        form.append("md5", uploadFile.md5);
        form.append("fileSize", file.size);
        form.append("fileName", file.name);

        const onUploadProgress = (progressEvent) => {
          let complete = (progressEvent.loaded / progressEvent.total * 100 | 0)
          console.info(`当前分片${index}: 上传进度：${complete}%`);
        }

        uploadChunkIF(form, onUploadProgress).then(() => {
          let endTime = new Date().valueOf();
          let timeDiff = (endTime - startTime) / 1000;
          console.log("分片位置" + index + " ,上传文件大小: " + formatSize(chunkSize) + ", 耗时: " + timeDiff);

          uploadFile.uploadSpeed = (10 / timeDiff).toFixed(1) + " M/s";
          uploadFile.chunkList.push(index);

          uploadFile.uploadPercentage = parseInt((uploadFile.chunkList.length / chunkTotal) * 100);

          if(index === chunkTotal) {
            console.log("========  End: 完成 =========")
            ElMessage.success("✔ 上传成功: " + file.name)
          } else {
            // 继续传，下一片文件。
            uploadChunk(file, index + 1, uploadFile);
          }
        })
      }
    } else {
      uploadFile.uploadPercentage = parseInt((uploadFile.chunkList.length / chunkTotal) * 100);

      // ②存在，继续传，下一片文件。
      uploadChunk(file, index + 1, uploadFile)
    }
  }
}

// 点击暂停或开始上传
const changeUploadingStop = (uploadFile) => {
  uploadFile.uploadingStop = !uploadFile.uploadingStop
  if (!uploadFile.uploadingStop) {
    uploadChunk(uploadFile.file, 1, uploadFile)
  }
}
</script>

<style scoped>

</style>