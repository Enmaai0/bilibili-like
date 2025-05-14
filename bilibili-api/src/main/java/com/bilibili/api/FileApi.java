package com.bilibili.api;

import com.bilibili.domain.JsonResponse;
import com.bilibili.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@RestController
public class FileApi {

    @Value("${file.storage.path}")
    private String storageBasePath;

    @Autowired
    private FileService fileService;

    @PostMapping("/md5files")
    public JsonResponse<String> getFileByMd5(MultipartFile file) throws Exception {
        String fileMd5 = fileService.getFileByMd5(file);
        return JsonResponse.success(fileMd5);
    }

    @GetMapping("/files/{fileName}")
    public ResponseEntity<Resource> serveFile(@PathVariable String fileName) throws IOException {
        File file = new File(storageBasePath + File.separator + fileName);

        if (!file.exists() || !file.canRead()) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(file);
        String contentType = Files.probeContentType(file.toPath());

        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                .body(resource);
    }

    @PutMapping("/file/file-slices")
    public JsonResponse<String> uploadFileSlice(MultipartFile slice, String fileMd5, Integer sliceNo, Integer totalSliceNo) throws Exception {
        String filePath = fileService.uploadFileBySlice(slice, fileMd5, sliceNo, totalSliceNo);
        return JsonResponse.success(filePath);
    }
}