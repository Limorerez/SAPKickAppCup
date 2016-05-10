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
		@NamedQuery(name = "AllWritingTests", query = "select w from WritingTest w"),
		@NamedQuery(name = "WritingTestById", query = "select w from WritingTest w where w.id = :id"),
		@NamedQuery(name = "LastWritingTestByPatientId", query = "select w from WritingTest w where w.created = (select max(w1.created) from WritingTest w1 where w1.patientId = w.patientId and w.patientId = :patientId)"),
		@NamedQuery(name = "SubmittedWritingCountByPatinetId", query = "select count (q) from WritingTest q where q.patientId = :patientId "),
		@NamedQuery(name = "WritingTestByPatientId", query = "select w from WritingTest w where w.patientId = :patientId ")
})
public class WritingTest implements Serializable, ITest  {

	private static final long serialVersionUID = 1L;

	public WritingTest() {
	}

	@Id
	@GeneratedValue
	private long id;
	private long patientId;
	private Timestamp created;
	
	private String deviceModel;
	

	@OneToMany(cascade = CascadeType.ALL)
	private List<WritingTestDrawing> drawings;
	
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

	public List<WritingTestDrawing> getDrawings() {
		return drawings;
	}

	public void setDrawings(List<WritingTestDrawing> drawings) {
		this.drawings = drawings;
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