package com.sap.als.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.rmi.ServerException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.sap.als.excel.ExcelCreator;
import com.sap.als.persistence.BreathTest;
import com.sap.als.persistence.BreathTestRecording;
import com.sap.als.persistence.CallTest;
import com.sap.als.persistence.ITest;
import com.sap.als.persistence.Patient;
import com.sap.als.persistence.Questionnaire;
import com.sap.als.persistence.QuestionnaireAnswer;
import com.sap.als.persistence.SMSTest;
import com.sap.als.persistence.SpeechTest;
import com.sap.als.persistence.SpeechTestRecording;
import com.sap.als.persistence.StepTest;
import com.sap.als.persistence.TappingTest;
import com.sap.als.persistence.WritingTest;
import com.sap.als.persistence.WritingTestDrawing;
import com.sap.als.utils.EmfGenerator;
import com.sap.als.utils.Task;

@RestController
@RequestMapping("/tasks")
@SuppressWarnings("unchecked")
public class TasksController {

	private static final String NAME_SEPARATOR = "_";
	private static final String LAST_QUESTIONNAIRE_BY_PATIENT_ID = "LastQuestionnaireByPatientId";
	private static final String LAST_STEP_TEST_BY_PATIENT_ID = "LastStepTestByPatientId";
	private static final String LAST_CALL_TEST_BY_PATIENT_ID = "LastCallTestByPatientId";
	private static final String LAST_SMS_TEST_BY_PATIENT_ID = "LastSMSTestByPatientId";
	private static final String LAST_BREATH_TEST_BY_PATIENT_ID = "LastBreathTestByPatientId";
	private static final String LAST_SPEECH_TEST_BY_PATIENT_ID = "LastSpeechTestByPatientId";
	private static final String LAST_WRITING_TEST_BY_PATIENT_ID = "LastWritingTestByPatientId";
	private static final String LAST_TAPPING_TEST_BY_PATIENT_ID = "LastTappingTestByPatientId";

	
	private static final String SUBMITTED_QUEST_COUNT_BY_PATINET_ID = "SubmittedQuestionnariesCountByPatinetId";
	private static final String SUBMITTED_WRITING_COUNT_BY_PATINET_ID = "SubmittedWritingCountByPatinetId";
	private static final String SUBMITTED_STEP_COUNT_BY_PATINET_ID = "SubmittedStepCountByPatinetId";
	private static final String SUBMITTED_CALL_COUNT_BY_PATINET_ID = "SubmittedCallCountByPatinetId";
	private static final String SUBMITTED_BREATH_COUNT_BY_PATINET_ID = "SubmittedBreathCountByPatinetId";
	private static final String SUBMITTED_SMS_COUNT_BY_PATINET_ID = "SubmittedSMSCountByPatinetId";
	private static final String SUBMITTED_SPEECH_COUNT_BY_PATINET_ID = "SubmittedSpeechCountByPatinetId";
	private static final String SUBMITTED_TAPPING_COUNT_BY_PATINET_ID = "SubmittedTappingCountByPatinetId";

	private static final String STATUS_NOT_FOUND = "{\"status\": \"NOT_FOUND\"}";
	private static final String STATUS_PATIENT_NOT_FOUND = "{\"status\": \"PATIENT_NOT_FOUND\"}";
	private static final String STEP_TEST = "STEP_TEST";
	private static final String CALL_TEST = "CALL_TEST";
	private static final String SMS_TEST = "SMS_TEST";
	private static final String BREATH_TEST = "BREATH_TEST";
	private static final String SPEECH_TEST = "SPEECH_TEST";
	private static final String WRITING_TEST = "WRITING_TEST";
	private static final String QUESTIONNAIRE_TYPE = "QUESTIONNAIRE";
	private static final String TAPPING_TEST = "TAPPING_TEST";
	private DataSource ds;
	private EntityManagerFactory emf;
	private Gson gson;
	
	private enum QuestionsNames {Speech(1), Salivation(2), Swallowing(3), Handwriting(4),Cutting_food_with_gastrostomy(5), 
		Dressing_and_hygiene(6), Turning_in_bed(7),Walking(8),Climbing_stairs(9),Dyspnea(10), Orthopnea(11),
		Respiratory_insufficiency(12);
			public final int Value;
	 
			private QuestionsNames(int value)
			{
				Value = value;
			}
			private static final Map<Integer, String> _map = new HashMap<Integer, String>();
		    static
		    {
		        for (QuestionsNames questionsNames : QuestionsNames.values())
		            _map.put(questionsNames.Value, questionsNames.name());
		    }
		 
		    /**
		     * Get questionsName string from value
		     * @param value Value
		     * @return questionsName
		     */
		    public static String from(int value)
		    {
		        return _map.get(value);
		    }
		}
	
	private enum DrawingNames {house("0"), smile("1"), spiral("2"), star("3"),hexagon("4");
			public final String Value;
	 
			private DrawingNames(String value)
			{
				Value = value;
			}
			private static final Map<String, String> _map = new HashMap<String, String>();
		    static
		    {
		        for (DrawingNames drawingNames : DrawingNames.values())
		            _map.put(drawingNames.Value, drawingNames.name());
		    }
		 
		    /**
		     * Get drawingNames string from value
		     * @param value Value
		     * @return drawingName
		     */
		    public static String from(String value)
		    {
		        return _map.get(value);
		    }
		}

	public TasksController() throws ServletException {
		emf = EmfGenerator.initEntityManagerFactory(ds);
		gson = com.sap.als.utils.GsonBuilder.create();
	}

	/// ***** Last Submitted Tasks *****

	@RequestMapping(value = "/lastSubmitted", method = RequestMethod.GET)
	@ResponseBody
	public String getLastSubmittedTasks(@RequestParam(value = "id", required = false) String id, HttpSession session) {
		long patientId = id != null ? Long.parseLong(id) : (long) session.getAttribute("patientId");
		return gson.toJson(lastSubmittedTasks(patientId));
	}
	
