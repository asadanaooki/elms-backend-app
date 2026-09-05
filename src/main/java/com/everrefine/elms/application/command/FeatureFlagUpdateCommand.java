package com.everrefine.elms.application.command;

import com.everrefine.elms.domain.model.featureflags.FeatureFlag;
import jakarta.validation.constraints.NotNull;

/** フィーチャーフラグ更新用のコマンド。 */
public record FeatureFlagUpdateCommand(String featureFlagKey, boolean enabled) {

  /**
   * 更新後のフィーチャーフラグに変換する。
   *
   * @param featureFlag 更新対象のフィーチャーフラグ
   * @return 更新後のフィーチャーフラグ
   */
  public FeatureFlag toFeatureFlag(FeatureFlag featureFlag) {
    return featureFlag.update(enabled);
  }
}
