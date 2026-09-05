package com.everrefine.elms.application.service;

import com.everrefine.elms.application.command.FeatureFlagUpdateCommand;
import com.everrefine.elms.application.dto.FeatureFlagDto;
import com.everrefine.elms.domain.exception.ResourceNotFoundException;
import com.everrefine.elms.domain.model.featureflags.FeatureFlag;
import com.everrefine.elms.domain.repository.FeatureFlagRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** {@link FeatureFlagApplicationService} の実装。 */
@Service
@AllArgsConstructor
public class FeatureFlagApplicationServiceImpl implements FeatureFlagApplicationService {

  private final FeatureFlagRepository featureFlagRepository;

  @Override
  @Transactional(readOnly = true)
  public FeatureFlagDto findFeatureFlagByKey(String key) {
    FeatureFlag featureFlag =
        featureFlagRepository
            .findFeatureFlagByKey(key)
            .orElseThrow(() -> new ResourceNotFoundException(FeatureFlag.class, key));
    return FeatureFlagDto.from(featureFlag);
  }

  @Override
  @Transactional
  public void updateFeatureFlag(FeatureFlagUpdateCommand featureFlagUpdateCommand) {
    FeatureFlag featureFlag =
        featureFlagRepository
            .findFeatureFlagByKey(featureFlagUpdateCommand.featureFlagKey())
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        FeatureFlag.class, featureFlagUpdateCommand.featureFlagKey()));
    featureFlagRepository.updateFeatureFlag(featureFlagUpdateCommand.toFeatureFlag(featureFlag));
  }
}
