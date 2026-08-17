package com.everrefine.elms.application.dto;

import com.everrefine.elms.domain.model.lesson.LessonSummary;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/** タグによるレッスン検索結果に含まれるレッスングループのDTO。 */
public record LessonSearchLessonGroupDto(
    @Schema(description = "レッスングループID", example = "22222222-2222-2222-2222-222222222222")
        UUID lessonGroupId,
    @Schema(description = "レッスングループの表示順", example = "1.0") BigDecimal lessonGroupOrder,
    @Schema(description = "レッスングループタイトル", example = "レッスングループA") String lessonGroupTitle,
    @Schema(description = "指定されたタグに紐づくレッスン一覧") List<LessonSearchLessonDto> lessons) {

  /**
   * タグ検索結果に含まれる同一レッスングループの読み取りモデル一覧からDTOを生成する。
   *
   * @param lessonSummaries 同一レッスングループに属するレッスンの読み取りモデル一覧
   * @return レッスン検索結果に含まれるレッスングループDTO
   */
  public static LessonSearchLessonGroupDto from(List<LessonSummary> lessonSummaries) {
    LessonSummary firstLessonSummary = lessonSummaries.getFirst();
    return new LessonSearchLessonGroupDto(
        firstLessonSummary.lessonGroupId(),
        firstLessonSummary.lessonGroupOrder(),
        firstLessonSummary.lessonGroupTitle(),
        lessonSummaries.stream()
            .map(lessonSummary -> LessonSearchLessonDto.from(lessonSummary.lesson()))
            .toList());
  }
}
