package com.habra.example.com;

import android.util.Log;

import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

//В данном классе записаны настройки того сервера, через который будет передаваться ваше сообщение. Здесь у нас есть несколько методов:
//public MailSenderClass(String user, String password) — Конструктор. В качестве аргументов передаются логин и пароль от нашего промежуточного
// ящика на gmail.com. Здесь же прописываются параметры smtp-подключения к серверу.
//protected PasswordAuthentication getPasswordAuthentication() — Аутентификация для сервера.
//public synchronized void sendMail(String subject, String body, String sender, String recipients, String filename) —
// Основной метод, в который передаются наши данные для отправки.


public class MailSenderClass extends javax.mail.Authenticator {
	private String mailhost = "smtp.gmail.com";
	private String user;
	private String password;
	private Session session;

	private Multipart _multipart;

	//static {
	//	Security.addProvider(new com.habra.example.com.JSSEProvider());
	//}

	public MailSenderClass(String user, String password) {
		this.user = user;
		this.password = password;
		
		_multipart = new MimeMultipart();

		Properties props = new Properties();

        //////////////////////////////////////////////////
       // props.put("mail.smtp.starttls.enable","true");
        props.setProperty("mail.smtp.ssl.enable", "true");

       // props.put("mail.smtp.auth", "true");

        //props.put("mail.smtp.host", "smtp.gmail.com");

        //props.put("mail.smtp.port", "587");

        props.put("mail.smtp.starttls.enable", "true");
        //////////////////////////////////////////////////

        props.setProperty("mail.transport.protocol", "smtp");
		props.setProperty("mail.host", mailhost);
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465"); //465
		props.put("mail.smtp.socketFactory.port", "465"); //465
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.socketFactory.fallback", "false");
		props.setProperty("mail.smtp.quitwait", "false");

        props.put("mail.smtp.host", "smtp.gmail.com");

        //Properties props = new Properties();
        //props.put("mail.smtp.starttls.enable","true");
        //props.put("mail.smtp.auth", "true"); // If you need to authenticate //
//Use the following if you need SSL
        //props.put("mail.smtp.socketFactory.port", d_port);
        //props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        //props.put("mail.smtp.socketFactory.fallback", "false");
/*
     //////////////////////////////////////////////////
       // props.put("mail.smtp.starttls.enable","true");
      //  props.setProperty("mail.smtp.ssl.enable", "true");

        //props.put("mail.smtp.host", "smtp.gmail.com");
        //////////////////////////////////////////////////

        props.setProperty("mail.transport.protocol", "smtp");
		props.setProperty("mail.host", mailhost);
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465"); //465
		props.put("mail.smtp.socketFactory.port", "465"); //465
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.socketFactory.fallback", "false");
		props.setProperty("mail.smtp.quitwait", "false");




        //Properties props = new Properties();
        //props.put("mail.smtp.starttls.enable","true");
        //props.put("mail.smtp.auth", "true"); // If you need to authenticate //
//Use the following if you need SSL
        //props.put("mail.smtp.socketFactory.port", d_port);
        //props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        //props.put("mail.smtp.socketFactory.fallback", "false");

 */

		session = Session.getDefaultInstance(props, this);
	}
	
	protected PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(user, password);
	}

	public synchronized void sendMail(String subject, String body, String sender, String recipients, String filename) throws Exception {
		try {
			MimeMessage message = new MimeMessage(session);

			//кто
			message.setSender(new InternetAddress(sender));
			//о чем
			message.setSubject(subject);
			//кому
			if (recipients.indexOf(',') > 0)
				message.setRecipients(Message.RecipientType.TO,
						InternetAddress.parse(recipients));
			else
				message.setRecipient(Message.RecipientType.TO,
						new InternetAddress(recipients));
			
			//хочет сказать
			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setText(body);
			_multipart.addBodyPart(messageBodyPart);
			
			//и что показать
			if (!filename.equalsIgnoreCase("")) {
				BodyPart attachBodyPart = new MimeBodyPart();
				DataSource source = new FileDataSource(filename);
				attachBodyPart.setDataHandler(new DataHandler(source));
				attachBodyPart.setFileName(filename);
	
				_multipart.addBodyPart(attachBodyPart);
			}
			
			message.setContent(_multipart);
			
			Transport.send(message);



            /*MimeMessage message = new MimeMessage(session);

            message.saveChanges();
            Transport transport = session.getTransport("smtp");
            transport.connect(host, "user", "pwd");
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();*/





		} catch (Exception e) {
			Log.e("sendMail","ошибка отправки функцией sendMail! ");
		}
	}
}


