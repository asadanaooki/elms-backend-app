package com.everrefine.elms.application.service;

import static com.everrefine.elms.domain.model.user.Password.encryptAndCreate;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.everrefine.elms.application.command.LessonCreateCommand;
import com.everrefine.elms.application.command.LessonOrderUpdateCommand;
import com.everrefine.elms.application.command.LessonSearchCommand;
import com.everrefine.elms.application.command.LessonUpdateCommand;
import com.everrefine.elms.application.dto.CourseLessonsDto;
import com.everrefine.elms.application.dto.LessonDto;
import com.everrefine.elms.application.dto.LessonPageDto;
import com.everrefine.elms.application.dto.TagDto;
import com.everrefine.elms.application.exception.ResourceNotFoundException;
import com.everrefine.elms.domain.model.LessonTag;
import com.everrefine.elms.domain.model.tag.Tag;
import com.everrefine.elms.presentation.request.LessonCreateRequest;
import com.everrefine.elms.presentation.request.LessonOrderUpdateRequest;
import com.everrefine.elms.presentation.request.LessonSearchRequest;
import com.everrefine.elms.presentation.request.LessonTagRequest;
import com.everrefine.elms.presentation.request.LessonUpdateRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * {@link LessonApplicationServiceImpl}の統合テストクラス。
 *
 * <p>このテストクラスでは、実際のPostgreSQLデータベースを使用した統合テストを実施します。 Testcontainersを利用してテスト用のDockerコンテナを起動し、Spring
 * Bootのテスト機能と組み合わせることで、 実運用環境に近い状態でのテストを保証します。
 *
 * <p>主な検証項目：
 *
 * <ul>
 *   <li>実運用に近いシナリオの網羅
 *   <li>エッジケースと例外処理の検証
 *   <li>実際のデータベース操作の検証
 *   <li>ビジネスロジックの正確性
 *   <li>トランザクション境界の考慮
 * </ul>
 */
@ActiveProfiles("dev")
@SpringBootTest(webEnvironment = WebEnvironment.NONE) // WebまわりのConfigurationはBean生成を無効にして高速化する。
@Testcontainers // DBはDockerコンテナを使用する。
@Transactional // 各テストメソッド終了時にテストデータをロールバックする。
public class LessonApplicationServiceImplTest {

  /** テストで使うDBを用意する。 */
  @Container @ServiceConnection
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17");

  /** テスト対象のサービスクラス。 */
  @Autowired private LessonApplicationServiceImpl lessonApplicationService;

  /** データ検証で使用するためのJdbcTemplate。 */
  @Autowired private JdbcTemplate jdbcTemplate;

  // coursesにデータを挿入しcourseIdを取得する
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

  // lessoGroupにデータを挿入しlessonGroupIdを取得する
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

