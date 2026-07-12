package com.everrefine.elms.domain.repository;

import com.everrefine.elms.domain.model.tag.Name;
import com.everrefine.elms.domain.model.tag.Tag;
import java.util.List;

/** タグのリポジトリインターフェース。 */
public interface TagRepository {

  /**
   * タグを作成する。
   *
   * @param course 作成するタグ
   * @return 登録タグ一覧(タグIDを含む)
   */
  List<Tag> createTags(List<Tag> tags);

  /**
   * 指定したタグが存在するか確認する。
   *
   * @param name タグ名
   */
  boolean existsTagByName(Name name);
}
