/**
 * 使用A标签去下载文件。
 *
 * @param {string|array|blob} data - [必传]下载链接或fetch 接口的array 或 axios接口返回的 blob数据。
 * @param {string} fileName - [必传]文件名带后缀。
 */
export function useALabelToDownload(data, fileName) {
  let url = '';
  if (typeof data !== 'string') {
    url = window.URL.createObjectURL(new Blob(Array.isArray(data) ? data : [data], { type: "application/octet-stream" }));
  } else {
    url = data;
  }

  const link = document.createElement('a');
  link.style.display = 'none';
  link.href = url;
  link.download = `${fileName}`;
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
}