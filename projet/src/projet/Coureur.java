package projet;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class Coureur extends Agent {
	int energie;
	int fatigue;
	int hydratation;
	int stockEau;
	int stockNourriture;
	int puissanceMax;
	char type;
	int vitesseNormale;
	int km = 0;
	int lieu;

	public Coureur() {
		int r = (Math.random() < 0.5) ? 0 : 1;
		if (r == 0)
			type = 's';
		if (r == 1)
			type = 'g';
		this.lieu = 0;
	}

	protected void setup() {
		addBehaviour(new subscribeBehaviour());
		addBehaviour(new getPente());
	}

	/*
	 * public void step(SimState arg0){ fatigue++; energie--; hydratation--;
	 * boire(); manger(); int p = getPente(); int c = getConsigne();
	 * majVitesse(p, c); }
	 */

	private class subscribeBehaviour extends OneShotBehaviour {
		@Override
		public void action() {
			ACLMessage message = new ACLMessage(ACLMessage.SUBSCRIBE);
			message.addReceiver(new AID("WORLD", AID.ISLOCALNAME));
			send(message);
		}
	}

	private class getPente extends Behaviour{
		int step=0;
		@Override
		public void action(){
			if(step==0){
				ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
				message.addReceiver(new AID("WORLD",AID.ISLOCALNAME));
				message.setContent(""+km);
				message.addReplyTo(getAID());
				send(message);
				System.out.println("coureur sent:"+message.getContent());
				step++;
			}else if(step==1){
				MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
				ACLMessage message = receive(mt);
				if(message != null){
					String s = message.getContent();
					System.out.println("coureur received:"+s);
					int pente = Integer.parseInt(s);
					majVitesseP(pente);
					step--;
					km++;
				}else
					block();
			} 
			
		}
		@Override
		public boolean done() {
			
			return km>10;
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
			vitesseNormale-=2;
			fatigue--;
		}
	}
	
	public void majVitesseP(int pente){
		if(type=='s')
			vitesseNormale+=pente;
		if(type=='g')
			vitesseNormale+=pente;
	}
	
	public void majVitesseC(int consigne){
			vitesseNormale+=consigne;
	}
	
}
