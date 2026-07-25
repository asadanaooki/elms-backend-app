package com.everrefine.elms.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.everrefine.elms.domain.model.lesson.Lesson;
import com.everrefine.elms.domain.model.lesson.LessonGroupWithLessonAndTag;
import com.everrefine.elms.domain.model.tag.Tag;

import lombok.Getter;

/** レッスン DTO。 */
@Getter
public class LessonDto extends BaseLessonDto {

  /**
   * LessonエンティティからLessonDtoを生成する。
   *
   * @param lesson レッスンエンティティ
   * @return レッスンDTO
   */
  public static LessonDto from(Lesson lesson, List<Tag> tags) {
    return new LessonDto(
        lesson.getId(),
        lesson.getLessonGroupId(),
        lesson.getCourseId(),
        lesson.getLessonOrder().getValue(),
        lesson.getTitle().getValue(),
        lesson.getContent() != null ? lesson.getContent().getValue() : null,
        lesson.getVideoUrl() != null ? lesson.getVideoUrl().getValue() : null,
        tags.stream().map(t -> new TagDto(t.getId(), t.getName().getValue())).toList(),
        lesson.getCreatedAt(),
        lesson.getUpdatedAt());
  }

  /**
   * LessonGroupWithLessonAndTagからLessonDtoを生成する。
   *
   * @param LessonGroupWithLessonAndTag レッスングループとレッスンの結合情報
   * @return レッスンDTO
   */
  public static LessonDto from(LessonGroupWithLessonAndTag lessonGroupWithLessonAndTag) {
      return new LessonDto(
        lessonGroupWithLessonAndTag.getLessonId(),
        lessonGroupWithLessonAndTag.getLessonGroupId(),
        lessonGroupWithLessonAndTag.getCourseId(),
        lessonGroupWithLessonAndTag.getLessonOrder(),
        lessonGroupWithLessonAndTag.getLessonTitle(),
        lessonGroupWithLessonAndTag.getLessonContent(),
        lessonGroupWithLessonAndTag.getLessonVideoUrl(),
        lessonGroupWithLessonAndTag.getTags().stream()
            .map(t -> new TagDto(t.getId(), t.getName().getValue()))
            .toList(),
        lessonGroupWithLessonAndTag.getLessonCreatedAt(),
        lessonGroupWithLessonAndTag.getLessonUpdatedAt());
  }

  private LessonDto(
      Integer id,
      Integer lessonGroupId,
      Integer courseId,
      BigDecimal lessonOrder,
      String title,
      String content,
      String videoUrl,
      List<TagDto> tags,
      LocalDateTime createdAt,
      LocalDateTime updatedAt) {
    super(
        id,
        lessonGroupId,
        courseId,
        lessonOrder,
        title,
        content,
        videoUrl,
        tags,
        createdAt,
        updatedAt);
  }
}
