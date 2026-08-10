package com.everrefine.elms.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.everrefine.elms.application.command.UserLessonCompletionStatusUpdateCommand;
import com.everrefine.elms.application.dto.TagDto;
import com.everrefine.elms.application.dto.UserLessonDetailDto;
import com.everrefine.elms.application.dto.UserLessonGroupDto;
import com.everrefine.elms.domain.exception.ResourceNotFoundException;
import com.everrefine.elms.domain.model.UserLesson;
import com.everrefine.elms.domain.repository.UserLessonRepository;
import com.everrefine.elms.presentation.request.UserLessonCompletionStatusUpdateRequest;
import com.everrefine.elms.testsupport.TestDataFactory;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
public class UserLessonApplicationServiceImplTest {

  @Container @ServiceConnection
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17");

  @Autowired private TestDataFactory testData;

  @Autowired private UserLessonApplicationServiceImpl userLessonApplicationService;

  @Autowired private JdbcTemplate jdbcTemplate;

  @Autowired private UserLessonRepository userLessonRepository;

  @Nested
  class レッスン詳細取得 {
    @Test
    void 未完了の場合isLessonCompletedがfalseになること() {
      UUID courseId = testData.createCourse(new BigDecimal("1"), "ULテストコース", "コース説明");
      UUID lessonGroupId = testData.createLessonGroup(courseId, new BigDecimal("1"), "ULテストグループ");
      UUID lessonId =
          testData.createLesson(
              lessonGroupId,
              courseId,
              new BigDecimal("1"),
              "UL未完了レッスン",
              "説明",
              "https://example.com/video.mp4");
      UUID userId = testData.createUser("ul-not-done@example.com", "p", "太郎", "ulnd", "GENERAL");

      UserLessonDetailDto result =
          userLessonApplicationService.findUserLessonDetail(
              userId, courseId, lessonGroupId, lessonId);

      assertNotNull(result);
      assertEquals(lessonId, result.id());
      assertFalse(result.lessonCompleted());
    }

    @Test
    void 受講完了済みの場合isLessonCompletedがtrueになること() {
      UUID courseId = testData.createCourse(new BigDecimal("1"), "UL完了コース", "コース説明");
      UUID lessonGroupId = testData.createLessonGroup(courseId, new BigDecimal("1"), "UL完了グループ");
      UUID lessonId =
          testData.createLesson(
              lessonGroupId,
              courseId,
              new BigDecimal("1"),
              "UL完了レッスン",
              "説明",
              "https://example.com/video.mp4");
      UUID userId = testData.createUser("ul-done@example.com", "p", "次郎", "uld", "GENERAL");
      testData.createUserLesson(userId, lessonId);

      UserLessonDetailDto result =
          userLessonApplicationService.findUserLessonDetail(
              userId, courseId, lessonGroupId, lessonId);

      assertNotNull(result);
      assertEquals(lessonId, result.id());
      assertTrue(result.lessonCompleted());
    }

    @Test
    void パスのコースまたはグループがレッスンと一致しない場合ResourceNotFoundExceptionが投げられること() {
      UUID courseId = testData.createCourse(new BigDecimal("1"), "UL整合コース", "説明");
      UUID lessonGroupId = testData.createLessonGroup(courseId, new BigDecimal("1"), "UL整合グループ");
      UUID lessonId =
          testData.createLesson(lessonGroupId, courseId, new BigDecimal("1"), "UL1", "d", null);
      UUID userId = testData.createUser("ul-mismatch@example.com", "p", "三郎", "ulm", "GENERAL");
      UUID otherCourseId = testData.createCourse(new BigDecimal("2"), "UL別コース", "説明");

      assertThrows(
          ResourceNotFoundException.class,
          () ->
              userLessonApplicationService.findUserLessonDetail(
                  userId, otherCourseId, lessonGroupId, lessonId));
    }
  }