  // Lessonにデータを挿入しlessonIdを取得する
  public Integer createLesson(
      Integer lessonGroupId,
      Integer courseId,
      BigDecimal lessonOrder,
      String title,
      String content,
      String videoRrl) {
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
        videoRrl,
        LocalDateTime.now(),
        LocalDateTime.now());
    return jdbcTemplate.queryForObject(
        "SELECT id FROM lessons WHERE title = ?", Integer.class, title);
  }

  // Userにデータを挿入しuserIdを取得する
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

  // Tagにデータを挿入しtagIdを取得する
  public Integer createTag(String name) {
    jdbcTemplate.update(
        """
                INSERT INTO tags (
                name, created_at, updated_at
                ) VALUES (?, ?, ?)
                ON CONFLICT (name) DO NOTHING
                """,
        name,
        LocalDateTime.now(),
        LocalDateTime.now());
    return jdbcTemplate.queryForObject("SELECT id FROM tags WHERE name = ?", Integer.class, name);
  }

  // LessonTagにデータを挿入する
  public void createLessonTag(Integer lessonId, Integer tagId) {
    jdbcTemplate.update(
        """
                INSERT INTO lesson_tags (
                lesson_id, tag_id, created_at, updated_at
                ) VALUES (?, ?, ?, ?)
                """,
        lessonId,
        tagId,
        LocalDateTime.now(),
        LocalDateTime.now());
  }

  @Test
  void 正常系_レッスンをIDで取得できること() {
    // Arrange - テストデータを準備（IDは自動生成）
    Integer courseId = createCourse(new BigDecimal("1"), "テストコース", "コース説明");
    Integer lessonGroupId = createLessonGroup(courseId, new BigDecimal("1"), "テストグループ");
    Integer lessonId =
        createLesson(
            lessonGroupId,
            courseId,
            new BigDecimal("1"),
            "テストレッスン",
            "テスト説明",
            "https://example.com/video.mp4");
    Integer tag1Id = createTag("タグ1");
    Integer tag2Id = createTag("タグ2");
    Integer unassociatedTagId = createTag("タグ3");
    createLessonTag(lessonId, tag2Id);
    createLessonTag(lessonId, tag1Id);

    // Act
    LessonDto result = lessonApplicationService.findLessonById(courseId, lessonGroupId, lessonId);

    // Assert
    assertNotNull(result);
    assertEquals(lessonId, result.getId());
    assertEquals(lessonGroupId, result.getLessonGroupId());
    assertEquals(courseId, result.getCourseId());
    assertEquals(new BigDecimal("1.0000"), result.getLessonOrder());
    assertEquals("テストレッスン", result.getTitle());
    assertEquals("テスト説明", result.getContent());
    assertEquals("https://example.com/video.mp4", result.getVideoUrl());
    assertEquals(2, result.getTags().size());
    assertEquals(List.of(tag1Id, tag2Id), result.getTags().stream().map(TagDto::getId).toList());
    assertEquals(List.of("タグ1", "タグ2"), result.getTags().stream().map(TagDto::getName).toList());
    assertTrue(result.getTags().stream().noneMatch(tag -> tag.getId().equals(unassociatedTagId)));
    assertNotNull(result.getCreatedAt());
    assertNotNull(result.getUpdatedAt());
  }

  @Test
  void 異常系_存在しないレッスンIDで例外が発生すること() {
    // Act & Assert
    ResourceNotFoundException exception =
        assertThrows(
            ResourceNotFoundException.class,
            () -> lessonApplicationService.findLessonById(1, 1, 999));
    assertEquals("Lesson が見つかりませんでした。id = 999", exception.getMessage());
  }

  @Test
  void 異常系_パスのコースまたはグループがレッスンと一致しない場合例外が発生すること() {
    Integer courseId = createCourse(new BigDecimal("1"), "整合コース", "説明");
    Integer lessonGroupId = createLessonGroup(courseId, new BigDecimal("1"), "整合グループ");
    Integer lessonId = createLesson(lessonGroupId, courseId, new BigDecimal("1"), "L1", "d", null);
    Integer otherCourseId = createCourse(new BigDecimal("2"), "別コース", "説明");

    ResourceNotFoundException exception =
        assertThrows(
            ResourceNotFoundException.class,
            () -> lessonApplicationService.findLessonById(otherCourseId, lessonGroupId, lessonId));
    assertEquals("Lesson が見つかりませんでした。id = " + lessonId, exception.getMessage());
  }

  @Test
  void 正常系_レッスンを検索できること() {
    // Arrange - テストデータを準備（IDは自動生成）
    Integer courseId = createCourse(new BigDecimal("1"), "テストコース", "コース説明");
    Integer lessonGroupId = createLessonGroup(courseId, new BigDecimal("1"), "テストグループ");
    createLesson(
        lessonGroupId,
        courseId,
        new BigDecimal("1"),
        "テストレッスン",
        "テスト説明",
        "https://example.com/video.mp4");

    LessonSearchRequest searchRequest = new LessonSearchRequest();
    searchRequest.setPageNum(1);
    searchRequest.setPageSize(10);
    searchRequest.setCourseId(String.valueOf(courseId));
    LessonSearchCommand searchCommand = searchRequest.toCommand();

    // Act
    LessonPageDto result = lessonApplicationService.findLessons(searchCommand);

    // Assert
    assertNotNull(result);
    assertTrue(result.getTotalSize() >= 1);
    assertEquals(1, result.getPageNum());
    assertEquals(10, result.getPageSize());
  }

  @Test
  void 正常系_検索結果が0件の場合() {
    // Arrange - データが存在しない状態
    LessonSearchRequest searchRequest = new LessonSearchRequest();
    searchRequest.setPageNum(1);
    searchRequest.setPageSize(10);
    searchRequest.setCourseId("999");
    LessonSearchCommand searchCommand = searchRequest.toCommand();

    // Act
    LessonPageDto result = lessonApplicationService.findLessons(searchCommand);

    // Assert
    assertNotNull(result);
    assertEquals(0, result.getTotalSize());
    assertEquals(1, result.getPageNum());
    assertEquals(10, result.getPageSize());
    assertTrue(result.getLessonDtos().isEmpty());
  }

  @Test
  void 正常系_コース別レッスン一覧を取得できること() {
    // Arrange - テストデータを準備（IDは自動生成）
    Integer courseId = createCourse(new BigDecimal("1"), "テストコース", "コース説明");
    Integer lessonGroupId = createLessonGroup(courseId, new BigDecimal("1"), "テストグループ");

    Integer lessonCId =
        createLesson(
            lessonGroupId,
            courseId,
            new BigDecimal("2"),
            "レッスンC",
            "レッスンCの説明",
            "https://example.com/lesson-c.mp4");
    Integer lessonBId =
        createLesson(
            lessonGroupId,
            courseId,
            new BigDecimal("1"),
            "レッスンB",
            "レッスンBの説明",
            "https://example.com/lesson-b.mp4");

    Integer tag1Id = createTag("タグ1");
    Integer tag2Id = createTag("タグ2");
    Integer tag3Id = createTag("タグ3");
    createLessonTag(lessonCId, tag1Id);
    createLessonTag(lessonCId, tag2Id);
    createLessonTag(lessonCId, tag3Id);

    // Act
    CourseLessonsDto result = lessonApplicationService.findLessonsGroupedByLessonGroup(courseId);

    // Assert
    assertNotNull(result);
    assertEquals(courseId, result.courseId());
    assertEquals(1, result.lessonGroups().size());

    List<LessonDto> lessons = result.lessonGroups().getFirst().lessons();
    assertEquals(2, lessons.size());
    assertEquals(List.of(lessonBId, lessonCId), lessons.stream().map(LessonDto::getId).toList());

    List<TagDto> lessonBTags = lessons.get(0).getTags();
    assertTrue(lessonBTags.isEmpty());

    List<TagDto> lessonCTags = lessons.get(1).getTags();
    assertEquals(3, lessonCTags.size());
    assertEquals(List.of(tag1Id, tag2Id, tag3Id), lessonCTags.stream().map(TagDto::getId).toList());
    assertEquals(List.of("タグ1", "タグ2", "タグ3"), lessonCTags.stream().map(TagDto::getName).toList());
  }

  @Test
  void 正常系_レッスンを作成できること() {
    // Arrange - 関連データを準備（IDは自動生成）
    Integer courseId = createCourse(new BigDecimal("1"), "テストコース", "コース説明");
    Integer lessonGroupId = createLessonGroup(courseId, new BigDecimal("1"), "テストグループ");
    Integer existingTagId = createTag("Java");

    LessonCreateRequest request = new LessonCreateRequest();
    request.setTitle("新規レッスン");
    request.setContent("新規説明");
    request.setVideoUrl("https://example.com/new-video.mp4");
    LessonTagRequest existingTag = new LessonTagRequest();
    existingTag.setName("Java");
    LessonTagRequest newTagFirst = new LessonTagRequest();
    newTagFirst.setName("Spring");
    LessonTagRequest newTagSecond = new LessonTagRequest();
    newTagSecond.setName("JUnit");
    request.setTags(List.of(newTagFirst, existingTag, newTagSecond));
    LessonCreateCommand command = request.toCommand(courseId, lessonGroupId);

    // Act
    LessonDto result = lessonApplicationService.createLesson(command);

    // Assert
    assertNotNull(result);
    assertEquals("新規レッスン", result.getTitle());
    assertEquals("新規説明", result.getContent());
    assertEquals("https://example.com/new-video.mp4", result.getVideoUrl());
    assertEquals(3, result.getTags().size());
    assertEquals(
        List.of("Spring", "Java", "JUnit"),
        result.getTags().stream().map(TagDto::getName).toList());
    assertEquals(existingTagId, result.getTags().get(1).getId());

    // DBにデータが保存されていることを確認
    Integer count =
        jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM lessons WHERE title = ?", Integer.class, "新規レッスン");
    assertEquals(1, count);
    String savedContent =
        jdbcTemplate.queryForObject(
            "SELECT content FROM lessons WHERE title = ?", String.class, "新規レッスン");
    assertEquals("新規説明", savedContent);
  }

  @Test
  void 正常系_タグを正規化および重複排除してレッスンを作成できること() {
    // Arrange
    Integer courseId = createCourse(new BigDecimal("1"), "テストコース", "コース説明");
    Integer lessonGroupId = createLessonGroup(courseId, new BigDecimal("1"), "テストグループ");

    LessonTagRequest javaTag = new LessonTagRequest();
    javaTag.setName("Java");
    LessonTagRequest javaTagWithSpaces = new LessonTagRequest();
    javaTagWithSpaces.setName(" Java ");
    LessonTagRequest javaTagWithFullWidthSpace = new LessonTagRequest();
    javaTagWithFullWidthSpace.setName("Java　");
    LessonTagRequest springTagWithSpaces = new LessonTagRequest();
    springTagWithSpaces.setName(" Spring　 ");
    LessonTagRequest junitTag = new LessonTagRequest();
    junitTag.setName("JUnit");

    LessonCreateRequest request = new LessonCreateRequest();
    request.setTitle("タグ正規化レッスン");
    request.setTags(
        List.of(
            javaTag, javaTagWithSpaces, javaTagWithFullWidthSpace, springTagWithSpaces, junitTag));
    LessonCreateCommand command = request.toCommand(courseId, lessonGroupId);

    // Act
    LessonDto result = lessonApplicationService.createLesson(command);

    // Assert
    assertEquals(3, result.getTags().size());
    assertEquals(
        List.of("Java", "Spring", "JUnit"),
        result.getTags().stream().map(TagDto::getName).toList());

    List<String> savedTagNames =
        jdbcTemplate.queryForList(
            """
                SELECT t.name
                FROM lesson_tags lt
                JOIN tags t ON t.id = lt.tag_id
                WHERE lt.lesson_id = ?
                """,
            String.class,
            result.getId());
    assertEquals(3, savedTagNames.size());
    assertEquals(Set.of("Java", "Spring", "JUnit"), Set.copyOf(savedTagNames));
  }

  @Test
  void 正常系_null許容フィールドを指定せずにレッスンを作成できること() {
    // Arrange - 関連データを準備（IDは自動生成）
    Integer courseId = createCourse(new BigDecimal("1"), "テストコース", "コース説明");
    Integer lessonGroupId = createLessonGroup(courseId, new BigDecimal("1"), "テストグループ");

    LessonCreateRequest request = new LessonCreateRequest();
    request.setTitle("最小構成レッスン");
    LessonCreateCommand command = request.toCommand(courseId, lessonGroupId);

    // Act
    LessonDto result = lessonApplicationService.createLesson(command);

    // Assert
    assertNotNull(result);
    assertEquals("最小構成レッスン", result.getTitle());
    assertNull(result.getContent());
    assertNull(result.getVideoUrl());
    assertTrue(result.getTags().isEmpty());

    Integer lessonTagCount =
        jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM lesson_tags WHERE lesson_id = ?", Integer.class, result.getId());
    assertEquals(0, lessonTagCount);
  }

  @Test
  void 正常系_レッスンを更新できること() {
    // Arrange - 既存レッスンを準備（IDは自動生成）
    Integer courseId = createCourse(new BigDecimal("1"), "テストコース", "コース説明");
    Integer lessonGroupId = createLessonGroup(courseId, new BigDecimal("1"), "テストグループ");
    Integer lessonId =
        createLesson(
            lessonGroupId,
            courseId,
            new BigDecimal("1"),
            "元のタイトル",
            "元の説明",
            "https://example.com/old-video.mp4");
    Integer tagId = createTag("Java");
    createLessonTag(lessonId, tagId);

    LessonUpdateRequest request = new LessonUpdateRequest();
    request.setTitle("更新後タイトル");
    request.setContent("更新後説明");
    request.setVideoUrl("https://example.com/updated-video.mp4");
    LessonTagRequest tagFirst = new LessonTagRequest();
    tagFirst.setName("Spring");
    LessonTagRequest tagSecond = new LessonTagRequest();
    tagSecond.setName("Java");
    LessonTagRequest tagThird = new LessonTagRequest();
    tagThird.setName("中級編");
    request.setTags(List.of(tagFirst, tagSecond, tagThird));
    LessonUpdateCommand command = request.toCommand(lessonId);

    // Act
    LessonDto result = lessonApplicationService.updateLesson(command);

    // Assert
    assertNotNull(result);
    assertEquals(lessonId, result.getId());
    assertEquals("更新後タイトル", result.getTitle());
    assertEquals("更新後説明", result.getContent());
    assertEquals("https://example.com/updated-video.mp4", result.getVideoUrl());
    assertTrue(result.getTags().stream().allMatch(t -> t.getId() != null));
    assertEquals(
        List.of("Spring", "Java", "中級編"), result.getTags().stream().map(TagDto::getName).toList());

    // DBが更新されていることを確認
    String updatedTitle =
        jdbcTemplate.queryForObject(
            "SELECT title FROM lessons WHERE id = ?", String.class, lessonId);
    assertEquals("更新後タイトル", updatedTitle);
    String updatedContent =
        jdbcTemplate.queryForObject(
            "SELECT content FROM lessons WHERE id = ?", String.class, lessonId);
    assertEquals("更新後説明", updatedContent);
    List<String> updatedTags =
        jdbcTemplate.queryForList(
            """
                SELECT t.name
                FROM lesson_tags lt
                JOIN tags t ON t.id = lt.tag_id
                WHERE lt.lesson_id = ?
                ORDER BY lt.id
                """,
            String.class,
            lessonId);
    assertEquals(3, updatedTags.size());
    assertEquals(List.of("Spring", "Java", "中級編"), updatedTags);

    List<Integer> tagIds =
        jdbcTemplate.queryForList(
            "SELECT tag_id FROM lesson_tags WHERE lesson_id = ? order by tag_id",
            Integer.class,
            lessonId);
    assertEquals(3, tagIds.size());
    assertEquals(result.getTags().stream().map(TagDto::getId).sorted().toList(), tagIds);
  }

  @Test
  void 正常系_null許容フィールドをnullで更新できること() {
    // Arrange - 既存レッスンを準備（IDは自動生成）
    Integer courseId = createCourse(new BigDecimal("1"), "テストコース", "コース説明");
    Integer lessonGroupId = createLessonGroup(courseId, new BigDecimal("1"), "テストグループ");
    Integer lessonId =
        createLesson(
            lessonGroupId,
            courseId,
            new BigDecimal("1"),
            "元のタイトル",
            "元の説明",
            "https://example.com/old-video.mp4");

    // nullを渡すと元の値が保持される仕様
    LessonUpdateRequest request = new LessonUpdateRequest();
    request.setTitle("タイトルのみ更新");
    LessonUpdateCommand command = request.toCommand(lessonId);

    // Act
    LessonDto result = lessonApplicationService.updateLesson(command);

    // Assert - nullを渡した場合は元の値が保持される
    assertNotNull(result);
    assertEquals("タイトルのみ更新", result.getTitle());
    assertEquals("元の説明", result.getContent()); // 元の値が保持される
    assertEquals("https://example.com/old-video.mp4", result.getVideoUrl()); // 元の値が保持される
  }

  @Test
  void 正常系_登録するタグが存在しない場合は既存のタグ紐づけが削除されること() {
    // Arrange - 既存レッスンを準備（IDは自動生成）
    Integer courseId = createCourse(new BigDecimal("1"), "テストコース", "コース説明");
    Integer lessonGroupId = createLessonGroup(courseId, new BigDecimal("1"), "テストグループ");
    Integer lessonId =
        createLesson(
            lessonGroupId,
            courseId,
            new BigDecimal("1"),
            "元のタイトル",
            "元の説明",
            "https://example.com/old-video.mp4");
    Integer tagId = createTag("Java");
    createLessonTag(lessonId, tagId);

    LessonUpdateRequest request = new LessonUpdateRequest();
    request.setTitle("更新後タイトル");
    request.setContent("更新後説明");
    request.setVideoUrl("https://example.com/updated-video.mp4");
    LessonUpdateCommand command = request.toCommand(lessonId);

    // Act
    LessonDto result = lessonApplicationService.updateLesson(command);

    // Assert
    assertNotNull(result);
    assertTrue(result.getTags().isEmpty());

    // タグに関するデータが変わっていないことを確認
    List<Tag> tagList =
        jdbcTemplate.query(
            "SELECT * FROM tags WHERE id = ?", new DataClassRowMapper(Tag.class), tagId);
    assertEquals(1, tagList.size());
    assertEquals(tagId, tagList.get(0).getId());
    assertEquals("Java", tagList.get(0).getName().getValue());

    List<LessonTag> lessonTagList =
        jdbcTemplate.query(
            "SELECT * FROM lesson_tags WHERE lesson_id = ?",
            new DataClassRowMapper(LessonTag.class),
            lessonId);
    assertEquals(0, lessonTagList.size());
  }

  @Test
  void 正常系_登録するタグが正規化されること() {
    // Arrange - 既存レッスンを準備（IDは自動生成）
    Integer courseId = createCourse(new BigDecimal("1"), "テストコース", "コース説明");
    Integer lessonGroupId = createLessonGroup(courseId, new BigDecimal("1"), "テストグループ");
    Integer lessonId =
        createLesson(
            lessonGroupId,
            courseId,
            new BigDecimal("1"),
            "元のタイトル",
            "元の説明",
            "https://example.com/old-video.mp4");

    LessonUpdateRequest request = new LessonUpdateRequest();
    request.setTitle("更新後タイトル");
    request.setContent("更新後説明");
    request.setVideoUrl("https://example.com/updated-video.mp4");
    LessonTagRequest tag1 = new LessonTagRequest();
    tag1.setName("Java");
    LessonTagRequest tag2 = new LessonTagRequest();
    tag2.setName(" Spring　 ");
    LessonTagRequest tag3 = new LessonTagRequest();
    tag3.setName("入門");
    LessonTagRequest tag4 = new LessonTagRequest();
    tag4.setName("入門");
    request.setTags(List.of(tag1, tag2, tag3, tag4));
    String[] expectedTags = new String[] {"Java", "Spring", "入門"};
    LessonUpdateCommand command = request.toCommand(lessonId);
    // Act
    LessonDto result = lessonApplicationService.updateLesson(command);

    // Assert
    assertArrayEquals(expectedTags, result.getTags().stream().map(TagDto::getName).toArray());

    // DBが更新されていることを確認
    List<String> updatedTags =
        jdbcTemplate.queryForList(
            """
                SELECT t.name
                FROM lesson_tags lt
                JOIN tags t ON t.id = lt.tag_id
                WHERE lt.lesson_id = ?
                ORDER BY lt.id
                """,
            String.class,
            lessonId);
    assertEquals(3, updatedTags.size());

    assertArrayEquals(expectedTags, updatedTags.toArray());
  }

  @Test
  void 異常系_存在しないレッスンを更新すると例外が発生すること() {
    // Arrange
    LessonUpdateRequest request = new LessonUpdateRequest();
    request.setTitle("存在しないレッスン");
    request.setContent("説明");
    request.setVideoUrl("https://example.com/video.mp4");
    LessonUpdateCommand command = request.toCommand(999);

    // Act & Assert
    ResourceNotFoundException exception =
        assertThrows(
            ResourceNotFoundException.class, () -> lessonApplicationService.updateLesson(command));
    assertEquals("Lesson が見つかりませんでした。id = 999", exception.getMessage());
  }

  @Test
  void 正常系_存在するレッスンを削除できること() {
    // Arrange - 削除対象のレッスンを準備（IDは自動生成）
    Integer courseId = createCourse(new BigDecimal("1"), "テストコース", "コース説明");
    Integer lessonGroupId = createLessonGroup(courseId, new BigDecimal("1"), "テストグループ");
    Integer lessonId =
        createLesson(
            lessonGroupId,
            courseId,
            new BigDecimal("1"),
            "削除対象レッスン",
            "説明",
            "https://example.com/video.mp4");

    // Act
    lessonApplicationService.deleteLessonById(lessonId);

    // Assert - レッスンが削除されていることを確認
    Integer count =
        jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM lessons WHERE id = ?", Integer.class, lessonId);
    assertEquals(0, count);
  }

  @Test
  void 正常系_存在しないレッスンを削除してもエラーにならないこと() {
    // Act - 存在しないIDで削除を実行
    lessonApplicationService.deleteLessonById(999);

    // Assert - 例外が発生しないことを確認（このテストが成功すればOK）
  }

  @Test
  void 正常系_複数回の削除操作が安全に実行できること() {
    // Arrange - 削除対象のレッスンを準備（IDは自動生成）
    Integer courseId = createCourse(new BigDecimal("1"), "テストコース", "コース説明");
    Integer lessonGroupId = createLessonGroup(courseId, new BigDecimal("1"), "テストグループ");
    Integer lessonId =
        createLesson(
            lessonGroupId,
            courseId,
            new BigDecimal("1"),
            "複数回削除テスト",
            "説明",
            "https://example.com/video.mp4");

    // Act
    lessonApplicationService.deleteLessonById(lessonId);
    lessonApplicationService.deleteLessonById(lessonId); // 2回目

    // Assert - 2回目の削除もエラーにならないことを確認
    Integer count =
        jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM lessons WHERE id = ?", Integer.class, lessonId);
    assertEquals(0, count);
  }

  @Test
  void 正常系_2つのレッスンの間に移動できること() {
    // Arrange - テストデータを準備
    Integer courseId = createCourse(new BigDecimal("1"), "テストコース", "コース説明");
    Integer lessonGroupId = createLessonGroup(courseId, new BigDecimal("1"), "テストグループ");

    // 3つのレッスンを作成（order: 1000, 2000, 3000）
    Integer lesson1Id =
        createLesson(
            lessonGroupId,
            courseId,
            new BigDecimal("1000"),
            "レッスン1",
            "説明1",
            "https://example.com/video1.mp4");

    Integer lesson2Id =
        createLesson(
            lessonGroupId,
            courseId,
            new BigDecimal("2000"),
            "レッスン2",
            "説明2",
            "https://example.com/video2.mp4");

    Integer lesson3Id =
        createLesson(
            lessonGroupId,
            courseId,
            new BigDecimal("3000"),
            "レッスン3",
            "説明3",
            "https://example.com/video3.mp4");

    // Act - レッスン3をレッスン1と2の間に移動
    LessonOrderUpdateRequest request = new LessonOrderUpdateRequest();
    request.setPrecedingLessonId(lesson1Id);
    request.setFollowingLessonId(lesson2Id);
    LessonOrderUpdateCommand command = request.toCommand(lesson3Id);
    LessonDto result = lessonApplicationService.updateLessonOrder(command);

    // Assert - 新しい順序は (1000 + 2000) / 2 = 1500
    assertNotNull(result);
    assertEquals(lesson3Id, result.getId());
    assertEquals(new BigDecimal("1500.0000"), result.getLessonOrder());

    // DBが更新されていることを確認
    BigDecimal updatedOrder =
        jdbcTemplate.queryForObject(
            "SELECT lesson_order FROM lessons WHERE id = ?", BigDecimal.class, lesson3Id);
    assertEquals(new BigDecimal("1500.0000"), updatedOrder);
  }

  @Test
  void 正常系_並び替え後のレスポンスに対象レッスンのタグが含まれること() {
    // Arrange
    Integer courseId = createCourse(new BigDecimal("1"), "テストコース", "コース説明");
    Integer lessonGroupId = createLessonGroup(courseId, new BigDecimal("1"), "テストグループ");
    Integer targetLessonId =
        createLesson(
            lessonGroupId,
            courseId,
            new BigDecimal("1000"),
            "並び替え対象レッスン",
            "説明1",
            "https://example.com/video1.mp4");
    Integer otherLessonId =
        createLesson(
            lessonGroupId,
            courseId,
            new BigDecimal("2000"),
            "別のレッスン",
            "説明2",
            "https://example.com/video2.mp4");

    Integer tag1Id = createTag("タグ1");
    Integer tag2Id = createTag("タグ2");
    Integer tag3Id = createTag("タグ3");
    Integer otherLessonTagId = createTag("別レッスンのタグ");
    createLessonTag(targetLessonId, tag3Id);
    createLessonTag(targetLessonId, tag1Id);
    createLessonTag(targetLessonId, tag2Id);
    createLessonTag(otherLessonId, otherLessonTagId);

    LessonOrderUpdateRequest request = new LessonOrderUpdateRequest();
    request.setPrecedingLessonId(otherLessonId);
    request.setFollowingLessonId(null);

    // Act
    LessonDto result =
        lessonApplicationService.updateLessonOrder(request.toCommand(targetLessonId));

    // Assert
    assertEquals(
        List.of(tag1Id, tag2Id, tag3Id), result.getTags().stream().map(TagDto::getId).toList());
    assertEquals(tag1Id, result.getTags().get(0).getId());
    assertEquals("タグ1", result.getTags().get(0).getName());
    assertTrue(result.getTags().stream().noneMatch(tag -> tag.getId().equals(otherLessonTagId)));
  }

  @Test
  void 正常系_先頭に移動できること() {
    // Arrange - テストデータを準備
    Integer courseId = createCourse(new BigDecimal("1"), "テストコース", "コース説明");
    Integer lessonGroupId = createLessonGroup(courseId, new BigDecimal("1"), "テストグループ");

    // 2つのレッスンを作成
    Integer lesson1Id =
        createLesson(
            lessonGroupId,
            courseId,
            new BigDecimal("1000"),
            "レッスン1",
            "説明1",
            "https://example.com/video1.mp4");
    Integer lesson2Id =
        createLesson(
            lessonGroupId,
            courseId,
            new BigDecimal("2000"),
            "レッスン2",
            "説明2",
            "https://example.com/video2.mp4");

    // Act - レッスン2を先頭に移動（precedingLessonIdをnullに）
    LessonOrderUpdateRequest request = new LessonOrderUpdateRequest();
    request.setPrecedingLessonId(null);
    request.setFollowingLessonId(lesson1Id);
    LessonOrderUpdateCommand command = request.toCommand(lesson2Id);
    LessonDto result = lessonApplicationService.updateLessonOrder(command);

    // Assert - 新しい順序は 1000 / 2 = 500
    assertNotNull(result);
    assertEquals(lesson2Id, result.getId());
    assertEquals(new BigDecimal("500.0000"), result.getLessonOrder());

    // DBが更新されていることを確認
    BigDecimal updatedOrder =
        jdbcTemplate.queryForObject(
            "SELECT lesson_order FROM lessons WHERE id = ?", BigDecimal.class, lesson2Id);
    assertEquals(new BigDecimal("500.0000"), updatedOrder);
  }

  @Test
  void 正常系_末尾に移動できること() {
    // Arrange - テストデータを準備
    Integer courseId = createCourse(new BigDecimal("1"), "テストコース", "コース説明");
    Integer lessonGroupId = createLessonGroup(courseId, new BigDecimal("1"), "テストグループ");

    // 2つのレッスンを作成
    Integer lesson1Id =
        createLesson(
            lessonGroupId,
            courseId,
            new BigDecimal("1000"),
            "レッスン1",
            "説明1",
            "https://example.com/video1.mp4");
    Integer lesson2Id =
        createLesson(
            lessonGroupId,
            courseId,
            new BigDecimal("2000"),
            "レッスン2",
            "説明2",
            "https://example.com/video2.mp4");

    // Act - レッスン1を末尾に移動（followingLessonIdをnullに）
    LessonOrderUpdateRequest request = new LessonOrderUpdateRequest();
    request.setPrecedingLessonId(lesson2Id);
    request.setFollowingLessonId(null);
    LessonOrderUpdateCommand command = request.toCommand(lesson1Id);
    LessonDto result = lessonApplicationService.updateLessonOrder(command);

    // Assert - 新しい順序は 2000 + 1024 = 3024
    assertNotNull(result);
    assertEquals(lesson1Id, result.getId());
    assertEquals(new BigDecimal("3024.0000"), result.getLessonOrder());

    // DBが更新されていることを確認
    BigDecimal updatedOrder =
        jdbcTemplate.queryForObject(
            "SELECT lesson_order FROM lessons WHERE id = ?", BigDecimal.class, lesson1Id);
    assertEquals(new BigDecimal("3024.0000"), updatedOrder);
  }

  @Test
  void 異常系_存在しないレッスンIDで並び替えすると例外が発生すること() {
    // Arrange - 存在するレッスンを1つ準備
    Integer courseId = createCourse(new BigDecimal("1"), "テストコース", "コース説明");
    Integer lessonGroupId = createLessonGroup(courseId, new BigDecimal("1"), "テストグループ");
    Integer lesson1Id =
        createLesson(
            lessonGroupId,
            courseId,
            new BigDecimal("1000"),
            "レッスン1",
            "説明1",
            "https://example.com/video1.mp4");

    // Act & Assert - 存在しないレッスンIDで並び替え
    LessonOrderUpdateRequest request = new LessonOrderUpdateRequest();
    request.setPrecedingLessonId(lesson1Id);
    request.setFollowingLessonId(null);
    ResourceNotFoundException exception =
        assertThrows(
            ResourceNotFoundException.class,
            () -> lessonApplicationService.updateLessonOrder(request.toCommand(999)));
    assertEquals("Lesson が見つかりませんでした。id = 999", exception.getMessage());
  }

  @Test
  void 異常系_存在しないprecedingLessonIdで例外が発生すること() {
    // Arrange - テストデータを準備
    Integer courseId = createCourse(new BigDecimal("1"), "テストコース", "コース説明");
    Integer lessonGroupId = createLessonGroup(courseId, new BigDecimal("1"), "テストグループ");
    Integer lesson1Id =
        createLesson(
            lessonGroupId,
            courseId,
            new BigDecimal("1000"),
            "レッスン1",
            "説明1",
            "https://example.com/video1.mp4");

    // Act & Assert - 存在しないprecedingLessonId
    LessonOrderUpdateRequest request = new LessonOrderUpdateRequest();
    request.setPrecedingLessonId(999);
    request.setFollowingLessonId(null);
    ResourceNotFoundException exception =
        assertThrows(
            ResourceNotFoundException.class,
            () -> lessonApplicationService.updateLessonOrder(request.toCommand(lesson1Id)));
    assertEquals("Lesson が見つかりませんでした。id = 999", exception.getMessage());
  }

  @Test
  void 異常系_存在しないfollowingLessonIdで例外が発生すること() {
    // Arrange - テストデータを準備
    Integer courseId = createCourse(new BigDecimal("1"), "テストコース", "コース説明");
    Integer lessonGroupId = createLessonGroup(courseId, new BigDecimal("1"), "テストグループ");
    Integer lesson1Id =
        createLesson(
            lessonGroupId,
            courseId,
            new BigDecimal("1000"),
            "レッスン1",
            "説明1",
            "https://example.com/video1.mp4");

    // Act & Assert - 存在しないfollowingLessonId
    LessonOrderUpdateRequest request = new LessonOrderUpdateRequest();
    request.setPrecedingLessonId(null);
    request.setFollowingLessonId(999);
    ResourceNotFoundException exception =
        assertThrows(
            ResourceNotFoundException.class,
            () -> lessonApplicationService.updateLessonOrder(request.toCommand(lesson1Id)));
    assertEquals("Lesson が見つかりませんでした。id = 999", exception.getMessage());
  }
}
