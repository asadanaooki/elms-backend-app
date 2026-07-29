package com.everrefine.elms.infrastructure.dao.extractor;

import com.everrefine.elms.domain.model.lesson.LessonGroupWithLesson;
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

@Component("lessonGroupWithLessonResultSetExtractor")
public class LessonGroupWithLessonResultSetExtractor
    implements ResultSetExtractor<List<LessonGroupWithLesson>> {

  @Override
  public List<LessonGroupWithLesson> extractData(ResultSet resultSet) throws SQLException {
    Map<GroupAndLessonKey, LessonGroupWithLesson> lessonsByKey =
        new LinkedHashMap<GroupAndLessonKey, LessonGroupWithLesson>();

    while (resultSet.next()) {
      Integer groupId = resultSet.getObject("lesson_group_id", Integer.class);
      Integer lessonId = resultSet.getObject("lesson_id", Integer.class);
      GroupAndLessonKey key = new GroupAndLessonKey(groupId, lessonId);

      LessonGroupWithLesson lessonWithTags = lessonsByKey.get(key);

      if (lessonWithTags == null) {
        lessonWithTags =
            new LessonGroupWithLesson(
                resultSet.getObject("lesson_id", Integer.class),
                resultSet.getString("lesson_title"),
                resultSet.getBigDecimal("lesson_order"),
                resultSet.getString("lesson_content"),
                resultSet.getString("lesson_video_url"),
                resultSet.getObject("lesson_created_at", LocalDateTime.class),
                resultSet.getObject("lesson_updated_at", LocalDateTime.class),
                resultSet.getObject("lesson_group_id", Integer.class),
                resultSet.getObject("course_id", Integer.class),
                resultSet.getString("lesson_group_title"),
                resultSet.getBigDecimal("lesson_group_order"),
                resultSet.getObject("lesson_group_created_at", LocalDateTime.class),
                resultSet.getObject("lesson_group_updated_at", LocalDateTime.class),
                new ArrayList<>());
        lessonsByKey.put(key, lessonWithTags);
      }
      Integer tagId = resultSet.getObject("tag_id", Integer.class);

      if (tagId != null) {
        lessonWithTags
            .getTags()
            .add(
                new Tag(
                    tagId,
                    new TagName(resultSet.getString("tag_name")),
                    resultSet.getObject("tag_created_at", LocalDateTime.class),
                    resultSet.getObject("tag_updated_at", LocalDateTime.class)));
      }
    }

    return new ArrayList<LessonGroupWithLesson>(lessonsByKey.values());
  }

  private record GroupAndLessonKey(Integer groupId, Integer lessonId) {}
}
