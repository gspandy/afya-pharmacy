package com.zpc.timesheet.view;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.Properties;

/**
 * Created by Administrator on 1/13/2015.
 */
public class EmailEmployeeDetailsCSV {
    public static String sendEmail(String emailId, String recipientName, String cc, String bcc, String pathOfCSV){
        String response;
        try {
            File file = new File(pathOfCSV);
            Properties properties = new Properties();
            properties.load(EmailEmployeeDetailsCSV.class.getClassLoader().getResourceAsStream("general.properties"));
            Session session = authenticateAndGetSession(properties);
            String bodyOfEmail = setBodyForCSVAttachedMail(recipientName);
            String subjectOfEmail = setSubjectForCSVAttachedMail();
            response = sendMail(session, properties, file, emailId, recipientName, cc,bcc,bodyOfEmail, subjectOfEmail);
        } catch (IOException e) {
            response = convertStackTraceToString(e);
            e.printStackTrace();
        }
        return response;
    }

    public static String emailConsolidatedSheet(String emailId, String recipientName, String cc, String bcc, String pathOfCSV){
        String response;
        try {
            File file = new File(pathOfCSV);
            Properties properties = new Properties();
            properties.load(EmailEmployeeDetailsCSV.class.getClassLoader().getResourceAsStream("general.properties"));
            Session session = authenticateAndGetSession(properties);
            String bodyOfEmail = setBodyForConsolidatedAttendanceRegisterAttachedMail(recipientName);
            String subjectOfEmail = setSubjectForConsolidatedAttendanceRegisterAttachedMail();
            response = sendMail(session, properties, file, emailId, recipientName, cc,bcc,bodyOfEmail, subjectOfEmail);
        } catch (IOException e) {
            response = convertStackTraceToString(e);
            e.printStackTrace();
        }
        return response;
    }

    public static Session authenticateAndGetSession(final Properties prop){
        try {
            final Properties properties = new Properties();
            properties.put("mail.smtp.host", prop.getProperty("mail.smtp.relay.host").trim());
            properties.put("mail.smtp.port", prop.getProperty("mail.smtp.port").trim());
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.smtp.ssl.trust", prop.getProperty("mail.smtp.relay.host"));
            Authenticator auth = new Authenticator() {
                public PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(prop.getProperty("mail.smtp.auth.user").trim(), prop.getProperty("mail.smtp.auth.password").trim());
                }
            };
            Session session = Session.getInstance(properties, auth);
            return session;
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static String sendMail(Session session, Properties properties, File file, String emailId, String recipientName, String cc, String bcc, String bodyOfEmail, String subjectOfEmail){
        String stacktrace;
        try {
            Message msg=new MimeMessage(session);
            msg.setFrom(new InternetAddress(properties.getProperty("mail.smtp.auth.user")));
            InternetAddress[] toAddresses = { new InternetAddress(emailId) };
            msg.setRecipients(Message.RecipientType.TO, toAddresses);
            if(bcc != null) {
                InternetAddress[] bccs = {new InternetAddress(bcc)};
                msg.setRecipients(Message.RecipientType.BCC, bccs);
            }
            if(cc != null) {
                InternetAddress[] ccs = {new InternetAddress(cc)};
                msg.setRecipients(Message.RecipientType.CC, ccs);
            }
            msg.setSubject(subjectOfEmail);
            msg.setSentDate(new Date());
            MimeBodyPart messageBodyPartForAttachment = new MimeBodyPart();
            MimeBodyPart messageBodyPartForBody= new MimeBodyPart();
            messageBodyPartForBody.setContent(bodyOfEmail, "text/html");
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPartForBody);
            multipart.addBodyPart(messageBodyPartForAttachment);
            DataSource source = new FileDataSource(file);
            messageBodyPartForAttachment.setDataHandler(new DataHandler(source));
            messageBodyPartForAttachment.setFileName(file.getName());
            msg.setContent(multipart);
            Transport.send(msg);
            stacktrace = "email sent";
        } catch (AddressException e) {
            stacktrace = convertStackTraceToString(e);
            e.printStackTrace();
        } catch (MessagingException e) {
            stacktrace = convertStackTraceToString(e);
            e.printStackTrace();
        }
        return stacktrace;
    }

    public static String setBodyForConsolidatedAttendanceRegisterAttachedMail(String recipientName) {
        if(recipientName==null)
            recipientName = "";
        StringBuffer body
                = new StringBuffer("<html><body style =\"font-size: 12\">");
        body.append("<p style=\"font-size:15px;color:black;font-family:cursive;\">Hello "+recipientName+",</p>");
        body.append("<p style=\"font-size:15px;color:black;font-family:cursive;\"><br>Please find the attached Attendance Register file in the email.</p><br>");
        body.append("<p style=\"font-size:15px;color:black;font-family:cursive;\">Regards,<br></p><p style=\"font-size:15px;color:black;font-family:cursive;\">Human Resource Team</p>");
        body.append("<p style=\"font-size:15px;color:black;font-family:cursive;\">Zambia Portland cement Ltd.\n</p>");
        return body.toString();
    }

    public static String setSubjectForConsolidatedAttendanceRegisterAttachedMail(){
        return "Consolidated Attendance Register attached";
    }

    public static String setBodyForCSVAttachedMail(String recipientName) {
        if(recipientName==null)
            recipientName = "";
        StringBuffer body
                = new StringBuffer("<html><body style =\"font-size: 12\">");
        body.append("<p style=\"font-size:15px;color:black;font-family:cursive;\">Hello "+recipientName+",</p>");
        body.append("<p style=\"font-size:15px;color:black;font-family:cursive;\"><br>Please find the attached Dove Payroll export file which contains.</p>");
        body.append("<p style=\"font-size:15px;color:black;font-family:cursive;margin-left:30px;\">1. Leave</p>");
        body.append("<p style=\"font-size:15px;color:black;font-family:cursive;margin-left:30px;\">2. Absentism</p>");
        body.append("<p style=\"font-size:15px;color:black;font-family:cursive;margin-left:30px;\">3. Time Card</p><br>");
        body.append("<p style=\"font-size:15px;color:black;font-family:cursive;\">Regards,<br></p><p style=\"font-size:15px;color:black;font-family:cursive;\">Human Resource Team</p>");
        body.append("<p style=\"font-size:15px;color:black;font-family:cursive;\">Zambia Portland cement Ltd.\n</p>");
        return body.toString();
    }

    public static String setSubjectForCSVAttachedMail(){
        return "Dove Payroll Export Files";
    }

    public static String convertStackTraceToString(Throwable e){
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}