	public ArrayList<Task> lastSubmittedTasks(long patientId) {
		ArrayList<Task> tasks = new ArrayList<Task>();
		EntityManager em = emf.createEntityManager();

		try {
			addTasklData(patientId, tasks, em, LAST_QUESTIONNAIRE_BY_PATIENT_ID, SUBMITTED_QUEST_COUNT_BY_PATINET_ID, QUESTIONNAIRE_TYPE);
			addTasklData(patientId, tasks, em, LAST_WRITING_TEST_BY_PATIENT_ID, SUBMITTED_WRITING_COUNT_BY_PATINET_ID, WRITING_TEST);
			addTasklData(patientId, tasks, em, LAST_SPEECH_TEST_BY_PATIENT_ID, SUBMITTED_SPEECH_COUNT_BY_PATINET_ID, SPEECH_TEST);
			addTasklData(patientId, tasks, em, LAST_BREATH_TEST_BY_PATIENT_ID, SUBMITTED_BREATH_COUNT_BY_PATINET_ID, BREATH_TEST);
			addTasklData(patientId, tasks, em, LAST_SMS_TEST_BY_PATIENT_ID, SUBMITTED_SMS_COUNT_BY_PATINET_ID, SMS_TEST);
			addTasklData(patientId, tasks, em, LAST_CALL_TEST_BY_PATIENT_ID, SUBMITTED_CALL_COUNT_BY_PATINET_ID, CALL_TEST);
			addTasklData(patientId, tasks, em, LAST_STEP_TEST_BY_PATIENT_ID, SUBMITTED_STEP_COUNT_BY_PATINET_ID, STEP_TEST);
			addTasklData(patientId, tasks, em, LAST_TAPPING_TEST_BY_PATIENT_ID, SUBMITTED_TAPPING_COUNT_BY_PATINET_ID, TAPPING_TEST);
			return tasks;
		} finally {
			em.close();
		}
	}

	private void addTasklData(long patientId,
			ArrayList<Task> tasks, EntityManager em,String lastQueryName,String countQueryName, String taskType) {
		Query lastQuery = em.createNamedQuery(lastQueryName);
		Query  numOfsubmittedQuery = em.createNamedQuery(countQueryName);
		addTaskFromQueryResult(tasks, lastQuery,taskType, patientId, numOfsubmittedQuery);
	}

	private void addTaskFromQueryResult(ArrayList<Task> tasks, Query query,
			String taskType, long patientId, Query numOfsubmittedQuery) {

		// ----------------------------number of submittion
		numOfsubmittedQuery.setParameter("patientId", patientId);
		long submittedCount = (Long) numOfsubmittedQuery.getSingleResult();

		// ------------------------------------- last submitted
		query.setParameter("patientId", patientId);
		List<ITest> questionnairesResults = query.getResultList();
		if (questionnairesResults.size() > 0) {
			ITest test = questionnairesResults.get(0);
			addTask(tasks, test, taskType, submittedCount);
		}

	}

	private void addTask(ArrayList<Task> tasks, ITest test, String testType, long submittedCount) {
		Task task;
		if (test != null) {
			task = new Task();
			task.setTaskId(testType);
			task.setLastSubmittedDate(test.getCreated());
			task.setSubmittedCount(submittedCount);
			tasks.add(task);
		}
	}

	/// ***** Questionnaires *****

	@RequestMapping(value = "/questionnaires", method = RequestMethod.GET)
	@ResponseBody
	public String getQuestionnaires(
			@RequestParam(value = "id", required = false) String id) {
		EntityManager em = emf.createEntityManager();
		List<Questionnaire> resultList;

		try {
			if (id != null) {
				Query query = em.createNamedQuery("QuestionnaireById");
				query.setParameter("id", Long.parseLong(id));
				resultList = query.getResultList();
				if (resultList.size() > 0) {
					if (handleQuestionnaire(em, resultList, 0)) {
						return gson.toJson(resultList.get(0));
					} else {
						return STATUS_PATIENT_NOT_FOUND;
					}
				} else {
					return STATUS_NOT_FOUND;
				}
			} else {
				String filteredQuest = getFilteredQuestionnaires(em);
				return filteredQuest;
			}
		} finally {
			em.close();
		}
	}

	private String getFilteredQuestionnaires(EntityManager em) {
		List<Questionnaire> resultList;
		resultList = em.createNamedQuery("AllQuestionnaires")
				.getResultList();
		List<Questionnaire> filteredResultList =  new ArrayList<Questionnaire>();
		for (int i = 0; i < resultList.size(); i++) {
			
			if (handleQuestionnaire(em, resultList, i)) {
				filteredResultList.add(resultList.get(i));
			}
		}
		String filteredQuest = gson.toJson(filteredResultList);
		return filteredQuest;
	}

	private boolean handleQuestionnaire(EntityManager em,
			List<Questionnaire> resultList, int i) {
		Questionnaire q = resultList.get(i);
		adjustQuestionnaireNewData(q);
		return addPatientDataToTest( q ,em);
	}

	private void adjustQuestionnaireNewData(Questionnaire q) {

		List<QuestionnaireAnswer> answers = q.getAnswers();
		for (int i = 0; i < answers.size(); i++) {
			QuestionnaireAnswer qa = answers.get(i);
			if (qa.getQuestionName() == null || qa.getQuestionName().isEmpty()) {
				String questionName = QuestionsNames.from(qa.getQuestionId());
				qa.setQuestionName(questionName);
			}
		}
	}
	
	private void adjustDrawingNewData(List<WritingTestDrawing> drawings) {

		for (int i = 0; i < drawings.size(); i++) {
			WritingTestDrawing dr= drawings.get(i);
			if (dr.getDrawingTitle() == null || dr.getDrawingTitle().isEmpty()) {
				String drawingTitle = DrawingNames.from(dr.getDrawingName());
				dr.setDrawingTitle(drawingTitle);
			}
		}
	}
	private Patient fixPatientData(EntityManager em, long patientId,
			Patient patientDetails) {
		if (patientDetails == null) {
			// System.out.println("Null patient Id");
			patientDetails = em.find(Patient.class, patientId);

		}
		fixPatientDataBeforePublish(patientDetails);
		return patientDetails;
	}

	private void fixPatientDataBeforePublish(Patient patientDetails) {
		if (patientDetails != null) {
			patientDetails.setEmail(null);
			patientDetails.setFirstName(null);
			patientDetails.setLastName(null);
			patientDetails.setPassword(null);
			patientDetails.setCreated(null);
		}
	}

	@RequestMapping(value = "/questionnaires", method = RequestMethod.POST)
	@ResponseBody
	public String createQuestionnaires(@RequestBody String body, HttpSession session) {
		EntityManager em = emf.createEntityManager();
		long patientId = (long) session.getAttribute("patientId");

		try {
			Questionnaire questionnaire = gson.fromJson(body, Questionnaire.class);
			questionnaire.setPatientId(patientId);
			questionnaire.setCreated(new Timestamp(System.currentTimeMillis()));
			em.getTransaction().begin();
			em.persist(questionnaire);
			em.getTransaction().commit();

			return "{\"status\": \"OK\", \"id\": " + questionnaire.getId() + "}";
		} finally {
			em.close();
		}
	}

