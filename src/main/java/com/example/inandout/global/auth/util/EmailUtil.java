package com.example.inandout.global.auth.util;

import com.example.inandout.api.domain.member.entity.Member;
import com.example.inandout.global.common.error.exception.MemberException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

import static com.example.inandout.global.common.response.BaseResponseStatus.FAILED_SEND_EMAIL;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailUtil {
    @Value("${mail.username}")
    private String email;
    @Value("${spring.mail.request-uri}")
    private String requestUri;

    private final JavaMailSender mailSender;

    public void sendEmail(Member member) {
        log.info("이메일 인증");
        String receiverMail = member.getEmail();
        MimeMessage message = mailSender.createMimeMessage();

        try {
            message.addRecipients(MimeMessage.RecipientType.TO, receiverMail);// 보내는 대상
            message.setSubject("In&Out 회원가입 이메일 인증");// 제목
            message.setText(getEmailCertificationBody(member), "utf-8", "html"); // 내용, charset 타입, subtype
            message.setFrom(new InternetAddress(email, "inandout")); // 보내는 사람의 이메일 주소, 보내는 사람 이름
            mailSender.send(message); // 메일 전송
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error(FAILED_SEND_EMAIL.getMessage());
            throw new MemberException(FAILED_SEND_EMAIL);
        }
    }

    private String getEmailCertificationBody(Member member) {
        return "<body>" +
                "    <div style=\"width: 592px\">" +
                "      <img" +
                "        src=\"https://inandout-bucket.s3.ap-northeast-2.amazonaws.com/logo.svg\"" +
                "      />\n" +
                "      <div style=\"margin: 0 20px\">" +
                "        <div style=\"font-size: 40px; font-weight: 700; margin-top: 32px\">" +
                "          회원가입을 위한<br />" +
                "          가장 마지막 절차예요!" +
                "        </div>" +
                "        <div" +
                "          style=\"" +
                "            color: #b4b4b4;" +
                "            font-size: 22px;" +
                "            font-weight: 500;" +
                "            margin-top: 16px;" +
                "          \"" +
                "        >" +
                "          메일 확인과 개인정보보호를 위해 인증절차를 진행하고 있어요.<br />" +
                "          인증 버튼을 눌러 회원가입을 완료해주세요." +
                "        </div>" +
                "          <a href='\"" + requestUri + member.getAuthToken() + "\"'>" +
                "          <button" +
                "            style=\"" +
                "              margin-top: 80px;" +
                "              background-color: black;" +
                "              color: white;" +
                "              border: none;" +
                "              border-radius: 99px;" +
                "              width: 100%;" +
                "              height: 80px;" +
                "              font-size: 22px;" +
                "              cursor: pointer;" +
                "            \"" +
                "          >" +
                "            메일 인증하기" +
                "          </button>" +
                "        </a>" +
                "        <div" +
                "          style=\"" +
                "            color: #b4b4b4;" +
                "            font-size: 22px;" +
                "            font-weight: 500;" +
                "            margin-top: 120px;" +
                "          \"" +
                "        >" +
                "          * 본 메일은 발신전용으로 회신이 불가능합니다." +
                "        </div>" +
                "      </div>" +
                "    </div>" +
                "  </body>";
    }
}
