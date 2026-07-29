package com.everrefine.elms.application.dto;

import com.everrefine.elms.domain.model.tag.Tag;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

/** タグDTOに関するクラス。 */
@AllArgsConstructor
@Getter
public class TagDto {

  @Schema(description = "タグID", example = "1")
  private final Integer id;

  @Schema(description = "タグ名", example = "Git")
  private final String name;

  /**
   * 複数のTagエンティティからTagDto一覧を生成する。
   *
   * @param tags 複数のタグエンティティ
   * @return タグDTO一覧
   */
  public static List<TagDto> from(List<Tag> tags) {
    return tags.stream().map(tag -> new TagDto(tag.getId(), tag.getName().getValue())).toList();
  }
}
