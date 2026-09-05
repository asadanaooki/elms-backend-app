package com.everrefine.elms.application.dto;

import com.everrefine.elms.domain.model.featureflags.FeatureFlag;
import io.swagger.v3.oas.annotations.media.Schema;

/** フィーチャーフラグDTO。 */
public record FeatureFlagDto(
    @Schema(description = "フィーチャーフラグのキー", example = "WELCOME_EMAIL") String featureFlagKey,
    @Schema(description = "フィーチャーフラグの有効状態", example = "true") boolean enabled) {

  /**
   * フィーチャーフラグのドメインモデルからDTOを生成する。
   *
   * @param featureFlag フィーチャーフラグのドメインモデル
   * @return フィーチャーフラグDTO
   */
  public static FeatureFlagDto from(FeatureFlag featureFlag) {
    return new FeatureFlagDto(featureFlag.key(), featureFlag.enabled());
  }
}
