package com.everrefine.elms.domain.repository;

import com.everrefine.elms.domain.model.tag.Tag;
import com.everrefine.elms.domain.model.tag.TagName;
import java.util.List;

/** タグのリポジトリインターフェース。 */
public interface TagRepository {

  /**
   * 複数のタグを一括保存する。
   *
   * @param tags 保存するタグ一覧
   */
  void saveTags(List<Tag> tags);

  /**
   * 指定したタグ名に一致するタグ一覧を取得する。
   *
   * @param names タグ名一覧
   * @return 全タグ一覧
   */
  List<Tag> findAllTagsByNames(List<TagName> names);
}
