package com.everrefine.elms.application.service;

import static com.everrefine.elms.domain.model.user.Password.encryptAndCreate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.everrefine.elms.application.command.UserLessonCompletionStatusUpdateCommand;
import com.everrefine.elms.application.dto.TagDto;
import com.everrefine.elms.application.dto.UserLessonDetailDto;
import com.everrefine.elms.application.dto.UserLessonGroupDto;
import com.everrefine.elms.application.exception.ResourceNotFoundException;
import com.everrefine.elms.domain.model.UserLesson;
import com.everrefine.elms.domain.repository.UserLessonRepository;
import com.everrefine.elms.presentation.request.UserLessonCompletionStatusUpdateRequest;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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

  @Autowired private UserLessonApplicationServiceImpl userLessonApplicationService;

  @Autowired private JdbcTemplate jdbcTemplate;

  @Autowired private UserLessonRepository userLessonRepository;

  public Integer createCourse(BigDecimal courseOrder, String title, String description) {
    jdbcTemplate.update(
        """
            INSERT INTO courses (course_order, title, description, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?)
            """,
        courseOrder,
        title,
        description,
        LocalDateTime.now(),
        LocalDateTime.now());
    return jdbcTemplate.queryForObject(
        "SELECT id FROM courses WHERE title = ?", Integer.class, title);
  }

  public Integer createLessonGroup(Integer courseId, BigDecimal lessonGroupOrder, String title) {
    jdbcTemplate.update(
        """
            INSERT INTO lesson_groups (course_id, lesson_group_order, title, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?)
            """,
        courseId,
        lessonGroupOrder,
        title,
        LocalDateTime.now(),
        LocalDateTime.now());
    return jdbcTemplate.queryForObject(
        "SELECT id FROM lesson_groups WHERE title = ?", Integer.class, title);
  }

  public Integer createLesson(
      Integer lessonGroupId,
      Integer courseId,
      BigDecimal lessonOrder,
      String title,
      String content,
      String videoUrl) {
    jdbcTemplate.update(
        """
            INSERT INTO lessons (
            lesson_group_id, course_id, lesson_order, title, content, video_url, created_at, updated_at
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """,
        lessonGroupId,
        courseId,
        lessonOrder,
        title,
        content,
        videoUrl,
        LocalDateTime.now(),
        LocalDateTime.now());
    return jdbcTemplate.queryForObject(
        "SELECT id FROM lessons WHERE title = ?", Integer.class, title);
  }

  public Integer createUser(
      String emailAddress, String password, String realName, String userName, String userRole) {
    jdbcTemplate.update(
        """
            INSERT INTO users (
                 email_address,
                 password,
                 real_name,
                 user_name,
                 thumbnail_url,
                 user_role,
                 created_at,
                 updated_at
             ) VALUES (?, ?, ?, ?, ?, ?, ?, ?);
            """,
        emailAddress,
        encryptAndCreate(password).getValue(),
        realName,
        userName,
        null,
        userRole,
        LocalDateTime.now(),
        LocalDateTime.now());
    return jdbcTemplate.queryForObject(
        "SELECT id FROM users WHERE email_address = ?", Integer.class, emailAddress);
  }

  public void createUserLesson(Integer userId, Integer lessonId) {
    jdbcTemplate.update(
        """
            INSERT INTO user_lessons (user_id, lesson_id, created_at, updated_at)
            VALUES (?, ?, ?, ?)
            """,
        userId,
        lessonId,
        LocalDateTime.now(),
        LocalDateTime.now());
  }

  public Integer createTag(String name) {
    jdbcTemplate.update(
        """
            INSERT INTO tags (name, created_at, updated_at)
            VALUES (?, ?, ?)
            """,
        name,
        LocalDateTime.now(),
        LocalDateTime.now());
    return jdbcTemplate.queryForObject("SELECT id FROM tags WHERE name = ?", Integer.class, name);
  }

  public void createLessonTag(Integer lessonId, Integer tagId) {
    jdbcTemplate.update(
        """
            INSERT INTO lesson_tags (lesson_id, tag_id, created_at, updated_at)
            VALUES (?, ?, ?, ?)
            """,
        lessonId,
        tagId,
        LocalDateTime.now(),
        LocalDateTime.now());
  }

  @Test
  void 正常系_レッスン詳細取得で未完了の場合isLessonCompletedがfalseになること() {
    Integer courseId = createCourse(new BigDecimal("1"), "ULテストコース", "コース説明");
    Integer lessonGroupId = createLessonGroup(courseId, new BigDecimal("1"), "ULテストグループ");
    Integer lessonId =
        createLesson(
            lessonGroupId,
            courseId,
            new BigDecimal("1"),
            "UL未完了レッスン",
            "説明",
            "https://example.com/video.mp4");
    Integer userId = createUser("ul-not-done@example.com", "p", "太郎", "ulnd", "GENERAL");
    createTag("UL未完了紐づかないタグ");

    UserLessonDetailDto result =
        userLessonApplicationService.findUserLessonDetail(
            userId, courseId, lessonGroupId, lessonId);

    assertNotNull(result);
    assertEquals(lessonId, result.getId());
    assertFalse(result.isLessonCompleted());
    assertTrue(result.getTags().isEmpty());
  }

  @Test
  void 正常系_レッスン詳細取得で受講完了済みの場合isLessonCompletedがtrueになること() {
    Integer courseId = createCourse(new BigDecimal("1"), "UL完了コース", "コース説明");
    Integer lessonGroupId = createLessonGroup(courseId, new BigDecimal("1"), "UL完了グループ");
    Integer lessonId =
        createLesson(
            lessonGroupId,
            courseId,
            new BigDecimal("1"),
            "UL完了レッスン",
            "説明",
            "https://example.com/video.mp4");
    Integer userId = createUser("ul-done@example.com", "p", "次郎", "uld", "GENERAL");
    createUserLesson(userId, lessonId);
    Integer tag1Id = createTag("UL完了タグ1");
    Integer tag2Id = createTag("UL完了タグ2");
    Integer tag3Id = createTag("UL完了タグ3");
    createTag("UL完了紐づかないタグ");
    createLessonTag(lessonId, tag3Id);
    createLessonTag(lessonId, tag1Id);
    createLessonTag(lessonId, tag2Id);

    UserLessonDetailDto result =
        userLessonApplicationService.findUserLessonDetail(
            userId, courseId, lessonGroupId, lessonId);

    assertNotNull(result);
    assertEquals(lessonId, result.getId());
    assertTrue(result.isLessonCompleted());
    List<TagDto> tags = result.getTags();
    assertEquals(3, tags.size());
    assertEquals(List.of(tag1Id, tag2Id, tag3Id), tags.stream().map(TagDto::getId).toList());
    assertEquals(tag1Id, tags.get(0).getId());
    assertEquals("UL完了タグ1", tags.get(0).getName());
  }

  @Test
  void 異常系_パスのコースまたはグループがレッスンと一致しない場合詳細取得で例外になること() {
    Integer courseId = createCourse(new BigDecimal("1"), "UL整合コース", "説明");
    Integer lessonGroupId = createLessonGroup(courseId, new BigDecimal("1"), "UL整合グループ");
    Integer lessonId = createLesson(lessonGroupId, courseId, new BigDecimal("1"), "UL1", "d", null);
    Integer userId = createUser("ul-mismatch@example.com", "p", "三郎", "ulm", "GENERAL");
    Integer otherCourseId = createCourse(new BigDecimal("2"), "UL別コース", "説明");

    assertThrows(
        ResourceNotFoundException.class,
        () ->
            userLessonApplicationService.findUserLessonDetail(
                userId, otherCourseId, lessonGroupId, lessonId));
  }

  @Test
  void 正常系_isLessonCompletedがtrueでレコードが存在するときuserLessonが更新されること() {
    Integer courseId = createCourse(new BigDecimal("1"), "UL更新コース", "コース説明");
    Integer lessonGroupId = createLessonGroup(courseId, new BigDecimal("1"), "UL更新グループ");
    Integer lessonId =
        createLesson(
            lessonGroupId,
            courseId,
            new BigDecimal("1000"),
            "UL更新レッスン",
            "説明",
            "https://example.com/video.mp4");
    Integer userId = createUser("ul-upd@example.com", "password", "テスト 太郎", "ulupd", "GENERAL");
    createUserLesson(userId, lessonId);

    Timestamp beforeUpdatedAt =
        jdbcTemplate.queryForObject(
            "SELECT updated_at FROM user_lessons WHERE user_id = ? AND lesson_id = ?",
            Timestamp.class,
            userId,
            lessonId);

    UserLessonCompletionStatusUpdateRequest req = new UserLessonCompletionStatusUpdateRequest();
    req.setLessonCompleted(true);
    UserLessonCompletionStatusUpdateCommand cmd = req.toCommand(userId, lessonId);
    userLessonApplicationService.updateUserLesson(courseId, lessonGroupId, cmd);

    Optional<UserLesson> userLessonOpt =
        userLessonRepository.findByUserIdAndLessonId(cmd.getUserId(), cmd.getLessonId());
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
  void 正常系_isLessonCompletedがtrueでレコードが存在しないときuserLessonが新規作成されること() {
    Integer courseId = createCourse(new BigDecimal("1"), "UL新規コース", "コース説明");
    Integer lessonGroupId = createLessonGroup(courseId, new BigDecimal("1"), "UL新規グループ");
    Integer lessonId =
        createLesson(
            lessonGroupId,
            courseId,
            new BigDecimal("1000"),
            "UL新規レッスン",
            "説明",
            "https://example.com/video.mp4");
    Integer userId = createUser("ul-new@example.com", "password", "テスト 太郎", "ulnew", "GENERAL");

    UserLessonCompletionStatusUpdateRequest req = new UserLessonCompletionStatusUpdateRequest();
    req.setLessonCompleted(true);
    UserLessonCompletionStatusUpdateCommand cmd = req.toCommand(userId, lessonId);
    assertFalse(userLessonRepository.findByUserIdAndLessonId(userId, lessonId).isPresent());

    userLessonApplicationService.updateUserLesson(courseId, lessonGroupId, cmd);

    assertTrue(userLessonRepository.findByUserIdAndLessonId(userId, lessonId).isPresent());
  }

  @Test
  void 正常系_isLessonCompletedがfalseの時userLessonが削除されること() {
    Integer courseId = createCourse(new BigDecimal("1"), "UL削除コース", "コース説明");
    Integer lessonGroupId = createLessonGroup(courseId, new BigDecimal("1"), "UL削除グループ");
    Integer lessonId =
        createLesson(
            lessonGroupId,
            courseId,
            new BigDecimal("1000"),
            "UL削除レッスン",
            "説明",
            "https://example.com/video.mp4");
    Integer userId = createUser("ul-del@example.com", "password", "テスト 太郎", "uldel", "GENERAL");
    createUserLesson(userId, lessonId);

    UserLessonCompletionStatusUpdateRequest req = new UserLessonCompletionStatusUpdateRequest();
    req.setLessonCompleted(false);
    UserLessonCompletionStatusUpdateCommand cmd = req.toCommand(userId, lessonId);
    userLessonApplicationService.updateUserLesson(courseId, lessonGroupId, cmd);

    assertFalse(userLessonRepository.findByUserIdAndLessonId(userId, lessonId).isPresent());
  }

  @Test
  void 異常系_Userに存在しないuserIdで受講状態を変更するとエラーになること() {
    Integer courseId = createCourse(new BigDecimal("1"), "ULユーザ無コース", "コース説明");
    Integer lessonGroupId = createLessonGroup(courseId, new BigDecimal("1"), "ULユーザ無グループ");
    Integer lessonId =
        createLesson(
            lessonGroupId,
            courseId,
            new BigDecimal("1000"),
            "ULユーザ無レッスン",
            "説明",
            "https://example.com/video.mp4");

    UserLessonCompletionStatusUpdateRequest req = new UserLessonCompletionStatusUpdateRequest();
    req.setLessonCompleted(true);
    UserLessonCompletionStatusUpdateCommand cmd = req.toCommand(-999, lessonId);

    assertThrows(
        ResourceNotFoundException.class,
        () -> userLessonApplicationService.updateUserLesson(courseId, lessonGroupId, cmd));
  }

  @Test
  void 異常系_Lessonに存在しないlessonIdで受講状態を変更するとエラーになること() {
    Integer courseId = createCourse(new BigDecimal("1"), "ULレッスン無コース", "コース説明");
    Integer lessonGroupId = createLessonGroup(courseId, new BigDecimal("1"), "ULレッスン無グループ");
    Integer userId = createUser("ul-no-lesson@example.com", "p", "テスト", "uln", "GENERAL");

    UserLessonCompletionStatusUpdateRequest req = new UserLessonCompletionStatusUpdateRequest();
    req.setLessonCompleted(true);
    UserLessonCompletionStatusUpdateCommand cmd = req.toCommand(userId, -999);

    assertThrows(
        ResourceNotFoundException.class,
        () -> userLessonApplicationService.updateUserLesson(courseId, lessonGroupId, cmd));
  }

  @Test
  void 異常系_パスのコースがレッスンと一致しない場合受講状態更新で例外になること() {
    Integer courseId = createCourse(new BigDecimal("1"), "ULPUT整合コース", "説明");
    Integer lessonGroupId = createLessonGroup(courseId, new BigDecimal("1"), "ULPUT整合グループ");
    Integer lessonId =
        createLesson(lessonGroupId, courseId, new BigDecimal("1"), "ULPUT1", "d", null);
    Integer userId = createUser("ul-put-mismatch@example.com", "p", "四郎", "ulpm", "GENERAL");
    Integer otherCourseId = createCourse(new BigDecimal("2"), "ULPUT別コース", "説明");

    UserLessonCompletionStatusUpdateRequest req = new UserLessonCompletionStatusUpdateRequest();
    req.setLessonCompleted(true);
    UserLessonCompletionStatusUpdateCommand cmd = req.toCommand(userId, lessonId);

    assertThrows(
        ResourceNotFoundException.class,
        () -> userLessonApplicationService.updateUserLesson(otherCourseId, lessonGroupId, cmd));
  }

  @Test
  void 正常系_該当ユーザーに紐づく該当コースのレッスン一覧を取得できること() {
    // Arrange
    Integer courseId1 = createCourse(new BigDecimal("1"), "はじめ", "コース１");
    Integer courseId2 = createCourse(new BigDecimal("2"), "つぎに", "コース２");
    Integer lessonGroupId1 = createLessonGroup(courseId1, new BigDecimal("1"), "コース１のレッスングループ1");
    Integer lessonGroupId2 = createLessonGroup(courseId1, new BigDecimal("2"), "コース１のレッスングループ2");
    Integer lessonGroupId3 = createLessonGroup(courseId2, new BigDecimal("1"), "コース2のレッスングループ1");
    Integer lessonId1 =
        createLesson(
            lessonGroupId1,
            courseId1,
            new BigDecimal("1000"),
            "UL更新レッスン",
            "コース１のレッスングループ１のレッスン1",
            "https://example.com/video.mp4");
    Integer lessonId2 =
        createLesson(
            lessonGroupId1,
            courseId1,
            new BigDecimal("4000"),
            "UI更新レッスン",
            "コース１のレッスングループ１のレッスン2",
            "https://example.com/video.mp4");
    Integer lessonId3 =
        createLesson(
            lessonGroupId2,
            courseId1,
            new BigDecimal("1000"),
            "Javaレッスン",
            "コース１のレッスングループ2のレッスン1",
            "https://example.com/video.mp4");
    Integer lessonId4 =
        createLesson(
            lessonGroupId3,
            courseId2,
            new BigDecimal("1000"),
            "SQLレッスン",
            "コース2のレッスングループ1のレッスン1",
            "https://example.com/video.mp4");
    Integer userId = createUser("ul-upd@example.com", "password", "テスト 太郎", "ulupd", "GENERAL");
    createUserLesson(userId, lessonId1);
    createUserLesson(userId, lessonId3);
    createUserLesson(userId, lessonId4);
    Integer lesson1FirstTagId = createTag("ULレッスン1タグ1");
    Integer lesson1SecondTagId = createTag("ULレッスン1タグ2");
    Integer lesson3TagId = createTag("ULレッスン3タグ");
    Integer lesson4TagId = createTag("ULレッスン4タグ");
    createLessonTag(lessonId1, lesson1SecondTagId);
    createLessonTag(lessonId1, lesson1FirstTagId);
    createLessonTag(lessonId3, lesson3TagId);
    createLessonTag(lessonId4, lesson4TagId);
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
            .anyMatch(l -> l.lesson().getId().equals(lessonId4)));
    // レッスングループ順になっていること
    assertEquals(lessonGroupId1, userLessonGroupDto.getFirst().id());
    assertEquals(lessonGroupId2, userLessonGroupDto.get(1).id());
    // レッスン順番になっていること
    assertEquals(lessonId1, userLessonGroupDto.getFirst().userLessons().get(0).lesson().getId());
    assertEquals(lessonId2, userLessonGroupDto.getFirst().userLessons().get(1).lesson().getId());
    // 各レッスンに紐づくタグが取得できること
    var lesson1Tags = userLessonGroupDto.getFirst().userLessons().get(0).lesson().getTags();
    assertEquals(2, lesson1Tags.size());
    assertEquals(
        List.of(lesson1FirstTagId, lesson1SecondTagId),
        lesson1Tags.stream().map(tag -> tag.getId()).toList());
    assertEquals(lesson1FirstTagId, lesson1Tags.getFirst().getId());
    assertEquals("ULレッスン1タグ1", lesson1Tags.getFirst().getName());

    var lesson2Tags = userLessonGroupDto.getFirst().userLessons().get(1).lesson().getTags();
    assertEquals(0, lesson2Tags.size());

    var lesson3Tags = userLessonGroupDto.get(1).userLessons().getFirst().lesson().getTags();
    assertEquals(1, lesson3Tags.size());
    assertEquals(lesson3TagId, lesson3Tags.getFirst().getId());
    assertEquals("ULレッスン3タグ", lesson3Tags.getFirst().getName());

    var lesson4Tags = userLessonGroupDto2.getFirst().userLessons().getFirst().lesson().getTags();
    assertEquals(1, lesson4Tags.size());
    assertEquals(lesson4TagId, lesson4Tags.getFirst().getId());
    assertEquals("ULレッスン4タグ", lesson4Tags.getFirst().getName());
    // 別コースは混ざらないこと
    assertEquals(2, userLessonGroupDto.size());
    assertEquals(lessonGroupId3, userLessonGroupDto2.getFirst().id());
  }

  @Test
  void 正常系_完了済みレッスンが存在しないとき全レッスンが未完了として取得できること() {
    // Arrange
    Integer courseId = createCourse(new BigDecimal("1"), "UL完了レッスンなしコース", "完了済みレッスンがないコース");
    Integer lessonGroupId = createLessonGroup(courseId, new BigDecimal("1"), "UL完了レッスンなしグループ");
    Integer lessonId1 =
        createLesson(lessonGroupId, courseId, new BigDecimal("1"), "UL未完了レッスン1", "未完了レッスン1", null);
    Integer lessonId2 =
        createLesson(lessonGroupId, courseId, new BigDecimal("2"), "UL未完了レッスン2", "未完了レッスン2", null);
    Integer userId =
        createUser(
            "ul-no-completed-lessons@example.com", "password", "未完了 太郎", "ulnocomplete", "GENERAL");

    // Act
    List<UserLessonGroupDto> result =
        userLessonApplicationService.findUserLessons(userId, courseId);

    // Assert
    assertEquals(1, result.size());
    assertEquals(
        List.of(lessonId1, lessonId2),
        result.getFirst().userLessons().stream()
            .map(userLesson -> userLesson.lesson().getId())
            .toList());
    assertTrue(
        result.getFirst().userLessons().stream()
            .noneMatch(userLesson -> userLesson.isLessonCompleted()));
  }

  @Test
  void 正常系_タグありタグなしレッスンとレッスンなしグループを正しく取得できること() {
    // Arrange
    Integer courseId =
        createCourse(new BigDecimal("1"), "ULExtractor確認コース", "Extractorの集約を確認するコース");
    Integer taggedLessonGroupId = createLessonGroup(courseId, new BigDecimal("1"), "ULタグありグループ");
    Integer untaggedLessonGroupId = createLessonGroup(courseId, new BigDecimal("2"), "ULタグなしグループ");
    Integer emptyLessonGroupId1 = createLessonGroup(courseId, new BigDecimal("3"), "ULレッスンなしグループ1");
    Integer emptyLessonGroupId2 = createLessonGroup(courseId, new BigDecimal("4"), "ULレッスンなしグループ2");

    Integer taggedLessonId1 =
        createLesson(
            taggedLessonGroupId, courseId, new BigDecimal("1"), "ULタグありレッスン1", "タグありレッスン1", null);
    Integer taggedLessonId2 =
        createLesson(
            taggedLessonGroupId, courseId, new BigDecimal("2"), "ULタグありレッスン2", "タグありレッスン2", null);
    Integer untaggedLessonId1 =
        createLesson(
            untaggedLessonGroupId, courseId, new BigDecimal("1"), "ULタグなしレッスン1", "タグなしレッスン1", null);
    Integer untaggedLessonId2 =
        createLesson(
            untaggedLessonGroupId, courseId, new BigDecimal("2"), "ULタグなしレッスン2", "タグなしレッスン2", null);

    Integer taggedLesson1TagId1 = createTag("ULタグありレッスン1タグ1");
    Integer taggedLesson1TagId2 = createTag("ULタグありレッスン1タグ2");
    Integer taggedLesson2TagId = createTag("ULタグありレッスン2タグ");
    createLessonTag(taggedLessonId1, taggedLesson1TagId2);
    createLessonTag(taggedLessonId1, taggedLesson1TagId1);
    createLessonTag(taggedLessonId2, taggedLesson2TagId);

    Integer userId =
        createUser("ul-extractor@example.com", "password", "抽出 太郎", "ulextractor", "GENERAL");

    // Act
    List<UserLessonGroupDto> result =
        userLessonApplicationService.findUserLessons(userId, courseId);

    // Assert
    assertEquals(
        List.of(
            taggedLessonGroupId, untaggedLessonGroupId, emptyLessonGroupId1, emptyLessonGroupId2),
        result.stream().map(UserLessonGroupDto::id).toList());
    assertEquals(
        List.of(2, 2, 0, 0), result.stream().map(group -> group.userLessons().size()).toList());

    assertEquals(
        List.of(taggedLessonId1, taggedLessonId2, untaggedLessonId1, untaggedLessonId2),
        result.stream()
            .flatMap(group -> group.userLessons().stream())
            .map(userLesson -> userLesson.lesson().getId())
            .toList());
    assertTrue(
        result.stream()
            .flatMap(group -> group.userLessons().stream())
            .allMatch(userLesson -> userLesson.lesson().getId() != null));

    var taggedLesson1Tags = result.get(0).userLessons().get(0).lesson().getTags();
    assertEquals(
        List.of(taggedLesson1TagId1, taggedLesson1TagId2),
        taggedLesson1Tags.stream().map(TagDto::getId).toList());
    assertEquals("ULタグありレッスン1タグ1", taggedLesson1Tags.getFirst().getName());

    var taggedLesson2Tags = result.get(0).userLessons().get(1).lesson().getTags();
    assertEquals(
        List.of(taggedLesson2TagId), taggedLesson2Tags.stream().map(TagDto::getId).toList());
    assertEquals("ULタグありレッスン2タグ", taggedLesson2Tags.getFirst().getName());

    assertTrue(result.get(1).userLessons().get(0).lesson().getTags().isEmpty());
    assertTrue(result.get(1).userLessons().get(1).lesson().getTags().isEmpty());
  }

  @Test
  void 異常系_ユーザーが存在しないとき例外になること() {
    // Arrange
    Integer courseId = createCourse(new BigDecimal("1"), "はじめ", "コース１");
    // Act & Assert
    assertThrows(
        ResourceNotFoundException.class,
        () -> userLessonApplicationService.findUserLessons(999, courseId));
  }

  @Test
  void 異常系_コースが存在しないとき例外になること() {
    // Arrange
    Integer userId = createUser("ul-upd@example.com", "password", "テスト 太郎", "ulupd", "GENERAL");
    // Act & Assert
    assertThrows(
        ResourceNotFoundException.class,
        () -> userLessonApplicationService.findUserLessons(userId, 999));
  }
}
