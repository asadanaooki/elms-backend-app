package com.everrefine.elms.domain.repository;

import com.everrefine.elms.domain.model.featureflags.FeatureFlag;
import java.util.Optional;

/** フィーチャーフラグのリポジトリインターフェース。 */
public interface FeatureFlagRepository {

  /**
   * 指定したキーに一致するフィーチャーフラグを取得する。
   *
   * @param key フィーチャーフラグのキー
   * @return フィーチャーフラグ（存在しない場合は空）
   */
  Optional<FeatureFlag> findFeatureFlagByKey(String key);
}