  @Nested
  class 受講状態更新 {
    @Test
    void isLessonCompletedがtrueでレコードが存在するときuserLessonが更新されること() {
      UUID courseId = testData.createCourse(new BigDecimal("1"), "UL更新コース", "コース説明");
      UUID lessonGroupId = testData.createLessonGroup(courseId, new BigDecimal("1"), "UL更新グループ");
      UUID lessonId =
          testData.createLesson(
              lessonGroupId,
              courseId,
              new BigDecimal("1000"),
              "UL更新レッスン",
              "説明",
              "https://example.com/video.mp4");
      UUID userId =
          testData.createUser("ul-upd@example.com", "password", "テスト 太郎", "ulupd", "GENERAL");
      testData.createUserLesson(userId, lessonId);

      Timestamp beforeUpdatedAt =
          jdbcTemplate.queryForObject(
              "SELECT updated_at FROM user_lessons WHERE user_id = ? AND lesson_id = ?",
              Timestamp.class,
              userId,
              lessonId);

      UserLessonCompletionStatusUpdateRequest req =
          new UserLessonCompletionStatusUpdateRequest(true);
      UserLessonCompletionStatusUpdateCommand cmd = req.toCommand(userId, lessonId);
      userLessonApplicationService.updateUserLesson(courseId, lessonGroupId, cmd);

      Optional<UserLesson> userLessonOpt =
          userLessonRepository.findByUserIdAndLessonId(cmd.userId(), cmd.lessonId());
      assertTrue(userLessonOpt.isPresent());

      Timestamp afterUpdatedAt =
          jdbcTemplate.queryForObject(
              "SELECT updated_at FROM user_lessons WHERE user_id = ? AND lesson_id = ?",
              Timestamp.class,
              userId,
              lessonId);
      assertNotNull(beforeUpdatedAt);
      assertNotNull(afterUpdatedAt);
      assertTrue(afterUpdatedAt.after(beforeUpdatedAt));
    }

    @Test
    void isLessonCompletedがtrueでレコードが存在しないときuserLessonが新規作成されること() {
      UUID courseId = testData.createCourse(new BigDecimal("1"), "UL新規コース", "コース説明");
      UUID lessonGroupId = testData.createLessonGroup(courseId, new BigDecimal("1"), "UL新規グループ");
      UUID lessonId =
          testData.createLesson(
              lessonGroupId,
              courseId,
              new BigDecimal("1000"),
              "UL新規レッスン",
              "説明",
              "https://example.com/video.mp4");
      UUID userId =
          testData.createUser("ul-new@example.com", "password", "テスト 太郎", "ulnew", "GENERAL");

      UserLessonCompletionStatusUpdateRequest req =
          new UserLessonCompletionStatusUpdateRequest(true);
      UserLessonCompletionStatusUpdateCommand cmd = req.toCommand(userId, lessonId);
      assertFalse(userLessonRepository.findByUserIdAndLessonId(userId, lessonId).isPresent());

      userLessonApplicationService.updateUserLesson(courseId, lessonGroupId, cmd);

      assertTrue(userLessonRepository.findByUserIdAndLessonId(userId, lessonId).isPresent());
    }

    @Test
    void isLessonCompletedがfalseのときuserLessonが削除されること() {
      UUID courseId = testData.createCourse(new BigDecimal("1"), "UL削除コース", "コース説明");
      UUID lessonGroupId = testData.createLessonGroup(courseId, new BigDecimal("1"), "UL削除グループ");
      UUID lessonId =
          testData.createLesson(
              lessonGroupId,
              courseId,
              new BigDecimal("1000"),
              "UL削除レッスン",
              "説明",
              "https://example.com/video.mp4");
      UUID userId =
          testData.createUser("ul-del@example.com", "password", "テスト 太郎", "uldel", "GENERAL");
      testData.createUserLesson(userId, lessonId);

      UserLessonCompletionStatusUpdateRequest req =
          new UserLessonCompletionStatusUpdateRequest(false);
      UserLessonCompletionStatusUpdateCommand cmd = req.toCommand(userId, lessonId);
      userLessonApplicationService.updateUserLesson(courseId, lessonGroupId, cmd);

      assertFalse(userLessonRepository.findByUserIdAndLessonId(userId, lessonId).isPresent());
    }

