package com.xx.tool.vo.bean;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "config")
public class DBConfig {
	private String jdbcClass = "oracle.jdbc.OracleDriver";
	private String url = "jdbc:oracle:thin:@";
	private String user = "apps";
	private String password = "";

	@XmlElement(name = "jdbcClass")
	public String getJdbcClass() {
		return jdbcClass;
	}

	public void setJdbcClass(String jdbcClass) {
		this.jdbcClass = jdbcClass;
	}

	@XmlElement(name = "url")
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@XmlElement(name = "username")
	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	@XmlElement(name = "password")
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "DBConfig [jdbcClass=" + jdbcClass + ", url=" + url + ", user=" + user + ", password=" + password + "]";
	}

}
