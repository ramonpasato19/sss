package com.powerfin.service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.persistence.Query;

import org.openxava.jpa.XPersistence;
import org.openxava.util.XSystem;

public class EmailSenderService {
	private final Properties properties = new Properties();
	private Session session;

	private List<String> toRecipients;
	private List<String> ccRecipients;
	private List<Attachment> attachments;
	private String subject;
	private String content;

	public EmailSenderService()
	{
		
	}
	public EmailSenderService(List<String> toRecipients, List<String> ccRecipients, String subject, String content,
			Attachment... attachments) {
		this.attachments = new ArrayList<Attachment>();
		this.toRecipients = toRecipients;
		this.ccRecipients = ccRecipients;
		this.subject = subject;
		this.content = content;
		for (Attachment attachment : attachments) {
			this.attachments.add(attachment);
		}
	}

	@SuppressWarnings("unchecked")
	public void init() {

		List<String> parameters = new ArrayList<String>();
		parameters.add("mail.smtp.host");
		parameters.add("mail.smtp.starttls.enable");
		parameters.add("mail.smtp.port");
		parameters.add("mail.smtp.mail.sender");
		parameters.add("mail.smtp.password");
		parameters.add("mail.smtp.auth");
		parameters.add("mail.smtp.socketFactory.port");
		parameters.add("mail.smtp.socketFactory.class");
		parameters.add("mail.smtp.socketFactory.fallback");
		parameters.add("mail.smtp.user");

		String sql = "select parameter_id, value from  " + XPersistence.getDefaultSchema().toLowerCase()
				+ ".parameter where parameter_id IN (:PKEYSID)";
		List<Object> result = new ArrayList<Object>();

		Query query = XPersistence.getManager().createNativeQuery(sql);
		query.setParameter("PKEYSID", parameters);

		try {
			result = query.getResultList();
			if (result != null && !result.isEmpty()) {
				for (Object data : result) {
					Object[] row = (Object[]) data;
					if ("mail.smtp.socketFactory.port".equals((String) row[0])) {
						properties.put((String) row[0], Integer.parseInt((String) row[1]));
					} else {
						properties.put((String) row[0], (String) row[1]);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			session = Session.getInstance(properties, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication((String) properties.get("mail.smtp.mail.sender"),
							(String) properties.get("mail.smtp.password"));
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void sendEmail() {
		try {

			MimeBodyPart mimeBodyPart = new MimeBodyPart();
			mimeBodyPart.setContent(content, "text/html; charset=" + XSystem.getEncoding());
			
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress((String) properties.get("mail.smtp.mail.sender"), (String) properties.get("mail.smtp.user")));
			message.setSubject(subject);

			for (String toRecipient : toRecipients) {
				if (toRecipient != null && !toRecipient.isEmpty()) {
					message.addRecipient(Message.RecipientType.TO, new InternetAddress(toRecipient));
				}
			}
			for (String ccRecipient : ccRecipients) {
				if (ccRecipient != null && !ccRecipient.isEmpty()) {
					message.addRecipient(Message.RecipientType.CC, new InternetAddress(ccRecipient));
				}
			}

			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(mimeBodyPart);

			for (Attachment attachment : attachments) {
				MimeBodyPart mimeBodyPartAttachment = new MimeBodyPart();
				ByteArrayDataSource bds = new ByteArrayDataSource(attachment.data, attachment.mimeType);
				mimeBodyPartAttachment.setDataHandler(new DataHandler(bds));
				mimeBodyPartAttachment.setFileName(attachment.name+"."+attachment.getExtension());

				multipart.addBodyPart(mimeBodyPartAttachment);
			}

			message.setContent(multipart, "text/html; charset=" + XSystem.getEncoding());

			Transport.send(message);
		} catch (MessagingException e) {
			System.out.println("Hubo un error al enviar el mensaje, "+e.getMessage());
			throw new RuntimeException(e);
		} catch (UnsupportedEncodingException e) {
			System.out.println("Hubo un error al enviar el mensaje, "+e.getMessage());
			throw new RuntimeException(e);
		}
	}
	
	public static class Attachment {
		private String name;
		private byte[] data;
		private String mimeType;
		private String extension;
		
		public Attachment(String name, byte[] data, String mimeType, String extension){
			this.name = name;
			this.data = data;
			this.mimeType = mimeType;
			this.extension = extension;
		}

		public String getExtension() {
			return extension;
		}

		public void setExtension(String extension) {
			this.extension = extension;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public byte[] getData() {
			return data;
		}

		public void setData(byte[] data) {
			this.data = data;
		}

		public String getMimeType() {
			return mimeType;
		}

		public void setMimeType(String mimeType) {
			this.mimeType = mimeType;
		}

	}

}
