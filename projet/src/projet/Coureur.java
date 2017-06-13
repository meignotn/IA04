package projet;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class Coureur extends Agent{
	int energie;
	int fatigue;
	int hydratation;
	int stockEau;
	int stockNourriture;
	//int vitesseMax;
	char type;
	int vitesse;
	int km;
	public boolean leader = false;
	String man;
<<<<<<< HEAD
	int lieu;

	public Coureur() {
		int r = (Math.random() < 0.5) ? 0 : 1;
		if (r == 0)
			type = 's';
		if (r == 1)
			type = 'g';
		this.lieu = 0;
=======
	
	public Coureur(){
		int r = (Math.random()<0.5)?0:1;
		if(r==0)
			type='s';
		else if(r==1)
			type='g';
		km = 0;
		energie = 50;
		fatigue = 0;
		hydratation = 50;
		stockEau = 10;
		stockNourriture = 10;
		//vitesseMax = 15;
		vitesse = 50;
		
>>>>>>> e996a7d5f3cadb04970ed69faab86ece7a8b2721
	}
	
	protected void setup() {
		 Object[] args = getArguments();
		 man = (String) args[0];
		addBehaviour(new subscribeBehaviour());
//		addBehaviour(new receiveInfo());
//		addBehaviour(new getPente());
//		addBehaviour(new getNextSupplies());
	}
	
	private class subscribeBehaviour extends OneShotBehaviour{
		@Override
		public void action() {
<<<<<<< HEAD
			ACLMessage message = new ACLMessage(ACLMessage.SUBSCRIBE);
			message.addReceiver(new AID("WORLD", AID.ISLOCALNAME));
			message.setContent(toJSON());
			send(message);
=======
			ACLMessage messageRace = new ACLMessage(ACLMessage.SUBSCRIBE);
			messageRace.addReceiver(new AID("WORLD",AID.ISLOCALNAME));
			send(messageRace);
>>>>>>> e996a7d5f3cadb04970ed69faab86ece7a8b2721
			ACLMessage messageManager = new ACLMessage(ACLMessage.SUBSCRIBE);
			messageManager.addReceiver(new AID(man,AID.ISLOCALNAME));
			send(messageManager);
		}
	}
	
	private class receiveInfo extends CyclicBehaviour {

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			ACLMessage message = receive(mt);
			if (message != null) {
				String s = message.getContent().substring(1);
				System.out.println("km:"+km);
				if (message.getContent().charAt(0) == 's') {
					System.out.println("prochain rav:" + s);
				} else if (message.getContent().charAt(0) == 'p') {
					System.out.println("Pente actuelle:" + s);
					km++;
					addBehaviour(new getPente());
					addBehaviour(new getNextSupplies());

				}
			} else
				block();
		}

	}

	private class getNextSupplies extends OneShotBehaviour {
		@Override
		public void action() {
			ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
			message.addReceiver(new AID("WORLD", AID.ISLOCALNAME));
			message.setContent("s" + km);
			message.addReplyTo(getAID());
			send(message);
		}
	}
	
	private class getPente extends OneShotBehaviour {
		@Override
		public void action() {
			ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
			message.addReceiver(new AID("WORLD", AID.ISLOCALNAME));
			message.setContent("p" + km);
			message.addReplyTo(getAID());
			send(message);
		}

	}

	private class getConsigne extends CyclicBehaviour{
		@Override
		public void action(){
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			ACLMessage message = receive(mt);
			if(message != null){
				String s = message.getContent();
				
			} else {
				block();
			}
		}
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
<<<<<<< HEAD
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

=======
	
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
	
>>>>>>> e996a7d5f3cadb04970ed69faab86ece7a8b2721
}
