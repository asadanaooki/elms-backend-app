package com.everrefine.elms.domain.model.tag;

import java.time.LocalDateTime;
import java.util.UUID;

/** タグのドメインモデル。 */
public record Tag(UUID id, TagName name, LocalDateTime createdAt, LocalDateTime updatedAt) {

  /**
   * 新規作成用のタグを作成する。
   *
   * <p>タグ名の前後の空白は取り除く。同じタグ名が空白の有無だけで重複登録されるのを防ぐため。
   *
   * @param name タグ名
   * @return 新規作成用のタグ
   */
  public static Tag create(String name) {
    LocalDateTime now = LocalDateTime.now();
    return new Tag(null, new TagName(name.strip()), now, now);
  }

  /**
   * IDを設定したタグを返す。
   *
   * <p>一括登録では {@code save()} を経由せず {@code insertAll()} で直接INSERTするため、DB採番に頼らずアプリケーション側でIDを確定できる。
   * IDが確定していると、JDBCドライバが複数レコードを1つのINSERT文にまとめられる。
   *
   * @param id タグID
   * @return IDを設定したタグ
   */
  public Tag withId(UUID id) {
    return new Tag(id, name, createdAt, updatedAt);
  }
}
