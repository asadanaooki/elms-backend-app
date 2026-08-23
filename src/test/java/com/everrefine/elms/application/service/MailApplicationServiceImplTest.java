package com.everrefine.elms.application.service;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class MailApplicationServiceImplTest {

  private static final String FROM_ADDRESS = "from@example.com";
  private static final String TO_ADDRESS = "user@example.com";
  private static final String PASSWORD_RESET_BASE_URL = "http://localhost:3000";

  @Mock private JavaMailSender mailSender;

  private MailApplicationServiceImpl mailApplicationService;

  @BeforeEach
  void setUp() {
    mailApplicationService = new MailApplicationServiceImpl(mailSender);
    ReflectionTestUtils.setField(mailApplicationService, "fromAddress", FROM_ADDRESS);
    ReflectionTestUtils.setField(
        mailApplicationService, "passwordResetBaseUrl", PASSWORD_RESET_BASE_URL);
  }

  @Test
  void sendPasswordResetEmailでパスワード再設定案内メールを送信すること() {
    String token = "reset-token";

    mailApplicationService.sendPasswordResetEmail(TO_ADDRESS, token);

    SimpleMailMessage message = captureSentMessage();
    assertMessageHeader(message, "【Javaエンジニア養成講座】パスワード再設定のご案内");
    assertEquals(
        """
        Javaエンジニア養成講座をご利用いただきありがとうございます。

        パスワード再設定のリクエストを受け付けました。
        以下のリンクをクリックして、新しいパスワードを設定してください。

        http://localhost:3000/reset-password?token=reset-token

        ※ このリンクは発行から30分間有効です。
        ※ ご自身でリクエストしていない場合は、このメールを無視してください。

        ──────────────────────────────
        Javaエンジニア養成講座
        """,
        message.getText());
  }

  @Test
  void sendPasswordResetCompleteEmailでパスワード再設定完了メールを送信すること() {
    mailApplicationService.sendPasswordResetCompleteEmail(TO_ADDRESS);

    SimpleMailMessage message = captureSentMessage();
    assertMessageHeader(message, "【Javaエンジニア養成講座】パスワード再設定が完了しました");
    assertEquals(
        """
        Javaエンジニア養成講座をご利用いただきありがとうございます。

        以下のアカウントのパスワード再設定が完了しました。

        メールアドレス：user@example.com

        ※ ご自身で操作していない場合は、お問い合わせください。

        ──────────────────────────────
        Javaエンジニア養成講座
        """,
        message.getText());
  }

  private SimpleMailMessage captureSentMessage() {
    ArgumentCaptor<SimpleMailMessage> messageCaptor =
        ArgumentCaptor.forClass(SimpleMailMessage.class);
    verify(mailSender).send(messageCaptor.capture());
    return messageCaptor.getValue();
  }

  private void assertMessageHeader(SimpleMailMessage message, String subject) {
    assertEquals(FROM_ADDRESS, message.getFrom());
    assertArrayEquals(new String[] {TO_ADDRESS}, message.getTo());
    assertEquals(subject, message.getSubject());
  }
}
