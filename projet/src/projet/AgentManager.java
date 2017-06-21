package projet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jade.core.AID;
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
import model.Voiture;


@SuppressWarnings("serial")
public class AgentManager extends Agent {
	private final int TEAM_SIZE = 10;
	protected Team team=null;
	private int leader;
	Map<Coureur,String> currentState;
	int[] circuit;
	int ravitaillement;
	private ArrayList<model.Coureur> runners = new ArrayList<model.Coureur>();
	private ArrayList<AID> runnersAID = new ArrayList<AID>();
	
	private AID carAid = null;
	private Voiture car = null;
	private boolean raceStarted = false;
	private boolean allRunnersDone = false;

	protected void setup() {
		dfRegister("AgentManager", "AgentManager");
		System.out.println("Manager Ready:"+this.getAID());
		//addBehaviour(new subscribeBehaviour());
		addBehaviour(new ManageTeamBehaviour());
		//addBehaviour(new acceptTeam());

	}
	
	public class ManageTeamBehaviour extends SequentialBehaviour {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		ManageTeamBehaviour() {
			addSubBehaviour(new WaitRunnersBehaviour());
			// addSubBehaviour(new WaitCarBehaviour());
			addSubBehaviour(new TeamIsReadyBehaviour());
			addSubBehaviour(new WaitForStartBehaviour());
			addSubBehaviour(new ManageRaceBehaviour());
		}
	}

	public class WaitRunnersBehaviour extends Behaviour {

		private static final long serialVersionUID = 1L;

		@Override
    	public void action() {
			System.out.println("WAITING FOR RUNNERS" + this.getAgent().getName());
			
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.SUBSCRIBE);
			ACLMessage message = myAgent.receive(mt);
    		if(message != null){
    			
				ObjectMapper mapper = new ObjectMapper();
				Coureur newRunner =null;
				try {
					newRunner = mapper.readValue(message.getContent(), Coureur.class);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				runners.add(newRunner);
				runnersAID.add(message.getSender());
				System.out.println("COUREUR INSCRIT" + runners.size());
				if(newRunner.leader)
					leader = runners.indexOf(newRunner);
    			
    		}
    		else
    			block();
    	}
    	@Override
    	public boolean done() {
    		return runners.size() == TEAM_SIZE;
    	}

	}

	public class WaitCarBehaviour extends Behaviour {

		private static final long serialVersionUID = 1L;

		@Override
		public void action() {
			System.out.println("WAITING FOR THE CAR");

			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
			ACLMessage message = myAgent.receive(mt);

    		if(message != null){
    			
				carAid = message.getSender();
				ObjectMapper mapper = new ObjectMapper();
				Voiture voiture =null;
				try {
					voiture = mapper.readValue(message.getContent(), Voiture.class);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				car = voiture;
    			
    		}
    		else
    			block();
    	}
    	@Override
    	public boolean done() {
    		return car != null;
    	}

	}

	public class TeamIsReadyBehaviour extends OneShotBehaviour {

		private static final long serialVersionUID = 1L;

		@Override
		public void action() {

			ACLMessage aclMessage =new ACLMessage(ACLMessage.SUBSCRIBE);
			ObjectMapper objectMapper = new ObjectMapper();
			AID aidReceiver = getReceiver("WORLD", "RaceWorld");
			aclMessage.addReceiver(aidReceiver);
			
			try {
				String arrayToJson = objectMapper.writeValueAsString(runners);
				aclMessage.setContent(arrayToJson);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			send(aclMessage);
		}



	}

	public class WaitForStartBehaviour extends Behaviour {

		private static final long serialVersionUID = 1L;

		@Override
		public void action() {
			System.out.println("WAITING FOR START OF THE RACE");

			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			ACLMessage message = myAgent.receive(mt);
			if (message != null) {
				if (message.getContent().equals("racestarted")) {
					raceStarted = true;
					for(int i = 0; i < runnersAID.size(); i++){
						//System.out.println("ON INFORME " + runnersAID.get(i));
						ACLMessage aclMessage =new ACLMessage(ACLMessage.INFORM);
						AID aidReceiver = runnersAID.get(i);
						aclMessage.addReceiver(aidReceiver);
						aclMessage.setContent("racestarted");
						send(aclMessage);
					}
					System.out.println("LET'S GOOOOOOO");
				}
			} else
				block();
		}

