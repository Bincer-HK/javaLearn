/**
 * 自定义的fetch函数封装，支持请求和响应拦截。
 *
 * @param {string} url - 请求url地址。
 * @param {object} [options = {}] - 传递给fetch请求的选项。
 * @param {string} [responseType = "json"] - 预期的响应类型("json", "text", "blob"等)。
 * @returns {Promise} - 返回一个promise, 解析为请求的响应结果。
 */
export function customFetch(url, options, responseType = "json") {
  // 预设请求和响应拦截器
  const requestInterceptors = [
    config => {
      // 比如添加身份验证的逻辑
      console.log('Running request interceptors');
      return config;
    }
  ];
  const responseInterceptors = [
    response => {
      // 比如对特定的状态码做统一处理
      console.log('Running response interceptors');
      return response;
    }
  ];

  // 运行请求拦截器
  options = requestInterceptors.reduce((acc, interceptor) => interceptor(acc), options);

  // 执行fetch请求
  return fetch(url, options)
    .then(async response => {
      // 运行响应拦截器
      response = responseInterceptors.reduce((acc, interceptor) => interceptor(acc), response);
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      if (responseType === 'blob') {
        const total = response.headers.get('Content-Length')
        const reader = response.body.getReader()
        let loaded = 0
        let chunks = []
        // eslint-disable-next-line no-constant-condition
        while (true) {
          const { done, value } = await reader.read()
          if(done) {
            break;
          }
          chunks.push(value)
          loaded += value?.length || 0
          const progress = Math.floor(loaded / total * 100)
          options.downloadProgress && options.downloadProgress(progress)
        }

        return chunks;
      } else if(responseType === 'text') {
        return response.text();
      } else if(responseType === 'formData') {
        return response.formData();
      } else if(responseType === 'arrayBuffer') {
        return response.arrayBuffer();
      } else {
        return response.json();
      }
    })
    .catch(error => {
      console.error('Fetch Error:', error);
      throw error;
    });
}

/**
 * 把 对象 转换为 URL查询字符串。
 * @param {Object} params - 需要转换的参数对象。
 * @returns {string} - 从参数对象创建的查询字符串。
 */
function toQueryString (params) {
  const esc = encodeURIComponent;
  return Object.keys(params)
    .map(k => esc(k) + '=' + esc(params[k]))
    .join('&');
}

/**
 * 封装 GET 请求。
 *
 * @param {string} url - 请求url地址。
 * @param {Object} [params = {}] - get请求的参数对象，默认{}。
 * @param {Object} [options = {}] - fetch 请求的选项对象。
 * @param {string} [responseType = "json"] - 预期的响应类型，默认 "json"。
 * @returns {Promise} - 返回一个promise, 解析为请求的响应结果。
 */
export function get(url, params = {}, options = {}, responseType = "json") {
  if (params && Object.keys(params).length) {
    url += (url.indexOf("?") === -1 ? "?" : "&") + toQueryString(params)
  }

  return customFetch(url, { ...options, method: "GET" }, responseType)
}


/**
 * 封装 POST 请求。可兼顾文件上传（无上传进度）。
 * 注意：若要实现 FormData 传文件，且需要进度条时，请改用XHR的上传进度方式，
 *
 * @param {string} url - 请求url地址。
 * @param {Object|FormData|File} [data = {}] - 需要POST的数据对象或FormData对象或直接是File对象。
 * @param {boolean} [json = true] - 如果为true，则发送JSON；为false，则发送FormData。
 * @param {Object} [options = {}] - fetch请求的选项对象。
 * @param {string} [responseType = "json"] - 预期的响应类型，默认 "json"。
 * @returns {Promise} - 返回一个promise，解析为请求的响应结果。
 */
export function post(url, data = {}, json = true, options = {}, responseType = "json") {
  const headers = new Headers(options.headers || {});

  let bodyData;
  if (json) {
    // 当json为true时，设置Content-Type头部为application/json，并将数据转换成JSON字符串
    headers.append('Content-Type', 'application/json');
    bodyData = JSON.stringify(data);
  } else {
    if (data instanceof File) {
      bodyData = data;
    } else {
      // 当json为false时，将对象转换为FormData
      bodyData = new FormData();
      for (const [key, value] of Object.entries(data)) {
        bodyData.append(key, value);
      }
    }
  }

  return customFetch(
    url,
    {
      ...options,
      method: 'POST',
      headers,
      body: bodyData,
    },
    responseType
  );
}

