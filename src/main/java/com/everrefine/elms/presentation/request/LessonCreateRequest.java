package com.everrefine.elms.presentation.request;

import com.everrefine.elms.application.command.LessonCreateCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Collections;
import java.util.List;
import lombok.Data;
import org.springframework.util.CollectionUtils;

/** レッスン作成リクエストに関するクラス。 */
@Data
public class LessonCreateRequest {

  @Schema(description = "レッスンタイトル（必須・255文字以内）", example = "変数とデータ型")
  @NotBlank(message = "レッスンタイトルは必須です")
  @Size(max = 255, message = "レッスンタイトルは255文字以内で入力してください")
  private String title;

  @Schema(description = "レッスン本文（Markdown対応・1000000文字以内）", example = "## 変数とは\n変数はデータを格納する箱です。")
  @Size(max = 1_000_000, message = "レッスン本文は1000000文字以内で入力してください")
  private String content;

  @Schema(description = "動画URL（2048文字以内）", example = "https://example.com/videos/lesson1.mp4")
  @Size(max = 2048, message = "動画URLは2048文字以内で入力してください")
  private String videoUrl;

  @Schema(description = "タグ一覧")
  @Size(max = 50, message = "登録できるタグの数は50個以内です。")
  private List<@NotNull @Valid LessonTagRequest> tags;

  /**
   * Commandオブジェクトに変換する。
   *
   * @param courseId コースID
   * @param lessonGroupId レッスングループID
   * @return レッスン作成Command
   */
  public LessonCreateCommand toCommand(Integer courseId, Integer lessonGroupId) {
    return new LessonCreateCommand(
        courseId, lessonGroupId, title, content, videoUrl, null, extractTagNames());
  }

  /**
   * タグ名一覧を取得する
   *
   * @return タグ名一覧
   */
  private List<String> extractTagNames() {
    if (CollectionUtils.isEmpty(tags)) {
      return Collections.emptyList();
    }
    return tags.stream().map(LessonTagRequest::getName).toList();
  }
}
