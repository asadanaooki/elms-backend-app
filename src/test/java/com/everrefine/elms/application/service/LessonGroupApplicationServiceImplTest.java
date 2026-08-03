package com.everrefine.elms.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.everrefine.elms.application.command.LessonGroupCreateCommand;
import com.everrefine.elms.application.command.LessonGroupUpdateCommand;
import com.everrefine.elms.application.dto.LessonDto;
import com.everrefine.elms.application.dto.LessonGroupDto;
import com.everrefine.elms.application.dto.TagDto;
import com.everrefine.elms.application.exception.ResourceNotFoundException;
import com.everrefine.elms.presentation.request.LessonGroupCreateRequest;
import com.everrefine.elms.presentation.request.LessonGroupUpdateRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@ActiveProfiles("dev")
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@Testcontainers
@Transactional
class LessonGroupApplicationServiceTest {

  @Container @ServiceConnection
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17");

  @Autowired private LessonGroupApplicationServiceImpl lessonGroupApplicationService;

  @Autowired private JdbcTemplate jdbcTemplate;

  private Integer createCourse(String title) {
    LocalDateTime now = LocalDateTime.now();
    return jdbcTemplate.queryForObject(
        """
        INSERT INTO courses (course_order, title, description, created_at, updated_at)
        VALUES (?, ?, ?, ?, ?)
        RETURNING id
        """,
        Integer.class,
        new BigDecimal("1"),
        title,
        "コース説明",
        now,
        now);
  }

  private Integer createLessonGroup(Integer courseId, String title) {
    LocalDateTime now = LocalDateTime.now();
    return jdbcTemplate.queryForObject(
        """
        INSERT INTO lesson_groups
          (course_id, lesson_group_order, title, created_at, updated_at)
        VALUES (?, ?, ?, ?, ?)
        RETURNING id
        """,
        Integer.class,
        courseId,
        new BigDecimal("1"),
        title,
        now,
        now);
  }

  private Integer createLesson(
      Integer lessonGroupId, Integer courseId, BigDecimal lessonOrder, String title) {
    LocalDateTime now = LocalDateTime.now();
    return jdbcTemplate.queryForObject(
        """
        INSERT INTO lessons
          (lesson_group_id, course_id, lesson_order, title, content, video_url, created_at, updated_at)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        RETURNING id
        """,
        Integer.class,
        lessonGroupId,
        courseId,
        lessonOrder,
        title,
        title + "の説明",
        "https://example.com/" + title + ".mp4",
        now,
        now);
  }

  private Integer createTag(String name) {
    LocalDateTime now = LocalDateTime.now();
    return jdbcTemplate.queryForObject(
        """
        INSERT INTO tags (name, created_at, updated_at)
        VALUES (?, ?, ?)
        RETURNING id
        """,
        Integer.class,
        name,
        now,
        now);
  }

  private void createLessonTag(Integer lessonId, Integer tagId) {
    LocalDateTime now = LocalDateTime.now();
    jdbcTemplate.update(
        """
        INSERT INTO lesson_tags (lesson_id, tag_id, created_at, updated_at)
        VALUES (?, ?, ?, ?)
        """,
        lessonId,
        tagId,
        now,
        now);
  }

  @Nested
  class 正常系 {

    @Test
    void レッスンが存在しないレッスングループを更新すると空のレッスン一覧が返ること() {
      // Arrange
      Integer courseId = createCourse("レッスンなしテストコース");
      Integer lessonGroupId = createLessonGroup(courseId, "レッスンなしグループ");

      LessonGroupUpdateRequest request = new LessonGroupUpdateRequest();
      request.setTitle("新しいタイトル");

      // Act
      LessonGroupDto result =
          lessonGroupApplicationService.updateLessonGroup(request.toCommand(lessonGroupId));

      // Assert
      assertNotNull(result.lessons());
      assertTrue(result.lessons().isEmpty());
    }

