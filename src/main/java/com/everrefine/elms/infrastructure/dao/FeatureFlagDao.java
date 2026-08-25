package com.everrefine.elms.infrastructure.dao;

import com.everrefine.elms.infrastructure.entity.featureflags.FeatureFlagEntity;
import org.springframework.data.repository.CrudRepository;

/** フィーチャーフラグのDAOインターフェース。 */
public interface FeatureFlagDao extends CrudRepository<FeatureFlagEntity, String> {}