	@RequestMapping(value = "/questionnaires", method = RequestMethod.DELETE)
	@ResponseBody
	public String deleteQuestionnaire(@RequestParam(value = "id", required = true) long id) {
		EntityManager em = emf.createEntityManager();
		List<Questionnaire> resultList;

		try {
			Query query = em.createNamedQuery("QuestionnaireById");
			query.setParameter("id", id);
			resultList = query.getResultList();

			if (resultList.size() > 0) {
				em.getTransaction().begin();
				Questionnaire toRemove = em.merge(resultList.get(0));
				em.remove(toRemove);
				em.getTransaction().commit();
				return "{\"status\": \"OK\"}";
			} else {
				return STATUS_NOT_FOUND;
			}
		} finally {
			em.close();
		}
	}
//--------------------- CSV 
	
	@RequestMapping(value = "/questionnaires/csv", method = RequestMethod.GET)
	@ResponseBody
	public void getQuestionnairesCSV(HttpServletResponse response) throws IOException {
		EntityManager em = emf.createEntityManager();

		try {
						
			List<Questionnaire> resultList = em.createNamedQuery("AllQuestionnaires").getResultList();
			List<Patient> patientsList = em.createNamedQuery("AllPatients").getResultList();
			response.setHeader("Content-Disposition", "attachment;filename=questionnaires.csv");
			String csvFormat = ExcelCreator.generateQuestionnaireCSV(response, resultList, patientsList);
			response.getOutputStream().write(csvFormat.getBytes());
		} finally {
			em.close();
		}
	}
	
	//-
	@RequestMapping(value = "/writingTests/csv", method = RequestMethod.GET)
	@ResponseBody
	public void getWritingsTestsCSV(HttpServletResponse response) throws IOException {
		EntityManager em = emf.createEntityManager();

		try {
			
			List<WritingTest> resultList = em.createNamedQuery("AllWritingTests").getResultList();
			List<Patient> patientsList = em.createNamedQuery("AllPatients").getResultList();
			response.setHeader("Content-Disposition", "attachment;filename=writingTests.csv");
			String csvFormat = ExcelCreator.generateWritingTestsCSV(response, resultList, patientsList);
			response.getOutputStream().write(csvFormat.getBytes());
		}
		 finally {
			em.close();
		}
	}

	/// ***** Writing Tests *****

	@RequestMapping(value = "/writingTests", method = RequestMethod.GET)
	@ResponseBody
	public String getWritingTests(@RequestParam(value = "id", required = false) String id) {
		EntityManager em = emf.createEntityManager();
		List<WritingTest> resultList;

		try {
			if (id != null) {
				Query query = em.createNamedQuery("WritingTestById");
				query.setParameter("id", Long.parseLong(id));
				resultList = query.getResultList();
				if (resultList.size() > 0) {
					
					if(handleWritingTest(em, resultList, 0))
						{return gson.toJson(resultList.get(0));}
					else{return STATUS_PATIENT_NOT_FOUND;} 
				} else {
					return STATUS_NOT_FOUND;
				}
			} else {
				resultList = em.createNamedQuery("AllWritingTests").getResultList();
				List<WritingTest> filteredResultList = new ArrayList<WritingTest>();
				for (int i = 0; i < resultList.size(); i++) {
					if(handleWritingTest(em, resultList, i))
					{
						filteredResultList.add(resultList.get(i));
					}						
				}
				return gson.toJson(filteredResultList);
			}
		} finally {
			em.close();
		}
	}

	private boolean handleWritingTest(EntityManager em,
			List<WritingTest> resultList, int i) {
		WritingTest wt = resultList.get(i);
		if(wt != null && wt.getDrawings().size() > 0){
			adjustDrawingNewData(wt.getDrawings());
		}
		boolean b = addPatientDataToTest(wt, em);
		if (b) {
			emptyDrawing(wt.getDrawings());// The drawing image is extracted
		} // from response
		return b;
	}
	
	private boolean addPatientDataToTest(ITest hp , EntityManager em)
	{
		Patient patientDetails = fixPatientData(em, hp.getPatientId(), hp.getPatientDetails());
		if(patientDetails != null)
		{
			hp.setPatientDetails(patientDetails);
			return true;
		}
		return false;
	}
	private void emptyDrawing(List<WritingTestDrawing> drawings) {
		for (int j = 0; j < drawings.size(); j++) {
			drawings.get(j).setDrawingImage(null);
		}
	}

	@RequestMapping(value = "/writingTests", method = RequestMethod.POST)
	@ResponseBody
	public String createWritingTest(HttpSession session,
			@RequestParam(value = "drawingNames", required = false) String[] drawingNames,
			@RequestParam(value = "files", required = false) MultipartFile[] files,
			@RequestParam(value = "drawingTimes", required = false) String[] drawingTimes,
			@RequestParam(value = "drawingTitles", required = false) String[] drawingTitles,
			@RequestParam(value = "deviceModel", required = false) String deviceModel) {
		long patientId = (long) session.getAttribute("patientId");
		EntityManager em = emf.createEntityManager();

		try {
			WritingTest test = new WritingTest();
			test.setPatientId(patientId);
			test.setCreated(new Timestamp(System.currentTimeMillis()));
			test.setDeviceModel(deviceModel);
			ArrayList<WritingTestDrawing> drawings = new ArrayList<WritingTestDrawing>();
			String drawTime = "";
			for (int i = 0; i < drawingNames.length; i++) {
				if ( drawingTimes != null ){
					drawTime = drawingTimes[i];
				}else{
					drawTime = "0000";
				}
				String drawingTitle = drawingTitles!=null?drawingTitles[i]:"";
				this.createWritingDrawing(drawings, drawingNames[i], drawTime, files[i],drawingTitle);
			}
   			test.setDrawings(drawings);

			em.getTransaction().begin();
			em.persist(test);
			em.getTransaction().commit();
			return "{\"status\": \"OK\", \"id\": " + test.getId() + "}";
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return "{\"status\": \"ERROR\"}";
		} finally {
			em.close();
		}
	}

	@RequestMapping(value = "/writingTests", method = RequestMethod.DELETE)
	@ResponseBody
	public String deleteWritingTest(@RequestParam(value = "id", required = true) long id) {
		EntityManager em = emf.createEntityManager();
		List<WritingTest> resultList;

		try {
			Query query = em.createNamedQuery("WritingTestById");
			query.setParameter("id", id);
			resultList = query.getResultList();

			if (resultList.size() > 0) {
				em.getTransaction().begin();
				WritingTest writingTestToRemove = em.merge(resultList.get(0));
				em.remove(writingTestToRemove);
				em.getTransaction().commit();
				return "{\"status\": \"OK\"}";
			} else {
				return STATUS_NOT_FOUND;
			}
		} finally {
			em.close();
		}
	}

