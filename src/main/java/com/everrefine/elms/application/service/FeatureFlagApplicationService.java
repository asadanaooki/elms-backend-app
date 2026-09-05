package com.everrefine.elms.application.service;

import com.everrefine.elms.application.dto.FeatureFlagDto;

/** フィーチャーフラグアプリケーションサービスのインターフェース。 */
public interface FeatureFlagApplicationService {

  /**
   * 指定したキーに一致するフィーチャーフラグを取得する。
   *
   * @param key フィーチャーフラグのキー
   * @return フィーチャーフラグDTO
   */
  FeatureFlagDto findFeatureFlagByKey(String key);
}
