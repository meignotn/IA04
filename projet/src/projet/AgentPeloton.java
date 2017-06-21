package projet;

import java.util.ArrayList;

import org.json.JSONObject;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import model.Coureur;
import model.Team;
import projet.AgentManager.sendConsigne;


public class AgentPeloton extends Agent{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public float position = 0 ;
	public boolean isActive;
	public ArrayList<Coureur> coureursInPeloton = new ArrayList<Coureur>();
	
	protected void setup() {
		dfRegister("AgentPeloton", "AgentPeloton");
		System.out.println("Manager Ready:"+this.getAID());
		addBehaviour(new waitStartBehaviour());


	}
	
	public class LifeOfPelotonBehaviour extends SequentialBehaviour{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		LifeOfPelotonBehaviour(){
			addSubBehaviour(new waitStartBehaviour());
			addSubBehaviour(new liveUntilEndBehaviour());
		}
	}
	
	private class waitStartBehaviour extends OneShotBehaviour{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			ACLMessage message = receive(mt);
			if (message != null) {	
				try{
					isActive = true;
					JSONObject json = new JSONObject(message.getContent());
					coureursInPeloton = Coureur.readAllRunners(json.getString("coureurs"));
				}catch(Exception e){
					System.out.println(e.getMessage());
				}

			} else
				block();
		}
	}
	
	private class liveUntilEndBehaviour extends CyclicBehaviour{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
			ACLMessage message = receive(mt);
			if (message != null) {
//				System.out.println(getLocalName()+"received request");
				try{
					//TODO
				}catch(Exception e){
					System.out.println(e.getMessage());
				}

			} else
				block();
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
