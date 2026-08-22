package com.everrefine.elms.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/** メールアプリケーションサービスの実装。 */
@Service
@RequiredArgsConstructor
public class MailApplicationServiceImpl implements MailApplicationService {

  private final JavaMailSender mailSender;

  @Value("${mail.from}")
  private String fromAddress;

  @Value("${password-reset.base-url}")
  private String passwordResetBaseUrl;

  @Override
  public void sendPasswordResetCompleteEmail(String to) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom(fromAddress);
    message.setTo(to);
    message.setSubject("【Javaエンジニア養成講座】パスワード再設定が完了しました");
    message.setText(
        """
        Javaエンジニア養成講座をご利用いただきありがとうございます。

        以下のアカウントのパスワード再設定が完了しました。

        メールアドレス：%s

        ※ ご自身で操作していない場合は、お問い合わせください。

        ──────────────────────────────
        Javaエンジニア養成講座
        """
            .formatted(to));
    mailSender.send(message);
  }

  @Override
  public void sendPasswordResetEmail(String to, String token) {
    String resetLink = passwordResetBaseUrl + "/reset-password?token=" + token;
    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom(fromAddress);
    message.setTo(to);
    message.setSubject("【Javaエンジニア養成講座】パスワード再設定のご案内");
    message.setText(
        """
        Javaエンジニア養成講座をご利用いただきありがとうございます。

        パスワード再設定のリクエストを受け付けました。
        以下のリンクをクリックして、新しいパスワードを設定してください。

        %s

        ※ このリンクは発行から30分間有効です。
        ※ ご自身でリクエストしていない場合は、このメールを無視してください。

        ──────────────────────────────
        Javaエンジニア養成講座
        """
            .formatted(resetLink));
    mailSender.send(message);
  }
}
