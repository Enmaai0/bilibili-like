package com.bilibili.service.util;

import com.bilibili.service.exception.ConditionalException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.UUID;

/**
 * Local file storage utility to replace FastDFS
 * Handles file uploads including sliced uploads for large files
 */
@Component
public class LocalFileStorage {

    @Value("${file.storage.path}")
    private String storageBasePath;

    @Value("${file.storage.url-prefix}")
    private String fileUrlPrefix;

    private static final String FILE_PATH_KEY = "path-key:";
    private static final String UPLOADED_SIZE_KEY = "uploaded-size-key:";
    private static final String UPLOADED_NO_KEY = "uploaded-no-key:";

    private final RedisTemplate<String, String> redisTemplate;

    public LocalFileStorage(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        // 打印工作目录和配置信息
        System.out.println("Working Directory = " + System.getProperty("user.dir"));
    }

    // After properties are set
    @PostConstruct
    public void init() {
        System.out.println("Working Directory = " + System.getProperty("user.dir"));
        System.out.println("User Home = " + System.getProperty("user.home"));
        System.out.println("Configured storage path: " + storageBasePath);
        System.out.println("Configured URL prefix: " + fileUrlPrefix);
        
        // 确保存储目录存在
        File storageDir = new File(storageBasePath);
        if (!storageDir.exists()) {
            boolean created = storageDir.mkdirs();
            if (!created) {
                throw new RuntimeException("Failed to create storage directory: " + storageBasePath);
            }
            System.out.println("Created storage directory: " + storageBasePath);
        }
        
        // 确保目录可写
        if (!storageDir.canWrite()) {
            throw new RuntimeException("Storage directory is not writable: " + storageBasePath);
        }
        
        // 列出目录中的文件
        File[] files = storageDir.listFiles();
        if (files != null) {
            System.out.println("Files in storage directory:");
            for (File file : files) {
                System.out.println(" - " + file.getName());
            }
        }
    }

