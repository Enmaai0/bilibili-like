package com.bilibili.service;

import ch.qos.logback.core.util.StringUtil;
import com.bilibili.dao.FileMapper;
import com.bilibili.domain.File;
import com.bilibili.service.util.FastdfsUtil;
import com.bilibili.service.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileService {
    @Autowired
    private FastdfsUtil fastdfsUtil;

    @Autowired
    private FileMapper fileMapper;

    public String uploadFileBySlice(MultipartFile slice, String fileMd5, Integer sliceNo, Integer totalSliceNo) throws Exception {
        File dbFile = fileMapper.getFileByMD5(fileMd5);
        if (dbFile != null) {
            return dbFile.getUrl();
        }
        String url = fastdfsUtil.uploadFileBySlices(slice, fileMd5, sliceNo, totalSliceNo);
        if(!StringUtil.isNullOrEmpty(url)) {
            dbFile = new File();
            dbFile.setUrl(url);
            dbFile.setMd5(fileMd5);
            dbFile.setType(fastdfsUtil.getFileType(slice));
            fileMapper.addFile(dbFile);
        }
        return url;
    }

    public String getFileByMd5(MultipartFile file) throws Exception {
        return MD5Util.getFileMD5(file);
    }
}
