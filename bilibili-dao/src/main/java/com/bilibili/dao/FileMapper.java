package com.bilibili.dao;


import com.bilibili.domain.File;

public interface FileMapper {
    Integer addFile(File file);

    File getFileByMD5(String md5);
}
