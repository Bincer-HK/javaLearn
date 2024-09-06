package com.example.miniobackend.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@TableName(value = "resources")
@Data
public class Resources implements Serializable {

    @TableId(type = IdType.AUTO)
    private Integer id;

    // 可以单文件接口上传计算出来的md5，也可以是前端分片上传计算出来的文件md5
    private String fileId;

    // 类型
    private String type;

    // 资源名
    private String name;

    // 文件类型
    private String extension;

    // 文件大小
    private Long size;

    // 存储磁盘
    private String disk;

    // 相对地址
    private String path;

    // URL 地址
    private String url;

    @JsonIgnore
    private Date createdAt;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Resources resources = (Resources) o;
        return Objects.equals(id, resources.id) && Objects.equals(fileId, resources.fileId) && Objects.equals(type, resources.type) && Objects.equals(name, resources.name) && Objects.equals(extension, resources.extension) && Objects.equals(size, resources.size) && Objects.equals(disk, resources.disk) && Objects.equals(path, resources.path) && Objects.equals(url, resources.url) && Objects.equals(createdAt, resources.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fileId, type, name, extension, size, disk, path, url, createdAt);
    }
}
