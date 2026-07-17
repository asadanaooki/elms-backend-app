package com.everrefine.elms.domain.repository;

import com.everrefine.elms.domain.model.LessonTag;
import java.util.List;

/** レッスンタグのリポジトリインターフェース。 */
public interface LessonTagRepository {

  /**
   * レッスンタグを保存する。
   *
   * @param lessonTags 保存するレッスンタグ
   */
  void saveAll(List<LessonTag> lessonTags);

  /**
   * 指定したレッスンIDに紐づくレッスンタグを全て削除する。
   *
   * @param lessonId レッスンID
   */
  void deleteAllByLessonId(Integer lessonId);
}
