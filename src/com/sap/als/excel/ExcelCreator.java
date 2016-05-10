package com.sap.als.excel;

import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.sap.als.persistence.BreathTest;
import com.sap.als.persistence.BreathTestRecording;
import com.sap.als.persistence.ITest;
import com.sap.als.persistence.Patient;
import com.sap.als.persistence.Questionnaire;
import com.sap.als.persistence.QuestionnaireAnswer;
import com.sap.als.persistence.SpeechTest;
import com.sap.als.persistence.SpeechTestRecording;
import com.sap.als.persistence.WritingTest;
import com.sap.als.persistence.WritingTestDrawing;

public class ExcelCreator {

	private static final String CSV_SEPARATOR = ",";

	private static String[] columns = { "Id", "Gender", "Year of birth",
			"Date of diagnosis", "Speech", "Salivation", "Swallowing",
			"Handwriting", "Cutting food with gastrostomy",
			"Dressing and hygiene", "Turning in bed", "Walking",
			"Climbing stairs", "Dyspnea", "Orthopnea",
			"Respiratory insufficiency" };

	public static String questHeaders = "Patient ID, Patient Gender, Patient Year of Birth, "
			+ "Patient User type , Patient date of diagnosis ,"
			+ " Task ID ,Task Created date ,"
			+ "Questionnaire Answer Id, Question Answer,Question Name ,Answer Remark";
	
	public static String writingHeaders = "Patient ID,Patient Gender,Patient Year of Birth,"
			+ "Patient User type, Patient date of diagnosis,"
			+ "Task ID ,Task Created date , Drawing Id,Drawing Name,Drawing Time,Drawing Title";
	
	public static String speechHeaders = "Patient ID,Patient Gender,Patient Year of Birth,"
			+ "Patient User type,"
			+ "Patient date of diagnosis,"
			+ "Task ID ,Task Created date, Recording ID, Recording Length, Recording Test ID";
	
	public static String breathHeaders = "Patient ID,Patient Gender,Patient Year of Birth,"
			+ "Patient User type, Patient date of diagnosis,"
			+ "Task ID ,Task Created date, Recording ID, Recording Length, Recording Test ID";

	public static String generateQuestionnaireCSV(HttpServletResponse response,
			List<Questionnaire> resultList, List<Patient> patients) {

		String CSVResult = questHeaders;
		for (Questionnaire quset : resultList) {

			for (QuestionnaireAnswer ans : quset.getAnswers()) {

				StringBuffer oneLine = getTaskCSVLine(patients, quset);
				if (oneLine != null) {
					oneLine.append(getAnswerResult(ans));
					CSVResult += oneLine.toString();
				}
			}

		}
		return CSVResult;
	}

	private static StringBuffer getTaskCSVLine(List<Patient> patients,
			ITest quset) {
		StringBuffer oneLine = new StringBuffer();
		oneLine.append("\n");
		String pDetails = getPatientDetails(quset, patients);
		if (pDetails.isEmpty()) {
			return null;
		}
		oneLine.append(pDetails);
		oneLine.append(CSV_SEPARATOR);
		oneLine.append(quset.getId());
		oneLine.append(CSV_SEPARATOR);
		oneLine.append(quset.getCreated());
		oneLine.append(CSV_SEPARATOR);
		return oneLine;
	}

	// public static String speechHeaders =
	// "patientId,pGender,PbirthDate,pUserType,pDiagnosis,Id,qCreated,recordingId,recordingLength,recordingTestID";

	public static String generateSpeechTestsCSV(HttpServletResponse response,
			List<SpeechTest> resultList, List<Patient> patients) {

		String CSVResult = speechHeaders;
		for (SpeechTest speechTest : resultList) {

			for (SpeechTestRecording speechRec : speechTest.getRecordings()) {

				StringBuffer oneLine = getTaskCSVLine(patients, speechTest);
				if (oneLine != null) {
					oneLine.append(getSpeechRecordingLine(speechRec));
					CSVResult += oneLine.toString();
				}

			}

		}
		return CSVResult;
	}

	public static String generateBreathTestsCSV(HttpServletResponse response,
			List<BreathTest> resultList, List<Patient> patients) {
		String CSVResult = breathHeaders;
		for (BreathTest breathTest : resultList) {

			for (BreathTestRecording breathRec : breathTest.getRecordings()) {
				StringBuffer oneLine = getTaskCSVLine(patients, breathTest);
				if (oneLine != null) {
					oneLine.append(geBreathRecordingLine(breathRec));
					CSVResult += oneLine.toString();
				}
			}
		}
		return CSVResult;
	}

	public static String generateWritingTestsCSV(HttpServletResponse response,
			List<WritingTest> resultList, List<Patient> patients) {

		String CSVResult = writingHeaders;
		for (WritingTest writingTest : resultList) {

			for (WritingTestDrawing wrtDraw : writingTest.getDrawings()) {

				StringBuffer oneLine = getTaskCSVLine(patients, writingTest);
				if (oneLine != null) {
					oneLine.append(getWritingTestLine(wrtDraw));
					CSVResult += oneLine.toString();
				}
			}

		}
		return CSVResult;
	}

	// public static String writingHeaders =
	// "patientId,pGender,PbirthDate,pUserType,pDiagnosis,testId,testCreated,drawingId,drawingName,drawingTime,drawingTitle";

	private static Object getWritingTestLine(WritingTestDrawing wrtDraw) {
		String res = "";
		if (wrtDraw != null) {
			res = wrtDraw.getId() + CSV_SEPARATOR + wrtDraw.getDrawingName()
					+ CSV_SEPARATOR + wrtDraw.getDrawTime() + CSV_SEPARATOR
					+ wrtDraw.getDrawingTitle();
		}
		return res;
	}

