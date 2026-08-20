package com.everrefine.elms.application.dto;

import com.everrefine.elms.domain.model.lesson.LessonSummary;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

/** タグによるレッスン検索結果とページ情報を表すDTO。 */
public record LessonSearchPageDto(
    @Schema(description = "検索対象のタグ名", example = "Java") String tag,
    @Schema(description = "検索対象のレッスンを含むコース一覧") List<LessonSearchCourseDto> courses,
    @Schema(description = "現在のページ番号", example = "1") int pageNum,
    @Schema(description = "1ページ当たりの件数", example = "10") int pageSize,
    @Schema(description = "総データ件数", example = "42") int totalSize) {

  /**
   * 検索対象のタグ名、そのタグに紐づくレッスンの読み取りモデル一覧、およびページ情報からDTOを生成する。
   *
   * @param tagName 検索対象のタグ名
   * @param lessonSummaries タグに紐づくレッスンの読み取りモデル一覧
   * @param pageNum ページ番号
   * @param pageSize 1ページ当たりの件数
   * @param totalSize 総データ件数
   * @return タグによるレッスン検索結果のページDTO
   */
  public static LessonSearchPageDto from(
      String tagName,
      List<LessonSummary> lessonSummaries,
      int pageNum,
      int pageSize,
      int totalSize) {
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
    return new LessonSearchPageDto(tagName, courseDtos, pageNum, pageSize, totalSize);
  }
}
