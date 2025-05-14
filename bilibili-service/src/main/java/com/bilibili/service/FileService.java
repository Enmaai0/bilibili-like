package com.bilibili.service;

import com.bilibili.dao.FileMapper;
import com.bilibili.domain.File;
import com.bilibili.service.util.LocalFileStorage;
import com.bilibili.service.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Service
public class FileService {
    @Autowired
    private LocalFileStorage localFileStorage;

    @Autowired
    private FileMapper fileMapper;

    public String uploadFileBySlice(MultipartFile slice, String fileMd5, Integer sliceNo, Integer totalSliceNo) throws Exception {
        // First check if the file already exists by MD5
        File dbFile = fileMapper.getFileByMD5(fileMd5);
        if (dbFile != null) {
            return dbFile.getUrl();
        }

        // Upload the file slice
        String url = localFileStorage.uploadFileBySlices(slice, fileMd5, sliceNo, totalSliceNo);

        // If all slices are uploaded (url is returned), save file info to database
        if (!StringUtils.isEmpty(url)) {
            dbFile = new File();
            dbFile.setUrl(url);
            dbFile.setMd5(fileMd5);
            dbFile.setType(localFileStorage.getFileExtension(slice));
            dbFile.setCreateTime(new Date());
            fileMapper.addFile(dbFile);
        }

        return url;
    }

    public String getFileByMd5(MultipartFile file) throws Exception {
        return MD5Util.getFileMD5(file);
    }
}