/**
 * 文件下载，支持 GET、POST。
 * 注：GET类型的下载，建议直接使用a标签的方式下载，使用系统文件自动保存。
 *
 * @param {string} url - 请求url地址。
 * @param {Object} [data = {}] - 如果是POST请求，这里是请求体的数据对象；如果是GET请求，这里是查询参数对象。
 * @param {string} [fileName = "file"] - 下载文件时希望使用的文件名。
 * @param {string} [method = "GET"] - 请求使用的HTTP方法（通常是"GET"或"POST"）。
 */
export function download(url, data, fileName, method = 'GET') {
  method = method.toUpperCase();
  if (method === 'GET') {
    if (data && Object.keys(data).length) {
      url += (url.indexOf('?') === -1 ? '?' : '&') + toQueryString(data);
    }
  }

  // 根据请求类型设置options
  const options = method === 'POST' ? {
    method: method,
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(data)
  } : {
    method: method
  };

  customFetch(url, options, 'blob')
    .then(blob => {
      // 创建blob链接
      const url = window.URL.createObjectURL(blob);
      // 创建a标签用于触发下载
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', fileName || 'file');
      document.body.appendChild(link);
      link.click();
      // 清除
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);
    }).catch(error => {
    console.error('Download Error:', error);
  });
}



// /**
//  * 文件类的请求
//  * @param {String} url 请求地址
//  * @param {Object} data 请求数据
//  * @param {String} type 请求方法
//  * @param {Function} cb 回调函数
//  * @returns Promise
//  */
// export default async (url = '', data = {}, type = 'GET', cb) => {
//   type = type.toUpperCase()
//
//   if (type === 'GET') {
//     let dataStr = qs(data)
//     if (dataStr !== '') {
//       dataStr = dataStr.substring(0, dataStr.lastIndexOf('&'))
//       url = url + dataStr
//     }
//   }
//
//   let requestConfig = {
//     credentials: 'same-origin', // 为了在当前域名内自动发送 cookie，必须提供这个选项
//     method: type,
//     headers: {
//       'Content-Type': 'application/octet-stream'
//     },
//     mode: 'cors', // 请求的模式，是否跨域的设置。如 cors、no-cors 或者 same-origin。
//     cache: 'default', // 是否缓存请求资源， default、 no-store、 reload 、 no-cache、 force-cache 或者 only-if-cached
//   }
//
//   // if (type === 'POST') {
//   //   Object.defineProperties(requestConfig, 'body', {
//   //     value: JSON.stringify(data)
//   //   })
//   // }
//
//   try{
//     const res = await fetch(url, requestConfig)
//     const total = res.headers.get('Content-Length')
//     const reader = res.body.getReader()
//     let loaded = 0
//     let chunks = []
//     // eslint-disable-next-line no-constant-condition
//     while (true) {
//       const { done, value } = await reader.read()
//       if(done) {
//         break;
//       }
//       chunks.push(value)
//       loaded += value?.length || 0
//       const progress = Math.floor(loaded / total * 100)
//       cb && cb(progress)
//     }
//
//     // 下载的一个示例
//     const a = document.createElement("a")
//     a.style.display = 'none'
//     document.body.append(a)
//     const urlTemp = window.URL.createObjectURL(new Blob(chunks, { type: "application/octet-stream"} ))
//     a.href = urlTemp
//     a.download = 'res.png'
//     a.click()
//     document.body.removeChild(a)
//     window.URL.revokeObjectURL(urlTemp)
//
//     // 返回请求文件的结果
//     // const responseBlob = await res.blob()
//     // return responseBlob
//
//   } catch (error) {
//     throw new Error(error)
//   }
// }
//


// // eslint-disable-next-line
// function trackUploadProgress(url, file) {
//   return new Promise((resolve, reject) => {
//     const reader = file.stream().getReader();
//     const totalSize = file.size;
//     let uploadedSize = 0;
//
//     fetch(url, {
//       method: 'POST',
//       body: file
//     }).then(response => {
//       if (!response.ok) {
//         throw new Error('Upload failed');
//       }
//       resolve(response);
//     }).catch(error => {
//       reject(error);
//     });
//
//     reader.read().then(function processChunk({ done, value }) {
//       if (done) {
//         return;
//       }
//
//       uploadedSize += value.length;
//       const progress = (uploadedSize / totalSize) * 100;
//       console.log(`Upload progress: ${progress.toFixed(2)}%`);
//
//       return reader.read().then(processChunk);
//     });
//   });
// }

// // 使用示例
// const fileInput = document.getElementById('fileInput');
// const uploadButton = document.getElementById('uploadButton');

// uploadButton.addEventListener('click', () => {
//   const file = fileInput.files[0];
//   const uploadUrl = 'http://example.com/upload';
//
//   trackUploadProgress(uploadUrl, file).then(response => {
//     console.log('Upload completed:', response);
//   }).catch(error => {
//     console.error('Upload failed:', error);
//   });
// });
