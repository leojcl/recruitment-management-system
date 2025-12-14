package com.leojcl.recruitmentsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoredFile {
    private String id;
    private Long ownerId;
    private String originalName;
    private String contentType;
    private long size;
    private String path;
}
