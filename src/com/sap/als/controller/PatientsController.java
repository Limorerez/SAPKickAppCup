package com.sap.als.controller;

import java.sql.Timestamp;
import java.util.List;

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
import com.sap.als.persistence.Patient;
import com.sap.als.security.HashCode;
import com.sap.als.utils.EmfGenerator;

@RestController
@RequestMapping("/patients")
@SuppressWarnings("unchecked")
public class PatientsController {

	private DataSource ds;
	private EntityManagerFactory emf;
	private Gson gson;
	private TasksController tasksController;
	
	private static final Logger LOG = LoggerFactory.getLogger(PatientsController.class);

	public PatientsController() throws ServletException {
		emf = EmfGenerator.initEntityManagerFactory(ds);
		gson = com.sap.als.utils.GsonBuilder.create();
		tasksController = new TasksController();
	}

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public String getPatients(
			@RequestParam(value = "email", required = false) String email,
			@RequestParam(value = "id", required = false) String id,
			HttpServletResponse response) {
		
		EntityManager em = emf.createEntityManager();
		List<Patient> resultList;
		Patient patient;

		try {
			if (email != null) {
				Query query = em.createNamedQuery("PatientByEmail");
				query.setParameter("email", email);
				resultList = query.getResultList();

				if (resultList.size() > 0) {
					patient = resultList.get(0);
					patient.setLastSubmittedTasks(tasksController.lastSubmittedTasks(patient.getId()));
					return gson.toJson(patient);
				} else {
					return "{\"status\": \"NOT_FOUND\"}";
				}
			} else if (id != null) {
				Query query = em.createNamedQuery("PatientById");
				query.setParameter("id", Long.parseLong(id));
				resultList = query.getResultList();

				if (resultList.size() > 0) {
					patient = resultList.get(0);
					patient.setLastSubmittedTasks(tasksController.lastSubmittedTasks(patient.getId()));
					return gson.toJson(patient);
				} else {
					return "{\"status\": \"NOT_FOUND\"}";
				}
			} else {
				resultList = em.createNamedQuery("AllPatients").getResultList();
				for (int i = 0; i < resultList.size(); i++) {
					patient = resultList.get(i);
					patient.setLastSubmittedTasks(tasksController.lastSubmittedTasks(patient.getId()));
				}
				return gson.toJson(resultList);
			}
		} finally {
			em.close();
		}

	}

	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public String createPatient(@RequestBody String body) {
		EntityManager em = emf.createEntityManager();

		try {
			Patient patient = gson.fromJson(body, Patient.class);
			patient.setEmail(patient.getEmail().toLowerCase());

			Query query = em.createNamedQuery("PatientByEmail");
			query.setParameter("email", patient.getEmail());
			List<Patient> resultList = query.getResultList();
			if (resultList.size() > 0) {
				LOG.error("Failed to create user " + patient.getEmail() + ": user already exists");
				return "{\"status\": \"USER_EXISTS\"}";
			} else {
				patient.setCreated(new Timestamp(System.currentTimeMillis()));
				patient.setPassword(HashCode.getHashPassword(patient
						.getPassword()));
				em.getTransaction().begin();
				em.persist(patient);
				em.getTransaction().commit();
				
				LOG.info("Created user " + patient.getEmail());
				return "{\"status\": \"OK\"}";
			}
		} finally {
			em.close();
		}
	}

	@RequestMapping(method = RequestMethod.PUT)
	@ResponseBody
	public String updatePatient(@RequestBody String body) {
		EntityManager em = emf.createEntityManager();

		try {
			Patient patient = gson.fromJson(body, Patient.class);
			Patient existingPatient = em.find(Patient.class, patient.getId());

			if (existingPatient == null) {
				LOG.error("Failed to update user " + patient.getEmail() + ": user does not exist");
				return "{\"status\": \"NOT FOUND\"}";
			} else {
				em.getTransaction().begin();
				existingPatient.setBirthday(patient.getBirthday());
				existingPatient.setDiagnoseDate(patient.getDiagnoseDate());
				existingPatient.setEmail(patient.getEmail());
				existingPatient.setFirstName(patient.getFirstName());
				existingPatient.setGender(patient.getGender());
				existingPatient.setLastName(patient.getLastName());
				existingPatient.setUserType(patient.getUserType());
				em.getTransaction().commit();
				
				LOG.info("Updated user " + patient.getEmail());
				return "{\"status\": \"OK\"}";
			}
		} finally {
			em.close();
		}
	}

	@RequestMapping(method = RequestMethod.DELETE)
	@ResponseBody
	public String deletePatient(
			@RequestParam(value = "id", required = true) String id) {
		EntityManager em = emf.createEntityManager();
		List<Patient> resultList;

		try {
			Query query = em.createNamedQuery("PatientById");
			query.setParameter("id", Long.parseLong(id));
			resultList = query.getResultList();

			if (resultList.size() > 0) {
				em.getTransaction().begin();
				Patient patientToRemove = em.merge(resultList.get(0));
				em.remove(patientToRemove);
				em.getTransaction().commit();
				
				LOG.info("Deleted user " + id);
				return "{\"status\": \"OK\"}";
			} else {
				LOG.error("Failed to delete user " + id + ": user does not exists");
				return "{\"status\": \"NOT_FOUND\"}";
			}
		} finally {
			em.close();
		}
	}

}
