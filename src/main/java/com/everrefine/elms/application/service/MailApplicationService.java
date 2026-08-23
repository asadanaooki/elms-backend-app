package com.everrefine.elms.application.service;

/** メールアプリケーションサービスのインターフェース。 */
public interface MailApplicationService {

  /**
   * パスワードリセットメールを送信する。
   *
   * @param to 送信先メールアドレス
   * @param token パスワードリセットトークン
   */
  void sendPasswordResetEmail(String to, String token);

  /**
   * パスワードリセット完了メールを送信する。
   *
   * @param to 送信先メールアドレス
   */
  void sendPasswordResetCompleteEmail(String to);
}
