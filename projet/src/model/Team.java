package model;

import java.io.IOException;
import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jade.core.AID;

public class Team{
	public String name;
	public ArrayList<Coureur> coureurs= new ArrayList<Coureur>();
	public ArrayList<Coureur> getCoureurs() {
		return coureurs;
	}

	public void setCoureurs(ArrayList<Coureur> coureurs) {
		this.coureurs = coureurs;
	}

	public Team(String name, ArrayList<Coureur> coureurs) {
		super();
		this.name = name;
		this.coureurs = coureurs;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Team(String name) {
		super();
		this.name = name;
	}
	
	public String toJSON() {
		ObjectMapper mapper = new ObjectMapper();
		String s = "";
		try {
			s = mapper.writeValueAsString(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return s;
	}
	
	public static Team read(String jsonString) {
		ObjectMapper mapper = new ObjectMapper();
		Team p = null;
		try {
			p = mapper.readValue(jsonString, Team.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return p;
	}

	public Team() {
		super();
	}
}
