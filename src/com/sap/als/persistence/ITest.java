package com.sap.als.persistence;

import java.sql.Timestamp;

public interface ITest {
	
	public Patient getPatientDetails();
	public void setPatientDetails(Patient p);
	public long getPatientId();
	public long getId();
	public Timestamp getCreated();

}