	private static String geBreathRecordingLine(BreathTestRecording breathRec) {
		String res = "";
		if (breathRec != null) {
			res = breathRec.getId() + CSV_SEPARATOR + breathRec.getLength()
					+ CSV_SEPARATOR + breathRec.getTestId();
		}
		return res;
	}

	private static String getSpeechRecordingLine(SpeechTestRecording speechRec) {
		String res = "";
		if (speechRec != null) {
			res = speechRec.getId() + CSV_SEPARATOR + speechRec.getLength()
					+ CSV_SEPARATOR + speechRec.getTestId();
		}
		return res;
	}

	private static String getAnswerResult(QuestionnaireAnswer ans) {
		String res = "";
		if (ans != null) {
			res = ans.getQuestionId() + CSV_SEPARATOR + ans.getAnswer()
					+ CSV_SEPARATOR + ans.getQuestionName() + CSV_SEPARATOR
					+ ans.getRemark();
		}
		return res;
	}

	private static String getPatientDetails(ITest test, List<Patient> patients) {
		String res = "";

		if (test != null) {
			Patient p = test.getPatientDetails();
			if (p == null) {
				p = getPatientById(test.getPatientId(), patients);
				if (p != null) {
					res = patientToCSV(p);
				}
			} else {
				res = patientToCSV(test.getPatientDetails());
			}
		}
		return res;
	}

	// format in the follow :patientId,pGender,PbirthDate,pUserType
	private static String patientToCSV(Patient patientDetails) {
		return patientDetails.getId()
				+ CSV_SEPARATOR
				+ patientDetails.getGender()
				+ CSV_SEPARATOR
				+ patientDetails.getBirthday().toString()
				+ CSV_SEPARATOR
				+ patientDetails.getUserType()
				+ CSV_SEPARATOR
				+ ((patientDetails.getDiagnoseDate() == null) ? "" : String
						.valueOf(patientDetails.getDiagnoseDate()));
	}

	public static void generateQuestionnairesExcel(
			HttpServletResponse response, List<Questionnaire> resultList,
			List<Patient> patients) {
		try {
			Workbook wb = new HSSFWorkbook();
			Sheet sheet = wb.createSheet("Questionnaires");
			CreationHelper factory = wb.getCreationHelper();
			Drawing drawing = sheet.createDrawingPatriarch();

			SimpleDateFormat yearSdf = new SimpleDateFormat("yyyy");
			SimpleDateFormat monthYearSdf = new SimpleDateFormat("MM/yyyy");

			Font defaultFont = wb.getFontAt((short) 0);
			defaultFont.setFontHeightInPoints((short) 12);

			CellStyle boldStyle = wb.createCellStyle();
			Font boldFont = wb.createFont();
			boldFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
			boldFont.setFontHeightInPoints((short) 12);
			boldStyle.setFont(boldFont);

			Row row;
			Cell cell;

			int rowNumber = 0;
			Patient thisPatient = null;

			row = sheet.createRow((short) rowNumber);
			for (int i = 0; i < columns.length; i++) {
				cell = row.createCell(i);
				cell.setCellValue(columns[i]);
				cell.setCellStyle(boldStyle);
			}

			for (int questionnaireNumber = 0; questionnaireNumber < resultList
					.size(); questionnaireNumber++) {
				Questionnaire questionnaire = resultList
						.get(questionnaireNumber);
				if (thisPatient == null
						|| !(questionnaire.getPatientId() == thisPatient
								.getId())) {
					thisPatient = getPatientById(questionnaire.getPatientId(),
							patients);
					rowNumber++;
				}

				rowNumber++;
				List<QuestionnaireAnswer> answers = questionnaire.getAnswers();
				row = sheet.createRow((short) rowNumber);

				cell = row.createCell(0);
				cell.setCellValue(thisPatient.getId());
				cell = row.createCell(1);
				cell.setCellValue(thisPatient.getGender() == 0 ? "Male"
						: "Female");
				cell = row.createCell(2);
				cell.setCellValue(Integer.parseInt(yearSdf.format(thisPatient
						.getBirthday())));
				cell = row.createCell(3);
				cell.setCellValue(monthYearSdf.format(thisPatient
						.getDiagnoseDate()));

				for (int answerNum = 0; answerNum < answers.size(); answerNum++) {
					QuestionnaireAnswer questionnaireAnswer = answers
							.get(answerNum);
					cell = row
							.createCell(questionnaireAnswer.getQuestionId() + 3);

					Integer answer = questionnaireAnswer.getAnswer();
					String remark = questionnaireAnswer.getRemark();
					if (answer != null) {
						cell.setCellValue(answer);
					}
					if (remark != null) {
						ClientAnchor anchor = factory.createClientAnchor();
						anchor.setCol1(cell.getColumnIndex());
						anchor.setCol2(cell.getColumnIndex() + 5);
						anchor.setRow1(row.getRowNum());
						anchor.setRow2(row.getRowNum() + 3);

						Comment comment = drawing.createCellComment(anchor);
						RichTextString str = factory
								.createRichTextString(remark);
						comment.setString(str);
						cell.setCellComment(comment);
					}
				}
			}

			wb.write(response.getOutputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static Patient getPatientById(long id, List<Patient> patients) {
		Patient patient;
		for (int i = 0; i < patients.size(); i++) {
			patient = patients.get(i);
			if (patient.getId() == id) {
				return patient;
			}
		}
		return null;
	}
	

}
