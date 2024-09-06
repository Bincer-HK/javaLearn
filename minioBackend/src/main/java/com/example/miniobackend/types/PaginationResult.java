package com.example.miniobackend.types;

import lombok.Data;

import java.util.List;

@Data
public class PaginationResult<T> {

    private List<T> data;
    private Long total;

}
