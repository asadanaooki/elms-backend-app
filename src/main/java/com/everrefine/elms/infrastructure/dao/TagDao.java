package com.everrefine.elms.infrastructure.dao;

import com.everrefine.elms.domain.model.tag.Tag;
import java.util.List;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/** タグのDAOインターフェース。 */
@Repository
public interface TagDao extends CrudRepository<Tag, Integer> {

  @Query(
      """
            SELECT EXISTS(
             SELECT *
             FROM tags
             WHERE name = :name
            )
            """)
  boolean existsByName(String name);

  @Query(
      """
            SELECT *
            FROM tags
            WHERE name IN (:names)
            """)
  List<Tag> findAllByNames(List<String> names);
}
