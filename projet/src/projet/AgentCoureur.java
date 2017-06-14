package projet;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import model.Coureur;

public class AgentCoureur extends Agent{
	String man;
	Coureur c = new Coureur();
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
			/*ACLMessage messageRace = new ACLMessage(ACLMessage.SUBSCRIBE);
			messageRace.addReceiver(new AID("WORLD",AID.ISLOCALNAME));
			send(messageRace);*/
			ACLMessage messageManager = new ACLMessage(ACLMessage.SUBSCRIBE);
			messageManager.setContent(c.toJSON());
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
			message.setContent("s" + c.km);
			message.addReplyTo(getAID());
			send(message);
		}
	}
	
	private class getPente extends OneShotBehaviour {
		@Override
		public void action() {
			ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
			message.addReceiver(new AID("WORLD", AID.ISLOCALNAME));
			message.setContent("p" + c.km);
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
}
