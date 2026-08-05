package com.everrefine.elms.presentation.request;

import com.everrefine.elms.application.command.LessonCreateCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

/** レッスン作成リクエスト。 */
public record LessonCreateRequest(
    @Schema(description = "レッスンタイトル（必須・255文字以内）", example = "変数とデータ型")
        @NotBlank(message = "レッスンタイトルは必須です")
        @Size(max = 255, message = "レッスンタイトルは255文字以内で入力してください")
        String title,
    @Schema(description = "レッスン本文（Markdown対応・1000000文字以内）", example = "## 変数とは\n変数はデータを格納する箱です。")
        @Size(max = 1_000_000, message = "レッスン本文は1000000文字以内で入力してください")
        String content,
    @Schema(description = "動画URL（2048文字以内）", example = "https://example.com/videos/lesson1.mp4")
        @Size(max = 2048, message = "動画URLは2048文字以内で入力してください")
        String videoUrl,
    @Schema(description = "タグ一覧（50個以内）") @Size(max = 50, message = "登録できるタグの数は50個以内です")
        List<@NotNull @Valid LessonTagRequest> tags) {

  /**
   * Commandオブジェクトに変換する。
   *
   * @param courseId コースID
   * @param lessonGroupId レッスングループID
   * @return レッスン作成Command
   */
  public LessonCreateCommand toCommand(UUID courseId, UUID lessonGroupId) {
    return new LessonCreateCommand(
        courseId, lessonGroupId, title, content, videoUrl, null, extractTagNames());
  }

  /**
   * タグ名一覧を取得する。
   *
   * @return タグ名一覧（タグ未指定の場合は空リスト）
   */
  private List<String> extractTagNames() {
    if (tags == null) {
      return List.of();
    }
    return tags.stream().map(LessonTagRequest::name).toList();
  }
}
