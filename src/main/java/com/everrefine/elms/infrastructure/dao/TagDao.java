package com.everrefine.elms.infrastructure.dao;

import com.everrefine.elms.infrastructure.entity.tag.TagEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/** タグのDAOインターフェース。 */
@Repository
public interface TagDao extends CrudRepository<TagEntity, UUID> {

  List<TagEntity> findAllByNameIn(List<String> names);

  /**
   * レッスンに紐づくタグ一覧を取得する。
   *
   * <p>IDはUUIDで採番順に意味を持たないため、表示順が実行のたびに変わらないようタグ名の昇順で並べる。
   *
   * @param lessonId レッスンID
   * @return タグ一覧
   */
  @Query(
      """
          SELECT
            t.id,
            t.name,
            t.created_at,
            t.updated_at
          FROM tags t
          INNER JOIN lesson_tags lt ON t.id = lt.tag_id
          WHERE lt.lesson_id = :lessonId
          ORDER BY t.name ASC
          """)
  List<TagEntity> findAllByLessonId(@Param("lessonId") UUID lessonId);
}