    @Test
    void 存在しないuserIdを指定するとResourceNotFoundExceptionが投げられること() {
      UUID courseId = testData.createCourse(new BigDecimal("1"), "ULユーザ無コース", "コース説明");
      UUID lessonGroupId = testData.createLessonGroup(courseId, new BigDecimal("1"), "ULユーザ無グループ");
      UUID lessonId =
          testData.createLesson(
              lessonGroupId,
              courseId,
              new BigDecimal("1000"),
              "ULユーザ無レッスン",
              "説明",
              "https://example.com/video.mp4");

      UserLessonCompletionStatusUpdateRequest req =
          new UserLessonCompletionStatusUpdateRequest(true);
      UserLessonCompletionStatusUpdateCommand cmd = req.toCommand(UUID.randomUUID(), lessonId);

      assertThrows(
          ResourceNotFoundException.class,
          () -> userLessonApplicationService.updateUserLesson(courseId, lessonGroupId, cmd));
    }

    @Test
    void 存在しないlessonIdを指定するとResourceNotFoundExceptionが投げられること() {
      UUID courseId = testData.createCourse(new BigDecimal("1"), "ULレッスン無コース", "コース説明");
      UUID lessonGroupId = testData.createLessonGroup(courseId, new BigDecimal("1"), "ULレッスン無グループ");
      UUID userId = testData.createUser("ul-no-lesson@example.com", "p", "テスト", "uln", "GENERAL");

      UserLessonCompletionStatusUpdateRequest req =
          new UserLessonCompletionStatusUpdateRequest(true);
      UserLessonCompletionStatusUpdateCommand cmd = req.toCommand(userId, UUID.randomUUID());

      assertThrows(
          ResourceNotFoundException.class,
          () -> userLessonApplicationService.updateUserLesson(courseId, lessonGroupId, cmd));
    }

    @Test
    void パスのコースがレッスンと一致しない場合ResourceNotFoundExceptionが投げられること() {
      UUID courseId = testData.createCourse(new BigDecimal("1"), "ULPUT整合コース", "説明");
      UUID lessonGroupId = testData.createLessonGroup(courseId, new BigDecimal("1"), "ULPUT整合グループ");
      UUID lessonId =
          testData.createLesson(lessonGroupId, courseId, new BigDecimal("1"), "ULPUT1", "d", null);
      UUID userId =
          testData.createUser("ul-put-mismatch@example.com", "p", "四郎", "ulpm", "GENERAL");
      UUID otherCourseId = testData.createCourse(new BigDecimal("2"), "ULPUT別コース", "説明");

      UserLessonCompletionStatusUpdateRequest req =
          new UserLessonCompletionStatusUpdateRequest(true);
      UserLessonCompletionStatusUpdateCommand cmd = req.toCommand(userId, lessonId);

      assertThrows(
          ResourceNotFoundException.class,
          () -> userLessonApplicationService.updateUserLesson(otherCourseId, lessonGroupId, cmd));
    }
  }

