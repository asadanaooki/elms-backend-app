package com.everrefine.elms.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.everrefine.elms.application.dto.FeatureFlagDto;
import com.everrefine.elms.domain.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/** {@link FeatureFlagApplicationServiceImpl} の統合テスト。 */
@ActiveProfiles("dev")
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@Testcontainers
@Transactional
class FeatureFlagApplicationServiceImplTest {

  private static final String ENABLED_FEATURE_KEY = "WELCOME_EMAIL";
  private static final String DISABLED_FEATURE_KEY = "NEW_UI";

  @Container @ServiceConnection
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17").withReuse(true);

  @Autowired private FeatureFlagApplicationServiceImpl featureFlagApplicationService;

  @Autowired private JdbcTemplate jdbcTemplate;

  @BeforeEach
  void setUpFeatureFlags() {
    jdbcTemplate.execute("DELETE FROM feature_flags");
    jdbcTemplate.update(
        "INSERT INTO feature_flags (key, enabled) VALUES (?, ?)", ENABLED_FEATURE_KEY, true);
    jdbcTemplate.update(
        "INSERT INTO feature_flags (key, enabled) VALUES (?, ?)", DISABLED_FEATURE_KEY, false);
  }

  @Nested
  class フィーチャーフラグ取得 {

    @Test
    void 指定したキーが見つかるとフィーチャーフラグを取得できること() {
      // Act
      FeatureFlagDto result =
          featureFlagApplicationService.findFeatureFlagByKey(DISABLED_FEATURE_KEY);

      // Assert
      assertEquals(DISABLED_FEATURE_KEY, result.featureKey());
      assertFalse(result.enabled());
    }

    @Test
    void 指定したキーが見つからないとResourceNotFoundExceptionが投げられること() {
      // Arrange
      String nonExistentKey = "UNKNOWN_FEATURE";

      // Act & Assert
      ResourceNotFoundException exception =
          assertThrows(
              ResourceNotFoundException.class,
              () -> featureFlagApplicationService.findFeatureFlagByKey(nonExistentKey));
      assertEquals("FeatureFlag が見つかりませんでした。id = " + nonExistentKey, exception.getMessage());
    }
  }
}
