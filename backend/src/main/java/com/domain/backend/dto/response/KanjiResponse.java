package com.domain.backend.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class KanjiResponse {
    private String id;
    private String kanjiCharacter;
    private String onyomi;
    private String kunyomi;
    private String meaning;
    private int strokeCount;
    private String strokeOrderSvg;
    private List<String> examples;
    private List<String> radicals;
    private String jlptLevel;
}