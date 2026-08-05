package com.everrefine.elms.domain.repository;

import com.everrefine.elms.domain.model.tag.Tag;
import com.everrefine.elms.domain.model.tag.TagName;
import java.util.List;
import java.util.UUID;

/** タグのリポジトリインターフェース。 */
public interface TagRepository {

  /**
   * 指定したタグ名に一致するタグ一覧を取得する。
   *
   * @param names タグ名一覧
   * @return タグ一覧
   */
  List<Tag> findAllTagsByNames(List<TagName> names);

  /**
   * 指定されたレッスンIDに紐づくタグを取得する。
   *
   * @param lessonId レッスンID
   * @return レッスンに紐づくタグの一覧
   */
  List<Tag> findAllTagsByLessonId(UUID lessonId);

  /**
   * 複数のタグを作成する。
   *
   * @param tags 作成するタグ一覧
   */
  void createTags(List<Tag> tags);
}
