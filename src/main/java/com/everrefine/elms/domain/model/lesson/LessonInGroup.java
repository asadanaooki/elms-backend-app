package com.everrefine.elms.domain.model.lesson;

import com.everrefine.elms.domain.model.tag.Tag;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.lang.Nullable;

/** レッスングループ配下の1レッスンを表す読み取りモデル。 */
public record LessonInGroup(
    UUID id,
    String title,
    BigDecimal lessonOrder,
    @Nullable String content,
    @Nullable String videoUrl,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    List<Tag> tags) {}
