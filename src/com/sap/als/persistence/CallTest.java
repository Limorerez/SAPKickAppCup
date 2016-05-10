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
		@NamedQuery(name = "AllCallTests", query = "select c from CallTest c"),
		@NamedQuery(name = "CallTestById", query = "select c from CallTest c where c.id = :id"),
		@NamedQuery(name = "LastCallTestByPatientId", query = "select c from CallTest c where c.created = (select max(c1.created) from CallTest c1 where c1.patientId = c.patientId and c.patientId = :patientId)"),
		@NamedQuery(name = "SubmittedCallCountByPatinetId", query = "select count (q) from CallTest q where q.patientId = :patientId ")
})
public class CallTest implements Serializable, ITest  {

	private static final long serialVersionUID = 1L;

	public CallTest() {
	}

	@Id
	@GeneratedValue
	private long id;
	private long patientId;
	private Timestamp created;

	@OneToMany(cascade = CascadeType.ALL)
	private List<CallTestCall> calls;
	
	@OneToOne(optional=false, fetch=FetchType.EAGER)
	@JoinColumn(name="PATIENTID",referencedColumnName="ID",updatable=false,insertable=false)
	private Patient patientDetails;

	
	public Patient getPatientDetails() {
		return patientDetails;
	}

	public void setPatientDetails(Patient patientDetails) {
		this.patientDetails = patientDetails;
	}

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

	public List<CallTestCall> getCalls() {
		return calls;
	}

	public void setCalls(List<CallTestCall> calls) {
		this.calls = calls;
	}

}