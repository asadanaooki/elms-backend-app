package com.everrefine.elms.application.service;

import com.everrefine.elms.application.command.LessonCreateCommand;
import com.everrefine.elms.application.command.LessonOrderUpdateCommand;
import com.everrefine.elms.application.command.LessonSearchCommand;
import com.everrefine.elms.application.command.LessonUpdateCommand;
import com.everrefine.elms.application.dto.CourseLessonsDto;
import com.everrefine.elms.application.dto.LessonDto;
import com.everrefine.elms.application.dto.LessonGroupDto;
import com.everrefine.elms.application.dto.LessonPageDto;
import com.everrefine.elms.application.dto.LessonWithCourseAndLessonGroupDto;
import com.everrefine.elms.application.dto.TagDto;
import com.everrefine.elms.application.exception.ResourceNotFoundException;
import com.everrefine.elms.domain.model.LessonTag;
import com.everrefine.elms.domain.model.lesson.Lesson;
import com.everrefine.elms.domain.model.lesson.LessonGroupWithLesson;
import com.everrefine.elms.domain.model.lesson.LessonWithCourseAndLessonGroup;
import com.everrefine.elms.domain.model.tag.Tag;
import com.everrefine.elms.domain.model.tag.TagName;
import com.everrefine.elms.domain.repository.LessonRepository;
import com.everrefine.elms.domain.repository.LessonTagRepository;
import com.everrefine.elms.domain.repository.TagRepository;
import com.everrefine.elms.domain.service.LessonDomainService;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** レッスンアプリケーションサービスの実装に関するクラス。 */
@Service
@AllArgsConstructor
public class LessonApplicationServiceImpl implements LessonApplicationService {

  private final LessonRepository lessonRepository;

  private final LessonDomainService lessonDomainService;

  private final LessonTagRepository lessonTagRepository;

  private final TagRepository tagRepository;

  /**
   * CSV出力用に値をエスケープする。
   *
   * <p>値にカンマ、ダブルクォーテーション、改行があった場合のエスケープ処理。
   *
   * @param value エスケープ対象の文字列
   * @return エスケープ後の文字列
   */
  private String escape(String value) {
    if (value == null) {
      return "";
    }
    if (value.contains(",")
        || value.contains("\"")
        || value.contains("\n")
        || value.contains("\r")) {
      return "\"" + value.replace("\"", "\"\"") + "\"";
    }
    return value;
  }

  @Override
  @Transactional(readOnly = true)
  public LessonDto findLessonById(Integer courseId, Integer lessonGroupId, Integer lessonId) {
    Lesson lesson = findLessonBelongingToCourseAndGroupOrThrow(lessonId, courseId, lessonGroupId);
    return LessonDto.from(lesson, Collections.emptyList());
  }

  @Override
  @Transactional(readOnly = true)
  public LessonPageDto findLessons(LessonSearchCommand lessonSearchCommand) {
    List<Lesson> lessons = lessonRepository.findLessons(lessonSearchCommand.toCriteria());
    int totalSize = lessonRepository.countLessons(lessonSearchCommand.toCriteria());

    List<LessonDto> lessonDtos =
        lessons.stream().map(l -> LessonDto.from(l, Collections.emptyList())).toList();

    return LessonPageDto.from(
        lessonDtos, lessonSearchCommand.getPageNum(), lessonSearchCommand.getPageSize(), totalSize);
  }

  /**
   * コースとレッスングループに属するレッスンを取得する。存在しない場合は例外をスローする。
   *
   * @param lessonId レッスンID
   * @param courseId コースID
   * @param lessonGroupId レッスングループID
   * @return レッスンエンティティ
   */
  private Lesson findLessonBelongingToCourseAndGroupOrThrow(
      Integer lessonId, Integer courseId, Integer lessonGroupId) {
    Lesson lesson = findLessonOrThrow(lessonId);
    if (!lesson.getCourseId().equals(courseId)
        || !lesson.getLessonGroupId().equals(lessonGroupId)) {
      throw new ResourceNotFoundException(Lesson.class, String.valueOf(lessonId));
    }
    return lesson;
  }

  /**
   * IDでレッスンを取得する。存在しない場合は例外をスローする。
   *
   * @param lessonId レッスンID
   * @return レッスンエンティティ
   */
  private Lesson findLessonOrThrow(Integer lessonId) {
    return lessonRepository
        .findById(lessonId)
        .orElseThrow(() -> new ResourceNotFoundException(Lesson.class, String.valueOf(lessonId)));
  }

