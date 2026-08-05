package com.everrefine.elms.infrastructure.row;

import com.everrefine.elms.domain.model.lesson.LessonGroupWithLessons;
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

/** レッスングループ・レッスン・タグをJOINしたセレクト結果の1行。 */
public record LessonGroupWithLessonRow(
    @Id UUID lessonId,
    String lessonTitle,
    BigDecimal lessonOrder,
    @Nullable String lessonContent,
    @Nullable String lessonVideoUrl,
    LocalDateTime lessonCreatedAt,
    LocalDateTime lessonUpdatedAt,
    UUID lessonGroupId,
    UUID courseId,
    String lessonGroupTitle,
    BigDecimal lessonGroupOrder,
    LocalDateTime lessonGroupCreatedAt,
    LocalDateTime lessonGroupUpdatedAt,
    @Nullable UUID tagId,
    @Nullable String tagName,
    @Nullable LocalDateTime tagCreatedAt,
    @Nullable LocalDateTime tagUpdatedAt) {

  /**
   * JOIN結果の行一覧を、レッスングループ単位で入れ子にまとめた読み取りモデルへ変換する。
   *
   * <p>行はレッスングループ順・レッスン順・タグ名順で並んでいることを前提とし、その並び順を保持する。 レッスンを持たないグループ（LEFT
   * JOINでレッスンがnullの行）は、空のレッスン一覧を持つグループとして扱う。
   *
   * <p>1レッスンに複数タグが紐づく場合、そのレッスンは複数行に展開されるため、レッスン単位でまとめ直す。
   *
   * @param rows JOIN結果の行一覧
   * @return レッスングループごとに配下レッスンをまとめた読み取りモデル一覧
   */
  public static List<LessonGroupWithLessons> toDomainList(List<LessonGroupWithLessonRow> rows) {
    return rows.stream()
        .collect(
            Collectors.groupingBy(
                LessonGroupWithLessonRow::lessonGroupId, LinkedHashMap::new, Collectors.toList()))
        .values()
        .stream()
        .map(LessonGroupWithLessonRow::toLessonGroup)
        .toList();
  }

  /**
   * 同一レッスングループに属する行一覧を、1つのレッスングループの読み取りモデルへ変換する。
   *
   * @param groupRows 同一レッスングループの行一覧
   * @return レッスングループの読み取りモデル
   */
  private static LessonGroupWithLessons toLessonGroup(List<LessonGroupWithLessonRow> groupRows) {
    LessonGroupWithLessonRow head = groupRows.getFirst();
    return new LessonGroupWithLessons(
        head.lessonGroupId(),
        head.courseId(),
        head.lessonGroupTitle(),
        head.lessonGroupOrder(),
        head.lessonGroupCreatedAt(),
        head.lessonGroupUpdatedAt(),
        toLessonsInGroup(groupRows));
  }

  /**
   * 行一覧をレッスン単位にまとめ直し、タグ一覧を持つレッスンの読み取りモデル一覧へ変換する。
   *
   * <p>タグを持たないレッスン（LEFT JOINでタグがnullの行）は、空のタグ一覧を持つレッスンとして扱う。
   *
   * @param rows 変換対象の行一覧
   * @return レッスングループ配下のレッスン読み取りモデル一覧
   */
  private static List<LessonInGroup> toLessonsInGroup(List<LessonGroupWithLessonRow> rows) {
    return rows.stream()
        .filter(row -> row.lessonId() != null)
        .collect(
            Collectors.groupingBy(
                LessonGroupWithLessonRow::lessonId, LinkedHashMap::new, Collectors.toList()))
        .values()
        .stream()
        .map(LessonGroupWithLessonRow::toLessonInGroup)
        .toList();
  }

  /**
   * 同一レッスンの行一覧を、タグ一覧を持つレッスンの読み取りモデルへ変換する。
   *
   * @param lessonRows 同一レッスンの行一覧
   * @return レッスングループ配下のレッスン読み取りモデル
   */
  private static LessonInGroup toLessonInGroup(List<LessonGroupWithLessonRow> lessonRows) {
    LessonGroupWithLessonRow head = lessonRows.getFirst();
    List<Tag> tags =
        lessonRows.stream()
            .filter(row -> row.tagId() != null)
            .map(LessonGroupWithLessonRow::toTag)
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
