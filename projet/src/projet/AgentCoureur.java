package projet;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import model.Coureur;
import jade.core.behaviours.TickerBehaviour;

public class AgentCoureur extends Agent{
	String man;
	Coureur c = new Coureur();
	Boolean isSubscribed = false;
	int circuit[] = null;
	private final int SECOND_PER_TICK=600;
	protected void setup() {
		dfRegister(this.getAID().getLocalName(), "AgentCoureur");
		 c.id = this.getAID().getLocalName();
		 Object[] args = getArguments();
		 man = (String) args[0];
		 addBehaviour(new subscribeBehaviour());
		 addBehaviour(new getCircuitBehaviour());
		 addBehaviour(new waitStartBehaviour());
		 addBehaviour(new NextStep());
	    //addBehaviour(new receivefezeffzeInfo());
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
			isSubscribed = true;
		}
	}
	
	public class waitStartBehaviour extends Behaviour {

		private static final long serialVersionUID = 1L;

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			ACLMessage message = myAgent.receive(mt);
			if (message != null) {
				if (message.getContent().equals("racestarted")) {
					//System.out.println("RUNNER" + c.dossard + "START TO RUN");
					c.isRunning = true ;
				}
			} else
				block();
		}

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			return c.isRunning;
		}


	}
	
	public class getCircuitBehaviour extends Behaviour {

		private static final long serialVersionUID = 1L;

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.AGREE);
			ACLMessage message = myAgent.receive(mt);
			if (message != null) {
				
					ObjectMapper mapper = new ObjectMapper();
					
					try {
						circuit = mapper.readValue(message.getContent(), int[].class);
					} catch (JsonParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JsonMappingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				
			} else
				block();
		}

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			return circuit != null;
		}


	}
	
	public class NextStep extends Behaviour {

		private static final long serialVersionUID = 1L;

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CONFIRM);
			ACLMessage message = myAgent.receive(mt);
			//System.out.println("WAITING TICK");	
			if (message != null) {
				//if (message.getContent().equals("tick")) {
				int tickCount = Integer.parseInt(message.getContent());
					if(circuit != null && c.getVitesse()!=0 && c.getPosition()<circuit.length){
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
						System.out.println(c.id+"\t"+km+"km"+m+"\tEnergie:"+c.getEnergie()+"\tVitesse:"+c.getVitesse() +"\tTick:"+tickCount);
						
						
						//Send message to IHM
						ACLMessage aclMessage =new ACLMessage(ACLMessage.CONFIRM);
						AID aidReceiver =getReceiver("carte", "carte");
						aclMessage.addReceiver(aidReceiver);
						float avancee = c.position / circuit.length;
						aclMessage.setContent(String.valueOf(c.dossard) + "," +  String.valueOf(avancee));
						send(aclMessage);
						
						//Get time of the run
						if(c.getPosition()>=circuit.length){
							c.isRunning = false;
							int time = (tickCount-1) * SECOND_PER_TICK ;
							float distance = circuit.length-tmp_position;
							time = (int)(time + distance*3600.0/tmp_vitesse);
							//podium.get(team.getValue().getName()).add(time);
							System.out.println("A runner "+c.id+" finished in "+time+"s");
							
							aclMessage =new ACLMessage(ACLMessage.CONFIRM);
							aidReceiver =getReceiver("WORLD", "RaceWOrld");
							aclMessage.addReceiver(aidReceiver);
							aclMessage.setContent(String.valueOf(c.id) + "," +  time);
							send(aclMessage);
						}
					}
					//System.out.println("COUREUR = " + c.id + " AT POSITION===>" + c.getPosition());
					//System.out.println("RUNNER" + c.id + "RECEIVE TICKS");	
				
			} else
				block();
		}

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			return !c.isRunning && !isSubscribed;
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
			message.setContent("s" + c.position);
			message.addReplyTo(getAID());
			send(message);
		}
	}
	
	private class getPente extends OneShotBehaviour {
		@Override
		public void action() {
			ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
			message.addReceiver(new AID("WORLD", AID.ISLOCALNAME));
			message.setContent("p" + c.position);
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