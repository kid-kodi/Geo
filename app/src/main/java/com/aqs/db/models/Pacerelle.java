package com.aqs.db.models;

public class Pacerelle {

	int id;
	String code;
	String description;
	int geo_num;
	String created_at;
	String photo;

	// constructors
	public Pacerelle() {
	}

	public Pacerelle(String code, String description, int geo_num, String photo) {
		this.code = code;
		this.description = description;
		this.geo_num = geo_num;
		this.photo = photo;
	}

	public Pacerelle(int id, String code, String description, int geo_num, String photo) {
		this.id = id;
		this.code = code;
		this.description = description;
		this.geo_num = geo_num;
		this.photo = photo;
	}

	// setters
	public void setId(int id) {
		this.id = id;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setGeoNum(int geo_num) {
		this.geo_num = geo_num;
	}
	
	public void setCreatedAt(String created_at){
		this.created_at = created_at;
	}

	// getters
	public long getId() {
		return this.id;
	}

	public String getCode() {
		return this.code;
	}
    public String getPhoto() {
        return this.photo;
    }
	public String getDateCreated() {
		return this.created_at;
	}

	public String getDescription() {
		return this.description;
	}
	public long getGeoNum() {
		return this.geo_num;
	}
}
