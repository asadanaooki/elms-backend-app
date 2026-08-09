package com.everrefine.elms.domain.exception;

/** ドメインエンティティが見つからない場合にスローされる例外。 */
public class EntityNotFoundException extends RuntimeException {

  /**
   * エンティティ未検出例外を生成する。
   *
   * @param entityClass エンティティのクラス
   * @param id エンティティID
   */
  public EntityNotFoundException(Class<?> entityClass, String id) {
    super(entityClass.getSimpleName() + " が見つかりませんでした。id = " + id);
  }
}
