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

    public void sendPasswordEmail(String email, String newPwd) {
        MimeMessage message = mailSender.createMimeMessage();

        try {
            message.addRecipients(MimeMessage.RecipientType.TO, email);// 보내는 대상
            message.setSubject("In&Out 비밀번호 찾기");// 제목

            log.info(email);

            String body = getFindPasswordBody(newPwd);

            message.setText(body, "utf-8", "html");// 내용, charset 타입, subtype
            // 보내는 사람의 이메일 주소, 보내는 사람 이름
            message.setFrom(new InternetAddress(email, "inandout"));// 보내는 사람
            mailSender.send(message); // 메일 전송
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error(FAILED_SEND_EMAIL.getMessage());
            throw new MemberException(FAILED_SEND_EMAIL);
        }
    }

    private String getFindPasswordBody(String newPwd) {
        return "<div style=\"width: 592px\">\n" +
                "      <img\n" +
                "        src=\"https://inandout-bucket.s3.ap-northeast-2.amazonaws.com/logo.svg\"\n" +
                "      />\n" +
                "      <div style=\"margin: 0 20px\">\n" +
                "        <div style=\"font-size: 40px; font-weight: 700; margin-top: 32px\">\n" +
                "          요청하신\n" +
                "          <br />\n" +
                "          임시 비밀번호가 발급됐어요\n" +
                "        </div>\n" +
                "        <div\n" +
                "          style=\"\n" +
                "            color: #b4b4b4;\n" +
                "            font-size: 22px;\n" +
                "            font-weight: 500;\n" +
                "            margin-top: 16px;\n" +
                "          \"\n" +
                "        >\n" +
                "          개인정보보호를 위해 임시 비밀번호는 꼭 변경해주세요.\n" +
                "        </div>\n" +
                "\n" +
                "        <div\n" +
                "          style=\"\n" +
                "            background-color: #fce9e0;\n" +
                "            display: flex;\n" +
                "            padding: 24px 0px;\n" +
                "            flex-direction: column;\n" +
                "            justify-content: center;\n" +
                "            align-items: center;\n" +
                "            gap: 12px;\n" +
                "            align-self: stretch;\n" +
                "            margin-top: 80px;\n" +
                "          \"\n" +
                "        >\n" +
                "          <div style=\"font-size: 22px; color: #898989; font-weight: 500\">\n" +
                "            임시비밀번호\n" +
                "          </div>\n" +
                "          <div\n" +
                "            style=\"\n" +
                "              color: #000;\n" +
                "              font-size: 56px;\n" +
                "              font-style: normal;\n" +
                "              font-weight: 500;\n" +
                "              line-height: 56px;\n" +
                "            \"\n" +
                "          >\n" +
                "            " + newPwd + "\n" +
                "          </div>\n" +
                "        </div>\n" +
                "\n" +
                "        <a href=\"http://stuffinout.site/login\">\n" +
                "          <button\n" +
                "            style=\"\n" +
                "              margin-top: 80px;\n" +
                "              background-color: black;\n" +
                "              color: white;\n" +
                "              border: none;\n" +
                "              border-radius: 99px;\n" +
                "              width: 100%;\n" +
                "              height: 80px;\n" +
                "              font-size: 22px;\n" +
                "              font-weight: 500;\n" +
                "              cursor: pointer;\n" +
                "            \"\n" +
                "          >\n" +
                "            인앤아웃 로그인하러 가기\n" +
                "          </button>\n" +
                "        </a>\n" +
                "        <div\n" +
                "          style=\"\n" +
                "            color: #b4b4b4;\n" +
                "            font-size: 22px;\n" +
                "            font-weight: 500;\n" +
                "            margin-top: 100px;\n" +
                "          \"\n" +
                "        >\n" +
                "          * 본 메일은 발신전용으로 회신이 불가능합니다.\n" +
                "        </div>\n" +
                "      </div>\n" +
                "    </div>";
    }
}
