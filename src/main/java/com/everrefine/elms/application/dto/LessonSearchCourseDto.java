package com.everrefine.elms.application.dto;

import com.everrefine.elms.domain.model.lesson.LessonSummary;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/** タグによるレッスン検索結果に含まれるコースのDTO。 */
public record LessonSearchCourseDto(
    @Schema(description = "コースID", example = "11111111-1111-1111-1111-111111111111") UUID courseId,
    @Schema(description = "コースの表示順", example = "1.0") BigDecimal courseOrder,
    @Schema(description = "コースタイトル", example = "コースA") String courseTitle,
    @Schema(description = "検索対象のレッスンを含むレッスングループ一覧") List<LessonSearchLessonGroupDto> lessonGroups) {

  /**
   * タグ検索結果に含まれる同一コースの読み取りモデルからDTOを生成する。
   *
   * @param groupedLessonSummaries 同一コースに属する読み取りモデルをレッスングループ単位にまとめたコレクション
   * @return レッスン検索結果に含まれるコースDTO
   */
  public static LessonSearchCourseDto from(Collection<List<LessonSummary>> groupedLessonSummaries) {
    LessonSummary firstLessonSummary = groupedLessonSummaries.iterator().next().getFirst();
    return new LessonSearchCourseDto(
        firstLessonSummary.courseId(),
        firstLessonSummary.courseOrder(),
        firstLessonSummary.courseTitle(),
        groupedLessonSummaries.stream().map(LessonSearchLessonGroupDto::from).toList());
  }
}
