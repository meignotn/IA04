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
	public boolean leader = false;
	String man;
	int lieu;

	public Coureur(String man) {
		this.man=man;
		int r = (Math.random() < 0.5) ? 0 : 1;
		if (r == 0)
			type = 's';
		if (r == 1)
			type = 'g';
		this.lieu = 0;
	}
	
	protected void setup() {
		addBehaviour(new subscribeBehaviour());
//		addBehaviour(new receiveInfo());
//		addBehaviour(new getPente());
//		addBehaviour(new getNextSupplies());
	}
	
	private class subscribeBehaviour extends OneShotBehaviour {
		@Override
		public void action() {
			ACLMessage message = new ACLMessage(ACLMessage.SUBSCRIBE);
			message.addReceiver(new AID("WORLD", AID.ISLOCALNAME));
			send(message);
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

	private class getConsigne extends CyclicBehaviour {
		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			ACLMessage message = receive(mt);
			if (message != null) {
				String s = message.getContent();

			} else {
				block();
			}
		}
	}

	public void boire() {
		if (hydratation < 10 && stockEau > 0) {
			hydratation += 5;
			stockEau--;
		}
	}

	public void manger() {
		if (energie < 10 && stockNourriture > 0) {
			energie += 5;
			stockNourriture--;
		}
	}

	public void repos() {
		if (fatigue > 20) {
			vitesseNormale -= 2;
			fatigue--;
		}
	}

	public void majVitesseP(int pente) {
		if (type == 's')
			vitesseNormale += pente;
		if (type == 'g')
			vitesseNormale += pente;
	}

	public void majVitesseC(int consigne) {
		vitesseNormale += consigne;
	}

}
