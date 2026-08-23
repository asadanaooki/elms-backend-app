package com.everrefine.elms.application.service;

import com.everrefine.elms.application.mail.MailTemplate;
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
    sendMail(to, MailTemplate.PASSWORD_RESET_COMPLETE, to);
  }

  @Override
  public void sendPasswordResetEmail(String to, String token) {
    String resetLink = passwordResetBaseUrl + "/reset-password?token=" + token;
    sendMail(to, MailTemplate.PASSWORD_RESET, resetLink);
  }

  private void sendMail(String to, MailTemplate template, Object... args) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom(fromAddress);
    message.setTo(to);
    message.setSubject(template.getSubject());
    message.setText(template.getText().formatted(args));
    mailSender.send(message);
  }
}
