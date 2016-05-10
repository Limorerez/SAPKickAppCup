package com.sap.als.controller;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.google.gson.Gson;
import com.sap.als.persistence.Patient;
import com.sap.als.utils.EmfGenerator;

@RestController
@RequestMapping("/auth")
@SuppressWarnings("unchecked")
public class AuthenticationController {

	private DataSource ds;
	private EntityManagerFactory emf;
	private Gson gson;

	public AuthenticationController() throws ServletException {
		emf = EmfGenerator.initEntityManagerFactory(ds);
		gson = com.sap.als.utils.GsonBuilder.create();
	}

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public String login(
			@RequestParam(value = "email", required = true) String email) {
		EntityManager em = emf.createEntityManager();

		try {
			Query query = em.createNamedQuery("PatientByEmail");
			query.setParameter("email", email.toLowerCase());
			List<Patient> resultList = query.getResultList();

			return gson.toJson(resultList);
		} catch (Exception e) {
			return e.toString();
		} finally {
			em.close();
		}
	}

	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public void logout(HttpServletRequest request, HttpServletResponse response) {
		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes();
		HttpSession session = attr.getRequest().getSession(false);
		if (session != null && session.getAttribute("isAuthenticated") != null) {
			session.setAttribute("isAuthenticated", null);
			session.setAttribute("auth", null);
			session.invalidate();
		}
		response.setStatus(HttpServletResponse.SC_OK);
	}

}
