package com.bracits.surveyengine.sitecontent.repository;

import com.bracits.surveyengine.sitecontent.entity.SiteContentPage;
import com.bracits.surveyengine.sitecontent.entity.SiteContentPageKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SiteContentPageRepository extends JpaRepository<SiteContentPage, UUID> {
    Optional<SiteContentPage> findByPageKeyAndActiveTrue(SiteContentPageKey pageKey);
}
