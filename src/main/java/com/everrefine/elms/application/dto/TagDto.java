package com.everrefine.elms.application.dto;

import com.everrefine.elms.domain.model.tag.Tag;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;

/** タグのDTO。 */
public record TagDto(
    @Schema(description = "タグID", example = "1") UUID id,
    @Schema(description = "タグ名", example = "Git") String name) {

  /**
   * TagエンティティからTagDtoを生成する。
   *
   * @param tag タグエンティティ
   * @return タグDTO
   */
  public static TagDto from(Tag tag) {
    return new TagDto(tag.id(), tag.name().value());
  }

  /**
   * 複数のTagエンティティからTagDto一覧を生成する。
   *
   * @param tags タグエンティティ一覧
   * @return タグDTO一覧
   */
  public static List<TagDto> from(List<Tag> tags) {
    return tags.stream().map(TagDto::from).toList();
  }
}
