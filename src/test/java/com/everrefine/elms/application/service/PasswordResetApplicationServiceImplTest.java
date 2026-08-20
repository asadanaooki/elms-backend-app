package com.everrefine.elms.application.service;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

import com.everrefine.elms.application.command.PasswordResetConfirmCommand;
import com.everrefine.elms.application.command.PasswordResetRequestCommand;
import com.everrefine.elms.domain.model.passwordreset.PasswordResetToken;
import com.everrefine.elms.domain.model.user.User;
import com.everrefine.elms.domain.model.user.UserRole;
import com.everrefine.elms.domain.repository.PasswordResetTokenRepository;
import com.everrefine.elms.domain.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(PasswordResetApplicationServiceImplTest.TestConfiguration.class)
@TestPropertySource(
    properties = {"mail.from=from@example.com", "password-reset.base-url=http://localhost:3000"})
class PasswordResetApplicationServiceImplTest {

  private static final String EMAIL_ADDRESS = "user@example.com";
  private static final String FROM_ADDRESS = "from@example.com";
  private static final String BASE_URL = "http://localhost:3000";
  private static final UUID USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

  @MockitoBean private UserRepository userRepository;
  @MockitoBean private PasswordResetTokenRepository passwordResetTokenRepository;
  @MockitoBean private JavaMailSender mailSender;
  @Autowired private PasswordResetApplicationService passwordResetApplicationService;

  @Test
  void requestPasswordResetでトークン保存後に再設定案内メールを送信すること() {
    User user = createUser();
    when(userRepository.findUserByEmailAddress(user.emailAddress())).thenReturn(Optional.of(user));

    passwordResetApplicationService.requestPasswordReset(
        new PasswordResetRequestCommand(EMAIL_ADDRESS));

    ArgumentCaptor<PasswordResetToken> tokenCaptor =
        ArgumentCaptor.forClass(PasswordResetToken.class);
    ArgumentCaptor<SimpleMailMessage> messageCaptor =
        ArgumentCaptor.forClass(SimpleMailMessage.class);
    InOrder inOrder = inOrder(passwordResetTokenRepository, mailSender);
    inOrder.verify(passwordResetTokenRepository).save(tokenCaptor.capture());
    inOrder.verify(mailSender).send(messageCaptor.capture());

    SimpleMailMessage message = messageCaptor.getValue();
    assertMessageHeader(message, "【Javaエンジニア養成講座】パスワード再設定のご案内");
    assertEquals(
        """
        Javaエンジニア養成講座をご利用いただきありがとうございます。

        パスワード再設定のリクエストを受け付けました。
        以下のリンクをクリックして、新しいパスワードを設定してください。

        http://localhost:3000/reset-password?token=%s

        ※ このリンクは発行から30分間有効です。
        ※ ご自身でリクエストしていない場合は、このメールを無視してください。

        ──────────────────────────────
        Javaエンジニア養成講座
        """
            .formatted(tokenCaptor.getValue().token()),
        message.getText());
  }

  @Test
  void confirmPasswordResetで更新保存後に再設定完了メールを送信すること() {
    String token = "reset-token";
    PasswordResetToken resetToken =
        new PasswordResetToken(
            UUID.fromString("00000000-0000-0000-0000-000000000002"),
            USER_ID,
            token,
            LocalDateTime.now().plusMinutes(30),
            null);
    User user = createUser();
    when(passwordResetTokenRepository.findByToken(token)).thenReturn(Optional.of(resetToken));
    when(userRepository.findUserById(USER_ID)).thenReturn(Optional.of(user));
    when(userRepository.updateUser(any(User.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    String result =
        passwordResetApplicationService.confirmPasswordReset(
            new PasswordResetConfirmCommand(token, "new-password"));

    ArgumentCaptor<SimpleMailMessage> messageCaptor =
        ArgumentCaptor.forClass(SimpleMailMessage.class);
    InOrder inOrder = inOrder(userRepository, passwordResetTokenRepository, mailSender);
    inOrder.verify(userRepository).updateUser(any(User.class));
    inOrder.verify(passwordResetTokenRepository).save(any(PasswordResetToken.class));
    inOrder.verify(mailSender).send(messageCaptor.capture());

    SimpleMailMessage message = messageCaptor.getValue();
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
    assertEquals(EMAIL_ADDRESS, result);
  }

  private void assertMessageHeader(SimpleMailMessage message, String subject) {
    assertEquals(FROM_ADDRESS, message.getFrom());
    assertArrayEquals(new String[] {EMAIL_ADDRESS}, message.getTo());
    assertEquals(subject, message.getSubject());
  }

  private User createUser() {
    User user =
        User.create(EMAIL_ADDRESS, "password", "テスト ユーザー", "test-user", null, UserRole.GENERAL);
    return new User(
        USER_ID,
        user.emailAddress(),
        user.password(),
        user.realName(),
        user.userName(),
        user.thumbnailUrl(),
        user.userRole(),
        user.createdAt(),
        user.updatedAt());
  }

  @Configuration(proxyBeanMethods = false)
  @ComponentScan(
      basePackageClasses = PasswordResetApplicationServiceImpl.class,
      useDefaultFilters = false,
      includeFilters =
          @ComponentScan.Filter(
              type = FilterType.REGEX,
              pattern =
                  "com\\.everrefine\\.elms\\.application\\.service\\.(PasswordReset|Mail)ApplicationServiceImpl"))
  static class TestConfiguration {}
}
