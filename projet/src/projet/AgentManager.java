package projet;

import java.util.Map;
import java.util.Random;

import org.json.JSONObject;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import model.Coureur;
import model.Team;


@SuppressWarnings("serial")
public class AgentManager extends Agent {
	private Team team=null;
	Map<Coureur,String> currentState;
	int[] circuit;
	int ravitaillement;
	//private AID carAid = null;

	protected void setup() {
		dfRegister("AgentManager", "AgentManager");
		System.out.println("Manager Ready:"+this.getAID());
		addBehaviour(new subscribeBehaviour());
		addBehaviour(new acceptTeam());
		//addBehaviour(new ManageTeamBehaviour());

	}
	private class subscribeBehaviour extends OneShotBehaviour{
		@Override
		public void action() {
			System.out.println(getLocalName()+" subscribing");
			ACLMessage messageRace = new ACLMessage(ACLMessage.SUBSCRIBE);
			messageRace.addReceiver(new AID("WORLD",AID.ISLOCALNAME));
			send(messageRace);
		}
	}
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
	
	public class sendConsigne extends OneShotBehaviour {
		@Override
		public void action(){
			// TODO HEURISTIC HERE
//			System.out.println("sending consigne");
			for(Coureur c:team.getCoureurs()){
				int vitesse = 0;
				if(c.getVitesse()==0)
					vitesse=40;
				else
					vitesse=c.getVitesse();
				do{
					if(Math.random()<0.5)
						vitesse =vitesse+new Random().nextInt(5);
					else
						vitesse =vitesse-new Random().nextInt(5);
				}while(vitesse>50 || vitesse<30);
				c.setVitesse(vitesse);
			}
			ACLMessage message = new ACLMessage(ACLMessage.INFORM);
			message.setContent(team.toJSON());
			message.addReceiver(new AID("WORLD",AID.ISLOCALNAME));
			send(message);
		}
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

	
}
