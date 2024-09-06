import axios from "@/utils/request/axios.js";

/**
 * 检查服务器上，文件是否上传完或已上传的分片
 * @param {object} params - 传参信息
 * @returns {Promise<axios.AxiosResponse<any>>}
 */
export function checkFileIF(params) {
  return axios.get("/mbapi/v1/files/check", {
    params
  })
}

/**
 * 上传分片
 * @param data 分片信息
 * @param onUploadProgress 上传进度
 * @returns {Promise<AxiosResponse<any>> | *}
 */
export function uploadChunkIF(data, onUploadProgress) {
  return axios({
    method: "post",
    url: "/mbapi/v1/files/chunk",
    data,
    onUploadProgress
  })
}