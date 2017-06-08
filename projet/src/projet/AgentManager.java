package projet;


import java.io.IOException;
import java.util.ArrayList;

import com.fasterxml.jackson.databind.ObjectMapper;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;


public class AgentManager extends Agent{
	private final int TEAM_SIZE = 10;
	private ArrayList<Coureur> runners = new ArrayList<Coureur>();
	
	private int leader;
	private AID car = null;
	
	private boolean raceStarted = false;
	private boolean allRunnersDone = false;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected void setup() {
		dfRegister("AgentManager","AgentManager");
		System.out.println(this.getAID());
		addBehaviour(new ManageTeamBehaviour());
		
	}
	
	
	public class ManageTeamBehaviour extends SequentialBehaviour{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		ManageTeamBehaviour(){
			addSubBehaviour(new WaitRunnersBehaviour());
//			addSubBehaviour(new WaitCarBehaviour());
			addSubBehaviour(new TeamIsReadyBehaviour());
			addSubBehaviour(new WaitForStartBehaviour());
			addSubBehaviour(new ManageRaceBehaviour());
		}
	}
	
	
	public class WaitRunnersBehaviour extends Behaviour{
    	
		private static final long serialVersionUID = 1L;

		@Override
    	public void action() {
			System.out.println("WAITING FOR RUNNERS");
			
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.SUBSCRIBE);
			ACLMessage message = myAgent.receive(mt);
    		if(message != null){
    			if(message.getContent().equals("subscribeRunner")){
    				ObjectMapper mapper = new ObjectMapper();
    				Coureur newRunner =null;
    				try {
    					newRunner = mapper.readValue(message.getContent(), Coureur.class);
    				} catch (IOException e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				}
    				System.out.println("Runner suscribed for team"+getLocalName()+":"+newRunner.getAID());
    				runners.add(newRunner);
    				if(newRunner.leader)
    					leader = runners.indexOf(newRunner);
    			}
    		}
    		else
    			block();
    	}
    	@Override
    	public boolean done() {
    		return runners.size() == TEAM_SIZE;
    	}
	}
	
public class WaitCarBehaviour extends Behaviour{
    	
		private static final long serialVersionUID = 1L;

		@Override
    	public void action() {
			System.out.println("WAITING FOR THE CAR");
			
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
			ACLMessage message = myAgent.receive(mt);
    		if(message != null){
    			if(message.getContent().equals("subscribeCar")){
    				car = message.getSender();
    			}
    		}
    		else
    			block();
    	}
    	@Override
    	public boolean done() {
    		return car != null;
    	}
	}
	
	public class TeamIsReadyBehaviour extends Behaviour{

		private static final long serialVersionUID = 1L;

		@Override
		public void action() {
			ACLMessage aclMessage =new ACLMessage(ACLMessage.INFORM);
			AID aidReceiver = getReceiver("WORLD", "RaceWorld");
			aclMessage.addReceiver(aidReceiver);
			aclMessage.setContent("TeamReady");
		}

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			return false;
		}
		
	}
	
public class WaitForStartBehaviour extends Behaviour{
    	
		private static final long serialVersionUID = 1L;

		@Override
    	public void action() {
			System.out.println("WAITING FOR START OF THE RACE");
			
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			ACLMessage message = myAgent.receive(mt);
    		if(message != null){
    			if(message.getContent().equals("racestarted")){
    				raceStarted = true;
    			}
    		}
    		else
    			block();
    	}
    	@Override
    	public boolean done() {
    		return raceStarted;
    	}
	}

public class ManageRaceBehaviour extends Behaviour{

	@Override
	public void action() {
		for(Coureur r : runners){
			ACLMessage aclMessage =new ACLMessage(ACLMessage.REQUEST);
			AID aidReceiver = r.getAID();
			aclMessage.addReceiver(aidReceiver);
			aclMessage.setContent("40");
		}
	}

	@Override
	public boolean done() {
		return allRunnersDone;
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
	
	private AID getReceiver(String type, String name) {
		AID rec = null;
		DFAgentDescription template =
		new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType(type);
		sd.setName(name);
		template.addServices(sd);
		try {
		DFAgentDescription[] result =
		DFService.search(this, template);
		if (result.length > 0)
		rec = result[0].getName();
		} catch(FIPAException fe) {fe.printStackTrace();}
		return rec;
		}
	
}
