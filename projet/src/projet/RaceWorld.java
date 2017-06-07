package projet;

import java.util.ArrayList;
import java.util.Iterator;

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
	}
	
	protected void setup(){
		addBehaviour(new ReceiveSubcription());
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
			ACLMessage message = new ACLMessage(ACLMessage.INFORM);
			//message.setContent();
			//message.addReceiver();
			send(message);
		}
	}
}
