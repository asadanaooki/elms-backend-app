package com.everrefine.elms.infrastructure.dao;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.everrefine.elms.domain.model.tag.Tag;

/** タグのDAOインターフェース。 */
public interface TagDao extends CrudRepository<Tag, Integer> {

  List<Tag> findAllByNameIn(List<String> names);
  
  @Query(
          """
          SELECT 
            t.id,
            t.name,
            t.created_at,
            t.updated_at
          FROM tags t
          INNER JOIN lesson_tags lt on t.id = lt.tag_id
          WHERE lt.lesson_id = :lessonId
          ORDER BY t.id ASC
          """)
  List<Tag> findAllByLessonId(@Param("lessonId") Integer lessonId);
}
