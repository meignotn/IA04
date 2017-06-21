package projet;

import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

public class ProjetMain {
	private final int TEAMS_NUMBER = 2;
	public static void main(String[] args) {
		startWithProfile();
	}

	public static void startWithProfile() {
		Runtime rt = Runtime.instance();
		ProfileImpl p = null;
		ContainerController cc;
		/*try {
			p = new ProfileImpl(null, 12345, "projet", false);
			cc = rt.createAgentContainer(p);
			AgentController ac = cc.createNewAgent("WORLD", "src.projet.RaceWorld", null);
			ac.start();
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}*/
		try {
			p = new ProfileImpl(null, 12345, "projet", false);
			cc = rt.createAgentContainer(p);
			AgentController ac = cc.createNewAgent("WORLD", "projet.RaceWorld", null);
			ac.start();
			AgentController ac1 = cc.createNewAgent("MAN1", "projet.AgentManager", null);
			AgentController ac2 = cc.createNewAgent("MAN2", "projet.AgentManager", null);
			ac1.start();
			ac2.start();

			for(int i =0;i<10;i++){
				ac = cc.createNewAgent("MAN1__C"+i, "projet.AgentCoureur",new Object[]{new String("MAN1")} );
				ac.start();
			}
			for(int j =10;j<20;j++){
				ac = cc.createNewAgent("MAN2__C"+j, "projet.AgentCoureur",new Object[]{new String("MAN2")} );
				ac.start();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
}