package com.sap.als.persistence;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
@NamedQueries({
		@NamedQuery(name = "AllSpeechTests", query = "select s from SpeechTest s"),
		@NamedQuery(name = "SpeechTestById", query = "select s from SpeechTest s where s.id = :id"),
		@NamedQuery(name = "LastSpeechTestByPatientId", query = "select s from SpeechTest s where s.created = (select max(s1.created) from SpeechTest s1 where s1.patientId = s.patientId and s.patientId = :patientId)"),
		@NamedQuery(name = "SubmittedSpeechCountByPatinetId", query = "select count (q) from SpeechTest q where q.patientId = :patientId "),
		@NamedQuery(name = "SpeechTestsByPatientId", query = "select q from SpeechTest q where q.patientId = :patientId ")
})

public class SpeechTest implements Serializable, ITest {

	private static final long serialVersionUID = 1L;

	public SpeechTest() {
	}

	@Id
	@GeneratedValue
	private long id;
	private long patientId;
	private Timestamp created;	
	private String deviceModel;
	
	@OneToMany(cascade = CascadeType.ALL)
	private List<SpeechTestRecording> recordings;
	
	
	@OneToOne(optional=false, fetch=FetchType.EAGER)
	@JoinColumn(name="PATIENTID",referencedColumnName="ID",updatable=false,insertable=false)
	private Patient patientDetails;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getPatientId() {
		return patientId;
	}

	public void setPatientId(long patientId) {
		this.patientId = patientId;
	}

	public Timestamp getCreated() {
		return created;
	}

	public void setCreated(Timestamp created) {
		this.created = created;
	}

	public void setCreated(long created) {
		this.created = new Timestamp( created);
	}

	public List<SpeechTestRecording> getRecordings() {
		return recordings;
	}

	public void setRecordings(List<SpeechTestRecording> recordings) {
		this.recordings = recordings;
	}
	public Patient getPatientDetails() {
		return patientDetails;
	}

	public void setPatientDetails(Patient patientDetails) {
		this.patientDetails = patientDetails;
	}

	public String getDeviceModel() {
		return deviceModel;
	}

	public void setDeviceModel(String deviceModel) {
		this.deviceModel = deviceModel;
	}
}