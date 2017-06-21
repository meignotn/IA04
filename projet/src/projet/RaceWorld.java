package projet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import model.Coureur;
import model.Team;


@SuppressWarnings("serial")
public class RaceWorld extends Agent{
	private final int TEAMS_NUMBER = 2;
	private final int RUNNERS_PER_TEAM = 10;
	private final long SECOND_PER_TICK=600;
	public static int dossard=1;
	int circuit[];
	Map<AID,Team> teams = new HashMap<AID,Team>();
	ArrayList<Team> teamList = new ArrayList<Team>();
	ArrayList<AID> teamAIDList = new ArrayList<AID>();
	Map<String,ArrayList<Integer>> podium = new HashMap();
	boolean isStarted = false;
	boolean end;
	int teamsReady = 0;
	int ravitaillement;
	
	public RaceWorld(){
		end=false;
		circuit = new int[new Random().nextInt(100)+150];
		for(int i=0;i<circuit.length;i++){
			circuit[i]=100;
			int r= new Random().nextInt(10)+1;
			int d = new Random().nextInt(2);
			if(d==0)
				d=-1;
			else
				d=1;
			circuit[i]=r*d;
		}
		if(circuit.length>200)
			ravitaillement = new Random().nextInt(50)+75;
		System.out.println("Race:"+circuit.length);
		System.out.println("Supplies:"+ravitaillement);

		/*for(int i=0;i<TEAMS_NUMBER;i++){
			Team team = new Team("Team"+i);
			for(int j=0;j<RUNNERS_PER_TEAM;j++){
				team.coureurs.add(new Coureur());
			}
			teamList.add(team);
		}*/
		for(int i=0;i<circuit.length;i++){
			System.out.print(circuit[i]+" ");
		}
	}

	
	
	protected void setup(){
		dfRegister("WORLD", "RaceWorld");
		System.out.println(getLocalName()+" created");
		/*for(int i=0;i<TEAMS_NUMBER;i++){
			try{
				AgentController ac = getContainerController().createNewAgent("MAN"+i, "projet.AgentManager", null);
				ac.start();
			}catch(Exception e){System.out.println(e.getMessage());}
		}*/
		addBehaviour(new WaitAndStardBehaviour());
	}	
	
	public class WaitAndStardBehaviour extends SequentialBehaviour{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		WaitAndStardBehaviour(){
			addSubBehaviour(new ReceiveSubcriptionBehaviour());
			addSubBehaviour(new SendCircuitBehaviour());
			addSubBehaviour(new StartRaceBehaviour());
			addBehaviour(new NextStep(getAgent(),500));
		}
	}
	
	
	private class ReceiveSubcriptionBehaviour extends Behaviour {
		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.SUBSCRIBE);
			ACLMessage message = receive(mt);

