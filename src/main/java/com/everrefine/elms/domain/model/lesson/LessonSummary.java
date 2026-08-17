package com.everrefine.elms.domain.model.lesson;

import java.math.BigDecimal;
import java.util.UUID;

/** レッスングループと、その配下のレッスン一覧を表す読み取りモデル。 */
public record LessonSummary(
    UUID courseId,
    BigDecimal courseOrder,
    String courseTitle,
    UUID lessonGroupId,
    BigDecimal lessonGroupOrder,
    String lessonGroupTitle,
    LessonSummaryItem lesson) {}
