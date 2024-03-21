package pojo;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;
import com.google.common.escape.Escaper;
import com.google.gson.internal.bind.util.ISO8601Utils;
import org.apache.commons.codec.binary.Base64;

import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Set;

public class Gmailer
{
	private static final String sender = "giovanni.f.liguori@gmail.com";
	// private final String mailRePair = "mail@repair.com";
	private final Gmail service;
	private static Gmailer instance = null;
	
	public static Gmailer getInstance()
	{
		try
		{
			if (instance == null)
			{
				instance = new Gmailer();
			}
		}catch (Exception e) { e.printStackTrace(); }
		
		return instance;
	}
	
	private Gmailer() throws Exception
	{
		final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
		final GsonFactory jsonFactory = GsonFactory.getDefaultInstance();
		service = new Gmail.Builder(httpTransport, jsonFactory, getCredentials(httpTransport, jsonFactory))
				.setApplicationName("Mailer")
				.build();
	}
	
	private static Credential getCredentials(final NetHttpTransport httpTransport, GsonFactory jsonFactory) throws IOException
	{
		final String secretPath = "/secrets.json";
		try
		{
			final InputStream secretFile = Gmailer.class.getResourceAsStream(secretPath);
			final GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(jsonFactory, new InputStreamReader(secretFile));
			final GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
					httpTransport, jsonFactory, clientSecrets, Set.of(GmailScopes.GMAIL_SEND))
					.setDataStoreFactory(new FileDataStoreFactory(Paths.get("/Users/giovanni/IdeaProjects/Re-pair/tokens").toFile()))
					.setAccessType("offline")
					.build();
			
			final LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
			return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
		}catch (Exception e) { e.printStackTrace(); }
		return null;
	} // fine getCredentials()
	
	public void sendMail(String receiver, String subject, String message) throws Exception
	{
		final InternetAddress SENDER, RECEIVER;
		final String mail_subject, mail_body, oggettoVuoto = "Oggetto vuoto", corpoVuoto = "Il corpo della mail è vuoto.";
		
		SENDER = new InternetAddress(sender);
		RECEIVER = new InternetAddress(receiver);
		
		mail_subject = subject.isBlank() ? oggettoVuoto : subject;
		mail_body = message.isBlank() ? corpoVuoto : message;
		
		// Creazione messaggio MIME
		final Properties props = new Properties();
		final Session session = Session.getDefaultInstance(props, null);
		final MimeMessage email = new MimeMessage(session);
		email.setFrom(SENDER);
		email.addRecipient(javax.mail.Message.RecipientType.TO,RECEIVER);
		email.setSubject(mail_subject);
		email.setText(mail_body);
		
		// Creazione del messaggio Gmail
		final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		email.writeTo(buffer);
		final byte[] rawMessageBytes = buffer.toByteArray();
		final String encodedEmail = Base64.encodeBase64URLSafeString(rawMessageBytes);
		Message msg = new Message();
		msg.setRaw(encodedEmail);
		
		try
		{
			service.users().messages().send("me",msg).execute();
			
			System.out.println(mail_subject);
			System.out.println(mail_body);
		}catch (GoogleJsonResponseException e)
		{
			GoogleJsonError error = e.getDetails();
			if (error.getCode() == 403)
				System.err.println("Unable to send message: " + e.getDetails());
			else
				throw e;
		}
		
	} // fine sendMail()
	
} // fine classe

