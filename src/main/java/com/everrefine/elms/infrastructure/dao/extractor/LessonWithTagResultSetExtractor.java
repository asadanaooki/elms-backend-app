package com.everrefine.elms.infrastructure.dao.extractor;

import com.everrefine.elms.domain.model.lesson.LessonWithTag;
import com.everrefine.elms.domain.model.tag.Tag;
import com.everrefine.elms.domain.model.tag.TagName;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

@Component("lessonWithTagResultSetExtractor")
public class LessonWithTagResultSetExtractor implements ResultSetExtractor<List<LessonWithTag>> {

  @Override
  public List<LessonWithTag> extractData(ResultSet resultSet) throws SQLException {
    Map<Integer, LessonWithTag> lessonsBylessonId = new LinkedHashMap<Integer, LessonWithTag>();

    while (resultSet.next()) {
      Integer lessonId = resultSet.getObject("lesson_id", Integer.class);
      LessonWithTag lesson = lessonsBylessonId.get(lessonId);

      if (lesson == null) {
        lesson =
            new LessonWithTag(
                resultSet.getObject("lesson_id", Integer.class),
                resultSet.getObject("lesson_group_id", Integer.class),
                resultSet.getObject("course_id", Integer.class),
                resultSet.getString("lesson_title"),
                resultSet.getBigDecimal("lesson_order"),
                resultSet.getString("lesson_content"),
                resultSet.getString("lesson_video_url"),
                resultSet.getObject("lesson_created_at", LocalDateTime.class),
                resultSet.getObject("lesson_updated_at", LocalDateTime.class),
                new ArrayList<>());
        lessonsBylessonId.put(lessonId, lesson);
      }
      Integer tagId = resultSet.getObject("tag_id", Integer.class);

      if (tagId != null) {
        lesson
            .getTags()
            .add(
                new Tag(
                    tagId,
                    new TagName(resultSet.getString("tag_name")),
                    resultSet.getObject("tag_created_at", LocalDateTime.class),
                    resultSet.getObject("tag_updated_at", LocalDateTime.class)));
      }
    }

    return new ArrayList<LessonWithTag>(lessonsBylessonId.values());
  }
}