  @Override
  @Transactional(readOnly = true)
  public CourseLessonsDto findLessonsGroupedByLessonGroup(Integer courseId) {
    List<LessonGroupWithLesson> lessons =
        lessonRepository.findLessonsGroupedByLessonGroup(courseId);
    Map<Integer, List<LessonGroupWithLesson>> lessonGroupIdAndLessonsMap =
        lessons.stream().collect(Collectors.groupingBy(LessonGroupWithLesson::getLessonGroupId));
    List<LessonGroupDto> lessonGroupDtos =
        lessonGroupIdAndLessonsMap.values().stream().map(LessonGroupDto::from).toList();
    return new CourseLessonsDto(courseId, lessonGroupDtos);
  }

  @Override
  @Transactional
  public LessonDto createLesson(LessonCreateCommand lessonCreateCommand) {
    BigDecimal lessonOrder =
        lessonDomainService.issueLessonOrder(lessonCreateCommand.getLessonGroupId());
    Lesson createdLesson = lessonRepository.createLesson(lessonCreateCommand.toLesson(lessonOrder));
    return LessonDto.from(createdLesson, Collections.emptyList());
  }

  @Override
  @Transactional
  public LessonDto updateLesson(LessonUpdateCommand lessonUpdateCommand) {
    Integer lessonId = lessonUpdateCommand.getId();
    Lesson currentLesson = findLessonOrThrow(lessonId);
    Lesson updatedLesson =
        lessonRepository.updateLesson(lessonUpdateCommand.toLesson(currentLesson));

    List<TagDto> associatedTagDtos = replaceLessonTags(lessonId, lessonUpdateCommand.getTagNames());

    return LessonDto.from(updatedLesson, associatedTagDtos);
  }

  @Override
  @Transactional
  public void deleteLessonById(Integer lessonId) {
    lessonRepository
        .findById(lessonId)
        .ifPresent(lesson -> lessonRepository.deleteLessonById(lessonId));
  }

  /**
   * レッスンIDに対応するレッスン順序を返す。IDがnullの場合はnullを返す。
   *
   * @param lessonId レッスンID（nullの場合はnullを返す）
   * @param lessonIdAndLessonMap レッスンIDとレッスンのMap
   * @return レッスン順序（lessonIdがnullの場合はnull）
   */
  private BigDecimal resolveLessonOrderOrNull(
      Integer lessonId, Map<Integer, Lesson> lessonIdAndLessonMap) {
    if (lessonId == null) {
      return null;
    }

    Lesson lesson = lessonIdAndLessonMap.get(lessonId);
    if (lesson == null) {
      throw new ResourceNotFoundException(Lesson.class, String.valueOf(lessonId));
    }

    return lesson.getLessonOrder().getValue();
  }

  @Override
  @Transactional
  public LessonDto updateLessonOrder(LessonOrderUpdateCommand lessonOrderUpdateCommand) {
    Integer targetLessonId = lessonOrderUpdateCommand.getLessonId();
    Integer precedingLessonId = lessonOrderUpdateCommand.getPrecedingLessonId();
    Integer followingLessonId = lessonOrderUpdateCommand.getFollowingLessonId();

    List<Integer> lessonIds = new ArrayList<>();
    lessonIds.add(targetLessonId);
    if (precedingLessonId != null && !lessonIds.contains(precedingLessonId)) {
      lessonIds.add(precedingLessonId);
    }
    if (followingLessonId != null && !lessonIds.contains(followingLessonId)) {
      lessonIds.add(followingLessonId);
    }

    Map<Integer, Lesson> lessonIdAndLessonMap =
        lessonRepository.findByIdIn(lessonIds).stream()
            .collect(Collectors.toMap(Lesson::getId, Function.identity()));

    Lesson targetLesson = lessonIdAndLessonMap.get(targetLessonId);
    if (targetLesson == null) {
      throw new ResourceNotFoundException(Lesson.class, String.valueOf(targetLessonId));
    }

    BigDecimal precedingOrder = resolveLessonOrderOrNull(precedingLessonId, lessonIdAndLessonMap);

    BigDecimal followingOrder = resolveLessonOrderOrNull(followingLessonId, lessonIdAndLessonMap);

    BigDecimal newOrder = lessonDomainService.calculateNewOrder(precedingOrder, followingOrder);

    Lesson updatedLesson = targetLesson.updateOrder(newOrder);
    Lesson savedLesson = lessonRepository.updateLesson(updatedLesson);

    return LessonDto.from(savedLesson, Collections.emptyList());
  }

