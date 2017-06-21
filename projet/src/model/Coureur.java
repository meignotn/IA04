package model;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

import org.json.JSONArray;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jade.core.AID;
import projet.RaceWorld;

public class Coureur implements Serializable{
	int energie;
	int stockNourriture;
	char type;
	int vitesse;
	public String id;
	public boolean isRunning = false;
	public float position;
	public boolean leader = false;
	public int MAX_STOCK_NOURRITURE=5;
	public int MAX_ENERGIE =100;
	public int VITESSE_MAX=50;
	public int VITESSE_CROISIERE=40;
	public int dossard;
	
	public Coureur(){
		int r = (Math.random()<0.5)?0:1;
		if(r==0)
			type='s';
		else if(r==1)
			type='g';
		for(int i=0;i<10;i++){
			int test =new Random().nextInt(4);
			if(test==0)
				MAX_ENERGIE++;
			if(test==1)
				MAX_STOCK_NOURRITURE++;
			if(test==2)
				VITESSE_MAX++;
			if(test==3)
				VITESSE_CROISIERE++;
		}
		this.dossard = RaceWorld.dossard;
		RaceWorld.dossard++;
		position = 0;
		energie = 100;
		stockNourriture = 10;
		vitesse = 0;
		leader = false;
	}
	
	public int getMAX_STOCK_NOURRITURE() {
		return MAX_STOCK_NOURRITURE;
	}

	public void setMAX_STOCK_NOURRITURE(int mAX_STOCK_NOURRITURE) {
		MAX_STOCK_NOURRITURE = mAX_STOCK_NOURRITURE;
	}

	public int getMAX_ENERGIE() {
		return MAX_ENERGIE;
	}

	public void setMAX_ENERGIE(int mAX_ENERGIE) {
		MAX_ENERGIE = mAX_ENERGIE;
	}

	public int getVITESSE_MAX() {
		return VITESSE_MAX;
	}

	public void setVITESSE_MAX(int vITESSE_MAX) {
		VITESSE_MAX = vITESSE_MAX;
	}

	public int getVITESSE_CROISIERE() {
		return VITESSE_CROISIERE;
	}

	public void setVITESSE_CROISIERE(int vITESSE_CROISIERE) {
		VITESSE_CROISIERE = vITESSE_CROISIERE;
	}

	public int getDossard() {
		return dossard;
	}

	public void setDossard(int dossard) {
		this.dossard = dossard;
	}

	public void manger(){
		if(stockNourriture>0){
			setEnergie(energie+10);
			stockNourriture--;
		}
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
	
	public static ArrayList<Coureur> readAllRunners(String jsonString) {
		ObjectMapper mapper = new ObjectMapper();
		ArrayList<Coureur> list = new ArrayList<Coureur>();
		Coureur p = null;
		TypeReference<ArrayList<Coureur>> mapType = new TypeReference<ArrayList<Coureur>>() {};
		try {
			list = mapper.readValue(jsonString,mapType);
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
		return list;
	}

	

	public Coureur(int energie, int stockNourriture, char type, int vitesse, float position, boolean leader,
			int mAX_STOCK_NOURRITURE, int mAX_ENERGIE, int vITESSE_MAX, int vITESSE_CROISIERE, int dossard) {
		super();
		this.energie = energie;
		this.stockNourriture = stockNourriture;
		this.type = type;
		this.vitesse = vitesse;
		this.position = position;
		this.leader = leader;
		MAX_STOCK_NOURRITURE = mAX_STOCK_NOURRITURE;
		MAX_ENERGIE = mAX_ENERGIE;
		VITESSE_MAX = vITESSE_MAX;
		VITESSE_CROISIERE = vITESSE_CROISIERE;
		this.dossard = dossard;
	}

	public int getEnergie() {
		return energie;
	}

	public void setEnergie(int energie) {
		if(energie<0)
			this.energie=0;
		else if(energie>MAX_ENERGIE)
			this.energie = MAX_ENERGIE;
		else
			this.energie = energie;
	}

	public int getStockNourriture() {
		return stockNourriture;
	}

	public void setStockNourriture(int stockNourriture) {
		if(stockNourriture<=MAX_STOCK_NOURRITURE)
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
		if(vitesse > VITESSE_MAX){
			this.vitesse=VITESSE_MAX;
		}else
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
