package com.domain.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Lombok annotation
@NoArgsConstructor // Lombok annotation cho constructor không đối số
@AllArgsConstructor // Lombok annotation cho constructor tất cả đối số
public class MessageResponse {
    private String message;
}