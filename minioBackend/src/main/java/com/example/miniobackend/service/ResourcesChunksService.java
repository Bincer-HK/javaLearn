package com.example.miniobackend.service;

import java.util.List;

public interface ResourcesChunksService {

    /**
     * 根据文件id，查询已上传的分片文件数据
     * @param md5 文件md5
     * @return List
     */
    List<Integer> selectChunkListByMd5(String md5);

}
