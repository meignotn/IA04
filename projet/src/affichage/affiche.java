package affichage;
 
import java.util.concurrent.TimeUnit;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
 
public class affiche extends Application {
 
    
	public static String[] message;
	public static double donne [][] = {{0.2, 10}, {0.2, -10},{0.2, 30},{0.1, -50},{0.3, 0}};
	public static double liste_courreur[][] = new double [10][8];;
	public static double taille = 1000;
	public static GraphicsContext gc;
	public static Stage primaryStage;
	
	public static void main() { 	
        launch(); // <-- fonction bloquante impossible de metre a jour l'image
    }
 
    @Override
    public void start(Stage Stage) {
    	primaryStage = Stage;
 
        primaryStage.setTitle( "course");
        Group root = new Group();
        Canvas canvas = new Canvas(1000, 400);
        gc = canvas.getGraphicsContext2D();
        root.getChildren().add(canvas);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        primaryStage.close();
        
    }

    static void drawShapes(String[] msg) {
    	boolean test=false;
    	double courreur = Double.parseDouble(msg[0]);
    	double position = Double.parseDouble(msg[1]);
		int pos=0;
    	for(int i = 0;liste_courreur [i][0]!=0; i++)
    	{
    		pos=i;    		
    		if (liste_courreur[i][0]==courreur)
    		{
    			test=true;
    			liste_courreur[i][1]=position;
    			break;
    		}
    		
    	}
    	if (!test)
    	{
    		double [] donnee = {courreur,position};
    		if (liste_courreur [0][0]==0)
    			pos=0;
    		else
    			pos=pos+1;
    		liste_courreur[pos]=donnee;
    	}
    	
    	double y_0=200 , x_0=0 ,x_1,y_1, sum;
		int j;
    	gc.setFill(Color.GREEN);
        gc.setStroke(Color.BLUE);
        gc.setLineWidth(2);
    	for(int i = 0; i < donne.length; i++)
        {

     	   x_1 = donne[i][0]*taille*Math.cos(donne[i][1]*2*Math.PI/360) + x_0;
     	   y_1 = -donne[i][0]*taille*Math.sin(donne[i][1]*2*Math.PI/360) + y_0;
     	   
     	   gc.strokeLine(x_0, y_0, x_1, y_1);
            
     	   x_0=x_1;
     	   y_0=y_1;    	  
     	   
        }
    	 gc.setStroke(Color.GREEN);
    	 gc.setFill(Color.GREEN);
    	for(int i = 0;liste_courreur[i][0]!= 0; i++){
    		System.out.print(liste_courreur[i][0]);
    		System.out.print("  ");
    		System.out.println(liste_courreur[i][1]);
    		sum=donne[0][0];
    		j=0;
    		x_0=0;
    		y_0=200-10;
    		test=true;
    		while  (test){ 
    			if (sum>=liste_courreur[i][1]){ 
    				x_0 = x_0 + (-liste_courreur[i][1]+sum)*taille*Math.cos(donne[j][1]*2*Math.PI/360);
    				y_0 = y_0 + (liste_courreur[i][1]-sum)*taille*Math.sin(donne[j][1]*2*Math.PI/360);
    				test=false;
    			}
    			else{
    				x_0 = x_0 +  donne[j][0]*taille*Math.cos(donne[j][1]*2*Math.PI/360);
    				y_0 = y_0 -  donne[j][0]*taille*Math.sin(donne[j][1]*2*Math.PI/360);
    				sum+=donne[j][0];
    				j+=1;
    			}
    			}
    			

        	gc.strokeOval(x_0, y_0, 10, 10);
    		} 	
    }
    	}
   
