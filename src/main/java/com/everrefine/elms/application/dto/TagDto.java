package com.everrefine.elms.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
}
