package com.everrefine.elms.domain.model.tag;

import com.everrefine.elms.domain.exception.InvalidValueException;

/** タグ名の値オブジェクト。 */
public record TagName(String value) {

  // 最大文字数
  private static final int MAX_LENGTH = 255;

  /**
   * タグ名を作成する。
   *
   * @param value タグ名（255文字以内）
   */
  public TagName {
    if (value == null || value.length() > MAX_LENGTH) {
      throw new InvalidValueException("タグ名は" + MAX_LENGTH + "文字以内で入力してください");
    }
  }
}