		@Override
		public boolean done() {
			return raceStarted;
		}
	}
	
	/*public class sendConsigne extends CyclicBehaviour {
		@Override
		public void action(){
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
			ACLMessage message = receive(mt);
			if (message != null && message.getContent().charAt(0)=='c') {
				message.setContent("c");
				message.clearAllReceiver();
				message.addReceiver((AID) message.getAllReplyTo().next());
				message.setPerformative(ACLMessage.INFORM);
				send(message);
			} else
				block();
		}
	}*/

	public class ManageRaceBehaviour extends Behaviour {

		@Override
		public void action() {
			for (Coureur r : runners) {
				ACLMessage aclMessage = new ACLMessage(ACLMessage.REQUEST);
				// AID aidReceiver = r.getAID();
				// aclMessage.addReceiver(aidReceiver);
				aclMessage.setContent("40");
			}
		}

		@Override
		public boolean done() {
			return allRunnersDone;
		}

	}
	
	/*private class subscribeBehaviour extends OneShotBehaviour{
		@Override
		public void action() {
			System.out.println(getLocalName()+" subscribing");
			ACLMessage messageRace = new ACLMessage(ACLMessage.SUBSCRIBE);
			messageRace.addReceiver(new AID("WORLD",AID.ISLOCALNAME));
			send(messageRace);
		}
	}*/
	
	private class acceptTeam extends Behaviour{
		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.SUBSCRIBE);
			ACLMessage message = receive(mt);
			if (message != null) {
				try{
					System.out.println(getLocalName()+" accepting team");
					JSONObject json = new JSONObject(message.getContent());
					team = Team.read(json.getString("team"));
					circuit = new int[json.getJSONArray("circuit").length()];
					for(int i=0;i<json.getJSONArray("circuit").length();i++){
						circuit[i]=json.getJSONArray("circuit").getInt(i);
					}
					ravitaillement=json.getInt("supplies");
					
				}catch(Exception e){
					System.out.println(e.getMessage());
				}

			} else
				block();
		}
		@Override
		public boolean done() {
			return team!=null;
		}
		
		public int onEnd(){
			System.out.println("Team "+team.getName()+" suscribed to his manager");
			addBehaviour(new receiveRequest());
			return super.onEnd();
		}
	}

	private class receiveRequest extends Behaviour{
		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
			ACLMessage message = receive(mt);
			if (message != null) {
//				System.out.println(getLocalName()+"received request");
				try{
					team = Team.read(message.getContent());
					addBehaviour(new sendConsigne());
				}catch(Exception e){
					System.out.println(e.getMessage());
				}

			} else
				block();
		}
		@Override
		public boolean done() {
			return isFinished();
		}
	}
	
	public class sendConsigne extends OneShotBehaviour{
		
		@Override
		public void action(){
			for(Coureur c:team.getCoureurs()){
				int vitesse = 0;
				if(c.getVitesse()==0)
					vitesse=c.getVITESSE_CROISIERE();
				else
					vitesse=c.getVitesse();
				if(c.getEnergie()<50){
					c.manger();
				}
				vitesse+=c.getEnergie()-50/10;
				vitesse+=penteMoyenne((int)c.getPosition())*-1;
				c.setVitesse(vitesse);

			}
			ACLMessage message = new ACLMessage(ACLMessage.INFORM);
			message.setContent(team.toJSON());
			message.addReceiver(new AID("WORLD",AID.ISLOCALNAME));
			send(message);
		}
	}
	public int penteMoyenne(int a){
		int somme=0;
		for(int i=a;i<a+50 && i<circuit.length;i++){
			somme+=circuit[i];
		}
		return somme/(50);
	}

	public boolean isFinished(){
		for(Coureur c:team.getCoureurs())
			if(c.getPosition() < circuit.length)
				return false;
		System.out.println(team.name +" has finished");
		return true;
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
	
	private AID getReceiver(String type, String name) {
		AID rec = null;
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType(type);
		sd.setName(name);
		template.addServices(sd);
		try {
			DFAgentDescription[] result = DFService.search(this, template);
			if (result.length > 0)
				rec = result[0].getName();
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
		return rec;
	}

	
}
