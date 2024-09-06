package com.example.miniobackend.types;

import java.util.HashMap;

public class BackendConstant {

    public static final String RESOURCE_TYPE_VIDEO = "VIDEO";
    public static final String RESOURCE_TYPE_IMAGE = "IMAGE";
    public static final String RESOURCE_TYPE_PDF = "PDF";
    public static final String RESOURCE_TYPE_WORD = "WORD";
    public static final String RESOURCE_TYPE_PPT = "PPT";
    public static final String RESOURCE_TYPE_EXCEL = "EXCEL";
    public static final String RESOURCE_TYPE_ZIP = "ZIP";
    public static final String RESOURCE_TYPE_RAR = "RAR";
    public static final String RESOURCE_TYPE_TXT = "TXT";

    public static final HashMap<String, String> RESOURCE_EXT_2_TYPE = new HashMap<>() {
        {
            put("png", RESOURCE_TYPE_IMAGE);
            put("jpg", RESOURCE_TYPE_IMAGE);
            put("jpeg", RESOURCE_TYPE_IMAGE);
            put("gif", RESOURCE_TYPE_IMAGE);
            put("pdf", RESOURCE_TYPE_PDF);
            put("mp4", RESOURCE_TYPE_VIDEO);
            put("doc", RESOURCE_TYPE_WORD);
            put("docx", RESOURCE_TYPE_WORD);
            put("ppt", RESOURCE_TYPE_PPT);
            put("pptx", RESOURCE_TYPE_PPT);
            put("xls", RESOURCE_TYPE_EXCEL);
            put("xlsx", RESOURCE_TYPE_EXCEL);
            put("txt", RESOURCE_TYPE_TXT);
            put("zip", RESOURCE_TYPE_ZIP);
            put("rar", RESOURCE_TYPE_RAR);
        }
    };

}
