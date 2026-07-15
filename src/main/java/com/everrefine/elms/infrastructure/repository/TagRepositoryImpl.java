package com.everrefine.elms.infrastructure.repository;

import com.everrefine.elms.domain.model.tag.Name;
import com.everrefine.elms.domain.model.tag.Tag;
import com.everrefine.elms.domain.repository.TagRepository;
import com.everrefine.elms.infrastructure.dao.TagDao;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

/** {@link TagRepository} の実装クラス。 */
@Repository
@AllArgsConstructor
public class TagRepositoryImpl implements TagRepository {

  private final TagDao tagDao;

  @Override
  public void createTags(List<Tag> tags) {
    tagDao.saveAll(tags);
  }

  @Override
  public boolean existsTagByName(Name name) {
    return tagDao.existsByName(name.getValue());
  }

  @Override
  public List<Tag> findAllTagsByNames(List<Name> names) {
    List<String> stringNames = names.stream().map(Name::getValue).toList();
    return tagDao.findAllByNames(stringNames);
  }
}