	private void createWritingDrawing(ArrayList<WritingTestDrawing> drawings, String drawingName, String drawingTime, MultipartFile file,String drawingTitle)
			throws IOException {
		if (drawingName != null && file != null && drawingTime != null) {
			WritingTestDrawing drawing = new WritingTestDrawing();
			drawing.setDrawingName(drawingName);
			drawing.setDrawTime(drawingTime);
			drawing.setDrawingImage(file.getBytes());
			drawing.setDrawingTitle(drawingTitle);
			drawings.add(drawing);
		}
	}
	@RequestMapping(value = "/writingTests/{id}", method = RequestMethod.GET)
	@ResponseBody
	public void getWritingTest(@PathVariable(value = "id") Long id, HttpServletResponse response) throws IOException {
		EntityManager em = emf.createEntityManager();

		try {
			Query query = em.createNamedQuery("WritingTestDrawingById");
			query.setParameter("id", id);
			List<WritingTestDrawing> resultList = query.getResultList();

			if (resultList.size() > 0) {
				WritingTestDrawing drawing = resultList.get(0);
				String fileName = "writing" + id+ ".jpg";
				response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
				response.getOutputStream().write(drawing.getDrawingImage());
				response.flushBuffer();
			} else {
				throw new ServerException("NOT_FOUND");
			}
		} finally {
			em.close();
		}
	}//------------------------------------- ZIP of archives
	
