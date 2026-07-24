package com.everrefine.elms.domain.model.lesson;

import com.everrefine.elms.domain.model.tag.Tag;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.lang.Nullable;

/** レッスングループ、レッスン、タグの情報を格納するクラス。 JOINクエリの結果をマッピングするために使用。 */
@Getter
@AllArgsConstructor
public class LessonGroupWithLessonAndTag {

  @Column("lesson_id")
  private final Integer lessonId;

  @Column("lesson_title")
  private final String lessonTitle;

  @Column("lesson_order")
  private final BigDecimal lessonOrder;

  @Nullable @Column("lesson_content")
  private final String lessonContent;

  @Nullable @Column("lesson_video_url")
  private final String lessonVideoUrl;

  @Column("lesson_created_at")
  private final LocalDateTime lessonCreatedAt;

  @Column("lesson_updated_at")
  private final LocalDateTime lessonUpdatedAt;

  @Column("lesson_group_id")
  private final Integer lessonGroupId;

  @Column("course_id")
  private final Integer courseId;

  @Column("lesson_group_title")
  private final String lessonGroupTitle;

  @Column("lesson_group_order")
  private final BigDecimal lessonGroupOrder;

  @Column("lesson_group_created_at")
  private final LocalDateTime lessonGroupCreatedAt;

  @Column("lesson_group_updated_at")
  private final LocalDateTime lessonGroupUpdatedAt;

  @Nullable private final List<Tag> tags;
}
