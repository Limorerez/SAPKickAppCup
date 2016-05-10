package com.sap.als.persistence;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class TappingDuration implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue
	private long id;
	
	private long ind;
	//the time in millies that the finger was on the screen
	private long tapOnDuration;
	//time between tapping - the time the finger was "in the air"
	private long tapOffDuration;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getTapOnDuration() {
		return tapOnDuration;
	}
	public void setTapOnDuration(long tapOnDuration) {
		this.tapOnDuration = tapOnDuration;
	}
	public long getTapOffDuration() {
		return tapOffDuration;
	}
	public void setTapOffDuration(long tapOffDuration) {
		this.tapOffDuration = tapOffDuration;
	}
	public long getIndex() {
		return ind;
	}
	public void setIndex(long index) {
		this.ind = index;
	}
	
	
}
