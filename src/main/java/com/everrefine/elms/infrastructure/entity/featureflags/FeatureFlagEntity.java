package com.everrefine.elms.infrastructure.entity.featureflags;

import com.everrefine.elms.domain.model.featureflags.FeatureFlag;
import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/** フィーチャーフラグのエンティティ。 */
@Table("feature_flags")
public record FeatureFlagEntity(
    @Id String key, boolean enabled, LocalDateTime createdAt, LocalDateTime updatedAt) {

  /**
   * ドメインモデルからエンティティを生成する。
   *
   * @param featureFlag フィーチャーフラグのドメインモデル
   * @return エンティティ
   */
  public static FeatureFlagEntity from(FeatureFlag featureFlag) {
    return new FeatureFlagEntity(
        featureFlag.key(), featureFlag.enabled(), featureFlag.createdAt(), featureFlag.updatedAt());
  }

  /**
   * ドメインモデルに変換する。
   *
   * @return フィーチャーフラグのドメインモデル
   */
  public FeatureFlag toDomain() {
    return new FeatureFlag(key, enabled, createdAt, updatedAt);
  }
}
