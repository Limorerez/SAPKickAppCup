package com.sap.als.persistence;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@NamedQueries({
	@NamedQuery(name = "CallTestCallById", query = "select c from CallTestCall c where c.id = :id")
})
public class CallTestCall implements Serializable {

	private static final long serialVersionUID = 1L;

	public CallTestCall() {
	}

	@Id
	@GeneratedValue
	private long id;
	private String callType;
	private Timestamp callDate;	
	private Integer duration;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	public String getCallType() {
		return callType;
	}

	public void setCallType(String callType) {
		this.callType = callType;
	}

	public Timestamp getCallDate() {
		return callDate;
	}

	public void setCallDate(Timestamp callDate) {
		this.callDate = callDate;
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer length) {
		this.duration = length;
	}

}