package com.everrefine.elms.presentation.controller;

import com.everrefine.elms.application.dto.FeatureFlagDto;
import com.everrefine.elms.application.service.FeatureFlagApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** フィーチャーフラグに関するコントローラー。 */
@Tag(name = "フィーチャーフラグ")
@RestController
@RequestMapping("/api/feature-flags")
@RequiredArgsConstructor
public class FeatureFlagController {

  private final FeatureFlagApplicationService featureFlagApplicationService;

  /**
   * 指定したキーに一致するフィーチャーフラグを取得する。
   *
   * @param featureFlagKey フィーチャーフラグのキー
   * @return フィーチャーフラグDTO
   */
  @Operation(summary = "フィーチャーフラグ取得", description = "指定したキーに一致するフィーチャーフラグの有効状態を取得します")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "取得成功"),
    @ApiResponse(responseCode = "400", description = "バリデーションエラー"),
    @ApiResponse(responseCode = "401", description = "認証されていません"),
    @ApiResponse(responseCode = "403", description = "管理者権限が必要です"),
    @ApiResponse(responseCode = "404", description = "フィーチャーフラグが見つかりません")
  })
  @PreAuthorize("hasAuthority('ADMIN')")
  @GetMapping("/{featureFlagKey}")
  public FeatureFlagDto findFeatureFlagByKey(@PathVariable String featureFlagKey) {
    return featureFlagApplicationService.findFeatureFlagByKey(featureFlagKey);
  }
}
