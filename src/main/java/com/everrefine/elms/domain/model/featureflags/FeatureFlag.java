package com.everrefine.elms.domain.model.featureflags;

import java.time.LocalDateTime;

/** フィーチャーフラグのドメインモデル。 */
public record FeatureFlag(
    String key, boolean enabled, LocalDateTime createdAt, LocalDateTime updatedAt) {}
