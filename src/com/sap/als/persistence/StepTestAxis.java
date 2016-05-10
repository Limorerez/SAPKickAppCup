package com.sap.als.persistence;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@NamedQueries({
	@NamedQuery(name = "StepTestAxisById", query = "select a from StepTestAxis a where a.id = :id")
})
public class StepTestAxis implements Serializable {

	private static final long serialVersionUID = 1L;

	public StepTestAxis() {
	}

	@Id
	@GeneratedValue
	private long id;
	private Integer time;
	private Integer xAxis;
	private Integer yAxis;
	private Integer zAxis;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Integer getTime() {
		return time;
	}

	public void setTime(Integer time) {
		this.time = time;
	}

	public Integer getxAxis() {
		return xAxis;
	}

	public void setxAxis(Integer xAxis) {
		this.xAxis = xAxis;
	}

	public Integer getyAxis() {
		return yAxis;
	}

	public void setyAxis(Integer yAxis) {
		this.yAxis = yAxis;
	}

	public Integer getzAxis() {
		return zAxis;
	}

	public void setzAxis(Integer zAxis) {
		this.zAxis = zAxis;
	}

}