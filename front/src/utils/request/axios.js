import axios from "axios";

const request = axios.create({
  baseURL: "",
  timeout: 0, // 0：无超时时间，1000 = 1秒
  withCredentials: true
});

request.interceptors.request.use(
  config => config,
  error => Promise.reject(error)
)

request.interceptors.response.use(
  response => Promise.resolve(response.data),
  error => Promise.reject(error)
)

export default request;