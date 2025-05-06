package com.bilibili.service.util;

import ch.qos.logback.core.util.StringUtil;
import com.bilibili.service.exception.ConditionalException;
import com.github.tobato.fastdfs.domain.fdfs.MetaData;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.AppendFileStorageClient;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Component
public class FastdfsUtil {

    @Autowired
    private FastFileStorageClient fastFileStorageClient;

    @Autowired
    private AppendFileStorageClient appendFileStorageClient;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String FILE_PATH_KEY = "path-key:";
    private static final String UPLOADED_SIZE_KEY = "uploaded-size-key:";
    private static final String UPLOADED_NO_KEY = "uploaded-no-key:";

    private static final String DEFAULT_GROUP = "group1";

    public String getFileType(MultipartFile file) {
        if(file == null || file.isEmpty()) {
            throw new ConditionalException("Illegal file");
        }
        String fileName = file.getOriginalFilename();
        int index = fileName.lastIndexOf(".");
        return fileName.substring(index + 1);
    }

    public String uploadCommonFile(MultipartFile file) throws Exception {
        Set<MetaData> metadataSet = new HashSet<>();
        String fileType = getFileType(file);
        StorePath storePath = fastFileStorageClient.uploadFile(file.getInputStream(), file.getSize(), fileType, metadataSet);
        return storePath.getPath();
    }

    public String uploadAppenderFile(MultipartFile file) throws Exception {
        String fileType = this.getFileType(file);
        StorePath storePath = appendFileStorageClient.uploadAppenderFile(DEFAULT_GROUP, file.getInputStream(), file.getSize(), fileType);
        return storePath.getPath();
    }

    public void modifyAppenderFile(String filePath, MultipartFile file, long offset) throws IOException {
        appendFileStorageClient.modifyFile(DEFAULT_GROUP, filePath, file.getInputStream(), file.getSize(), offset);
    }

    public String uploadFileBySlices(MultipartFile file, String fileMd5, Integer sliceNo, Integer totalSliceNo) throws Exception {

        String pathKey = FILE_PATH_KEY + fileMd5;
        String uploadedSizeKey = UPLOADED_SIZE_KEY + fileMd5;
        String uploadedNoKey = UPLOADED_NO_KEY + fileMd5;
        String upoadedSizeStr = redisTemplate.opsForValue().get(uploadedSizeKey);
        Long uploadedSize = 0L;
        if(!StringUtil.isNullOrEmpty(upoadedSizeStr)) {
            uploadedSize = Long.valueOf(upoadedSizeStr);
        }

        if(sliceNo == 1) {
            String path = this.uploadAppenderFile(file);
            if(StringUtil.isNullOrEmpty(path)) {
                throw new ConditionalException("Upload failed");
            }
            redisTemplate.opsForValue().set(pathKey, path);
            redisTemplate.opsForValue().set(uploadedNoKey, "1");
        } else {
            String path = redisTemplate.opsForValue().get(pathKey);
            if(StringUtil.isNullOrEmpty(path)) {
                throw new ConditionalException("Upload failed");
            }
            this.modifyAppenderFile(path, file, uploadedSize);
            redisTemplate.opsForValue().increment(uploadedNoKey);
        }
        uploadedSize += file.getSize();
        redisTemplate.opsForValue().set(uploadedSizeKey, String.valueOf(uploadedSize));
        String uploadedNoStr = redisTemplate.opsForValue().get(uploadedNoKey);
        Integer uploadedNo = Integer.valueOf(uploadedNoStr);
        String resultPath = "";
        if(uploadedNo.equals(totalSliceNo)) {
            resultPath = redisTemplate.opsForValue().get(pathKey);
            redisTemplate.delete(pathKey);
            redisTemplate.delete(uploadedSizeKey);
            redisTemplate.delete(uploadedNoKey);
        }
        return resultPath;
    }

    public void deleteFile(String filePath) throws IOException {
        fastFileStorageClient.deleteFile(filePath);
    }

    public String getFileMd5(MultipartFile file) throws Exception {
        return MD5Util.getFileMD5(file);
    }
}
