package com.everrefine.elms.infrastructure.repository;

import com.everrefine.elms.domain.model.featureflags.FeatureFlag;
import com.everrefine.elms.domain.repository.FeatureFlagRepository;
import com.everrefine.elms.infrastructure.dao.FeatureFlagDao;
import com.everrefine.elms.infrastructure.entity.featureflags.FeatureFlagEntity;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

/** {@link FeatureFlagRepository} の実装。 */
@Repository
@AllArgsConstructor
public class FeatureFlagRepositoryImpl implements FeatureFlagRepository {

  private final FeatureFlagDao featureFlagDao;

  @Override
  public Optional<FeatureFlag> findFeatureFlagByKey(String key) {
    return featureFlagDao.findById(key).map(FeatureFlagEntity::toDomain);
  }
}
