package com.everrefine.elms.infrastructure.row;

import com.everrefine.elms.domain.model.lesson.LessonSummary;
import com.everrefine.elms.domain.model.tag.Tag;
import com.everrefine.elms.domain.model.tag.TagName;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 指定されたタグに紐づくレッスンを検索するJOINクエリの、1行分の結果を表す読み取りモデル。
 *
 * <p>1件のレッスンに複数のタグが紐づく場合は、タグごとに1行ずつ取得される。 {@link #toDomainList(List)} はそれらの行をレッスン単位に集約し、タグ一覧を持つ
 * {@link LessonSummary} へ変換する。
 */
public record LessonSummaryRow(
    UUID courseId,
    BigDecimal courseOrder,
    String courseTitle,
    UUID lessonGroupId,
    BigDecimal lessonGroupOrder,
    String lessonGroupTitle,
    UUID lessonId,
    BigDecimal lessonOrder,
    String title,
    UUID tagId,
    String tagName,
    LocalDateTime tagCreatedAt,
    LocalDateTime tagUpdatedAt) {

  /**
   * JOINクエリの結果をレッスン単位に集約し、レッスン概要の一覧へ変換する。
   *
   * @param rows JOINクエリによって取得した行の一覧
   * @return コース・レッスングループ・タグの情報を含むレッスン概要の一覧
   */
  public static List<LessonSummary> toDomainList(List<LessonSummaryRow> rows) {
    return rows.stream()
        .collect(
            Collectors.groupingBy(
                LessonSummaryRow::lessonId, LinkedHashMap::new, Collectors.toList()))
        .values()
        .stream()
        .map(LessonSummaryRow::toLessonSummary)
        .toList();
  }

  /**
   * 同一レッスンを表す行の一覧を、1件のレッスン概要へ変換する。
   *
   * @param rows 同一レッスンを表す行の一覧
   * @return コース・レッスングループ・タグの情報を含むレッスン概要
   */
  private static LessonSummary toLessonSummary(List<LessonSummaryRow> rows) {
    LessonSummaryRow head = rows.getFirst();
    return new LessonSummary(
        head.courseId(),
        head.courseOrder(),
        head.courseTitle(),
        head.lessonGroupId(),
        head.lessonGroupOrder(),
        head.lessonGroupTitle(),
        head.lessonId,
        head.lessonOrder,
        head.title,
        rows.stream()
            .map(
                row ->
                    new Tag(
                        row.tagId(),
                        new TagName(row.tagName()),
                        row.tagCreatedAt(),
                        row.tagUpdatedAt()))
            .toList());
  }
}
