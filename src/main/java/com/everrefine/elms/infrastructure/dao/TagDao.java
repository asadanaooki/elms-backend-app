package com.everrefine.elms.infrastructure.dao;

import com.everrefine.elms.domain.model.tag.Tag;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

/** タグのDAOインターフェース。 */
public interface TagDao extends CrudRepository<Tag, Integer> {

  List<Tag> findAllByNameIn(List<String> names);
}
