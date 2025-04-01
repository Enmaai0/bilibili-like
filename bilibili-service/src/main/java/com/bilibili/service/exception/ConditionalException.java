package com.bilibili.service.exception;

public class ConditionalException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  private String code;

    public ConditionalException(String code, String message) {
        super(message);
        this.code = code;
    }

    public ConditionalException(String name) {
        super(name);
        this.code = "500";
    }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }
}
