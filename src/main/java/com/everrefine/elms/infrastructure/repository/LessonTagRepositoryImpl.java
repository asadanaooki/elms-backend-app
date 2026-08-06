package com.everrefine.elms.infrastructure.repository;

import com.everrefine.elms.domain.model.LessonTag;
import com.everrefine.elms.domain.repository.LessonTagRepository;
import com.everrefine.elms.infrastructure.dao.LessonTagDao;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/** {@link LessonTagRepository} の実装クラス。 */
@Repository
@AllArgsConstructor
public class LessonTagRepositoryImpl implements LessonTagRepository {

  private final LessonTagDao lessonTagDao;
  private final JdbcTemplate jdbcTemplate;

  @Override
  public void deleteAllByLessonId(Integer lessonId) {
    lessonTagDao.deleteAllByLessonId(lessonId);
  }

  @Override
  public void createLessonTags(List<LessonTag> lessonTags) {
    if (lessonTags.isEmpty()) {
      return;
    }

    jdbcTemplate.batchUpdate(
        """
                INSERT INTO lesson_tags (lesson_id, tag_id, created_at, updated_at)
                VALUES (?, ?, ?, ?)
              """,
        lessonTags,
        lessonTags.size(),
        (ps, lessonTag) -> {
          ps.setInt(1, lessonTag.getLessonId());
          ps.setInt(2, lessonTag.getTagId());
          ps.setObject(3, lessonTag.getCreatedAt());
          ps.setObject(4, lessonTag.getUpdatedAt());
        });
  }
}
