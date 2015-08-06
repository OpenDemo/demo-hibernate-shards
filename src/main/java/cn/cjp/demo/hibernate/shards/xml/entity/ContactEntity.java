package cn.cjp.demo.hibernate.shards.xml.entity;

import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "anno_contact")
public class ContactEntity implements ShardableEntity {

	@Id
	@GeneratedValue(generator = "EntityIdGenerator")
	@GenericGenerator(name = "EntityIdGenerator", strategy = "org.hibernate.shards.id.ShardedUUIDGenerator")
	@Column(name = "ID",columnDefinition="decimal(45) not null")
	private BigInteger id;

	@Column(name = "NAME")
	private String name;

	@Column(name = "EMAIL")
	private String email;

	@Column(name = "LOGINid")
	private String loginId;

	@Column(name = "PASSWORD")
	private String password;

	public ContactEntity() {
	}

	public ContactEntity(BigInteger id, String loginId, String password, String name,
			String email) {
		this(loginId, password, name, email);
		this.id = id;
	}
	public ContactEntity(String loginId, String password, String name,
			String email) {
		this.loginId = loginId;
		this.password = password;
		this.name = name;
		this.email = email;
	}

	public BigInteger getId() {
		return this.id;
	}

	public void setId(BigInteger id) {
		this.id = id;
	}

	public String getEMail() {
		return this.email;
	}

	public void setEMail(String email) {
		this.email = email;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLoginId() {
		return this.loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public BigInteger getIdentifier() {
		return this.id;
	}

	public String toString() {
		return "{ Id=\"" + this.id + "\"" + ", LoginId=\"" + this.loginId
				+ "\"" + ", Name=\"" + this.name + "\"" + ", EMail=\""
				+ this.email + "\" }";
	}
}