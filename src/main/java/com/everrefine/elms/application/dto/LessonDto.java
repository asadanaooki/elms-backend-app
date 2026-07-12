package com.everrefine.elms.application.dto;

import com.everrefine.elms.domain.model.lesson.Lesson;
import com.everrefine.elms.domain.model.lesson.LessonGroupWithLesson;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
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
  public static LessonDto from(Lesson lesson, List<TagDto> tags) {
    return new LessonDto(
        lesson.getId(),
        lesson.getLessonGroupId(),
        lesson.getCourseId(),
        lesson.getLessonOrder().getValue(),
        lesson.getTitle().getValue(),
        lesson.getContent() != null ? lesson.getContent().getValue() : null,
        lesson.getVideoUrl() != null ? lesson.getVideoUrl().getValue() : null,
        tags != null ? tags : Collections.emptyList(),
        lesson.getCreatedAt(),
        lesson.getUpdatedAt());
  }

  /**
   * LessonGroupWithLessonからLessonDtoを生成する。
   *
   * @param lessonGroupWithLesson レッスングループとレッスンの結合情報
   * @return レッスンDTO
   */
  public static LessonDto from(LessonGroupWithLesson lessonGroupWithLesson) {
    return new LessonDto(
        lessonGroupWithLesson.getLessonId(),
        lessonGroupWithLesson.getLessonGroupId(),
        lessonGroupWithLesson.getCourseId(),
        lessonGroupWithLesson.getLessonOrder(),
        lessonGroupWithLesson.getLessonTitle(),
        lessonGroupWithLesson.getLessonContent(),
        lessonGroupWithLesson.getLessonVideoUrl(),
        Collections.emptyList(),
        lessonGroupWithLesson.getLessonCreatedAt(),
        lessonGroupWithLesson.getLessonUpdatedAt());
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
