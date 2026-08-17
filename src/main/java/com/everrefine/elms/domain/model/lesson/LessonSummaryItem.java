package com.everrefine.elms.domain.model.lesson;

import com.everrefine.elms.domain.model.tag.Tag;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/** タグ検索結果に含まれる、レッスン1件の概要を表す読み取りモデル。 */
public record LessonSummaryItem(
    UUID lessonId, BigDecimal lessonOrder, String lessonTitle, List<Tag> tags) {}
