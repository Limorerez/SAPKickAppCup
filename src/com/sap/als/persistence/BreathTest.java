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
		@NamedQuery(name = "AllBreathTests", query = "select s from BreathTest s"),
		@NamedQuery(name = "BreathTestById", query = "select s from BreathTest s where s.id = :id"),
		@NamedQuery(name = "LastBreathTestByPatientId", query = "select s from BreathTest s where s.created = (select max(s1.created) from BreathTest s1 where s1.patientId = s.patientId and s.patientId = :patientId)"),
	    @NamedQuery(name = "SubmittedBreathCountByPatinetId", query = "select count (q) from BreathTest q where q.patientId = :patientId "),
	    @NamedQuery(name = "BreathTestsByPatientId", query = "select s from BreathTest s where s.patientId = :patientId")})


public class BreathTest implements Serializable , ITest {

	private static final long serialVersionUID = 1L;

	public BreathTest() {
	}

	@Id
	@GeneratedValue
	private long id;
	private long patientId;
	private Timestamp created;	
	
	@OneToMany(cascade = CascadeType.ALL) 
	private List<BreathTestRecording> recordings;
	
	private String deviceModel;
	
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

	public List<BreathTestRecording> getRecordings() {
		return recordings;
	}

	public void setRecordings(List<BreathTestRecording> recordings) {
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