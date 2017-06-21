package projet;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import model.Voiture;

public class AgentVoiture extends Agent{

	private static final long serialVersionUID = 1L;
	private Voiture voiture;
	private String manager;
	
	protected void setup() {
		Object[] args = getArguments();
		manager = (String) args[0];
		dfRegister("AgentVoiture","AgentVoiture");
		System.out.println(this.getAID());
		addBehaviour(new subscribeBehaviour());
		
	}
	
	
	private class subscribeBehaviour extends OneShotBehaviour{
		
		private static final long serialVersionUID = 1L;

		@Override
		public void action() {

			ACLMessage messageManager = new ACLMessage(ACLMessage.SUBSCRIBE);
			messageManager.setContent(voiture.toJSON());
			messageManager.addReceiver(new AID(manager,AID.ISLOCALNAME));
			send(messageManager);
		}
	}
	
	public void dfRegister(String type,String name){
		DFAgentDescription dagent = new DFAgentDescription();
		dagent.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType(type);
		sd.setName(name);
		dagent.addServices(sd);
		try {
		 DFService.register(this, dagent);
		}
		catch (FIPAException fe) {
		 fe.printStackTrace();
		}
	}

}
