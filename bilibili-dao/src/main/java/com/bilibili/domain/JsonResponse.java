package com.bilibili.domain;

public class JsonResponse<T> {
    private String code;
    private String message;
    private T data;

    public JsonResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public JsonResponse(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> JsonResponse<T> success() {
        return new JsonResponse<T>("0", "success");
    }

    public static <T> JsonResponse<T> success(T data) {
        return new JsonResponse<T>("0", "success", data);
    }

    public static <T> JsonResponse<T> error(String code, String message) {
        return new JsonResponse<T>(code, message);
    }

    public static <T> JsonResponse<T> error() {
        return new JsonResponse<T>("1", "error");
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