  @Transactional(readOnly = true)
  @Override
  public Resource exportAllLessonsCsv() {

    String[] header = {
      "コースID", "コース名", "レッスングループID", "レッスングループ名", "レッスンID", "レッスンタイトル", "レッスンの動画URL"
    };

    List<LessonWithCourseAndLessonGroup> allLessons = lessonRepository.findAllLessons();

    if (allLessons.isEmpty()) {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      try (PrintWriter file =
          new PrintWriter(
              new BufferedWriter(new OutputStreamWriter(baos, StandardCharsets.UTF_8)))) {
        file.print('\uFEFF');
        file.println(String.join(",", header));
      } catch (Exception e) {
        throw new RuntimeException("CSVファイルの作成に失敗しました", e);
      }
      return new ByteArrayResource(baos.toByteArray());
    }

    List<LessonWithCourseAndLessonGroupDto> dtos =
        allLessons.stream().map(LessonWithCourseAndLessonGroupDto::from).toList();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    try (
    // 書き込むファイルを作成
    PrintWriter file =
        new PrintWriter(
            new BufferedWriter(new OutputStreamWriter(baos, StandardCharsets.UTF_8))); ) {
      // Excelで開いたときの文字化けを防ぐ
      file.print('\uFEFF');

      // ヘッダーをセットする
      file.println(String.join(",", header));

      // 中身をセットする ( 1レッスンごとに改行 )
      for (LessonWithCourseAndLessonGroupDto dto : dtos) {
        String[] lessons = {
          escape(String.valueOf(dto.getCourseId())),
          escape(dto.getCourseTitle()),
          escape(String.valueOf(dto.getLessonGroupId())),
          escape(dto.getLessonGroupTitle()),
          escape(String.valueOf(dto.getLessonId())),
          escape(dto.getLessonTitle()),
          escape(dto.getVideoUrl())
        };
        file.println(String.join(",", lessons));
      }
    } catch (Exception e) {
      throw new RuntimeException("CSVファイルの作成に失敗しました", e);
    }
    return new ByteArrayResource(baos.toByteArray());
  }

  private List<TagDto> replaceLessonTags(Integer lessonId, List<String> requestedTagNames) {
    Map<String, Tag> uniqueTagsByName = new LinkedHashMap<>();
    requestedTagNames.stream()
        .map(Tag::create)
        .forEach(t -> uniqueTagsByName.putIfAbsent(t.getName().getValue(), t));

    List<Tag> normalizedTags = new ArrayList<Tag>(uniqueTagsByName.values());
    if (normalizedTags.isEmpty()) {
      lessonTagRepository.deleteAllByLessonId(lessonId);

      return List.of();
    }

    // 正規化済みのタグと既存タグをマージし、保存する
    List<Tag> existingTags =
        tagRepository.findAllTagsByNames(normalizedTags.stream().map(Tag::getName).toList());
    Map<TagName, Tag> existingTagMap =
        existingTags.stream().collect(Collectors.toMap(Tag::getName, Function.identity()));
    List<Tag> mergedTags =
        normalizedTags.stream().map(t -> existingTagMap.getOrDefault(t.getName(), t)).toList();
    tagRepository.saveTags(mergedTags);

    // 正規化後のタグ名に対応するタグを、既存タグを含めて取得（レッスンタグの洗い替えのために使用）
    List<Tag> tagsInRequestOrder = findTagsInRequestOrder(normalizedTags);

    // レッスンタグの洗い替え
    lessonTagRepository.deleteAllByLessonId(lessonId);
    List<LessonTag> lessonTagAssociations =
        tagsInRequestOrder.stream().map(t -> LessonTag.create(lessonId, t.getId())).toList();
    lessonTagRepository.saveAll(lessonTagAssociations);

    return toTagDtos(tagsInRequestOrder);
  }

  private List<Tag> findTagsInRequestOrder(List<Tag> normalizedTags) {
    Map<TagName, Tag> persistedTagsByName =
        tagRepository
            .findAllTagsByNames(normalizedTags.stream().map(Tag::getName).toList())
            .stream()
            .collect(Collectors.toMap(Tag::getName, Function.identity()));

    return normalizedTags.stream().map(t -> persistedTagsByName.get(t.getName())).toList();
  }

  private List<TagDto> toTagDtos(List<Tag> tags) {
    return tags.stream().map(t -> new TagDto(t.getId(), t.getName().getValue())).toList();
  }
}
