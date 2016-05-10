package com.sap.als.controller;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Random;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.mail.internet.MimeMultipart;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.sap.als.persistence.PasswordResetToken;
import com.sap.als.persistence.Patient;
import com.sap.als.persistence.StepTest;
import com.sap.als.security.HashCode;
import com.sap.als.utils.EmfGenerator;
import com.sap.als.utils.ResetToken;

@RestController
@RequestMapping("/forgotPassword")
public class PasswordResetTokenController {

	private DataSource ds;
	private EntityManagerFactory emf;
	private Gson gson;

//	 @Resource(name = "mail/Session")
	// private Session mailSession;
//	//
	private static final long EXPIRATION_TIME = 2 * 60 * 60 * 1000;

	private static final Logger LOG = LoggerFactory
			.getLogger(PasswordResetTokenController.class);

	//
	public PasswordResetTokenController() throws ServletException {
		emf = EmfGenerator.initEntityManagerFactory(ds);
		//EmfGenerator.MailInit(mailSession);
		gson = com.sap.als.utils.GsonBuilder.create();
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public String getTokens(
			@RequestParam(value = "email", required = true) String email,
			HttpServletResponse response) {
		email = email.toLowerCase();
		EntityManager em = emf.createEntityManager();
		List<PasswordResetToken> resultList;

		try {
			Query query = em.createNamedQuery("tokenByMail");
			query.setParameter("email", email);
			resultList = query.getResultList();

			if (resultList.size() > 0) {
				return gson.toJson(resultList);
			}
		} finally {
			em.close();
		}

		return "{\"status\": \"NOT_FOUND\"}";
	}

	@RequestMapping(value = "/sendMail", method = RequestMethod.POST)
	@ResponseBody
	public String sendMail(@RequestBody String body)
			 throws MessagingException, IOException {

	ResetToken  resetParams = gson.fromJson(body, ResetToken.class);
	// First check if Patient with this mail exists
		boolean emailExists = true;
		if (!isEmailExist(resetParams.getEmail().toLowerCase())) {
			emailExists =false;
		}

		// create or update tokens in the DB
		String token = getOrCreateToken(resetParams.getEmail());

		
		// send mail with the token
	//	 try {
			 
			 
			 if(EmfGenerator.MailInit() == null)
			 {
				 return "{\"status\": \"NO MAIL\"}";
			 }else
			 {
				 sendMail(resetParams.getEmail(),token, EmfGenerator.MailInit(),emailExists);
			 }
//		 } catch (MessagingException | IOException e) {
//		 // TODO Auto-generated catch block
//		 return "{\"status\": \"ERROR\"}";
//		
//		 }
		
		return "{\"status\": \"OK\" , \"token\": \" " + token + "\"} ";
	}

	private PasswordResetToken createTokenEntity(String email) {
		EntityManager em = emf.createEntityManager();
		// Create Token for this user
		PasswordResetToken newToken = new PasswordResetToken();
		email = email.toLowerCase();
		updateTokenData(email, newToken);
		em.getTransaction().begin();
		em.persist(newToken);
		em.getTransaction().commit();

		return newToken;
	}

	private void updateTokenData(String email, PasswordResetToken newToken) {
		email = email.toLowerCase();
		newToken.setCreated(new Timestamp(System.currentTimeMillis()));
		newToken.setEmail(email);
		//String token = UUID.randomUUID().toString();
		String token = generateToken(7);
		newToken.setToken(token);
	}
	private  String generateToken(int len){
		String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		Random  rnd = new Random ();
 	    StringBuilder sb = new StringBuilder( len );
		for( int i = 0; i < len; i++ ) 
			sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
		return sb.toString();
		
	}

	@SuppressWarnings("unchecked")
	private String getOrCreateToken(String email) {
		EntityManager em = emf.createEntityManager();
		List<PasswordResetToken> resultList;
		email = email.toLowerCase();
		try {
			Query query = em.createNamedQuery("tokenByMail");
			query.setParameter("email", email);
			resultList = query.getResultList();

			if (resultList.size() >= 1) {

				PasswordResetToken rtoken = resultList.get(0);
				em.getTransaction().begin();
				updateTokenData(email, rtoken);
				em.getTransaction().commit();
				return rtoken.getToken();
			} else {

				return createTokenEntity(email).getToken();
			}

		} finally {
			em.close();
		}

	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/updatePassword", method = RequestMethod.POST)
	@ResponseBody
	public String updatePassword(@RequestBody String body)
			{

		ResetToken  resetParams = gson.fromJson(body, ResetToken.class);
		
		EntityManager em = emf.createEntityManager();
		if (!(resetParams.getPassword().equals(resetParams.getRePassword()))) {
			return "{\"password is not the same\"}";
		}
		try {
			// //get token by mail
			List<PasswordResetToken> tokenResultList;
			String email = resetParams.getEmail().toLowerCase();
			Query tokenQuery = em.createNamedQuery("tokenByMail");
			tokenQuery.setParameter("email", email);
			tokenResultList = tokenQuery.getResultList();
			if (tokenResultList.size() > 0) {
				// check that the token is equal to user

				PasswordResetToken passwordResetTokenStored = tokenResultList.get(0);

				if (resetParams.getToken().equals(passwordResetTokenStored.getToken())) {
					// check if token valid (expiration)
					long current = System.currentTimeMillis();
					long pp = passwordResetTokenStored.getCreated().getTime();
					if (current - pp < EXPIRATION_TIME) {
						// update password in Patient
						updatePassword(resetParams.getEmail().toLowerCase(), resetParams.getPassword(), em);
						// Delete token
						deleteToken(passwordResetTokenStored, em);
						return "{\"status\": \"UPDATED\" }";
					} else {
						return "{\"status\": \"EXPIRED\" }";
					}

				}
			}
			return "{\"status\": \"No such token for this mail \" }";
		} finally {
			em.close();
		}
	}

	private void deleteToken(PasswordResetToken passwordResetToken,
			EntityManager em) {

		em.getTransaction().begin();
		em.remove(passwordResetToken);
		em.getTransaction().commit();
	}

	@SuppressWarnings("unchecked")
	private String updatePassword(String email, String password,
			EntityManager em) {
		email = email.toLowerCase();
		List<Patient> resultList;
		Query query = em.createNamedQuery("PatientByEmail");
		query.setParameter("email", email);
		resultList = query.getResultList();
		if (resultList.size() > 0) {
			Patient patient = resultList.get(0);
			em.getTransaction().begin();
			patient.setPassword(HashCode.getHashPassword(password));
			em.getTransaction().commit();
			return "{\"password changed\"}";
		}
		return "error";
	}

	@SuppressWarnings("unchecked")
	private boolean isEmailExist(String email) {
		EntityManager em = emf.createEntityManager();
		List<Patient> resultList;
		email = email.toLowerCase();
		try {
			if (email != null) {
				Query query = em.createNamedQuery("PatientByEmail");
				query.setParameter("email", email);
				resultList = query.getResultList();

				if (resultList.size() > 0) {
					return true;
				} else {
					return false;
				}
			}
		} finally {
			em.close();
		}
		return false;
	}

	private String sendMail(String email, String token, Session mailSession,boolean emailExists)
			throws MessagingException, IOException {

		MimeMessage mimeMessage = new MimeMessage(mailSession);
		String from = "DONotReply@ALS";
		InternetAddress[] fromAddress = InternetAddress.parse(from);
		String to = email;
		InternetAddress[] toAddresses = InternetAddress.parse(to);
		mimeMessage.setFrom(fromAddress[0]);
		mimeMessage.setRecipients(RecipientType.TO, toAddresses);
		String subjectText = "ALS Mobile Analyzer – Resetting your password";
		mimeMessage.setSubject(subjectText, "UTF-8");
		MimeMultipart multiPart = new MimeMultipart("alternative");
		MimeBodyPart part = new MimeBodyPart();
		String mailText = "";
		if(emailExists){
			mailText = "Hello!" + "\n\n" +
						"Please use this code to reset your password in the ALS Mobile Analyzer app.\n" + 
						"Code: " + token + "\n\n" + 
						"It's only valid for 2 hours. If you haven’t used it within this time period, no problem, you can generate a " +
						"new code by tapping 'Forgot your password?' again inside the app.\n\n" + 
						"If you aren't having problems with your password, simply ignore this mail.\n\n" +  
						"Thank you.\nYour ALS Mobile Analyzer Team\n\n" +
						"**** Do not respond to this mail **** " ;
		}else {
			mailText = "Dear user,  " + "\n\n" +
					"You used this e-mail address to request a password reset for the ALS Mobile" + 
					"Analyzer app; however, this email account is not registered as an active " + 
					"account in our database.\n\n" +
					"If you did not request to reset your password, you can ignore this mail.\n\n"   
					+"Thank you.\n\n" +
					"**** Do not respond to this mail **** " ;
			
		}

		
		//String mailText = "this is your token " + token;
		part.setText(mailText, "utf-8", "plain");
		multiPart.addBodyPart(part);
		mimeMessage.setContent(multiPart);

		// Send mail
		Transport transport = mailSession.getTransport();
		transport.connect();
		transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());

		return "OK";
		// // Confirm mail sending
		// response.getWriter()
		// .println(
		// "E-mail was sent (in local scenario stored in '<local-server>/work/mailservice'"
		// + " - in cloud scenario using configured mail session).");
	}
}
