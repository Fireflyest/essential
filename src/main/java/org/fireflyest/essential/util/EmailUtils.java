package org.fireflyest.essential.util;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.fireflyest.essential.Essential;


public class EmailUtils {

    private static MimeMessage mimeMessage;
    private static Transport transport;
    private static String user;
    private static String password;
    private static String host;

    private static final Properties props = new Properties();

    static {
        props.put("mail.smtp.host", "smtp.qq.com");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.port", "587");
        props.put("mail.smtp.port", "587");
        props.put("mail.user", "fireflyest@qq.com");
        props.put("mail.password", "zhxywaxfoobxbchd");
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.debug", "false");
    }
    

    private EmailUtils() {
    }

    /**
     * 初始化
     * @throws MessagingException 信息错误
     */
    public static void init() throws MessagingException {
        user = props.getProperty("mail.user");
        password = props.getProperty("mail.password");
        host = props.getProperty("mail.smtp.host");

        String protocol = props.getProperty("mail.transport.protocol");

        // 获取会话
        Session mailSession = Session.getInstance(props);

        mimeMessage = new MimeMessage(mailSession);
        mimeMessage.setFrom(new InternetAddress(user));

        transport = mailSession.getTransport(protocol);
    }

    /**
     * 发送邮件
     * @param emailTo 收件人
     * @param subject 主题
     * @param content 内容
     */
    public static void sendEmail(String emailTo, String subject, String content) {
        if (mimeMessage == null || transport == null) {
            Essential.getPlugin().getLogger().severe("email don't work");
            return;
        }
        // 发送
        try {
            mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(emailTo));// 设置收件人地址并规定其类型
            mimeMessage.setSentDate(new Date()); // 设置发信时间
            mimeMessage.setSubject(subject); // 设置主题
            mimeMessage.setContent(content, "text/html;charset=UTF-8"); // 设置 正文
            mimeMessage.saveChanges();
            transport.connect(host, user, password);
            transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                transport.close();
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
    }

}
