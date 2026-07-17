package com.everrefine.elms.domain.model.tag;

import com.everrefine.elms.domain.exception.InvalidValueException;
import lombok.Value;

/** タグ名の値オブジェクト。 */
@Value
public class TagName {

  // 最大文字数
  private static final int MAX_LENGTH = 255;

  String value;

  /**
   * タグ名を作成する。
   *
   * @param value タグ名（255文字以内）
   */
  public TagName(String value) {
    if (value == null || value.length() > MAX_LENGTH) {
      throw new InvalidValueException("タグ名は" + MAX_LENGTH + "文字以内で入力してください");
    }
    this.value = value;
  }
}
