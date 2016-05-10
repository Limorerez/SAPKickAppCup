package com.sap.als.utils;

import java.sql.Timestamp;

public class Task {
	private String taskId;
	private Timestamp lastSubmittedDate;
	private long submittedCount;
	
	public String getTaskId() {
		return taskId;
	}
	
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	
	public Timestamp getLastSubmittedDate() {
		return lastSubmittedDate;
	}

	public void setLastSubmittedDate(Timestamp lastSubmittedDate) {
		this.lastSubmittedDate = lastSubmittedDate;
	}

	public long getSubmittedCount() {
		return submittedCount;
	}

	public void setSubmittedCount(long submittedCount) {
		this.submittedCount = submittedCount;
	}

}
