package com.everrefine.elms.infrastructure.repository;

import com.everrefine.elms.domain.model.tag.LessonTag;
import com.everrefine.elms.domain.repository.LessonTagRepository;
import com.everrefine.elms.infrastructure.dao.LessonTagDao;
import com.everrefine.elms.infrastructure.entity.tag.LessonTagEntity;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.stereotype.Repository;

/** {@link LessonTagRepository} の実装。 */
@Repository
@AllArgsConstructor
public class LessonTagRepositoryImpl implements LessonTagRepository {

  private final LessonTagDao lessonTagDao;
  private final JdbcAggregateTemplate jdbcAggregateTemplate;

  @Override
  public void deleteAllByLessonId(UUID lessonId) {
    lessonTagDao.deleteAllByLessonId(lessonId);
  }

  /**
   * 複数のレッスンタグを一括登録する。
   *
   * <p>IDは呼び出し側で採番済みであること。IDが確定していると、Spring Data JDBCが採番結果の問い合わせを行わないため、 JDBCドライバの {@code
   * reWriteBatchedInserts} が複数レコードを1つのINSERT文にまとめられる。
   *
   * @param lessonTags 登録するレッスンタグ一覧（IDは採番済み）
   */
  @Override
  public void createLessonTags(List<LessonTag> lessonTags) {
    if (lessonTags.isEmpty()) {
      return;
    }

    jdbcAggregateTemplate.insertAll(lessonTags.stream().map(LessonTagEntity::from).toList());
  }
}
