package projet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class RaceWorld extends Agent{
	int tabPente[];
	//ArrayList<Coureur> ListCoureur;
	boolean end;
	
	public RaceWorld(){
		end=false;
		tabPente = new int[new Random().nextInt(200)+100];
		for(int i=0;i<tabPente.length;i++)
			tabPente[i]=new Random().nextInt(60);
	}
	
	protected void setup(){
		addBehaviour(new ReceiveSubcription());
		addBehaviour(new sendPente());
	}
	
	private class ReceiveSubcription extends CyclicBehaviour {
		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.SUBSCRIBE);
			ACLMessage message = receive(mt);
			if (message != null) {
			} else
				block();
		}
	}
	
	private class End extends CyclicBehaviour {
		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			ACLMessage message = receive(mt);
			if (message != null) {
				end = true;
			} else
				block();
		}
	}
	
	private class sendPente extends CyclicBehaviour {
		@Override
		public void action(){
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
			ACLMessage message = receive(mt);
			if (message != null) {
				int currentKM = Integer.parseInt(message.getContent());
				message.setContent(""+tabPente[currentKM]);
				System.out.println("world received:"+currentKM);
				message.clearAllReceiver();
				message.addReceiver((AID) message.getAllReplyTo().next());
				message.setPerformative(ACLMessage.INFORM);
				System.out.println("world sent:"+message.getContent());
				send(message);
			} else
				block();
		}
	}
}
