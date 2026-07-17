package com.everrefine.elms.domain.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/** レッスンタグのエンティティ。 */
@Getter
@AllArgsConstructor
@Table("lesson_tags")
public class LessonTag {

  @Id private final Integer id;

  @Column("lesson_id")
  private Integer lessonId;

  @Column("tag_id")
  private Integer tagId;

  @Column("created_at")
  private LocalDateTime createdAt;

  @Column("updated_at")
  private LocalDateTime updatedAt;

  /**
   * 新規作成用のレッスンタグを作成する。
   *
   * @param lessonId レッスンID
   * @param tagId タグID
   * @return 新規作成用のレッスンタグ
   */
  public static LessonTag create(Integer lessonId, Integer tagId) {
    LocalDateTime now = LocalDateTime.now();
    return new LessonTag(null, lessonId, tagId, now, now);
  }
}
