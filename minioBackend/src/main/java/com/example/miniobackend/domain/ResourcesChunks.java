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

@TableName(value = "chunk")
@Data
public class ResourcesChunks implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;

    // 分片id
    private Long chunkId;

    // 文件id
    private String fileId;

    // 序号
    private Integer index;

    @JsonIgnore
    private Date createdAt;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResourcesChunks chunk = (ResourcesChunks) o;
        return Objects.equals(id, chunk.id) && Objects.equals(chunkId, chunk.chunkId) && Objects.equals(fileId, chunk.fileId) && Objects.equals(index, chunk.index);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, chunkId, fileId, index);
    }
}
