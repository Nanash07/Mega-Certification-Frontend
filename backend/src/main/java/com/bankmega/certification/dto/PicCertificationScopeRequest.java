package com.bankmega.certification.dto;

import lombok.*;
import java.util.List;;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PicCertificationScopeRequest {
    private Long userId;
    private List<Long> certificationIds;
}
