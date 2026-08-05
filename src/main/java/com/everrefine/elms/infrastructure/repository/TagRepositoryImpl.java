package com.everrefine.elms.infrastructure.repository;

import com.everrefine.elms.domain.model.tag.Tag;
import com.everrefine.elms.domain.model.tag.TagName;
import com.everrefine.elms.domain.repository.TagRepository;
import com.everrefine.elms.infrastructure.dao.TagDao;
import com.everrefine.elms.infrastructure.entity.tag.TagEntity;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.stereotype.Repository;

/** {@link TagRepository} の実装。 */
@Repository
@AllArgsConstructor
public class TagRepositoryImpl implements TagRepository {

  private final TagDao tagDao;
  private final JdbcAggregateTemplate jdbcAggregateTemplate;

  @Override
  public List<Tag> findAllTagsByNames(List<TagName> names) {
    if (names.isEmpty()) {
      return List.of();
    }
    return tagDao.findAllByNameIn(names.stream().map(TagName::value).toList()).stream()
        .map(TagEntity::toDomain)
        .toList();
  }

  @Override
  public List<Tag> findAllTagsByLessonId(UUID lessonId) {
    return tagDao.findAllByLessonId(lessonId).stream().map(TagEntity::toDomain).toList();
  }

  /**
   * 複数のタグを一括登録する。
   *
   * <p>IDは呼び出し側で採番済みであること。IDが確定していると、Spring Data JDBCが採番結果の問い合わせを行わないため、 JDBCドライバの {@code
   * reWriteBatchedInserts} が複数レコードを1つのINSERT文にまとめられる。
   *
   * @param tags 登録するタグ一覧（IDは採番済み）
   */
  @Override
  public void createTags(List<Tag> tags) {
    if (tags.isEmpty()) {
      return;
    }

    jdbcAggregateTemplate.insertAll(tags.stream().map(TagEntity::from).toList());
  }
}
