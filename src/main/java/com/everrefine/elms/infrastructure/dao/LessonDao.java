package com.everrefine.elms.infrastructure.dao;

import com.everrefine.elms.infrastructure.entity.lesson.LessonEntity;
import com.everrefine.elms.infrastructure.row.LessonSummaryRow;
import com.everrefine.elms.infrastructure.row.LessonWithCourseAndLessonGroupRow;
import com.everrefine.elms.infrastructure.row.LessonWithTagRow;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/** レッスンのDAOインターフェース。 */
public interface LessonDao extends CrudRepository<LessonEntity, UUID> {

  @Query(
      """
          SELECT * FROM lessons WHERE
          (CAST(:courseId AS UUID) IS NULL OR course_id = CAST(:courseId AS UUID)) AND
          (CAST(:lessonGroupId AS UUID) IS NULL OR lesson_group_id = CAST(:lessonGroupId AS UUID)) AND
          (:title IS NULL OR title LIKE CONCAT('%', :title, '%')) AND
          (CAST(:createdDateFrom AS DATE) IS NULL OR created_at >= CAST(:createdDateFrom AS DATE)) AND
          (CAST(:createdDateTo AS DATE) IS NULL OR created_at < CAST(:createdDateTo AS DATE) + INTERVAL '1 day')
          ORDER BY lesson_order ASC
          LIMIT :limit OFFSET :offset
          """)
  List<LessonEntity> findLessons(
      @Param("courseId") UUID courseId,
      @Param("lessonGroupId") UUID lessonGroupId,
      @Param("title") String title,
      @Param("createdDateFrom") LocalDate createdDateFrom,
      @Param("createdDateTo") LocalDate createdDateTo,
      @Param("limit") int limit,
      @Param("offset") int offset);

  @Query(
      """
          SELECT COUNT(*) FROM lessons WHERE
          (CAST(:courseId AS UUID) IS NULL OR course_id = CAST(:courseId AS UUID)) AND
          (CAST(:lessonGroupId AS UUID) IS NULL OR lesson_group_id = CAST(:lessonGroupId AS UUID)) AND
          (:title IS NULL OR title LIKE CONCAT('%', :title, '%')) AND
          (CAST(:createdDateFrom AS DATE) IS NULL OR created_at >= CAST(:createdDateFrom AS DATE)) AND
          (CAST(:createdDateTo AS DATE) IS NULL OR created_at < CAST(:createdDateTo AS DATE) + INTERVAL '1 day')
          """)
  int countLessons(
      @Param("courseId") UUID courseId,
      @Param("lessonGroupId") UUID lessonGroupId,
      @Param("title") String title,
      @Param("createdDateFrom") LocalDate createdDateFrom,
      @Param("createdDateTo") LocalDate createdDateTo);

  @Query(
      """
          SELECT MAX(lesson_order)
          FROM lessons
          WHERE lesson_group_id = :lessonGroupId
          """)
  Optional<BigDecimal> findMaxLessonOrderByLessonGroupId(
      @Param("lessonGroupId") UUID lessonGroupId);

  List<LessonEntity> findByIdIn(@Param("lessonIds") List<UUID> lessonIds);

  @Query(
      """
          SELECT
            l.id as lesson_id,
            l.title as lesson_title,
            l.lesson_order,
            l.content as lesson_content,
            l.video_url as lesson_video_url,
            l.created_at as lesson_created_at,
            l.updated_at as lesson_updated_at,
            t.id as tag_id,
            t.name as tag_name,
            t.created_at as tag_created_at,
            t.updated_at as tag_updated_at
          FROM lessons l
          LEFT JOIN lesson_tags lt ON l.id = lt.lesson_id
          LEFT JOIN tags t ON lt.tag_id = t.id
          WHERE l.lesson_group_id = :lessonGroupId
          ORDER BY l.lesson_order ASC, t.name ASC
          """)
  List<LessonWithTagRow> findLessonsByLessonGroupId(@Param("lessonGroupId") UUID lessonGroupId);

  @Modifying
  @Query(
      """
          DELETE FROM lessons
          WHERE course_id = :courseId
          """)
  void deleteByCourseId(@Param("courseId") UUID courseId);

  @Query(
      """
      SELECT COUNT(*)
      FROM lessons
      """)
  int countAllLessons();

  @Query(
      """
          SELECT video_url
          FROM lessons
          WHERE video_url LIKE CONCAT(:prefix, '%')
          """)
  List<String> findByVideoUrlStartingWith(String prefix);

  @Query(
      """
         SELECT
          c.id AS course_id,
          c.title AS course_title,
          lg.id AS lesson_group_id,
          lg.title AS lesson_group_title,
          l.id AS lesson_id,
          l.title AS lesson_title,
          l.video_url AS lesson_video_url
         FROM courses c
          INNER JOIN lesson_groups lg
          ON lg.course_id = c.id
          INNER JOIN lessons l
          ON l.lesson_group_id = lg.id
         ORDER BY c.course_order ASC, lg.lesson_group_order ASC, l.lesson_order ASC
         """)
  List<LessonWithCourseAndLessonGroupRow> findByAllLessons();

  @Query(
      """
          WITH paged_lessons AS (
            SELECT
              c.id AS course_id,
              c.course_order,
              c.title AS course_title,
              lg.id AS lesson_group_id,
              lg.lesson_group_order,
              lg.title AS lesson_group_title,
              l.id AS lesson_id,
              l.lesson_order,
              l.title
            FROM lessons l
            INNER JOIN lesson_tags matched_lt ON matched_lt.lesson_id = l.id
            INNER JOIN tags matched_t ON matched_t.id = matched_lt.tag_id
            INNER JOIN lesson_groups lg ON lg.id = l.lesson_group_id
            INNER JOIN courses c ON c.id = l.course_id
            WHERE matched_t.name = :tagName
            ORDER BY c.course_order ASC, lg.lesson_group_order ASC, l.lesson_order ASC
            LIMIT :pageSize
            OFFSET :offset
          )
          SELECT
            pl.course_id,
            pl.course_order,
            pl.course_title,
            pl.lesson_group_id,
            pl.lesson_group_order,
            pl.lesson_group_title,
            pl.lesson_id,
            pl.lesson_order,
            pl.title,
            t.id AS tag_id,
            t.name AS tag_name,
            t.created_at AS tag_created_at,
            t.updated_at AS tag_updated_at
          FROM paged_lessons pl
          INNER JOIN lesson_tags lt ON lt.lesson_id = pl.lesson_id
          INNER JOIN tags t ON lt.tag_id = t.id
          ORDER BY pl.course_order ASC, pl.lesson_group_order ASC, pl.lesson_order ASC
          """)
  List<LessonSummaryRow> findLessonsByTagName(
      @Param("tagName") String tagName,
      @Param("pageSize") int pageSize,
      @Param("offset") int offset);

  @Query(
      """
          SELECT COUNT(*)
          FROM lessons l
          INNER JOIN lesson_tags lt ON lt.lesson_id = l.id
          INNER JOIN tags t ON t.id = lt.tag_id
          WHERE t.name = :tagName
          """)
  int countLessonsByTagName(@Param("tagName") String tagName);
}
