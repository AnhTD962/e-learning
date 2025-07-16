package com.domain.backend.repository;

import com.domain.backend.entity.KanjiEntry;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface KanjiEntryRepository extends MongoRepository<KanjiEntry, String> {
    Optional<KanjiEntry> findByKanjiCharacter(String kanjiCharacter);
    List<KanjiEntry> findByJlptLevel(String jlptLevel);
    List<KanjiEntry> findByMeaningRegex(String regex, String options); // Tìm kiếm theo nghĩa (regex)
    List<KanjiEntry> findByOnyomiRegex(String regex, String options); // Tìm kiếm theo Onyomi (regex)
    List<KanjiEntry> findByKunyomiRegex(String regex, String options); // Tìm kiếm theo Kunyomi (regex)
}