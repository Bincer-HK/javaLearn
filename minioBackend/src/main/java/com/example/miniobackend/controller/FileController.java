package com.example.miniobackend.controller;

import cn.hutool.core.map.MapUtil;
import com.example.miniobackend.domain.Resources;
import com.example.miniobackend.service.FileService;
import com.example.miniobackend.service.ResourcesChunksService;
import com.example.miniobackend.service.ResourcesService;
import com.example.miniobackend.types.BackendConstant;
import com.example.miniobackend.types.JsonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/v1/files")
public class FileController {

    @Autowired private FileService fileService;
    @Autowired private ResourcesService resourcesService;
    @Autowired private ResourcesChunksService resourcesChunksService;

    /**
     * 【单个文件上传】
     * 前端通过 post 的 form 表单请求，MultipartFile 方式。 @RequestParam 接收。
     * 后端把文件上传 到 minio 中去。
     *
     * @param file post form 请求参数及传文件内容
     * @return Json 上传完后minio存储文件的相关信息
     * @throws IOException 上传异常
     */
    @PostMapping("/upload")
    public JsonResponse uploadFile(@RequestParam MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return JsonResponse.error("请上传文件！");
        }

        String originalFilename = file.getOriginalFilename();
        String fileOriginName = originalFilename.substring(0, originalFilename.lastIndexOf("."));
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        String type = BackendConstant.RESOURCE_EXT_2_TYPE.get(extension.toLowerCase());
        if (type == null) {
            return JsonResponse.error("该格式文件不支持上传");
        }

        HashMap<String, String> data = fileService.uploadFile(file, fileOriginName, extension, type);

        if (data == null) {
            return JsonResponse.error("文件保存失败，请重试！");
        }
        return JsonResponse.data(data);
    }

    /**
     * 【单个文件下载】
     * 下载文件，从 minion 中获取文件
     * @param fileId 上传接口返回的 fileId 的信息
     * @return 文件流，网页内可直接打开
     * @throws IOException 异常信息
     */
    @GetMapping("/download/{fileId}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String fileId) throws IOException {
        Resources resources = resourcesService.queryResourcesByFileId(fileId);

        String fileName = resources.getName() + '.' + resources.getExtension();
        byte[] data = fileService.downloadFile(resources);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(data);
    }

    /**
     * 【大文件上传】，检查是否已有传过的文件或分片信息
     * @param md5 前端计算的md5文件值
     * @return result
     */
    @GetMapping("check")
    public JsonResponse check(@RequestParam("md5") String md5) {
        // 是否有完整的文件
        Resources resources = resourcesService.queryResourcesByFileId(md5);
        Boolean isUploaded = resources != null;
        HashMap<String, Object> data = new HashMap<>();
        data.put("isUploaded", isUploaded);

        // 有则，文件传完，实现秒传
        if (isUploaded) {
            return JsonResponse.data(data, "文件已经秒传");
        }

        // 没有分片，查找分片信息，返回给前端
        List<Integer> chunkList = resourcesChunksService.selectChunkListByMd5(md5);
        data.put("chunkList", chunkList);
        return JsonResponse.data(data);
    }

    @PostMapping("/chunk")
    public JsonResponse uploadChunk(
            @RequestParam("chunk") MultipartFile chunk,
            @RequestParam("md5") String md5,
            @RequestParam Integer index,
            @RequestParam("chunkTotal") Integer chunkTotal,
            @RequestParam("fileSize") Integer fileSize,
            @RequestParam("fileName") String fileName,
            @RequestParam("chunkSize") Long chunkSize
    ) {

        return JsonResponse.success();
    }

    /**
     * 【单个文件】生成预上传url信息，文件可以一直上传，每上传一次就替换之前的图片，1个小时内有效。
     * @param fileName 文件名称
     * @return url
     */
    @PostMapping("/getUploadUrl")
    public JsonResponse presignedUpload(@RequestParam String fileName){
        if (fileName.isEmpty()) {
            return JsonResponse.error("fileName必传值，请上传！");
        }

        String url = fileService.getPresignedUrl(fileName);
        HashMap<String, Object> map = new HashMap<>();
        map.put("url", url);
        return JsonResponse.data(map);
    }

    @GetMapping("/getFileList")
    public JsonResponse queryFileList(@RequestParam HashMap<String, Object> params) {
        Integer page = MapUtil.getInt(params, "page");
        Integer size = MapUtil.getInt(params, "size");
        HashMap<String, Object> result = resourcesService.paginate(page, size);
        return JsonResponse.data(result);
    }

}
