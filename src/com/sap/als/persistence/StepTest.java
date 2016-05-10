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
		@NamedQuery(name = "AllStepTests", query = "select s from StepTest s"),
		@NamedQuery(name = "StepTestById", query = "select s from StepTest s where s.id = :id"),
		@NamedQuery(name = "LastStepTestByPatientId", query = "select s from StepTest s where s.created = (select max(s1.created) from StepTest s1 where s1.patientId = s.patientId and s.patientId = :patientId)"),
		@NamedQuery(name = "SubmittedStepCountByPatinetId", query = "select count (q) from StepTest q where q.patientId = :patientId")		
})
public class StepTest implements Serializable, ITest {

	private static final long serialVersionUID = 1L;

	public StepTest() {
	}

	@Id
	@GeneratedValue
	private long id;
	private long patientId;
	private Integer stepsCount;
	private Timestamp created;

	@OneToMany(cascade = CascadeType.ALL)
	private List<StepTestAxis> axes;
	
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

	public Integer getStepsCount() {
		return stepsCount;
	}

	public void setStepsCount(Integer stepsCount) {
		this.stepsCount = stepsCount;
	}

	public List<StepTestAxis> getAxes() {
		return axes;
	}

	public void setAxes(List<StepTestAxis> axes) {
		this.axes = axes;
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