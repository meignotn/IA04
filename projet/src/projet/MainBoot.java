package projet;
import jade.core.ProfileImpl;
import jade.core.Runtime;

public class MainBoot {

	public static String MAIN_PROPERTIES_FILE = "profil";

	public static void main(String[] args) {
		boot_gui();
	}

	public static void boot_gui() {
		Runtime rt = Runtime.instance();
		ProfileImpl p = null;
		try {
			p = new ProfileImpl(MAIN_PROPERTIES_FILE);
			rt.createMainContainer(p);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}