    @Test
    void レッスングループを更新するとタグを含むレッスン一覧が並び順どおりに返ること() {
      // Arrange
      Integer courseId = createCourse("タグ取得テストコース");
      Integer lessonGroupId = createLessonGroup(courseId, "タグ取得テストグループ");

      // INSERT順とlesson_order順を変え、ORDER BYの有無を検出できるようにする。
      Integer lesson4Id = createLesson(lessonGroupId, courseId, new BigDecimal("4"), "レッスン4");
      Integer lesson2Id = createLesson(lessonGroupId, courseId, new BigDecimal("2"), "レッスン2");
      Integer lesson1Id = createLesson(lessonGroupId, courseId, new BigDecimal("1"), "レッスン1");
      Integer lesson3Id = createLesson(lessonGroupId, courseId, new BigDecimal("3"), "レッスン3");

      Integer lesson2TagId = createTag("レッスン2タグ");
      Integer lesson4Tag1Id = createTag("レッスン4タグ1");
      Integer lesson4Tag2Id = createTag("レッスン4タグ2");
      Integer lesson4Tag3Id = createTag("レッスン4タグ3");
      createLessonTag(lesson2Id, lesson2TagId);
      // 関連付け順とタグID順を変え、t.idによる並び替えを確認する。
      createLessonTag(lesson4Id, lesson4Tag3Id);
      createLessonTag(lesson4Id, lesson4Tag1Id);
      createLessonTag(lesson4Id, lesson4Tag2Id);

      LessonGroupUpdateRequest request = new LessonGroupUpdateRequest();
      request.setTitle("新しいタイトル");

      // Act
      LessonGroupDto result =
          lessonGroupApplicationService.updateLessonGroup(request.toCommand(lessonGroupId));

      // Assert
      assertEquals(lessonGroupId, result.id());
      assertEquals(courseId, result.courseId());
      assertEquals(0, result.lessonGroupOrder().compareTo(new BigDecimal("1")));
      assertEquals("新しいタイトル", result.name());
      assertNotNull(result.createdAt());
      assertNotNull(result.updatedAt());
      assertNotNull(result.lessons());
      assertEquals(4, result.lessons().size());

      List<LessonDto> lessons = result.lessons();
      assertEquals(
          List.of(lesson1Id, lesson2Id, lesson3Id, lesson4Id),
          lessons.stream().map(LessonDto::getId).toList());

      LessonDto lesson1 = lessons.get(0);
      LessonDto lesson2 = lessons.get(1);
      LessonDto lesson3 = lessons.get(2);
      LessonDto lesson4 = lessons.get(3);
      assertTrue(lesson1.getTags().isEmpty());
      assertEquals(1, lesson2.getTags().size());
      assertEquals(lesson2TagId, lesson2.getTags().get(0).getId());
      assertEquals("レッスン2タグ", lesson2.getTags().get(0).getName());
      assertTrue(lesson3.getTags().isEmpty());
      assertEquals(
          List.of(lesson4Tag1Id, lesson4Tag2Id, lesson4Tag3Id),
          lesson4.getTags().stream().map(TagDto::getId).toList());
      assertEquals(lesson4Tag1Id, lesson4.getTags().get(0).getId());
      assertEquals("レッスン4タグ1", lesson4.getTags().get(0).getName());

      String updatedTitle =
          jdbcTemplate.queryForObject(
              "SELECT title FROM lesson_groups WHERE id = ?", String.class, lessonGroupId);
      assertEquals("新しいタイトル", updatedTitle);
    }

    @Test
    void レッスングループを作成できること() {
      // Arrange
      LocalDateTime now = LocalDateTime.now();
      jdbcTemplate.update(
          "INSERT INTO courses (course_order, title, description, created_at, updated_at) VALUES (?, ?, ?, ?, ?)",
          new BigDecimal("1"),
          "作成用コース",
          "説明",
          now,
          now);
      Integer courseId =
          jdbcTemplate.queryForObject(
              "SELECT id FROM courses WHERE title = ?", Integer.class, "作成用コース");

      LessonGroupCreateRequest request = new LessonGroupCreateRequest();
      request.setTitle("新しいグループ");
      LessonGroupCreateCommand command = request.toCommand(courseId);

      // Act
      LessonGroupDto result = lessonGroupApplicationService.createLessonGroup(command);

      // Assert
      assertNotNull(result.id());
      assertEquals(courseId, result.courseId());
      assertNotNull(result.lessonGroupOrder());
      assertEquals("新しいグループ", result.name());
      assertNotNull(result.createdAt());
      assertNotNull(result.updatedAt());
      assertNull(result.lessons());

      Integer count =
          jdbcTemplate.queryForObject(
              "SELECT COUNT(*) FROM lesson_groups WHERE id = ?", Integer.class, result.id());
      assertEquals(1, count);
    }

    @Test
    void レッスングループを削除できること() {
      // Arrange
      LocalDateTime now = LocalDateTime.now();
      jdbcTemplate.update(
          "INSERT INTO courses (course_order, title, description, created_at, updated_at) VALUES (?, ?, ?, ?, ?)",
          new BigDecimal("1"),
          "削除用コース",
          "説明",
          now,
          now);
      Integer courseId =
          jdbcTemplate.queryForObject(
              "SELECT id FROM courses WHERE title = ?", Integer.class, "削除用コース");

      jdbcTemplate.update(
          "INSERT INTO lesson_groups (course_id, lesson_group_order, title, created_at, updated_at) VALUES (?, ?, ?, ?, ?)",
          courseId,
          new BigDecimal("1"),
          "削除対象グループ",
          now,
          now);
      Integer lessonGroupId =
          jdbcTemplate.queryForObject(
              "SELECT id FROM lesson_groups WHERE title = ?", Integer.class, "削除対象グループ");

      // Act
      lessonGroupApplicationService.deleteLessonGroupById(lessonGroupId);

      // Assert
      Integer count =
          jdbcTemplate.queryForObject(
              "SELECT COUNT(*) FROM lesson_groups WHERE id = ?", Integer.class, lessonGroupId);
      assertEquals(0, count);
    }

    @Test
    void 存在しないレッスングループを削除してもエラーにならないこと() {
      // Arrange
      // Act
      lessonGroupApplicationService.deleteLessonGroupById(999);

      // Assert
    }
  }

  @Nested
  class 異常系 {

    @Test
    void 存在しないレッスングループIDの場合ResourceNotFoundExceptionがスローされること() {
      // Arrange
      LessonGroupUpdateRequest request = new LessonGroupUpdateRequest();
      request.setTitle("新しいタイトル");
      LessonGroupUpdateCommand command = request.toCommand(999);

      // Act & Assert
      ResourceNotFoundException exception =
          assertThrows(
              ResourceNotFoundException.class,
              () -> lessonGroupApplicationService.updateLessonGroup(command));
      assertEquals("LessonGroup が見つかりませんでした。id = 999", exception.getMessage());
    }
  }
}
