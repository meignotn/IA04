package model;

import java.io.IOException;
import java.util.Random;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Coureur{
	int energie;
	int stockNourriture;
	char type;
	int vitesse;
	public float position;
	public boolean leader = false;
	
	public Coureur(){
		int r = (Math.random()<0.5)?0:1;
		if(r==0)
			type='s';
		else if(r==1)
			type='g';
		position = 0;
		energie = 100;
		stockNourriture = 100;
		vitesse = 0;
	}
	
	public void manger(){
		energie++;
		stockNourriture--;
	}
	
	
	public void avancer(int seconde){
		position+=vitesse*seconde/3600.0;
		if(energie==0)
			vitesse-=10;
		else if(energie<10){
			vitesse-=5;
		}else if(energie<20){
			vitesse-=4;
		}else if(energie<30){
			vitesse-=3;
		}
		else if(energie<40){
			vitesse-=2;
		}
		else if(energie<50){
			vitesse-=1;
		}
	}
	
	public void ravitailler(){
		stockNourriture=100;
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
	
	public static Coureur read(String jsonString) {
		ObjectMapper mapper = new ObjectMapper();
		Coureur p = null;
		try {
			p = mapper.readValue(jsonString, Coureur.class);
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

	public Coureur(int energie, int stockNourriture, char type, int vitesse,
			int position, boolean leader, String man, int seconde) {
		super();
		this.energie = energie;
		this.stockNourriture = stockNourriture;
		this.type = type;
		this.vitesse = vitesse;
		this.position = position;
		this.leader = leader;
	}

	public int getEnergie() {
		return energie;
	}

	public void setEnergie(int energie) {
		if(energie<0)
			this.energie=0;
		else if(energie>100)
			this.energie = 100;
		else
			this.energie = energie;
	}

	public int getStockNourriture() {
		return stockNourriture;
	}

	public void setStockNourriture(int stockNourriture) {
		this.stockNourriture = stockNourriture;
	}

	public char getType() {
		return type;
	}

	public void setType(char type) {
		this.type = type;
	}

	public int getVitesse() {
		return vitesse;
	}

	public void setVitesse(int vitesse) {
		this.vitesse = vitesse;
	}

	public float getPosition() {
		return position;
	}

	public void setPosition(float position) {
		this.position = position;
	}

	public boolean isLeader() {
		return leader;
	}

	public void setLeader(boolean leader) {
		this.leader = leader;
	}

}
