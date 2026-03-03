package com.bracits.surveyengine.questionbank.repository;

import com.bracits.surveyengine.questionbank.entity.CategoryVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryVersionRepository extends JpaRepository<CategoryVersion, UUID> {

    List<CategoryVersion> findByCategoryIdOrderByVersionNumberDesc(UUID categoryId);

    Optional<CategoryVersion> findByCategoryIdAndVersionNumber(UUID categoryId, Integer versionNumber);

    Optional<CategoryVersion> findTopByCategoryIdOrderByVersionNumberDesc(UUID categoryId);
}
