package com.everrefine.elms.domain.model.tag;

import java.time.LocalDateTime;
import java.util.UUID;

/** レッスンとタグの紐付けを表すドメインモデル。 */
public record LessonTag(
    UUID id, UUID lessonId, UUID tagId, LocalDateTime createdAt, LocalDateTime updatedAt) {

  /**
   * 新規作成用のレッスンタグを作成する。
   *
   * @param lessonId レッスンID
   * @param tagId タグID
   * @return 新規作成用のレッスンタグ
   */
  public static LessonTag create(UUID lessonId, UUID tagId) {
    LocalDateTime now = LocalDateTime.now();
    return new LessonTag(null, lessonId, tagId, now, now);
  }

  /**
   * IDを設定したレッスンタグを返す。
   *
   * <p>一括登録では {@code save()} を経由せず {@code insertAll()} で直接INSERTするため、DB採番に頼らずアプリケーション側でIDを確定できる。
   * IDが確定していると、JDBCドライバが複数レコードを1つのINSERT文にまとめられる。
   *
   * @param id レッスンタグID
   * @return IDを設定したレッスンタグ
   */
  public LessonTag withId(UUID id) {
    return new LessonTag(id, lessonId, tagId, createdAt, updatedAt);
  }
}
