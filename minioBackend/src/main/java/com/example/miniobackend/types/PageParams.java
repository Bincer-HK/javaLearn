package com.example.miniobackend.types;

import lombok.Data;

@Data
public class PageParams {
    Integer page = 1;
    Integer size = 10;
}
