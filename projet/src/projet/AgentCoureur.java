package projet;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import model.Coureur;

public class AgentCoureur extends Agent{
	String man;
	Coureur c = new Coureur();
	Boolean isSubscribed = false;
	protected void setup() {
		dfRegister(this.getAID().getLocalName(), "AgentCoureur");
		 c.id = this.getAID().getLocalName();
		 Object[] args = getArguments();
		 man = (String) args[0];
		 addBehaviour(new subscribeBehaviour());
		 addBehaviour(new waitStartBehaviour());
		 addBehaviour(new NextStep());
	    //addBehaviour(new receivefezeffzeInfo());
//		addBehaviour(new getPente());
//		addBehaviour(new getNextSupplies());
	}
	
	private class subscribeBehaviour extends OneShotBehaviour{
		@Override
		public void action() {
			/*ACLMessage messageRace = new ACLMessage(ACLMessage.SUBSCRIBE);
			messageRace.addReceiver(new AID("WORLD",AID.ISLOCALNAME));
			send(messageRace);*/
			ACLMessage messageManager = new ACLMessage(ACLMessage.SUBSCRIBE);
			messageManager.setContent(c.toJSON());
			messageManager.addReceiver(new AID(man,AID.ISLOCALNAME));
			send(messageManager);
			isSubscribed = true;
		}
	}
	
	public class waitStartBehaviour extends Behaviour {

		private static final long serialVersionUID = 1L;

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			ACLMessage message = myAgent.receive(mt);
			if (message != null) {
				if (message.getContent().equals("racestarted")) {
					System.out.println("RUNNER" + c.dossard + "START TO RUN");
					c.isRunning = true ;
				}
			} else
				block();
		}

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			return c.isRunning;
		}


	}
	
	public class NextStep extends Behaviour {

		private static final long serialVersionUID = 1L;

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			ACLMessage message = myAgent.receive(mt);
			System.out.println("WAITING TICK");	
			if (message != null) {
				if (message.getContent().equals("tick")) {
					System.out.println("RUNNER" + c.id + "RECEIVE TICKS");	
				}
			} else
				block();
		}

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			return !c.isRunning && !isSubscribed;
		}


	}
	
	private class receiveInfo extends CyclicBehaviour {

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			ACLMessage message = receive(mt);
			if (message != null) {
				String s = message.getContent().substring(1);
				if (message.getContent().charAt(0) == 's') {
					System.out.println("prochain rav:" + s);
				} else if (message.getContent().charAt(0) == 'p') {
					System.out.println("Pente actuelle:" + s);
					addBehaviour(new getPente());
					addBehaviour(new getNextSupplies());
				} else if (message.getContent().charAt(0) == 'c') {
					addBehaviour(new getConsigne());
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
			message.setContent("s" + c.position);
			message.addReplyTo(getAID());
			send(message);
		}
	}
	
	private class getPente extends OneShotBehaviour {
		@Override
		public void action() {
			ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
			message.addReceiver(new AID("WORLD", AID.ISLOCALNAME));
			message.setContent("p" + c.position);
			message.addReplyTo(getAID());
			send(message);
		}

	}

	private class getConsigne extends CyclicBehaviour {
		@Override
		public void action(){
			ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
			message.addReceiver(new AID(man, AID.ISLOCALNAME));
			message.addReplyTo(getAID());
			send(message);
		}
	}
	
	public void dfRegister(String type, String name) {
		DFAgentDescription dagent = new DFAgentDescription();
		dagent.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType(type);
		sd.setName(name);
		dagent.addServices(sd);
		try {
			DFService.register(this, dagent);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
	}
}