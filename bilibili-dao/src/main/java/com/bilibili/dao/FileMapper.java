package com.bilibili.dao;


import com.bilibili.domain.File;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FileMapper {
    Integer addFile(File file);

    File getFileByMD5(String md5);
}
