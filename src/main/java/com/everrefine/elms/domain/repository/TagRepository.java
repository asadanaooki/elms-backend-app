package com.everrefine.elms.domain.repository;

import com.everrefine.elms.domain.model.tag.Name;
import com.everrefine.elms.domain.model.tag.Tag;
import java.util.List;

/** タグのリポジトリインターフェース。 */
public interface TagRepository {

  /**
   * 複数のタグを一括作成する。
   *
   * @param tags 作成するタグ一覧
   */
  void createTags(List<Tag> tags);

  /**
   * 指定したタグが存在するか確認する。
   *
   * @param name タグ名
   */
  boolean existsTagByName(Name name);

  /**
   * 指定したタグ名に一致するタグ一覧を取得する。
   *
   * @param names タグ名一覧
   * @return 全タグ一覧
   */
  List<Tag> findAllTagsByNames(List<Name> names);
}
