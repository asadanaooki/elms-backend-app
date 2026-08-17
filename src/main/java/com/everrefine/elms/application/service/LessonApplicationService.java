package com.everrefine.elms.application.service;

import com.everrefine.elms.application.command.LessonCreateCommand;
import com.everrefine.elms.application.command.LessonImportCommand;
import com.everrefine.elms.application.command.LessonOrderUpdateCommand;
import com.everrefine.elms.application.command.LessonSearchCommand;
import com.everrefine.elms.application.command.LessonUpdateCommand;
import com.everrefine.elms.application.dto.CourseLessonsDto;
import com.everrefine.elms.application.dto.LessonDto;
import com.everrefine.elms.application.dto.LessonImportResponseDto;
import com.everrefine.elms.application.dto.LessonPageDto;
import com.everrefine.elms.application.dto.LessonSearchResultDto;
import java.util.UUID;
import org.springframework.core.io.Resource;

/** レッスンアプリケーションサービスのインターフェース。 */
public interface LessonApplicationService {

  /**
   * IDでレッスンを取得する。
   *
   * @param courseId コースID
   * @param lessonGroupId レッスングループID
   * @param lessonId レッスンID
   * @return レッスンDTO
   */
  LessonDto findLessonById(UUID courseId, UUID lessonGroupId, UUID lessonId);

  /**
   * レッスン一覧をページング取得する。
   *
   * @param lessonSearchCommand レッスン検索Command
   * @return レッスンページDTO
   */
  LessonPageDto findLessons(LessonSearchCommand lessonSearchCommand);

  /**
   * 指定されたタグ名に紐づくレッスンをページング検索し、コースおよびレッスングループ単位にまとめて取得する。
   *
   * @param tagName 検索対象のタグ名
   * @param pageNum ページ番号（1以上）
   * @param pageSize 1ページ当たりの件数（1以上）
   * @return タグに紐づくレッスンの検索結果
   */
  LessonSearchResultDto searchLessonsByTagName(String tagName, int pageNum, int pageSize);

  /**
   * コースIDに紐づくレッスンをレッスングループごとにまとめて取得する。
   *
   * @param courseId コースID
   * @return コースとレッスン一覧DTO
   */
  CourseLessonsDto findLessonsGroupedByLessonGroup(UUID courseId);

  /**
   * レッスンを作成する。
   *
   * @param lessonCreateCommand レッスン作成Command
   * @return 作成したレッスンDTO
   */
  LessonDto createLesson(LessonCreateCommand lessonCreateCommand);

  /**
   * レッスンを更新する。
   *
   * @param lessonUpdateCommand レッスン更新Command
   * @return 更新したレッスンDTO
   */
  LessonDto updateLesson(LessonUpdateCommand lessonUpdateCommand);

  /**
   * IDでレッスンを削除する。
   *
   * @param lessonId レッスンID
   */
  void deleteLessonById(UUID lessonId);

  /**
   * レッスン順序を更新する。
   *
   * @param lessonOrderUpdateCommand レッスン順序更新Command
   * @return 更新したレッスンDTO
   */
  LessonDto updateLessonOrder(LessonOrderUpdateCommand lessonOrderUpdateCommand);

  /**
   * 全レッスンをCSV形式でエクスポートする。
   *
   * @return CSVリソース
   */
  Resource exportAllLessonsCsv();

  /**
   * CSVファイルをアップロードして指定コースのレッスン構成を一括更新する。
   *
   * @param lessonImportCommand レッスン取込用Command
   * @return 取込したレッスングループ件数とレッスン件数
   */
  LessonImportResponseDto importLessonsCsv(LessonImportCommand lessonImportCommand);
}
