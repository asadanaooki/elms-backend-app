package com.everrefine.elms.infrastructure.entity.tag;

import com.everrefine.elms.domain.model.tag.LessonTag;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/** レッスンタグのエンティティ。 */
@Table("lesson_tags")
public record LessonTagEntity(
    @Id UUID id, UUID lessonId, UUID tagId, LocalDateTime createdAt, LocalDateTime updatedAt) {

  /**
   * ドメインモデルからエンティティを生成する。
   *
   * @param lessonTag レッスンタグのドメインモデル
   * @return エンティティ
   */
  public static LessonTagEntity from(LessonTag lessonTag) {
    return new LessonTagEntity(
        lessonTag.id(),
        lessonTag.lessonId(),
        lessonTag.tagId(),
        lessonTag.createdAt(),
        lessonTag.updatedAt());
  }

  /**
   * ドメインモデルに変換する。
   *
   * @return レッスンタグのドメインモデル
   */
  public LessonTag toDomain() {
    return new LessonTag(id, lessonId, tagId, createdAt, updatedAt);
  }
}
