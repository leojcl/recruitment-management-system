package com.leojcl.recruitmentsystem.controller;

import com.leojcl.recruitmentsystem.dto.StoredFile;
import com.leojcl.recruitmentsystem.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {
    private final FileStorageService storage;

    @GetMapping("/{ownerId}/{id}")
    public ResponseEntity<Resource> download(
            @PathVariable Long ownerId,
            @PathVariable String id,
            @AuthenticationPrincipal UserDetails user
    ) throws IOException {
        // 1) Kiểm tra quyền: user chỉ được tải file của chính mình (hoặc ROLE phù hợp)
        if (!storage.canAccess(ownerId, user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        // 2) Lấy meta + resource
        StoredFile meta = storage.getMeta(id, ownerId); // chứa filename gốc đã sanitize, contentType, size, path
        Resource resource = storage.loadAsResource(id, ownerId);
        if (!resource.exists()) return ResponseEntity.notFound().build();

        // 3) Header an toàn: Content-Type/Disposition
        ContentDisposition disposition = ContentDisposition.attachment()
                .filename(meta.getOriginalName(), StandardCharsets.UTF_8)
                .build();
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(meta.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .body(resource);
    }
}
