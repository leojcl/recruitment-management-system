package com.leojcl.recruitmentsystem.exception;

/**
 * Chuẩn hoá mã lỗi để client có thể xử lý theo từng trường hợp cụ thể.
 */
public enum ErrorCode {
    // Nhóm lỗi tài nguyên
    RESOURCE_NOT_FOUND,
    RESOURCE_ALREADY_EXISTS,

    // Nhóm lỗi xác thực/ủy quyền
    UNAUTHORIZED,
    FORBIDDEN,

    // Nhóm lỗi yêu cầu không hợp lệ
    BAD_REQUEST,
    VALIDATION_FAILED,
    METHOD_NOT_ALLOWED,
    MEDIA_TYPE_NOT_SUPPORTED,
    MESSAGE_NOT_READABLE,

    // Nhóm lỗi hạ tầng/lưu trữ tệp
    STORAGE_ERROR,

    // Lỗi chung
    INTERNAL_ERROR
}
