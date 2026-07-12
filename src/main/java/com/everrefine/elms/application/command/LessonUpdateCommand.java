package com.everrefine.elms.application.command;

import com.everrefine.elms.domain.model.lesson.Lesson;
import java.util.List;
import lombok.Data;

/** レッスン更新用のコマンド。 */
@Data
public class LessonUpdateCommand {

  private final Integer id;
  private final String title;
  private final String content;
  private final String videoUrl;
  private final List<String> tags;

  /**
   * Lessonエンティティに変換する。
   *
   * @param lesson 更新対象のレッスン
   * @return 更新後のレッスンエンティティ
   */
  public Lesson toLesson(Lesson lesson) {
    return lesson.update(title, content, videoUrl);
  }
}
