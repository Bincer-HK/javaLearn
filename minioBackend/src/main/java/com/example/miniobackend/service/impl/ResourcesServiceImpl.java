package com.example.miniobackend.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.miniobackend.domain.Resources;
import com.example.miniobackend.mapper.ResourcesMapper;
import com.example.miniobackend.service.ResourcesService;
import com.example.miniobackend.types.PaginationResult;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;

@Service
public class ResourcesServiceImpl extends ServiceImpl<ResourcesMapper, Resources> implements ResourcesService {

    /**
     * 新增上传文件记录
     * @param fileId {String} 文件id
     * @param type {String} 文件类型
     * @param filename {String} 文件名
     * @param ext {String} 后缀
     * @param size {Long} 大小
     * @param disk {String} 存储位置，就是桶的名字
     * @return 保存结果
     */
    @Override
    public Boolean create(String fileId, String type, String filename, String ext, Long size, String disk) {
        String path = type + '/' + fileId + "." + ext;
        String url = '/' + disk + '/' + path;

        Resources newResources = new Resources();
        newResources.setFileId(fileId);
        newResources.setType(type);
        newResources.setName(filename); // 这个是原始文件名
        newResources.setExtension(ext);
        newResources.setSize(size);
        newResources.setDisk(disk);
        newResources.setPath(path);
        newResources.setUrl(url);
        newResources.setCreatedAt(new Date());

        return save(newResources);
    }

    /**
     * 根据文件id，查询文件信息
     * @param fileId 文件id标识
     * @return result
     */
    @Override
    public Resources queryResourcesByFileId(String fileId) {
        return getOne(query().getWrapper().eq("file_id", fileId));
    }

    /**
     * 分页查询资源信息
     * @param page 页码
     * @param size 数量
     * @return result
     */
    @Override
    public HashMap<String, Object> paginate(Integer page, Integer size) {
        HashMap<String, Object> pageResult = new HashMap<>();

        IPage<Resources> pageInfo = new Page<>(page, size);
        IPage<Resources> resourcesIPage = page(pageInfo);
        pageResult.put("list", resourcesIPage.getRecords());
        pageResult.put("total", resourcesIPage.getTotal());
        return pageResult;
    }


}
