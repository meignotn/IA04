package affichage;


import java.util.Random;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;


public class carte extends Agent {
	protected void setup() {
		System.out.println(getLocalName() + "--> Installed");
		addBehaviour(new MultBehaviour());
		affiche.main();
	}


	@Override
	protected void takeDown() {
		System.out.println("---> "+getLocalName() + " : Good bye");
		try {
			DFService.deregister(this);
		} catch (FIPAException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private class MultBehaviour extends CyclicBehaviour {

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate
					.MatchPerformative(ACLMessage.INFORM);
			ACLMessage message = myAgent.receive(mt);
			if (message != null) {
				System.out.println(myAgent.getLocalName() + "--> "
						+ message.getContent());
				
				String [] msg =message.getContent().split(",");
				affiche.drawShapes(msg);

			} else
				block();
		}

	}

	
}
