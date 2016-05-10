package com.sap.als.persistence;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@NamedQueries({
	@NamedQuery(name = "tokenByID", query = "select p from PasswordResetToken p where p.id = :tokenID" ),
	@NamedQuery(name = "tokenByMail", query = "select p from PasswordResetToken p where p.email = :email" )})

public class PasswordResetToken {

	 
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String email;
    private String token;
    private Timestamp created;
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public Timestamp getCreated() {
		return created;
	}
	public void setCreated(Timestamp created) {
		this.created = created;
	}
   

//	@OneToOne(targetEntity = Patient.class, fetch = FetchType.EAGER)
//    @JoinColumn(nullable = false, name = "id")
//    private Patient patient;
// 

	
}
