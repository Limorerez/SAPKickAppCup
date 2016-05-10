package com.sap.als.security;

import java.io.IOException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.google.gson.Gson;
import com.sap.als.persistence.Patient;
import com.sap.als.utils.EmfGenerator;

@SuppressWarnings("unchecked")
public class SuccessHandler implements AuthenticationSuccessHandler {

	private Gson gson = com.sap.als.utils.GsonBuilder.create();;

	private DataSource ds;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request,
			HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {
		response.setStatus(HttpServletResponse.SC_OK);
		EntityManagerFactory emf = EmfGenerator.initEntityManagerFactory(ds);

		EntityManager em = emf.createEntityManager();
		Query query = em.createNamedQuery("PatientByEmail");
		query.setParameter("email", authentication.getName());
		List<Patient> resultList = query.getResultList();
		response.getWriter().println(gson.toJson(resultList));
		em.close();

	}
}
