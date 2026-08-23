package com.everrefine.elms.application.mail;

import lombok.Getter;

/** メールテンプレートを表す列挙型。 */
@Getter
public enum MailTemplate {
  PASSWORD_RESET(
      "【Javaエンジニア養成講座】パスワード再設定のご案内",
      """
            Javaエンジニア養成講座をご利用いただきありがとうございます。

            パスワード再設定のリクエストを受け付けました。
            以下のリンクをクリックして、新しいパスワードを設定してください。

            %s

            ※ このリンクは発行から30分間有効です。
            ※ ご自身でリクエストしていない場合は、このメールを無視してください。

            ──────────────────────────────
            Javaエンジニア養成講座
            """),
  PASSWORD_RESET_COMPLETE(
      "【Javaエンジニア養成講座】パスワード再設定が完了しました",
      """
            Javaエンジニア養成講座をご利用いただきありがとうございます。

            以下のアカウントのパスワード再設定が完了しました。

            メールアドレス：%s

            ※ ご自身で操作していない場合は、お問い合わせください。

            ──────────────────────────────
            Javaエンジニア養成講座
            """);

  private final String subject;
  private final String text;

  MailTemplate(String subject, String text) {
    this.subject = subject;
    this.text = text;
  }
}
