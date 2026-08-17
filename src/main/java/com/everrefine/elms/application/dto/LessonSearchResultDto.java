package com.everrefine.elms.application.dto;

import com.everrefine.elms.domain.model.lesson.LessonSummary;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

/** タグによるレッスン検索結果のDTO。 */
public record LessonSearchResultDto(
    @Schema(description = "検索対象のタグ名", example = "Java") String tag,
    @Schema(description = "検索対象のレッスンを含むコース一覧") List<LessonSearchCourseDto> courses) {

  /**
   * 検索対象のタグ名と、そのタグに紐づくレッスンの読み取りモデル一覧からDTOを生成する。
   *
   * @param tagName 検索対象のタグ名
   * @param lessonSummaries タグに紐づくレッスンの読み取りモデル一覧
   * @return タグによるレッスン検索結果DTO
   */
  public static LessonSearchResultDto from(String tagName, List<LessonSummary> lessonSummaries) {
    List<LessonSearchCourseDto> courseDtos =
        lessonSummaries.stream()
            .collect(
                Collectors.groupingBy(
                    LessonSummary::courseId,
                    LinkedHashMap::new,
                    Collectors.groupingBy(
                        LessonSummary::lessonGroupId, LinkedHashMap::new, Collectors.toList())))
            .values()
            .stream()
            .map(
                lessonSummariesByLessonGroupId ->
                    LessonSearchCourseDto.from(lessonSummariesByLessonGroupId.values()))
            .toList();
    return new LessonSearchResultDto(tagName, courseDtos);
  }
}
