package com.everrefine.elms.infrastructure.repository;

import com.everrefine.elms.domain.model.tag.Tag;
import com.everrefine.elms.domain.model.tag.TagName;
import com.everrefine.elms.domain.repository.TagRepository;
import com.everrefine.elms.infrastructure.dao.TagDao;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/** {@link TagRepository} の実装クラス。 */
@Repository
@AllArgsConstructor
public class TagRepositoryImpl implements TagRepository {

  private final TagDao tagDao;
  private final JdbcTemplate jdbcTemplate;

  @Override
  public List<Tag> findAllTagsByNames(List<TagName> names) {
    List<String> stringNames = names.stream().map(TagName::getValue).toList();
    return tagDao.findAllByNameIn(stringNames);
  }

  @Override
  public List<Tag> findAllTagsByLessonId(Integer lessonId) {
    return tagDao.findAllByLessonId(lessonId);
  }

  @Override
  public void createTags(List<Tag> tags) {
    if (tags.isEmpty()) {
      return;
    }

    jdbcTemplate.batchUpdate(
        """
                  INSERT INTO tags (name, created_at, updated_at)
                  VALUES (?, ?, ?)
                """,
        tags,
        tags.size(),
        (ps, tag) -> {
          ps.setString(1, tag.getName().getValue());
          ps.setObject(2, tag.getCreatedAt());
          ps.setObject(3, tag.getUpdatedAt());
        });
  }
}
