package model;

import java.io.IOException;
import java.util.Random;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Coureur{
	int energie;
	int fatigue;
	int hydratation;
	int stockEau;
	int stockNourriture;
	char type;
	int vitesse;
	public int km;
	public boolean leader = false;
	String man;
	
	public Coureur(){
		int r = (Math.random()<0.5)?0:1;
		if(r==0)
			type='s';
		else if(r==1)
			type='g';
		km = 0;
		energie = new Random().nextInt(10)+10;
		fatigue = new Random().nextInt(10);
		hydratation = new Random().nextInt(10)+10;
		stockEau = 10;
		stockNourriture = 10;
		vitesse = new Random().nextInt(10)+50;
		
	}
		
	public void boire(){
		if(hydratation<10 && stockEau>0){
			hydratation+=5;
			stockEau--;
		}
	}
	
	public void manger(){
		if(energie<10 && stockNourriture>0){
			energie+=5;
			stockNourriture--;
		}
	}
	
	public void repos(){
		if(fatigue>20){
			vitesse-=2;
			fatigue--;
		}
	}
	
	public void avancer(){
		km+=km+(vitesse/3.6);
	}
	
	public void majVitesseP(int pente){
		if(type=='s')
			vitesse+=(pente*-0.01)*vitesse;
		if(type=='g')
			vitesse+=(pente*-0.005)*vitesse;
	}

	
	public void majVitesseC(int consigne){
			vitesse+=consigne;
	}
	
	public void ravitailler(){
		stockEau+=10;
		stockNourriture+=10;
	}
	
	public void ravitaillerVoiture(){
		stockEau+=10;
		stockNourriture+=10;
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

}