	private void zipDrawingsForPatients( String[] patintsList, ZipOutputStream zipStream) {
		for (String patientId : patintsList) {
			List<WritingTest> writingTests = getTestForPatinet(patientId, "WritingTestByPatientId");			
			for (WritingTest wt : writingTests) {
				List<WritingTestDrawing> drawings = wt.getDrawings();
				for(WritingTestDrawing draw : drawings)
				{
					String fileName = generateFileName(patientId, wt.getId() , draw.getId(),".jpg");

					try {
						addFileToZip(fileName,  draw.getDrawingImage(), zipStream );
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			
				
			}
		}
	}
	private void zipSpeechRecordingForPatients( String[] patintsList, ZipOutputStream zipStream) {
		for (String patientId : patintsList) {
			List<SpeechTest> speechs = getSpeechTestsForPatinet(patientId);			
			for (SpeechTest wt : speechs) {
				List<SpeechTestRecording> recs = wt.getRecordings();
				for(SpeechTestRecording sRec : recs)
				{
					String fileName = generateFileName(patientId, wt.getId() , sRec.getId(),".mp3");

					try {
						addFileToZip(fileName,  sRec.getRecording(), zipStream );
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			
				
			}
		}
	}
	private void zipBreathRecordngForPatients( String[] patintsList, ZipOutputStream zipStream) {
		for (String patientId : patintsList) {
			List<BreathTest> speechs = getBreathTestForPatinet(patientId);			
			for (BreathTest wt : speechs) {
				List<BreathTestRecording> recs = wt.getRecordings();
				for(BreathTestRecording sRec : recs)
				{
					String fileName = generateFileName(patientId, wt.getId() , sRec.getId(),".mp3");

					try {
						addFileToZip(fileName,  sRec.getRecording(), zipStream );
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			
				
			}
		}
	}
	
	private void addFileToZip(String fileName,
			byte[] fileContent, ZipOutputStream zipStream) throws IOException {

		addOneFileToZipArchive(zipStream , fileName , fileContent );
		
	}

	private String generateFileName(String patientId, long testId, long resultId , String extention) {
		String fileName = patientId + NAME_SEPARATOR + String.valueOf(testId) + NAME_SEPARATOR + String.valueOf(resultId) + extention;
		return fileName;
	}

	private List<WritingTest> getTestForPatinet(String patientId, String queryName) {
		EntityManager em = emf.createEntityManager();
		try{
		
		Query query = em.createNamedQuery(queryName);//WritingTestsByPatientID
		query.setParameter("patientId",Long.valueOf(patientId) );
		return query.getResultList();
		}finally
		{
			em.close();
		}
		
				
	}
	private List<SpeechTest> getSpeechTestsForPatinet(String patientId) {
		EntityManager em = emf.createEntityManager();
		try{
		
		Query query = em.createNamedQuery("SpeechTestsByPatientId");//WritingTestsByPatientID
		query.setParameter("patientId",Long.valueOf(patientId) );
		return query.getResultList();
		}finally
		{
			em.close();
		}
		
				
	}
	private List<BreathTest> getBreathTestForPatinet(String patientId) {
		EntityManager em = emf.createEntityManager();
		try{
		
		Query query = em.createNamedQuery("BreathTestsByPatientId");//WritingTestsByPatientID
		query.setParameter("patientId",Long.valueOf(patientId) );
		return query.getResultList();
		}finally
		{
			em.close();
		}
		
				
	}
	

	@RequestMapping(value = "/writingTests/zipDrawings", method = RequestMethod.GET)
	@ResponseBody
	public void getWritingTestDrawingsByPatients(
			HttpSession session,
			@RequestParam(value = "patientNames[]", required = false) String[] patientsNames,
			HttpServletResponse response)

	{
		// prepare the return zip
		ByteArrayOutputStream outputBuffer = new ByteArrayOutputStream();
			
		ZipOutputStream zipOutputStream = prepareZipForResponse(response,
				"Drawings.zip", outputBuffer );
		zipDrawingsForPatients(patientsNames, zipOutputStream);
		
		try {
			zipOutputStream.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// get the drawings for patinet and add to archive
		try {
			response.getOutputStream().write(outputBuffer.toByteArray());
			response.getOutputStream().flush();
			outputBuffer.close();
			response.flushBuffer();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private ZipOutputStream prepareZipForResponse(HttpServletResponse response,
													String zipFileName,
													ByteArrayOutputStream outputBuffer) {
		  response.setContentType("application/zip");
		  response.addHeader("Content-Disposition", "attachment; filename=\"" + zipFileName +"\"");
		  response.addHeader("Content-Transfer-Encoding", "binary");
		  ZipOutputStream zipOutputStream = new ZipOutputStream(outputBuffer);
		  zipOutputStream.setLevel(ZipOutputStream.STORED);
		  return zipOutputStream;
		
	}

	@RequestMapping(value = "/writingTests/zip/{id}", method = RequestMethod.GET)
	@ResponseBody
	public void getWritingTestZIp(@PathVariable(value = "id") Long id, HttpServletResponse response) throws IOException {
		EntityManager em = emf.createEntityManager();

		try {
			Query query = em.createNamedQuery("WritingTestDrawingById");
			query.setParameter("id", id);
			List<WritingTestDrawing> resultList = query.getResultList();

			if (resultList.size() > 0) {
				WritingTestDrawing drawing = resultList.get(0);
				//String fileName = "writing" + id+ ".jpg";
				//response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
				
				//response.getOutputStream().write(drawing.getDrawingImage());
				controller(response,drawing.getDrawingImage(),"firstd.jpg");
				response.flushBuffer();
			} else {
				throw new ServerException("NOT_FOUND");
			}
		} finally {
			em.close();
		}
	}
	
	
	public static void controller(HttpServletResponse response, byte[] oneFileContent, String imageName)
	{
		  response.setContentType("application/zip");
		  response.addHeader("Content-Disposition", "attachment; filename=\"compress.zip\"");
		  response.addHeader("Content-Transfer-Encoding", "binary");
		  ByteArrayOutputStream outputBuffer = new ByteArrayOutputStream();
		  compress(outputBuffer, oneFileContent,imageName );
		  try {
			response.getOutputStream().write(outputBuffer.toByteArray());
			  response.getOutputStream().flush();
			  outputBuffer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	 //start
	static void compress(final OutputStream out, byte[] oneFileContent, String imageName) {
		  ZipOutputStream zipOutputStream = new ZipOutputStream(out);
		  zipOutputStream.setLevel(ZipOutputStream.STORED);

		  //for(int i = 0; i < 10; i++) {
		     //of course you need the file content of the i-th file
		     try {
				addOneFileToZipArchive(zipOutputStream, imageName, oneFileContent);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		//  }

		  try {
			zipOutputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}

		static void addOneFileToZipArchive(final ZipOutputStream zipStream,
		          String fileName,
		          byte[] content) throws IOException {
		    ZipEntry zipEntry = new ZipEntry(fileName);
		    zipStream.putNextEntry(zipEntry);
		    zipStream.write(content);
		    zipStream.closeEntry();
		}
	
	// ---------------------------- End Zips
		
		
		@RequestMapping(value = "/speechTests/zipRecording", method = RequestMethod.GET)
		@ResponseBody
		public void getSpeechTestRecordingsByPatients(
				HttpSession session,
				@RequestParam(value = "patientNames[]", required = false) String[] patientsNames,
				HttpServletResponse response)

		{
			// prepare the return zip
			ByteArrayOutputStream outputBuffer = new ByteArrayOutputStream();
				
			ZipOutputStream zipOutputStream = prepareZipForResponse(response,
					"SpeechRecordings.zip", outputBuffer );
			zipSpeechRecordingForPatients(patientsNames, zipOutputStream);
			
			try {
				zipOutputStream.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				//e1.printStackTrace();
			}
			// get the drawings for patinet and add to archive
			try {
				response.getOutputStream().write(outputBuffer.toByteArray());
				response.getOutputStream().flush();
				outputBuffer.close();
				response.flushBuffer();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}

		}	
		@RequestMapping(value = "/breathTests/zipRecording", method = RequestMethod.GET)
		@ResponseBody
		public void getBreathTestRecordingsByPatients(
				HttpSession session,
				@RequestParam(value = "patientNames[]", required = false) String[] patientsNames,
				HttpServletResponse response)

		{
			// prepare the return zip
			ByteArrayOutputStream outputBuffer = new ByteArrayOutputStream();
				
			ZipOutputStream zipOutputStream = prepareZipForResponse(response,
					"BreathRecordings.zip", outputBuffer );
			zipBreathRecordngForPatients(patientsNames, zipOutputStream);
			
			try {
				zipOutputStream.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				//e1.printStackTrace();
			}
			// get the drawings for patinet and add to archive
			try {
				response.getOutputStream().write(outputBuffer.toByteArray());
				response.getOutputStream().flush();
				outputBuffer.close();
				response.flushBuffer();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}

		}	
		
	/// ***** Speech Tests *****
	
	

	@RequestMapping(value = "/speechTests/csv", method = RequestMethod.GET)
	@ResponseBody
	public void getSpeechTestsCSV(HttpServletResponse response) {
		EntityManager em = emf.createEntityManager();

		try {
			
			List<SpeechTest> resultList = em.createNamedQuery("AllSpeechTests").getResultList();
			List<Patient> patientsList = em.createNamedQuery("AllPatients").getResultList();
			response.setHeader("Content-Disposition", "attachment;filename=speechTests.csv");
			String csvFormat = ExcelCreator.generateSpeechTestsCSV(response, resultList, patientsList);
			response.getOutputStream().write(csvFormat.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		} finally {
			em.close();
		}
	}
	

	@RequestMapping(value = "/speechTests", method = RequestMethod.GET)
	@ResponseBody
	public String getSpeechTests(
			@RequestParam(value = "id", required = false) String id,
			HttpServletResponse response) throws IOException {
		EntityManager em = emf.createEntityManager();

		try {
			if (id != null) {
				Query query = em.createNamedQuery("SpeechTestById");
				query.setParameter("id", Long.parseLong(id));
				List<SpeechTest> resultList = query.getResultList();

				if (resultList.size() > 0) {
					if (handleSpeechTest(em, resultList, 0)) {
						return gson.toJson(resultList.get(0));
					} else {
						return STATUS_PATIENT_NOT_FOUND;
					}
				} else {
					return STATUS_NOT_FOUND;
				}
			} else {
				List<SpeechTest> resultList = em.createNamedQuery(
						"AllSpeechTests").getResultList();
				List<SpeechTest> filterResultList =  new ArrayList<SpeechTest>();
				for (int i = 0; i < resultList.size(); i++) {

					if (handleSpeechTest(em, resultList, i)) {
						filterResultList.add(resultList.get(i));
					}
				}
				return gson.toJson(filterResultList);
			}
		} finally {
			em.close();
		}
	}

	private boolean handleSpeechTest(EntityManager em,
			List<SpeechTest> resultList, int i) {
		boolean b = addPatientDataToTest(resultList.get(i), em);
		if(b){resetRecording(resultList,i);}
		return b;
	}

	private void resetRecording(List<SpeechTest> resultList, int index) {
		List<SpeechTestRecording> recordings = resultList.get(index).getRecordings();
		for (int j = 0; j < recordings.size(); j++) {
			recordings.get(j).setRecording(null);
		}
	}

	@RequestMapping(value = "/speechTests/{id}", method = RequestMethod.GET)
	@ResponseBody
	public void getSpeechTests(@PathVariable(value = "id") Long id, HttpServletResponse response) throws IOException {
		EntityManager em = emf.createEntityManager();

		try {
			Query query = em.createNamedQuery("SpeechTestRecordingById");
			query.setParameter("id", id);
			List<SpeechTestRecording> resultList = query.getResultList();

			if (resultList.size() > 0) {
				SpeechTestRecording recording = resultList.get(0);
				String fileName = "record" + id + ".mp3";
				response.setHeader("Content-Disposition", "attachment;filename="+fileName);
				response.getOutputStream().write(recording.getRecording());
				response.flushBuffer();
			} else {
				throw new ServerException("NOT_FOUND");
			}
		} finally {
			em.close();
		}
	}

	@RequestMapping(value = "/speechTests", method = RequestMethod.POST)
	@ResponseBody
	public String createSpeechTest(HttpSession session,
			@RequestParam(value = "testIds", required = false) Integer[] testIds,
			@RequestParam(value = "files", required = false) MultipartFile[] files,
			@RequestParam(value = "lengthes", required = false) long[] lengthes,
			@RequestParam(value = "deviceModel", required = false) String deviceModel) {
		long patientId = (long) session.getAttribute("patientId");
		EntityManager em = emf.createEntityManager();

		try {
			SpeechTest test = new SpeechTest();
			test.setPatientId(patientId);
			test.setCreated(new Timestamp(System.currentTimeMillis()));
			test.setDeviceModel(deviceModel);
			ArrayList<SpeechTestRecording> recordings = new ArrayList<SpeechTestRecording>();

			for (int i = 0; i < testIds.length; i++) {
				long speechLength = lengthes!=null? lengthes[i]:0;
				this.createSpeechRecording(recordings, testIds[i], files[i],speechLength);
			}

			test.setRecordings(recordings);

			em.getTransaction().begin();
			em.persist(test);
			em.getTransaction().commit();
			return "{\"status\": \"OK\", \"id\": " + test.getId() + "}";
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return "{\"status\": \"ERROR\"}";
		} finally {
			em.close();
		}
	}

	private void createSpeechRecording(ArrayList<SpeechTestRecording> recordings, Integer answerId, 
			MultipartFile file, long speechLength)
			throws IOException {
		if (answerId != null && file != null) {
			SpeechTestRecording recording = new SpeechTestRecording();
			recording.setTestId(answerId);
			recording.setRecording(file.getBytes());
			recording.setLength(speechLength);
			recordings.add(recording);
		}
	}

	@RequestMapping(value = "/speechTests", method = RequestMethod.DELETE)
	@ResponseBody
	public String deleteSpeechTest(@RequestParam(value = "id", required = true) long id) {
		EntityManager em = emf.createEntityManager();
		List<SpeechTest> resultList;

		try {
			Query query = em.createNamedQuery("SpeechTestById");
			query.setParameter("id", id);
			resultList = query.getResultList();

			if (resultList.size() > 0) {
				em.getTransaction().begin();
				SpeechTest speechTestToRemove = em.merge(resultList.get(0));
				em.remove(speechTestToRemove);
				em.getTransaction().commit();
				return "{\"status\": \"OK\"}";
			} else {
				return STATUS_NOT_FOUND;
			}
		} finally {
			em.close();
		}
	}

	/// ***** Breath Tests *****
	@RequestMapping(value = "/breathTests/csv", method = RequestMethod.GET)
	@ResponseBody
	public void getBreathTestsCSV(HttpServletResponse response) {
		EntityManager em = emf.createEntityManager();

		try {
			
			List<BreathTest> resultList = em.createNamedQuery("AllBreathTests").getResultList();
			List<Patient> patientsList = em.createNamedQuery("AllPatients").getResultList();
			response.setHeader("Content-Disposition", "attachment;filename=breathTests.csv");
			String csvFormat = ExcelCreator.generateBreathTestsCSV(response, resultList, patientsList);
			response.getOutputStream().write(csvFormat.getBytes()); 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		} finally {
			em.close();
		}
	}
	
	/// ***** Breath Tests *****

			@RequestMapping(value = "/breathTests", method = RequestMethod.GET)
			@ResponseBody
			public String getBreathTests(@RequestParam(value = "id", required = false) String id, HttpServletResponse response)
					throws IOException {
				EntityManager em = emf.createEntityManager();

				try {
					if (id != null) {
						Query query = em.createNamedQuery("BreathTestById");
						query.setParameter("id", Long.parseLong(id));
						List<BreathTest> resultList = query.getResultList();
						
						if (resultList.size() > 0) {
							if(handleBreathTest(em, resultList, 0))
								{return gson.toJson(resultList.get(0));}
							else
							{
								return STATUS_PATIENT_NOT_FOUND;
							}
						} else {
							return STATUS_NOT_FOUND;
						}
					} else {
						List<BreathTest> resultList = em.createNamedQuery("AllBreathTests").getResultList();
						List<BreathTest> filteredResultList = new ArrayList<BreathTest>();
						for (int i = 0; i < resultList.size(); i++) {
							if(handleBreathTest(em, resultList, i))
							{
								filteredResultList.add(resultList.get(i));
							}
						}
						return gson.toJson(filteredResultList);
					}
				} finally {
					em.close();
				}
			}
	


	private boolean handleBreathTest(EntityManager em,
			List<BreathTest> resultList, int i) {
		BreathTest bt = resultList.get(i);
		boolean b = addPatientDataToTest(bt, em);
		if (b) {
			List<BreathTestRecording> recordings = bt.getRecordings();
			for (int j = 0; j < recordings.size(); j++) {
				recordings.get(j).setRecording(null);
			}
		}
		return b;
	}

	@RequestMapping(value = "/breathTests/{id}", method = RequestMethod.GET)
	@ResponseBody
	public void getBreathTests(@PathVariable(value = "id") Long id, HttpServletResponse response) throws IOException {
		EntityManager em = emf.createEntityManager();

		try {
			Query query = em.createNamedQuery("BreathTestRecordingById");
			query.setParameter("id", id);
			List<BreathTestRecording> resultList = query.getResultList();

			if (resultList.size() > 0) {
				BreathTestRecording recording = resultList.get(0);
				String fileName = "record" + id + ".mp3";
				response.setHeader("Content-Disposition", "attachment;filename="+fileName);
				response.getOutputStream().write(recording.getRecording());
				response.flushBuffer();
			} else {
				throw new ServerException("NOT_FOUND");
			}
		} finally {
			em.close();
		}
	}

	@RequestMapping(value = "/breathTests", method = RequestMethod.POST)
	@ResponseBody
	public String createBreathTest(HttpSession session,
			@RequestParam(value = "testIds", required = false) Integer[] testIds,
			@RequestParam(value = "files", required = false) MultipartFile[] files,
			@RequestParam(value = "lengthes", required = false) Integer[] lengthes,
			@RequestParam(value = "deviceModel", required = false) String deviceModel) {
		long patientId = (long) session.getAttribute("patientId");
		EntityManager em = emf.createEntityManager();
		// TODO:Verify testIDs and field has same size

		try {
			BreathTest test = new BreathTest();
			test.setPatientId(patientId);
			test.setCreated(new Timestamp(System.currentTimeMillis()));
			test.setDeviceModel(deviceModel);
			ArrayList<BreathTestRecording> recordings = new ArrayList<BreathTestRecording>();

			for (int i = 0; i < testIds.length; i++) {
				this.createBreathTestRecording(recordings, testIds[i], files[i], lengthes[i]);
			}

			test.setRecordings(recordings);

			em.getTransaction().begin();
			em.persist(test);
			em.getTransaction().commit();
			return "{\"status\": \"OK\", \"id\": " + test.getId() + "}";
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return "{\"status\": \"ERROR\"}";
		} finally {
			em.close();
		}
	}

	private void createBreathTestRecording(ArrayList<BreathTestRecording> recordings, Integer answerId,
			MultipartFile file, Integer length) throws IOException {

		if (answerId != null && file != null) {
			BreathTestRecording recording = new BreathTestRecording();
			recording.setTestId(answerId);
			recording.setRecording(file.getBytes());
			recording.setLength(length);
			recordings.add(recording);
		}
	}

	@RequestMapping(value = "/breathTests", method = RequestMethod.DELETE)
	@ResponseBody
	public String deleteBreathTest(@RequestParam(value = "id", required = true) long id) {
		EntityManager em = emf.createEntityManager();
		List<BreathTest> resultList;

		try {
			Query query = em.createNamedQuery("BreathTestById");
			query.setParameter("id", id);
			resultList = query.getResultList();

			if (resultList.size() > 0) {
				em.getTransaction().begin();
				BreathTest breathTestToRemove = em.merge(resultList.get(0));
				em.remove(breathTestToRemove);
				em.getTransaction().commit();
				return "{\"status\": \"OK\"}";
			} else {
				return STATUS_NOT_FOUND;
			}
		} finally {
			em.close();
		}
	}

	// ***** SMS Tests *****

	@RequestMapping(value = "/smsTests", method = RequestMethod.GET)
	@ResponseBody
	public String getSMSTests(@RequestParam(value = "id", required = false) String id, HttpServletResponse response)
			throws IOException {
		EntityManager em = emf.createEntityManager();
		List<SMSTest> resultList;

		try {
			if (id != null) {
				Query query = em.createNamedQuery("SMSTestById");
				query.setParameter("id", Long.parseLong(id));
				resultList = query.getResultList();
				if (resultList.size() > 0) {
					if (handleSMSTest(em, resultList, 0)) {
						return gson.toJson(resultList.get(0));
					} else {
						return STATUS_PATIENT_NOT_FOUND;
					}
				} else {
					return STATUS_NOT_FOUND;
				}
			} else {
				resultList = em.createNamedQuery("AllSMSTests").getResultList();
				List<SMSTest> filteredResultList =  new ArrayList<SMSTest>();
				for (int i = 0; i < resultList.size(); i++) {
					
					if (handleSMSTest(em, resultList, i)) {
						filteredResultList.add(resultList.get(i));
					}
				}
				return gson.toJson(filteredResultList);
		
			}
		} finally {
			em.close();
		}
	}

	private boolean handleSMSTest(EntityManager em, List<SMSTest> resultList, int i) {
		return  addPatientDataToTest(resultList.get(i),em);		
		
	}

	@RequestMapping(value = "/smsTests", method = RequestMethod.POST)
	@ResponseBody
	public String createSMSTest(@RequestBody String body, HttpSession session) {
		EntityManager em = emf.createEntityManager();
		long patientId = (long) session.getAttribute("patientId");

		try {
			SMSTest smsTest = gson.fromJson(body, SMSTest.class);
			smsTest.setPatientId(patientId);
			smsTest.setCreated(new Timestamp(System.currentTimeMillis()));
			em.getTransaction().begin();
			em.persist(smsTest);
			em.getTransaction().commit();

			return "{\"status\": \"OK\", \"id\": " + smsTest.getId() + "}";
		} finally {
			em.close();
		}
	}

	@RequestMapping(value = "/smsTests", method = RequestMethod.DELETE)
	@ResponseBody
	public String deleteSMSTest(@RequestParam(value = "id", required = true) long id) {
		EntityManager em = emf.createEntityManager();
		List<SMSTest> resultList;

		try {
			Query query = em.createNamedQuery("SMSTestById");
			query.setParameter("id", id);
			resultList = query.getResultList();

			if (resultList.size() > 0) {
				em.getTransaction().begin();
				SMSTest toRemove = em.merge(resultList.get(0));
				em.remove(toRemove);
				em.getTransaction().commit();
				return "{\"status\": \"OK\"}";
			} else {
				return STATUS_NOT_FOUND;
			}
		} finally {
			em.close();
		}
	}

	// ***** Call Tests *****

	@RequestMapping(value = "/callTests", method = RequestMethod.GET)
	@ResponseBody
	public String getCallTests(@RequestParam(value = "id", required = false) String id, HttpServletResponse response)
			throws IOException {
		EntityManager em = emf.createEntityManager();
		List<CallTest> resultList;

		try {
			if (id != null) {
				Query query = em.createNamedQuery("CallTestById");
				query.setParameter("id", Long.parseLong(id));
				resultList = query.getResultList();

				if (resultList.size() > 0) {
					if (addPatientDataToTest(resultList.get(0), em)) {
						return gson.toJson(resultList.get(0));
					} else {
						return STATUS_PATIENT_NOT_FOUND;
					}
				} else {
					return STATUS_NOT_FOUND;
				}
			} else {
				resultList = em.createNamedQuery("AllCallTests").getResultList();
				List<CallTest> filteredResultList = new ArrayList<CallTest>();
				for (int i = 0; i < resultList.size(); i++) {
					if(addPatientDataToTest(resultList.get(i), em))
					{
						filteredResultList.add(resultList.get(i));
					}						
				}
				return gson.toJson(filteredResultList);
			}
		} finally {
			em.close();
		}
	}

	@RequestMapping(value = "/callTests", method = RequestMethod.POST)
	@ResponseBody
	public String createCallTest(@RequestBody String body, HttpSession session) {
		EntityManager em = emf.createEntityManager();
		long patientId = (long) session.getAttribute("patientId");

		try {
			CallTest callTest = gson.fromJson(body, CallTest.class);
			callTest.setPatientId(patientId);
			callTest.setCreated(new Timestamp(System.currentTimeMillis()));
			em.getTransaction().begin();
			em.persist(callTest);
			em.getTransaction().commit();

			return "{\"status\": \"OK\", \"id\": " + callTest.getId() + "}";
		} finally {
			em.close();
		}
	}

	@RequestMapping(value = "/callTests", method = RequestMethod.DELETE)
	@ResponseBody
	public String deleteCallTest(@RequestParam(value = "id", required = true) long id) {
		EntityManager em = emf.createEntityManager();
		List<CallTest> resultList;

		try {
			Query query = em.createNamedQuery("CallTestById");
			query.setParameter("id", id);
			resultList = query.getResultList();

			if (resultList.size() > 0) {
				em.getTransaction().begin();
				CallTest toRemove = em.merge(resultList.get(0));
				em.remove(toRemove);
				em.getTransaction().commit();
				return "{\"status\": \"OK\"}";
			} else {
				return STATUS_NOT_FOUND;
			}
		} finally {
			em.close();
		}
	}

	// ***** Step Tests *****

	@RequestMapping(value = "/stepTests", method = RequestMethod.GET)
	@ResponseBody
	public String getStepTests(
			@RequestParam(value = "id", required = false) String id,
			HttpServletResponse response) throws IOException {
		EntityManager em = emf.createEntityManager();
		List<StepTest> resultList;

		try {
			if (id != null) {
				Query query = em.createNamedQuery("StepTestById");
				query.setParameter("id", Long.parseLong(id));
				resultList = query.getResultList();

				if (resultList.size() > 0) {

					if (handleStepTest(em, resultList, 0, false)) {
						return gson.toJson(resultList.get(0));
					} else {
						return STATUS_PATIENT_NOT_FOUND;
					}
				} else {
					return STATUS_NOT_FOUND;
				}
			} else {
				resultList = em.createNamedQuery("AllStepTests")
						.getResultList();
				// --------------------
				// Add Patient Data to result and reset Axes in 'all' query
				List<StepTest> filteredResultList = new ArrayList<StepTest>();
				for (int i = 0; i < resultList.size(); i++) {

					if(handleStepTest(em, resultList, i, true))
					{
						filteredResultList.add(resultList.get(i));
					}
				}
				return gson.toJson(filteredResultList);
			}
		} finally {
			em.close();
		}
	}

	private boolean handleStepTest(EntityManager em, List<StepTest> resultList,
			int i, boolean resetAxes) {
		boolean pExists = addPatientDataToTest(resultList.get(i),em);		
		if(pExists && resetAxes)
			resetAxes(resultList.get(i));
		return pExists;
	}

	private void resetAxes(StepTest stepTest) {	
		stepTest.setAxes(null);
	}

	@RequestMapping(value = "/stepTests", method = RequestMethod.POST)
	@ResponseBody
	public String createStepTest(@RequestBody String body, HttpSession session) {
		EntityManager em = emf.createEntityManager();
		long patientId = (long) session.getAttribute("patientId");

		try {
			StepTest stepTest = gson.fromJson(body, StepTest.class);
			stepTest.setPatientId(patientId);
			stepTest.setCreated(new Timestamp(System.currentTimeMillis()));
			em.getTransaction().begin();
			em.persist(stepTest);
			em.getTransaction().commit();

			return "{\"status\": \"OK\", \"id\": " + stepTest.getId() + "}";
		} finally {
			em.close();
		}
	}

	@RequestMapping(value = "/stepTests", method = RequestMethod.DELETE)
	@ResponseBody
	public String deleteStepTest(@RequestParam(value = "id", required = true) long id) {
		EntityManager em = emf.createEntityManager();
		List<StepTest> resultList;

		try {
			Query query = em.createNamedQuery("StepTestById");
			query.setParameter("id", id);
			resultList = query.getResultList();

			if (resultList.size() > 0) {
				em.getTransaction().begin();
				StepTest toRemove = em.merge(resultList.get(0));
				em.remove(toRemove);
				em.getTransaction().commit();
				return "{\"status\": \"OK\"}";
			} else {
				return STATUS_NOT_FOUND;
			}
		} finally {
			em.close();
		}
	}
	
	
	//-------------------------------------------------------------------
	//					Tapping Test
	//--------------------------------------------------------------------
	
	

	@RequestMapping(value = "/tappingTests", method = RequestMethod.GET)
	@ResponseBody
	public String getTappingTests(
			@RequestParam(value = "id", required = false) String id,
			HttpServletResponse response) throws IOException {
		EntityManager em = emf.createEntityManager();

		try {
			if (id != null) {
				Query query = em.createNamedQuery("TappingTestById");
				query.setParameter("id", Long.parseLong(id));
				List<TappingTest> resultList = query.getResultList();

				if (resultList.size() > 0) {
					if (handleTappingTest(em, resultList, 0)) {
						return gson.toJson(resultList.get(0));
					} else {
						return STATUS_PATIENT_NOT_FOUND;
					}
				} else {
					return STATUS_NOT_FOUND;
				}

			} else {
				List<TappingTest> resultList = em.createNamedQuery(
						"AllTappingTests").getResultList();
				List<TappingTest> filteredResultList = new ArrayList<TappingTest>();
				for (int i = 0; i < resultList.size(); i++) {
					if (handleTappingTest(em, resultList, i)) {
						filteredResultList.add(resultList.get(i));
					}
				}
				return gson.toJson(filteredResultList);
			}
		} finally {
			em.close();
		}
	}

	private boolean handleTappingTest(EntityManager em,
			List<TappingTest> resultList, int i)
	{
			TappingTest wt = resultList.get(i);
			return  addPatientDataToTest(wt, em);
		 
	}
	
	@RequestMapping(value = "/tappingTests", method = RequestMethod.POST)
	@ResponseBody
	public String createTappingTest(@RequestBody String body,
			HttpSession session) {
		EntityManager em = emf.createEntityManager();
		long patientId = (long) session.getAttribute("patientId");
		//tapping duration
		try {
			TappingTest tappTest = gson.fromJson(body, TappingTest.class);
			tappTest.setPatientId(patientId);
			tappTest.setCreated(new Timestamp(System.currentTimeMillis()));
			em.getTransaction().begin();
			em.persist(tappTest);
			em.getTransaction().commit();

			return "{\"status\": \"OK\", \"id\": " + tappTest.getId() + "}";
		} finally {
			em.close();
		}
	}

		@RequestMapping(value = "/tappingTests", method = RequestMethod.DELETE)
		@ResponseBody
		public String deleteTappingTest(@RequestParam(value = "id", required = true) long id) {
			EntityManager em = emf.createEntityManager();
			List<TappingTest> resultList;

			try {
				Query query = em.createNamedQuery("TappingTestById");
				query.setParameter("id", id);
				resultList = query.getResultList();

				if (resultList.size() > 0) {
					em.getTransaction().begin();
					TappingTest tappingTestToRemove = em.merge(resultList.get(0));
					em.remove(tappingTestToRemove);
					em.getTransaction().commit();
					return "{\"status\": \"OK\"}";
				} else {
					return STATUS_NOT_FOUND;
				}
			} finally {
				em.close();
			}
		}

}
