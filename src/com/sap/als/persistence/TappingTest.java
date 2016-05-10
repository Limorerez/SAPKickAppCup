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
		@NamedQuery(name = "AllTappingTests", query = "select w from TappingTest w"),
		@NamedQuery(name = "TappingTestById", query = "select w from TappingTest w where w.id = :id"),
		@NamedQuery(name = "LastTappingTestByPatientId", query = "select w from TappingTest w where w.created = (select max(w1.created) from TappingTest w1 where w1.patientId = w.patientId and w.patientId = :patientId)"),
		@NamedQuery(name = "SubmittedTappingCountByPatinetId", query = "select count (q) from TappingTest q where q.patientId = :patientId ")		
})
public class TappingTest implements Serializable, ITest  {

	private static final long serialVersionUID = 1L;

	public TappingTest() {
	}

	@Id
	@GeneratedValue
	private long id;
	private long patientId;
	private Timestamp created;
	

	@OneToMany(cascade = CascadeType.ALL)
	private List<TappingTestResult> tappingResults;
	
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
		this.created = new Timestamp(created);
	}
	public List<TappingTestResult> getTappingResults() {
		return tappingResults;
	}

	public void setTappingResults(List<TappingTestResult> tappingResults) {
		this.tappingResults = tappingResults;
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