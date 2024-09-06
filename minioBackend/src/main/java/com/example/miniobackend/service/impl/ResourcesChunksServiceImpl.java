package com.example.miniobackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.miniobackend.domain.ResourcesChunks;
import com.example.miniobackend.mapper.ResourcesChunksMapper;
import com.example.miniobackend.service.ResourcesChunksService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResourcesChunksServiceImpl extends ServiceImpl<ResourcesChunksMapper, ResourcesChunks> implements ResourcesChunksService {

    /**
     * 根据文件id，查询已上传的分片文件数据
     * @param md5 文件md5
     * @return List
     */
    @Override
    public List<Integer> selectChunkListByMd5(String md5){
        List<ResourcesChunks> chunksList = list(query().getWrapper().eq("file_id", md5));

        return chunksList.stream().map(ResourcesChunks::getIndex).toList();
    }

}