    /**
     * Uploads a file to the local file system
     * @param file File to upload
     * @return URL to access the file
     * @throws IOException If an error occurs during file upload
     */
    public String uploadFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new ConditionalException("Illegal file");
        }

        // Create storage directory if it doesn't exist
        File storageDir = new File(storageBasePath);
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        // Generate a unique filename
        String fileExtension = getFileExtension(file);
        String fileName = UUID.randomUUID() + "." + fileExtension;
        String filePath = storageBasePath + File.separator + fileName;

        // Save the file
        File destFile = new File(filePath);
        file.transferTo(destFile);

        // Return the URL path for accessing the file
        return getFileUrl(fileName);
    }

    /**
     * Upload file by slices for large files
     * @param slice Current slice of the file
     * @param fileMd5 MD5 hash of the complete file (used as identifier)
     * @param sliceNo Current slice number
     * @param totalSliceNo Total number of slices
     * @return URL to access the file (only returned when all slices are uploaded)
     * @throws IOException If an error occurs during file upload
     */
    public String uploadFileBySlices(MultipartFile slice, String fileMd5, Integer sliceNo, Integer totalSliceNo) throws IOException {
        String pathKey = FILE_PATH_KEY + fileMd5;
        String uploadedSizeKey = UPLOADED_SIZE_KEY + fileMd5;
        String uploadedNoKey = UPLOADED_NO_KEY + fileMd5;

        String uploadedSizeStr = redisTemplate.opsForValue().get(uploadedSizeKey);
        Long uploadedSize = 0L;
        if (!StringUtils.isEmpty(uploadedSizeStr)) {
            uploadedSize = Long.valueOf(uploadedSizeStr);
        }

        // Create storage directory if it doesn't exist
        File storageDir = new File(storageBasePath);
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        if (sliceNo == 1) {
            // For first slice, create a new file
            String fileExtension = getFileExtension(slice);
            String fileName = fileMd5 + "." + fileExtension;
            String filePath = storageBasePath + File.separator + fileName;

            // Create the file
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
            }

            // Save the first slice
            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                outputStream.write(slice.getBytes());
            }

            // Save metadata in Redis
            redisTemplate.opsForValue().set(pathKey, fileName);
            redisTemplate.opsForValue().set(uploadedNoKey, "1");
        } else {
            // For subsequent slices, append to existing file
            String fileName = redisTemplate.opsForValue().get(pathKey);
            if (StringUtils.isEmpty(fileName)) {
                throw new ConditionalException("Upload failed: File not found");
            }

            String filePath = storageBasePath + File.separator + fileName;
            File file = new File(filePath);

            if (!file.exists()) {
                throw new ConditionalException("Upload failed: File not found");
            }

            // Append the slice to the file
            try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw")) {
                randomAccessFile.seek(uploadedSize);
                randomAccessFile.write(slice.getBytes());
            }

            // Increment the uploaded slice counter
            redisTemplate.opsForValue().increment(uploadedNoKey);
        }

        // Update the uploaded size
        uploadedSize += slice.getSize();
        redisTemplate.opsForValue().set(uploadedSizeKey, String.valueOf(uploadedSize));

        // Check if all slices have been uploaded
        String uploadedNoStr = redisTemplate.opsForValue().get(uploadedNoKey);
        Integer uploadedNo = Integer.valueOf(uploadedNoStr);

        if (uploadedNo.equals(totalSliceNo)) {
            // All slices uploaded, return the file URL
            String fileName = redisTemplate.opsForValue().get(pathKey);

            // Clean up Redis keys
            redisTemplate.delete(pathKey);
            redisTemplate.delete(uploadedSizeKey);
            redisTemplate.delete(uploadedNoKey);

            return getFileUrl(fileName);
        }

        return "";
    }

    /**
     * Delete a file by its URL path
     * @param fileUrl URL of the file to delete
     * @return true if deleted successfully
     */
    public boolean deleteFile(String fileUrl) {
        if (StringUtils.isEmpty(fileUrl)) {
            return false;
        }

        String fileName = fileUrl.substring(fileUrlPrefix.length());
        String filePath = storageBasePath + File.separator + fileName;

        File file = new File(filePath);
        return file.exists() && file.delete();
    }

    /**
     * Get a file by its MD5 hash
     * @param md5 MD5 hash of the file
     * @return File path if found, null otherwise
     */
    public String getFileByMd5(String md5) {
        File storageDir = new File(storageBasePath);
        if (!storageDir.exists()) {
            return null;
        }

        // Search for files that start with the MD5 hash
        File[] files = storageDir.listFiles((dir, name) -> name.startsWith(md5 + "."));

        if (files != null && files.length > 0) {
            // Return the URL for the first matching file
            return getFileUrl(files[0].getName());
        }

        return null;
    }

    /**
     * Get the file extension from a MultipartFile
     * @param file The MultipartFile
     * @return File extension
     */
    public String getFileExtension(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            return "bin";
        }
        int index = fileName.lastIndexOf(".");
        return index > 0 ? fileName.substring(index + 1) : "bin";
    }

    /**
     * Generate a URL for accessing the file
     * @param fileName Name of the file
     * @return URL for accessing the file
     */
    private String getFileUrl(String fileName) {
        return fileUrlPrefix + "/" + fileName;
    }

    public String getStorageBasePath() {
        return storageBasePath;
    }

    /**
     * 移动上传的文件到存储目录
     * @param sourceFile 源文件
     * @param targetFilename 目标文件名
     * @return 文件访问URL
     * @throws IOException 如果文件操作失败
     */
    private String moveToStorage(File sourceFile, String targetFilename) throws IOException {
        File targetFile = new File(storageBasePath, targetFilename);
        if (!sourceFile.renameTo(targetFile)) {
            throw new IOException("Failed to move file to storage: " + targetFilename);
        }
        return getFileUrl(targetFilename);
    }
}