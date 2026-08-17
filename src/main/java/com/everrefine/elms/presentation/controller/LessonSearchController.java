package com.everrefine.elms.presentation.controller;

import com.everrefine.elms.application.dto.LessonSearchResultDto;
import com.everrefine.elms.application.service.LessonApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** タグによるレッスン検索に関するコントローラー。 */
@Tag(name = "レッスン")
@RestController
@RequestMapping("/api/lessons")
@RequiredArgsConstructor
public class LessonSearchController {

  private final LessonApplicationService lessonApplicationService;

  /**
   * 指定されたタグ名に紐づくレッスンをページング検索する。
   *
   * @param tagName 検索対象のタグ名
   * @param pageNum ページ番号
   * @param pageSize 1ページあたりの件数
   * @return タグによるレッスン検索結果DTO
   */
  @Operation(summary = "タグによるレッスン検索", description = "指定されたタグ名に紐づくレッスンを全コースから検索します")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "検索成功"),
    @ApiResponse(responseCode = "400", description = "バリデーションエラー"),
    @ApiResponse(responseCode = "401", description = "認証されていません")
  })
  @GetMapping("/search")
  public LessonSearchResultDto searchLessonsByTagName(
      @RequestParam(name = "tag") @NotBlank String tagName,
      @RequestParam(name = "page", defaultValue = "1") @Positive int pageNum,
      @RequestParam(name = "pageSize", defaultValue = "10") @Positive int pageSize) {
    return lessonApplicationService.searchLessonsByTagName(tagName, pageNum, pageSize);
  }
}
