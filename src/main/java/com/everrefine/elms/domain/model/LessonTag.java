package com.everrefine.elms.domain.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Table;

/** レッスンタグのエンティティ。 */
@Getter
@AllArgsConstructor
@Table("lesson_tags")
public class LessonTag {

  @Id @Embedded.Nullable private final LessonTagId id;

  @Column("created_at")
  private LocalDateTime createdAt;

  @Column("updated_at")
  private LocalDateTime updatedAt;

  public record LessonTagId(Integer lessonId, Integer tagId) {}

  /**
   * 新規作成用のレッスンタグを作成する。
   *
   * @param lessonId レッスンID
   * @param tagId タグID
   * @return 新規作成用のレッスンタグ
   */
  public static LessonTag create(Integer lessonId, Integer tagId) {
    LocalDateTime now = LocalDateTime.now();
    return new LessonTag(new LessonTagId(lessonId, tagId), now, now);
  }
}
