package com.everrefine.elms.domain.model.featureflags;

import java.time.LocalDateTime;

/** フィーチャーフラグのドメインモデル。 */
public record FeatureFlag(
    String key, boolean enabled, LocalDateTime createdAt, LocalDateTime updatedAt) {

  /**
   * フィーチャーフラグの有効状態を更新する。
   *
   * @param enabled 新しい有効状態
   * @return 更新後のフィーチャーフラグ
   */
  public FeatureFlag update(boolean enabled) {
    return new FeatureFlag(this.key, enabled, this.createdAt, LocalDateTime.now());
  }
}
