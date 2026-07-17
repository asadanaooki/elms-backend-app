package com.everrefine.elms.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/** レッスンのタグに関するクラス。 */
@Data
public class LessonTagRequest {

  @Schema(description = "タグ名", example = "Git")
  @NotBlank
  @Size(max = 255)
  private String name;
}
