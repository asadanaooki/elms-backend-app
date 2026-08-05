package com.everrefine.elms.infrastructure.dao;

import com.everrefine.elms.infrastructure.entity.tag.LessonTagEntity;
import java.util.UUID;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/** レッスンタグのDAOインターフェース。 */
@Repository
public interface LessonTagDao extends CrudRepository<LessonTagEntity, UUID> {

  @Modifying
  @Query(
      """
          DELETE FROM lesson_tags
          WHERE lesson_id = :lessonId
          """)
  void deleteAllByLessonId(@Param("lessonId") UUID lessonId);
}
