package com.example.miniobackend.service;

import com.example.miniobackend.domain.Resources;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;

public interface FileService {

    /**
     * 上传文件流 到 minio 中
     * @param file 流
     * @param fileOriginName {String} 不包含后缀的文件名
     * @param extension {String} 文件后缀
     * @param type {String} 存储目录
     * @return 上传完后minio存储文件的相关信息
     * @throws IOException 保存异常
     */
    HashMap<String, String> uploadFile(MultipartFile file, String fileOriginName, String extension, String type) throws IOException;

    /**
     * 下载文件，用流去接收得到的文件信息
     * @param resources 数据库查询到的信息
     * @return 文件流
     * @throws IOException 异常信息
     */
    byte[] downloadFile(Resources resources) throws IOException;

    /**
     * 获取预上传连接地址
     * @param fileName 文件名
     * @return url
     */
    String getPresignedUrl(String fileName);
}
