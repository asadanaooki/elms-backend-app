package com.everrefine.elms.domain.model.lesson;

import com.everrefine.elms.domain.model.tag.Tag;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/** タグ検索結果に含まれる、コース・レッスングループ・タグの情報を持つ1件のレッスン概要を表す読み取りモデル。 */
public record LessonSummary(
    UUID courseId,
    BigDecimal courseOrder,
    String courseTitle,
    UUID lessonGroupId,
    BigDecimal lessonGroupOrder,
    String lessonGroupTitle,
    UUID lessonId,
    BigDecimal lessonOrder,
    String lessonTitle,
    List<Tag> tags) {}
