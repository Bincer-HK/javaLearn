package com.example.miniobackend.types;

import java.util.Objects;

public class JsonResponse {

    private Integer code;
    private String message;
    private Object data;

    public JsonResponse(Integer code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static JsonResponse success(String msg) {
        return new JsonResponse(0, msg, null);
    }

    public static JsonResponse success() {
        return new JsonResponse(0, "", null);
    }

    public static JsonResponse data(Object data){
        return new JsonResponse(0, "", data);
    }

    public static JsonResponse data(Object data, String message){
        return new JsonResponse(0, message, data);
    }

    public static JsonResponse error(String msg, Integer code) {
        return new JsonResponse(code, msg, null);
    }

    public static JsonResponse error(String msg) {
        return new JsonResponse(-1, msg, null);
    }

    public static JsonResponse error(String msg, Object data) {
        return new JsonResponse(-1, msg, data);
    }


    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JsonResponse that = (JsonResponse) o;
        return Objects.equals(code, that.code) && Objects.equals(message, that.message) && Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, message, data);
    }
}
