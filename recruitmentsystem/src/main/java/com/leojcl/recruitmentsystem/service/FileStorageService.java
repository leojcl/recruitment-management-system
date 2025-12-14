package com.leojcl.recruitmentsystem.service;

import com.leojcl.recruitmentsystem.dto.StoredFile;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class FileStorageService {
    @Value("${app.upload.dir}")
    private Path rootDir;

    private static final Set<String> ALLOWED_EXT = Set.of("jpg", "jpeg", "png", "pdf");
    private static final Set<String> ALLOWED_CT = Set.of("image/jpeg", "image/png", "application/pdf");

    private final Map<String, StoredFile> metaIndex = new ConcurrentHashMap<>();

    public StoredFile store(MultipartFile file, Long ownerId) throws IOException {
        // 1) Kiểm tra rỗng
        if (file.isEmpty()) throw new IllegalArgumentException("Empty file");

        // 2) Xác thực content type (từ header) + đuôi
        String originalName = sanitizeFilename(file.getOriginalFilename());
        String ext = getExtension(originalName).toLowerCase();
        if (!ALLOWED_EXT.contains(ext)) throw new IllegalArgumentException("Unsupported file extension");
        String contentType = Optional.ofNullable(file.getContentType()).orElse("");
        if (!ALLOWED_CT.contains(contentType)) throw new IllegalArgumentException("Unsupported content type");

        // 3) Đổi tên: UUID + đuôi
        String id = UUID.randomUUID().toString().replace("-", "");
        String storedName = id + "." + ext;

        // 4) Thư mục theo user
        Path userDir = rootDir.resolve(String.valueOf(ownerId));
        Files.createDirectories(userDir);

        // 5) Lưu file (ngăn path traversal)
        Path target = userDir.resolve(storedName).normalize();
        if (!target.startsWith(userDir)) throw new SecurityException("Path traversal detected");
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }

        // 6) Lưu meta (DB hoặc file .json kèm)
        StoredFile meta = new StoredFile(id, ownerId, originalName, contentType, file.getSize(), target.toString());
        saveMeta(meta); // tự cài đặt: DB table hoặc local index
        return meta;
    }

    public Resource loadAsResource(String id, Long ownerId) {
        StoredFile meta = getMeta(id, ownerId);
        return new FileSystemResource(meta.getPath());
    }

    public boolean canAccess(Long ownerId, UserDetails user) {
        // Nếu user có ROLE_ADMIN thì cho phép; nếu không, cần map userId từ principal
        Long currentUserId = resolveUserId(user);
        return currentUserId.equals(ownerId) || hasRole(user, "ROLE_ADMIN");
    }

    private static String sanitizeFilename(String name) {
        if (name == null) name = "file";
        // remove path separators and special chars
        name = name.replaceAll("[\\\\/]+", "_")
                .replace("..", "_")
                .replaceAll("[^A-Za-z0-9._-]", "_");
        // giới hạn độ dài
        if (name.length() > 100) name = name.substring(name.length() - 100);
        return name;
    }

    private static String getExtension(String name) {
        int i = name.lastIndexOf('.');
        return (i >= 0 && i < name.length() - 1) ? name.substring(i + 1) : "";
    }

    // Meta in-memory (có thể thay bằng DB)
    public void saveMeta(StoredFile meta) {
        metaIndex.put(buildKey(meta.getOwnerId(), meta.getId()), meta);
    }

    public StoredFile getMeta(String id, Long ownerId) {
        StoredFile sf = metaIndex.get(buildKey(ownerId, id));
        if (sf == null) throw new IllegalArgumentException("File not found");
        return sf;
    }

    // Expose method cho controller lấy ownerId từ principal
    public Long extractUserId(UserDetails user) {
        return resolveUserId(user);
    }

    private Long resolveUserId(UserDetails user) {
        // Tùy hệ thống của bạn. Tạm thời: nếu username là số thì dùng làm userId, ngược lại 0L
        try {
            return Long.parseLong(user.getUsername());
        } catch (Exception e) {
            return 0L;
        }
    }

    private boolean hasRole(UserDetails user, String role) {
        return user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(role));
    }

    private static String buildKey(Long ownerId, String id) {
        return ownerId + ":" + id;
    }
}
