package com.everrefine.elms.infrastructure.repository;

import com.everrefine.elms.domain.model.LessonTag;
import com.everrefine.elms.domain.repository.LessonTagRepository;
import com.everrefine.elms.infrastructure.dao.LessonTagDao;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

/** {@link LessonTagRepository} の実装クラス。 */
@Repository
@AllArgsConstructor
public class LessonTagRepositoryImpl implements LessonTagRepository {

  private final LessonTagDao lessonTagDao;

  @Override
  public void saveAll(List<LessonTag> lessonTags) {
    lessonTagDao.saveAll(lessonTags);
  }

  @Override
  public void deleteByLessonId(Integer lessonId) {
    lessonTagDao.deleteByLessonId(lessonId);
  }
}
