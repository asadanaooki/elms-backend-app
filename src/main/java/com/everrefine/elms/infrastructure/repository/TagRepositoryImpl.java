package com.everrefine.elms.infrastructure.repository;

import com.everrefine.elms.domain.model.tag.Name;
import com.everrefine.elms.domain.model.tag.Tag;
import com.everrefine.elms.domain.repository.TagRepository;
import com.everrefine.elms.infrastructure.dao.TagDao;
import java.util.List;
import java.util.stream.StreamSupport;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

/** {@link TagRepository} の実装クラス。 */
@Repository
@AllArgsConstructor
public class TagRepositoryImpl implements TagRepository {

  private final TagDao tagDao;

  @Override
  public List<Tag> createTags(List<Tag> tags) {
    return StreamSupport.stream(tagDao.saveAll(tags).spliterator(), false).toList();
  }

  @Override
  public boolean existsTagByName(Name name) {
    return tagDao.existsByName(name.getValue());
  }
}
