package com.everrefine.elms.domain.model.tag;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/** タグのエンティティ。 */
@Getter
@AllArgsConstructor
@Table("tags")
public class Tag {

  @Id private final Integer id;

  private TagName name;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;

  /**
   * 新規作成用のタグを作成する。
   *
   * @param name タグ名
   * @return 新規作成用のタグ
   */
  public static Tag create(String name) {
    LocalDateTime now = LocalDateTime.now();
    return new Tag(null, new TagName(name.strip()), now, now);
  }
}
