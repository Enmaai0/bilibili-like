package com.bilibili.api;

import com.bilibili.domain.JsonResponse;
import com.bilibili.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class FileApi {
    @Autowired
    private FileService fileService;

    @PostMapping("/md5files")
    public JsonResponse<String> getFileByMd5(MultipartFile file) throws Exception {
        String fileMd5 = fileService.getFileByMd5(file);
        return JsonResponse.success(fileMd5);
    }

    @PutMapping("/file-slices")
    public JsonResponse<String> uploadFileSlice(MultipartFile slice, String fileMd5, Integer sliceNo, Integer totalSliceNo) throws Exception {
        String filePath = fileService.uploadFileBySlice(slice, fileMd5, sliceNo, totalSliceNo);
        return JsonResponse.success(filePath);
    }
}
