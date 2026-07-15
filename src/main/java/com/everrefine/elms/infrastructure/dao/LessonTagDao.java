package com.everrefine.elms.infrastructure.dao;

import com.everrefine.elms.domain.model.LessonTag;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/** レッスンタグのDAOインターフェース。 */
@Repository
public interface LessonTagDao extends CrudRepository<LessonTag, Integer> {

  @Modifying
  void deleteAllByLessonId(Integer lessonId);
}
