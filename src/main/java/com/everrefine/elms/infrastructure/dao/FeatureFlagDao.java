package com.everrefine.elms.infrastructure.dao;

import com.everrefine.elms.infrastructure.entity.featureflags.FeatureFlagEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/** フィーチャーフラグのDAOインターフェース。 */
@Repository
public interface FeatureFlagDao extends CrudRepository<FeatureFlagEntity, String> {}
