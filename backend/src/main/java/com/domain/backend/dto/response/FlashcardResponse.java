package com.domain.backend.dto.response;

import lombok.Data;

@Data
public class FlashcardResponse {
    private String id;
    private String front;
    private String back;
    private int orderIndex;
}
