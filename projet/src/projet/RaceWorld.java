package projet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import projet.AgentManager.ManageRaceBehaviour;
import projet.AgentManager.TeamIsReadyBehaviour;
import projet.AgentManager.WaitForStartBehaviour;
import projet.AgentManager.WaitRunnersBehaviour;

public class RaceWorld extends Agent{

	private static final long serialVersionUID = 1L;
	private final int TEAMS_NUMBER = 2;
	int tabPente[];
	//ArrayList<Coureur> ListCoureur;
	boolean end;
	int teamsReady = 0;
	int supplies[];
	public RaceWorld(){
		end=false;
		tabPente = new int[new Random().nextInt(200)+100];
		for(int i=0;i<tabPente.length;i++)
			if(new Random().nextInt(1)==0)
				tabPente[i]=new Random().nextInt(60);
			else
				tabPente[i]=new Random().nextInt(60)*-1;
		supplies = new int[tabPente.length/100];
		for(int i=0;i<supplies.length;i++){
			supplies[i]=i*100+new Random().nextInt(100);
		}
		System.out.println("Race:"+tabPente.length);
	}
	
	protected void setup(){
		addBehaviour(new WaitAndStardBehaviour());
		addBehaviour(new sendPente());
		addBehaviour(new sendNextSupplies());
	}
	
	public class WaitAndStardBehaviour extends SequentialBehaviour{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		WaitAndStardBehaviour(){
			addSubBehaviour(new ReceiveSubcriptionBehaviour());
			addSubBehaviour(new StartRaceBehaviour());
		}
	}
	
	
	private class ReceiveSubcriptionBehaviour extends Behaviour {
		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.SUBSCRIBE);
			ACLMessage message = receive(mt);
			if (message != null) {
				if(message.getContent().equals("TeamReady")){
					teamsReady++;
					System.out.println("Team" + teamsReady + "SUSCRIBED");
				}
					

			} else
				block();
		}
		
		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			return teamsReady == TEAMS_NUMBER;
		}
	}
	
	private class StartRaceBehaviour extends Behaviour {
		@Override
		public void action() {
			System.out.println("RACE STARTED");
		}
		
		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			return false;
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
			if (message != null && message.getContent().charAt(0)=='p') {
				int currentKM = Integer.parseInt(message.getContent().substring(1));
				message.setContent("p"+tabPente[currentKM]);
				message.clearAllReceiver();
				message.addReceiver((AID) message.getAllReplyTo().next());
				message.setPerformative(ACLMessage.INFORM);
				send(message);
			} else
				block();
		}
	}

	private class sendNextSupplies extends CyclicBehaviour {
		@Override
		public void action(){
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
			ACLMessage message = receive(mt);
			if (message != null && message.getContent().charAt(0)=='s') {
				int currentKM = Integer.parseInt(message.getContent().substring(1));
				message.setContent("s"+supplies[currentKM/100]);
				message.clearAllReceiver();
				message.addReceiver((AID) message.getAllReplyTo().next());
				message.setPerformative(ACLMessage.INFORM);
				send(message);
			} else
				block();
		}
	}
}
