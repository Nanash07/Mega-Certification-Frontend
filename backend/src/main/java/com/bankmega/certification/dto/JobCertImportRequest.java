package com.bankmega.certification.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class JobCertImportRequest {
    private MultipartFile file;
}
