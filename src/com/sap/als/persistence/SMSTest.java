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
		@NamedQuery(name = "AllSMSTests", query = "select s from SMSTest s"),
		@NamedQuery(name = "SMSTestById", query = "select s from SMSTest s where s.id = :id"),
		@NamedQuery(name = "LastSMSTestByPatientId", query = "select s from SMSTest s where s.created = (select max(s1.created) from SMSTest s1 where s1.patientId = s.patientId and s.patientId = :patientId)"),
		@NamedQuery(name = "SubmittedSMSCountByPatinetId", query = "select count (q) from SMSTest q where q.patientId = :patientId ")
	
})
public class SMSTest implements Serializable, ITest {

	private static final long serialVersionUID = 1L;

	public SMSTest() {
	}

	@Id
	@GeneratedValue
	private long id;
	private long patientId;
	private Timestamp created;

	@OneToMany(cascade = CascadeType.ALL)
	private List<SMSTestMessage> messages;
	
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

	public List<SMSTestMessage> getMessages() {
		return messages;
	}

	public void setMessages(List<SMSTestMessage> messages) {
		this.messages = messages;
	}
	public Patient getPatientDetails() {
		return patientDetails;
	}

	public void setPatientDetails(Patient patientDetails) {
		this.patientDetails = patientDetails;
	}

}