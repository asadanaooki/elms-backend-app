package com.everrefine.elms.infrastructure.row;

import com.everrefine.elms.domain.model.lesson.LessonInGroup;
import com.everrefine.elms.domain.model.tag.Tag;
import com.everrefine.elms.domain.model.tag.TagName;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.data.annotation.Id;
import org.springframework.lang.Nullable;

/** レッスンとタグをJOINしたセレクト結果の1行。 */
public record LessonWithTagRow(
    @Id UUID lessonId,
    String lessonTitle,
    BigDecimal lessonOrder,
    @Nullable String lessonContent,
    @Nullable String lessonVideoUrl,
    LocalDateTime lessonCreatedAt,
    LocalDateTime lessonUpdatedAt,
    @Nullable UUID tagId,
    @Nullable String tagName,
    @Nullable LocalDateTime tagCreatedAt,
    @Nullable LocalDateTime tagUpdatedAt) {

  /**
   * JOIN結果の行一覧を、レッスン単位でタグをまとめた読み取りモデルへ変換する。
   *
   * <p>行はレッスン順・タグ名順で並んでいることを前提とし、その並び順を保持する。 1レッスンに複数タグが紐づく場合、そのレッスンは複数行に展開されるため、レッスン単位でまとめ直す。
   * タグを持たないレッスン（LEFT JOINでタグがnullの行）は、空のタグ一覧を持つレッスンとして扱う。
   *
   * @param rows JOIN結果の行一覧
   * @return レッスンごとにタグをまとめた読み取りモデル一覧
   */
  public static List<LessonInGroup> toDomainList(List<LessonWithTagRow> rows) {
    return rows.stream()
        .collect(
            Collectors.groupingBy(
                LessonWithTagRow::lessonId, LinkedHashMap::new, Collectors.toList()))
        .values()
        .stream()
        .map(LessonWithTagRow::toLessonInGroup)
        .toList();
  }

  /**
   * 同一レッスンの行一覧を、タグ一覧を持つレッスンの読み取りモデルへ変換する。
   *
   * @param lessonRows 同一レッスンの行一覧
   * @return レッスンの読み取りモデル
   */
  private static LessonInGroup toLessonInGroup(List<LessonWithTagRow> lessonRows) {
    LessonWithTagRow head = lessonRows.getFirst();
    List<Tag> tags =
        lessonRows.stream()
            .filter(row -> row.tagId() != null)
            .map(LessonWithTagRow::toTag)
            .toList();
    return new LessonInGroup(
        head.lessonId(),
        head.lessonTitle(),
        head.lessonOrder(),
        head.lessonContent(),
        head.lessonVideoUrl(),
        head.lessonCreatedAt(),
        head.lessonUpdatedAt(),
        tags);
  }

  /**
   * この行のタグ部分を、タグのドメインモデルへ変換する。
   *
   * @return タグのドメインモデル
   */
  private Tag toTag() {
    return new Tag(tagId, new TagName(tagName), tagCreatedAt, tagUpdatedAt);
  }
}
