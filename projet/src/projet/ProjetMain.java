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
			for(int i =0;i<10;i++){
				ac = cc.createNewAgent("C"+i, "projet.Coureur", null);
				ac.start();
			}
			ac.start();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}