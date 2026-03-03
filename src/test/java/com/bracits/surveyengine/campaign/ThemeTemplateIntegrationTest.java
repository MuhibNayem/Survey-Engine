package com.bracits.surveyengine.campaign;

import com.bracits.surveyengine.TestcontainersConfiguration;
import com.bracits.surveyengine.campaign.repository.ThemeTemplateRepository;
import com.bracits.surveyengine.campaign.entity.ThemeTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class ThemeTemplateIntegrationTest {

    @Autowired
    private ThemeTemplateRepository themeTemplateRepository;

    @Test
    void shouldHaveSeededThemeTemplates() {
        List<ThemeTemplate> templates = themeTemplateRepository.findBySystemTrue();

        assertThat(templates).hasSizeGreaterThanOrEqualTo(5);
        assertThat(templates).extracting(ThemeTemplate::getName)
                .contains("Modern Dark", "Classic Light", "Ocean Blue", "Forest Green", "Sunset Warm");
    }
}
