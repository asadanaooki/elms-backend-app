package com.everrefine.elms.infrastructure.dao;

import com.everrefine.elms.domain.model.LessonTag;
import com.everrefine.elms.domain.model.LessonTag.LessonTagId;
import java.time.LocalDateTime;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/** レッスンタグのDAOインターフェース。 */
@Repository
public interface LessonTagDao extends CrudRepository<LessonTag, LessonTagId> {

  @Modifying
  @Query(
      """
          INSERT INTO lesson_tags(lesson_id, tag_id, created_at, updated_at)
          VALUES(:lessonId, :tagId, :createdAt, :updatedAt)
          """)
  void create(
      @Param("lessonId") Integer lessonId,
      @Param("tagId") Integer tagId,
      @Param("createdAt") LocalDateTime createdAt,
      @Param("updatedAt") LocalDateTime updatedAt);

  @Modifying
  void deleteByLessonId(Integer lessonId);
}