			Team team = new Team();
			if (message != null) {
				//System.out.println("AID baba" + message.getSender());
				//System.out.println("CONTENT" + message.getContent());
				//if(message.getContent().equals("TeamReady")){
				teamsReady++;
				teamAIDList.add(message.getSender());
				//System.out.println("MESSAGE =>>>" + message.getContent());
				
				team.coureurs = Coureur.readAllRunners(message.getContent());
				team.name = message.getSender().getName();
				teams.put(message.getSender(), team);
				
				System.out.println("Team" + teamsReady + "SUSCRIBED");
				//}
					

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
			//System.out.println("RACE STARTED");
			isStarted =  true;
			ACLMessage aclMessage =new ACLMessage(ACLMessage.INFORM);
			for(int i = 0; i < teamAIDList.size(); i++){
				aclMessage.addReceiver(teamAIDList.get(i));
				aclMessage.setContent("racestarted");
				send(aclMessage);
			}
		}
		
		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			return false;
		}
	}
	
	private class SendCircuitBehaviour extends OneShotBehaviour {
		@Override
		public void action() {
			System.out.println("SEND CIRCUIT");
			ACLMessage aclMessage =new ACLMessage(ACLMessage.AGREE);
			for(Entry<AID,Team> team : teams.entrySet()){
				for(Coureur c: team.getValue().getCoureurs()){
					ObjectMapper mapper = new ObjectMapper();
					String jsonObj = null;
					//Object to JSON in String
					try {
						jsonObj = mapper.writeValueAsString(circuit);
					} catch (JsonProcessingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					AID aidReceiver =getReceiver(c.id, "AgentCoureur");
					aclMessage.addReceiver(aidReceiver);
					aclMessage.setContent(jsonObj);
					send(aclMessage);

				}
				
			}
		}
		
	}
	
	/*private class ReceiveSubcriptionBehaviour extends Behaviour {
		
		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.SUBSCRIBE);
			ACLMessage message = receive(mt);
			if (message != null) {
				try{
//					System.out.println(getLocalName()+" sub received");
					JSONObject json = new JSONObject();
					teams.put(message.getSender(), teamList.get(teamsReady));
					json.put("circuit", circuit);
					json.put("supplies", ravitaillement);
					json.put("team", teamList.get(teamsReady).toJSON());
					teamsReady++;
					message.setContent(json.toString());
					message.removeReceiver((AID) message.getAllReceiver().next());
					message.addReceiver(message.getSender());
					send(message);
				}catch(Exception e){System.out.println(e.getMessage());}
			} else
				block();
		}
		
		@Override
		public boolean done() {
			return teamsReady == TEAMS_NUMBER;
		}
		public int onEnd(){
			for(Team t : teamList){
				podium.put(t.name, new ArrayList<Integer>());
			}
			addBehaviour(new updateTeam());
			addBehaviour(new nextStep(getAgent(),500));
			return super.onEnd();
		}
	}*/
	
	private class askManager extends OneShotBehaviour {
		AID manager;
		Team t;
		askManager(AID manager,Team t){
			this.manager=manager;
			this.t=t;
		}
		@Override
		public void action() {
			ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
//			System.out.println("sending request to manager");
			message.addReceiver(manager);
			message.setContent(t.toJSON());
			send(message);
		}
	}
	public boolean isFinished(){
		if(!isStarted)
			return false;
		for(Team t : teams.values()){
			System.out.println("TEAM===>" + t.name);
			for(Coureur c:t.getCoureurs())
				if(c.getPosition() < circuit.length){
					//System.out.println("COUREUR = " + c.id + " AT POSITION===>" + c.getPosition());
					return false;
				}
					
		}
		System.out.println("Race is over");
		return true;
	}
	
	private class updateTeam extends Behaviour {
		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			ACLMessage message = receive(mt);
			if (message != null) {
//				System.out.println(getLocalName()+"received request");
				Team t = Team.read(message.getContent());
				AID manager =null;
				
				for(Entry<AID, Team> e : teams.entrySet()){
					if(e.getValue().getName().equals(t.name)){
						manager = e.getKey();
						break;
					}
				}
				teams.put(manager, t);
				
			}else
				block();
		}

		@Override
		public boolean done() {
			return isFinished();
		}
	}
	
	private class NextStep extends TickerBehaviour{
		
		public NextStep(Agent a, long period) {
			super(a, period);
		}

		@Override
		protected void onTick() {
			for(Entry<AID,Team> team : teams.entrySet()){
				for(Coureur c: team.getValue().getCoureurs()){
					
					ACLMessage aclMessage =new ACLMessage(ACLMessage.CONFIRM);
					AID aidReceiver =getReceiver(c.id, "AgentCoureur");
					aclMessage.addReceiver(aidReceiver);
					aclMessage.setContent(String.valueOf(getTickCount()));
					send(aclMessage);
				
				}
				
			}
			if(isFinished()){
				this.stop();
				affClassement();
			}else{
				for(Entry<AID,Team> team : teams.entrySet()){
					addBehaviour(new askManager(team.getKey(), team.getValue()));
				}
			}
			
		}
	}
	int penteMoyenne(int a,int b){
		int somme=0;
		if(b>circuit.length)
			b=circuit.length;
		if(b-a==0)
			return circuit[a];
		for(int i=a;i<b;i++){
			somme+=circuit[i];
		}
		return somme/(b-a);
	}
	public void affClassement(){
		int min=0;
		String team = null;
		System.out.println("Individual Ranking");
		for(int k=0;k<TEAMS_NUMBER*RUNNERS_PER_TEAM;k++){
			for(Entry<String, ArrayList<Integer>> e:podium.entrySet()){
				for(int i:e.getValue()){
					if(team==null){
						team=e.getKey();
						min=i;
					}
					if(i<min){
						team=e.getKey();
						min=i;
					}
				}
			}
			System.out.println(team+"\t"+min/3600+"h"+min%3600/60+"m"+min%3600%60+"s");
			//System.out.println(team+"\t"+min);
			podium.get(team).remove((Integer)min);	
			team = null;
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