  @Nested
  class レッスン一覧取得 {
    @Test
    void 該当ユーザーに紐づく該当コースのレッスン一覧を取得できること() {
      // Arrange
      UUID courseId1 = testData.createCourse(new BigDecimal("1"), "はじめ", "コース１");
      UUID courseId2 = testData.createCourse(new BigDecimal("2"), "つぎに", "コース２");
      UUID lessonGroupId1 =
          testData.createLessonGroup(courseId1, new BigDecimal("1"), "コース１のレッスングループ1");
      UUID lessonGroupId2 =
          testData.createLessonGroup(courseId1, new BigDecimal("2"), "コース１のレッスングループ2");
      UUID lessonGroupId3 =
          testData.createLessonGroup(courseId2, new BigDecimal("1"), "コース2のレッスングループ1");
      UUID lessonId1 =
          testData.createLesson(
              lessonGroupId1,
              courseId1,
              new BigDecimal("1000"),
              "UL更新レッスン",
              "コース１のレッスングループ１のレッスン1",
              "https://example.com/video.mp4");
      UUID lessonId2 =
          testData.createLesson(
              lessonGroupId1,
              courseId1,
              new BigDecimal("4000"),
              "UI更新レッスン",
              "コース１のレッスングループ１のレッスン2",
              "https://example.com/video.mp4");
      UUID lessonId3 =
          testData.createLesson(
              lessonGroupId2,
              courseId1,
              new BigDecimal("1000"),
              "Javaレッスン",
              "コース１のレッスングループ2のレッスン1",
              "https://example.com/video.mp4");
      UUID lessonId4 =
          testData.createLesson(
              lessonGroupId3,
              courseId2,
              new BigDecimal("1000"),
              "SQLレッスン",
              "コース2のレッスングループ1のレッスン1",
              "https://example.com/video.mp4");
      UUID userId =
          testData.createUser("ul-upd@example.com", "password", "テスト 太郎", "ulupd", "GENERAL");
      testData.createUserLesson(userId, lessonId1);
      testData.createUserLesson(userId, lessonId3);
      testData.createUserLesson(userId, lessonId4);
      // Act
      List<UserLessonGroupDto> userLessonGroupDto =
          userLessonApplicationService.findUserLessons(userId, courseId1);
      List<UserLessonGroupDto> userLessonGroupDto2 =
          userLessonApplicationService.findUserLessons(userId, courseId2);
      // Assert
      // コースの順番が正しいか
      assertEquals(courseId1, userLessonGroupDto.getFirst().courseId());
      assertEquals(courseId2, userLessonGroupDto2.getFirst().courseId());
      // 完了状態を判定
      assertTrue(userLessonGroupDto.getFirst().userLessons().getFirst().isLessonCompleted());
      assertFalse(userLessonGroupDto.getFirst().userLessons().getLast().isLessonCompleted());
      // 別コースのレッスンは結果に含まれないこと
      assertFalse(
          userLessonGroupDto.stream()
              .flatMap(g -> g.userLessons().stream())
              .anyMatch(l -> l.lesson().id().equals(lessonId4)));
      // レッスングループ順になっていること
      assertEquals(lessonGroupId1, userLessonGroupDto.getFirst().id());
      assertEquals(lessonGroupId2, userLessonGroupDto.get(1).id());
      // レッスン順番になっていること
      assertEquals(lessonId1, userLessonGroupDto.getFirst().userLessons().get(0).lesson().id());
      assertEquals(lessonId2, userLessonGroupDto.getFirst().userLessons().get(1).lesson().id());
      // 別コースは混ざらないこと
      assertEquals(2, userLessonGroupDto.size());
      assertEquals(lessonGroupId3, userLessonGroupDto2.getFirst().id());
    }

    @Test
    void レッスンが空のレッスングループでも一覧取得できること() {
      // Arrange
      UUID courseId = testData.createCourse(new BigDecimal("1"), "空グループコース", "説明");
      UUID emptyLessonGroupId =
          testData.createLessonGroup(courseId, new BigDecimal("1"), "レッスンなしグループ");
      UUID lessonGroupIdWithLesson =
          testData.createLessonGroup(courseId, new BigDecimal("2"), "レッスンありグループ");
      UUID lessonId =
          testData.createLesson(
              lessonGroupIdWithLesson,
              courseId,
              new BigDecimal("1000"),
              "存在するレッスン",
              "本文",
              "https://example.com/video.mp4");
      UUID userId =
          testData.createUser("ul-empty@example.com", "password", "テスト 太郎", "ulempty", "GENERAL");

      // Act
      List<UserLessonGroupDto> result =
          userLessonApplicationService.findUserLessons(userId, courseId);

      // Assert
      assertEquals(2, result.size());
      assertEquals(emptyLessonGroupId, result.getFirst().id());
      assertTrue(result.getFirst().userLessons().isEmpty());
      assertEquals(lessonGroupIdWithLesson, result.get(1).id());
      assertEquals(1, result.get(1).userLessons().size());
      assertEquals(lessonId, result.get(1).userLessons().getFirst().lesson().id());
    }

    @Test
    void ユーザーが存在しないときResourceNotFoundExceptionが投げられること() {
      // Arrange
      UUID courseId = testData.createCourse(new BigDecimal("1"), "はじめ", "コース１");
      // Act & Assert
      assertThrows(
          ResourceNotFoundException.class,
          () -> userLessonApplicationService.findUserLessons(UUID.randomUUID(), courseId));
    }

    @Test
    void コースが存在しないときResourceNotFoundExceptionが投げられること() {
      // Arrange
      UUID userId =
          testData.createUser("ul-upd@example.com", "password", "テスト 太郎", "ulupd", "GENERAL");
      // Act & Assert
      assertThrows(
          ResourceNotFoundException.class,
          () -> userLessonApplicationService.findUserLessons(userId, UUID.randomUUID()));
    }
  }

