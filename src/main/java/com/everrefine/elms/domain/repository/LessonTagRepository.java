package com.everrefine.elms.domain.repository;

import com.everrefine.elms.domain.model.LessonTag;
import java.util.List;

/** レッスンタグのリポジトリインターフェース。 */
public interface LessonTagRepository {

  /**
   * 指定したレッスンIDに紐づくレッスンタグを全て削除する。
   *
   * @param lessonId レッスンID
   */
  void deleteAllByLessonId(Integer lessonId);

  /**
   * 複数のレッスンタグを作成する。
   *
   * @param lessonTags 作成するレッスンタグ一覧
   */
  void createLessonTags(List<LessonTag> lessonTags);
}
