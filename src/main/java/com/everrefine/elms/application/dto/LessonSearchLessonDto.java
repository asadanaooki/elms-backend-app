package com.everrefine.elms.application.dto;

import com.everrefine.elms.domain.model.lesson.LessonSummaryItem;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/** タグによるレッスン検索結果に含まれるレッスンのDTO。 */
public record LessonSearchLessonDto(
    @Schema(description = "レッスンID", example = "33333333-3333-3333-3333-333333333333") UUID lessonId,
    @Schema(description = "レッスンの表示順", example = "1.0") BigDecimal lessonOrder,
    @Schema(description = "レッスンタイトル", example = "レッスン1") String title,
    @Schema(description = "レッスンに紐づくタグ一覧") List<TagDto> tags) {

  /**
   * タグ検索結果に含まれるレッスンの読み取りモデルからDTOを生成する。
   *
   * @param lessonSummaryItem レッスンの概要とタグ一覧を持つ読み取りモデル
   * @return レッスン検索結果に含まれるレッスンDTO
   */
  public static LessonSearchLessonDto from(LessonSummaryItem lessonSummaryItem) {
    return new LessonSearchLessonDto(
        lessonSummaryItem.lessonId(),
        lessonSummaryItem.lessonOrder(),
        lessonSummaryItem.lessonTitle(),
        lessonSummaryItem.tags().stream()
            .map(tag -> new TagDto(tag.id(), tag.name().value()))
            .toList());
  }
}
