package com.everrefine.elms.infrastructure.entity.tag;

import com.everrefine.elms.domain.model.tag.Tag;
import com.everrefine.elms.domain.model.tag.TagName;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/** タグのエンティティ。 */
@Table("tags")
public record TagEntity(
    @Id UUID id, String name, LocalDateTime createdAt, LocalDateTime updatedAt) {

  /**
   * ドメインモデルからエンティティを生成する。
   *
   * @param tag タグのドメインモデル
   * @return エンティティ
   */
  public static TagEntity from(Tag tag) {
    return new TagEntity(tag.id(), tag.name().value(), tag.createdAt(), tag.updatedAt());
  }

  /**
   * ドメインモデルに変換する。
   *
   * @return タグのドメインモデル
   */
  public Tag toDomain() {
    return new Tag(id, new TagName(name), createdAt, updatedAt);
  }
}