  @Nested
  class ユーザーレッスンのタグ {
    @Test
    void レッスン詳細にタグがタグ名の昇順で含まれること() {
      // Arrange - テストデータを準備（登録順とタグ名の昇順が異なるようにする）
      UUID courseId = testData.createCourse(new BigDecimal("1"), "ULタグコース", "コース説明");
      UUID lessonGroupId = testData.createLessonGroup(courseId, new BigDecimal("1"), "ULタググループ");
      UUID lessonId =
          testData.createLesson(
              lessonGroupId, courseId, new BigDecimal("1"), "ULタグレッスン", null, null);
      UUID userId = testData.createUser("ul-tag@example.com", "p", "太郎", "ultag", "GENERAL");
      testData.createLessonTag(lessonId, testData.createTag("ULタグzzz"));
      testData.createLessonTag(lessonId, testData.createTag("ULタグaaa"));

      // Act
      UserLessonDetailDto result =
          userLessonApplicationService.findUserLessonDetail(
              userId, courseId, lessonGroupId, lessonId);

      // Assert
      assertEquals(
          List.of("ULタグaaa", "ULタグzzz"), result.tags().stream().map(TagDto::name).toList());
    }

    @Test
    void レッスン詳細でタグがない場合は空リストになること() {
      // Arrange - テストデータを準備
      UUID courseId = testData.createCourse(new BigDecimal("1"), "UL無タグコース", "コース説明");
      UUID lessonGroupId = testData.createLessonGroup(courseId, new BigDecimal("1"), "UL無タググループ");
      UUID lessonId =
          testData.createLesson(
              lessonGroupId, courseId, new BigDecimal("1"), "UL無タグレッスン", null, null);
      UUID userId = testData.createUser("ul-notag@example.com", "p", "太郎", "ulnotag", "GENERAL");

      // Act
      UserLessonDetailDto result =
          userLessonApplicationService.findUserLessonDetail(
              userId, courseId, lessonGroupId, lessonId);

      // Assert
      assertTrue(result.tags().isEmpty());
    }

    @Test
    void レッスン一覧で複数タグを持つレッスンが1件にまとめられること() {
      // Arrange - テストデータを準備（JOINでレッスンの行が展開される状況を作る）
      UUID courseId = testData.createCourse(new BigDecimal("1"), "UL一覧タグコース", "コース説明");
      UUID lessonGroupId = testData.createLessonGroup(courseId, new BigDecimal("1"), "UL一覧タググループ");
      UUID taggedLessonId =
          testData.createLesson(
              lessonGroupId, courseId, new BigDecimal("1"), "UL一覧タグ付き", null, null);
      UUID untaggedLessonId =
          testData.createLesson(
              lessonGroupId, courseId, new BigDecimal("2"), "UL一覧タグなし", null, null);
      UUID userId = testData.createUser("ul-listtag@example.com", "p", "太郎", "ullist", "GENERAL");
      testData.createLessonTag(taggedLessonId, testData.createTag("UL一覧タグ2"));
      testData.createLessonTag(taggedLessonId, testData.createTag("UL一覧タグ1"));
      testData.createUserLesson(userId, taggedLessonId);

      // Act
      List<UserLessonGroupDto> result =
          userLessonApplicationService.findUserLessons(userId, courseId);

      // Assert - タグで行が増えてもレッスンは重複せず、受講状態も保たれる
      assertEquals(1, result.size());
      assertEquals(2, result.getFirst().userLessons().size());
      assertEquals(taggedLessonId, result.getFirst().userLessons().getFirst().lesson().id());
      assertTrue(result.getFirst().userLessons().getFirst().isLessonCompleted());
      assertEquals(
          List.of("UL一覧タグ1", "UL一覧タグ2"),
          result.getFirst().userLessons().getFirst().lesson().tags().stream()
              .map(TagDto::name)
              .toList());
      assertEquals(untaggedLessonId, result.getFirst().userLessons().get(1).lesson().id());
      assertFalse(result.getFirst().userLessons().get(1).isLessonCompleted());
      assertTrue(result.getFirst().userLessons().get(1).lesson().tags().isEmpty());
    }
  }
}
