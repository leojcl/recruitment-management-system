package com.leojcl.recruitmentsystem.dto.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageMetaDto {
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}
