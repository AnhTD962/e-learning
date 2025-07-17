package com.domain.backend.repository;

import com.domain.backend.entity.VocabularyEntry;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VocabularyEntryRepository extends MongoRepository<VocabularyEntry, String> {
    Optional<VocabularyEntry> findByJapaneseWord(String japaneseWord);

    List<VocabularyEntry> findByJlptLevel(String jlptLevel);

    List<VocabularyEntry> findByMeaningRegex(String regex, String options);

    List<VocabularyEntry> findByFuriganaRegex(String regex, String options);

    List<VocabularyEntry> findByRomajiRegex(String regex, String options);
}