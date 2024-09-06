package com.example.miniobackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.miniobackend.domain.Resources;
import com.example.miniobackend.types.PaginationResult;

import java.util.HashMap;

public interface ResourcesService extends IService<Resources> {

    /**
     * 创建并保存资源
     * @param fileId {String} 文件id
     * @param type {String} 文件类型
     * @param filename {String} 文件名
     * @param ext {String} 后缀
     * @param size {Long} 大小
     * @param disk {String} 存储位置，就是桶的名字
     * @return Resources
     */
    Boolean create(String fileId, String type, String filename, String ext, Long size, String disk);

    /**
     * 根据文件id，查询文件信息
     * @param fileId 文件id标识
     * @return 查询结果
     */
    Resources queryResourcesByFileId(String fileId);

    /**
     * 查询数据库中的资源信息
     * @param page 页码
     * @param size 数量
     * @return 分页结果
     */
    HashMap<String, Object> paginate(Integer page, Integer size);
}
