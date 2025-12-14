package com.leojcl.recruitmentsystem.controller;

import com.leojcl.recruitmentsystem.dto.StoredFile;
import com.leojcl.recruitmentsystem.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class UploadController {
    private final FileStorageService storage;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> upload(@RequestPart("file") MultipartFile file, @AuthenticationPrincipal UserDetails user) throws IOException {
        Long ownerId = storage.extractUserId(user);
        StoredFile saved = storage.store(file, ownerId);
        // Trả về id để sau này tải qua /files/{ownerId}/{id}
        return ResponseEntity.ok(Map.of("id", saved.getId(), "ownerId", saved.getOwnerId()));
    }
}
