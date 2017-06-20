package projet;

import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.json.JSONObject;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.AgentController;
import model.Coureur;
import model.Team;

@SuppressWarnings("serial")
public class RaceWorld extends Agent{
	private final int TEAMS_NUMBER = 3;
	private final int RUNNERS_PER_TEAM = 3;
	private final int SECOND_PER_TICK=600;
	public static int dossard=1;
	int circuit[];
	Map<AID,Team> teams = new HashMap<AID,Team>();
	ArrayList<Team> teamList = new ArrayList<Team>();
	Map<String,ArrayList<Integer>> podium = new HashMap();
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

		for(int i=0;i<TEAMS_NUMBER;i++){
			Team team = new Team("Team"+i);
			for(int j=0;j<RUNNERS_PER_TEAM;j++){
				team.coureurs.add(new Coureur());
			}
			teamList.add(team);
		}
		for(int i=0;i<circuit.length;i++){
			System.out.print(circuit[i]+" ");
		}
	}

	
	
	protected void setup(){
		
		System.out.println(getLocalName()+" created");
		for(int i=0;i<TEAMS_NUMBER;i++){
			try{
				AgentController ac = getContainerController().createNewAgent("MAN"+i, "projet.AgentManager", null);
				ac.start();
			}catch(Exception e){System.out.println(e.getMessage());}
		}
		addBehaviour(new ReceiveSubcriptionBehaviour());
	}	
	
	private class ReceiveSubcriptionBehaviour extends Behaviour {
		
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
	}
	
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
		for(Team t : teams.values()){
			for(Coureur c:t.getCoureurs())
				if(c.getPosition() < circuit.length)
					return false;
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
	
	private class nextStep extends TickerBehaviour{
		
		public nextStep(Agent a, long period) {
			super(a, period);
		}

		@Override
		protected void onTick() {
			for(Entry<AID,Team> team : teams.entrySet()){
				for(Coureur c: team.getValue().getCoureurs()){
					if(c.getVitesse()!=0 && c.getPosition()<circuit.length){
						int tmp_vitesse = c.getVitesse();
						float tmp_position = c.getPosition();
						int energierelief = penteMoyenne((int)c.getPosition(), (int)(c.getPosition()+c.getVitesse()*5.0/60.0));
						// Les grimpeurs s'epuisent moins dans les montées
						if(c.getType()=='g')
							if(energierelief>4)
								energierelief = 4;
						c.setEnergie(c.getEnergie()-c.getVitesse()+c.getVITESSE_CROISIERE()-energierelief);
						c.avancer(SECOND_PER_TICK);
						int km=(int)c.getPosition();
						int m = (int)((c.getPosition()-(int)c.getPosition())*1000.0);
						System.out.println(team.getValue().getName()+"\t"+km+"km"+m+"\tEnergie:"+c.getEnergie()+"\tVitesse:"+c.getVitesse() +"\tTick:"+getTickCount());
						
						
						//Get time of the run
						if(c.getPosition()>=circuit.length){
							int time = (getTickCount()-1) * SECOND_PER_TICK ;
							float distance = circuit.length-tmp_position;
							time = (int)(time + distance*3600.0/tmp_vitesse);
							podium.get(team.getValue().getName()).add(time);
							System.out.println("A runner of "+team.getValue().getName()+"finished in "+time+"s");
						}
					}
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
		String s= "";
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
	
}
