package com.example.miniobackend.service.impl;

import com.example.miniobackend.domain.Resources;
import com.example.miniobackend.service.FileService;
import com.example.miniobackend.service.ResourcesService;
import com.example.miniobackend.utils.HelperUtil;
import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private ResourcesService resourcesService;

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucketName;

    /**
     * 上传文件流 到 minio 中
     * @param file 流
     * @return 文件信息
     * @throws IOException 保存异常
     */
    @Override
    public HashMap<String, String> uploadFile(MultipartFile file, String fileOriginName, String extension, String type) throws IOException {
        String fileId = HelperUtil.md5(fileOriginName + HelperUtil.randomString(12));
        String fileName = fileId + "." + extension;

        try {
            // object中可以传path路径、文件名等，本项目存储以
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(type + '/' + fileName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build()
            );

            Boolean isSave = resourcesService.create(fileId, type, fileOriginName, extension, file.getSize(), bucketName);
            HashMap<String, String> data = new HashMap<>();
            if (isSave) {
                data.put("fileId", fileId);
                return data;
            }
            return null;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        return null;
    }

    /**
     * 下载文件，用流去接收得到的文件信息
     * @param resources 数据库查询到的信息
     * @return 文件流
     * @throws IOException 异常信息
     */
    @Override
    public byte[] downloadFile(Resources resources) throws IOException {
        String objStr = resources.getType() + '/' + resources.getFileId() + '.' + resources.getExtension();
        try (
            InputStream stream = minioClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(objStr).build());
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = stream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }

            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取预上传连接地址，一个小时后过期
     * @param fileName 文件名
     * @return url
     */
    @Override
    public String getPresignedUrl(String fileName) {
        try {
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Method.PUT)
                    .bucket(bucketName)
                    .object(fileName)
                    .expiry(1, TimeUnit.HOURS)
                    .build()
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
