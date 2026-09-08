package com.itvedant.StudentManagement.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name="users")
public class Users {
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private long id;

	@Column(nullable=false,unique=true)
	private String userName;

	@Column(nullable=false)
	private boolean active ;

	@Column(nullable=false)
	private String password;

	@Column(name = "full_name")
	private String fullName;

	@Column(name = "email")
	private String email;

	@Column(name = "phone_number")
	private String phoneNumber;

	@Column(name = "role")
	private String role;

	@Lob
	@Column(name = "profile_image", columnDefinition = "LONGBLOB")
	private byte[] profileImage;

	@Column(name = "profile_image_content_type")
	private String profileImageContentType;

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public byte[] getProfileImage() {
		return profileImage;
	}
	public void setProfileImage(byte[] profileImage) {
		this.profileImage = profileImage;
	}
	public String getProfileImageContentType() {
		return profileImageContentType;
	}
	public void setProfileImageContentType(String profileImageContentType) {
		this.profileImageContentType = profileImageContentType;
	}
}