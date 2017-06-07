package projet;

import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

public class ProjetMain {
	public static void main(String[] args) {
		startWithProfile();
	}

	public static void startWithProfile() {
		Runtime rt = Runtime.instance();
		ProfileImpl p = null;
		ContainerController cc;
		try {
			p = new ProfileImpl(null, 12345, "projet", false);
			cc = rt.createAgentContainer(p);
			AgentController ac = cc.createNewAgent("WORLD", "projet.RaceWorld", null);
			ac.start();
			AgentController ac1 = cc.createNewAgent("MAN1", "projet.AgentManager", null);
			AgentController ac2 = cc.createNewAgent("MAN2", "projet.AgentManager", null);
			ac1.start();
			ac2.start();

			for(int i =0;i<5;i++){
				ac = cc.createNewAgent("C"+i, "projet.Coureur",new String[]{"MAN1"} );
				ac.start();
			}
			for(int i =0;i<5;i++){
				ac = cc.createNewAgent("C"+i, "projet.Coureur",new String[]{"MAN2"} );
				ac.start();
			}
			ac.start();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}