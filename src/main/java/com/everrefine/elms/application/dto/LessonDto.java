package com.everrefine.elms.application.dto;

import com.everrefine.elms.domain.model.lesson.Lesson;
import com.everrefine.elms.domain.model.lesson.LessonGroupWithLessons;
import com.everrefine.elms.domain.model.lesson.LessonInGroup;
import com.everrefine.elms.domain.model.tag.Tag;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/** レッスンのDTO。 */
public record LessonDto(
    @Schema(description = "レッスンID", example = "1") UUID id,
    @Schema(description = "レッスングループID", example = "2") UUID lessonGroupId,
    @Schema(description = "コースID", example = "3") UUID courseId,
    @Schema(description = "レッスンの表示順", example = "1.0") BigDecimal lessonOrder,
    @Schema(description = "レッスンタイトル", example = "変数とデータ型") String title,
    @Schema(description = "レッスン本文（Markdown対応）", example = "## 変数とは\n変数はデータを格納する箱です。")
        String content,
    @Schema(description = "動画URL", example = "https://example.com/videos/lesson1.mp4")
        String videoUrl,
    @Schema(description = "タグ一覧") List<TagDto> tags,
    @Schema(description = "登録日時", example = "2024-01-01T09:00:00") LocalDateTime createdAt,
    @Schema(description = "更新日時", example = "2024-06-01T10:30:00") LocalDateTime updatedAt) {

  /**
   * LessonエンティティからLessonDtoを生成する（タグなし）。
   *
   * @param lesson レッスンエンティティ
   * @return レッスンDTO
   */
  public static LessonDto from(Lesson lesson) {
    return from(lesson, List.of());
  }

  /**
   * Lessonエンティティとタグ一覧からLessonDtoを生成する。
   *
   * @param lesson レッスンエンティティ
   * @param tags レッスンに紐づくタグ一覧
   * @return レッスンDTO
   */
  public static LessonDto from(Lesson lesson, List<Tag> tags) {
    return new LessonDto(
        lesson.id(),
        lesson.lessonGroupId(),
        lesson.courseId(),
        lesson.lessonOrder().value(),
        lesson.title().value(),
        lesson.content() != null ? lesson.content().value() : null,
        lesson.videoUrl() != null ? lesson.videoUrl().value() : null,
        TagDto.from(tags),
        lesson.createdAt(),
        lesson.updatedAt());
  }

  /**
   * レッスングループと配下レッスンの読み取りモデルからLessonDtoを生成する。
   *
   * @param group 所属するレッスングループの読み取りモデル
   * @param lesson レッスングループ配下のレッスン読み取りモデル
   * @return レッスンDTO
   */
  public static LessonDto from(LessonGroupWithLessons group, LessonInGroup lesson) {
    return from(group.id(), group.courseId(), lesson);
  }

  /**
   * レッスングループ配下のレッスン読み取りモデルからLessonDtoを生成する。
   *
   * @param lessonGroupId 所属するレッスングループのID
   * @param courseId 所属するコースのID
   * @param lesson レッスングループ配下のレッスン読み取りモデル
   * @return レッスンDTO
   */
  public static LessonDto from(UUID lessonGroupId, UUID courseId, LessonInGroup lesson) {
    return new LessonDto(
        lesson.id(),
        lessonGroupId,
        courseId,
        lesson.lessonOrder(),
        lesson.title(),
        lesson.content(),
        lesson.videoUrl(),
        TagDto.from(lesson.tags()),
        lesson.createdAt(),
        lesson.updatedAt());
  }
}
