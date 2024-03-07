import javafx.stage.Stage;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.geometry.Pos;
import java.io.OutputStream;
import java.net.Socket;
import java.net.InetAddress;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import java.io.File;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Alert;
import java.util.Scanner;
import java.io.PrintWriter;
import javafx.application.Platform;
import java.util.Date;
import java.io.IOException;
public class ImageClassification extends Application{
		String currentImage="select.png";
		Socket socket;
		OutputStream out;
		Scanner sc;
		Text result;
		boolean shouldContinue=true;
		Alert dialog=null;
		Process serverProccess;
		boolean inProcess=false;
		boolean notConnected=true;
	public static void main(String[] args) {
		launch(args);
	}
	public void init(){


	}
	public void start(Stage stage){
		dialog=new Alert(AlertType.NONE);
		dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

		BorderPane root=new BorderPane();

		Scene scene=new Scene(root,600,600);
		scene.getStylesheets().add("image.css");

		root.prefWidthProperty().bind(scene.widthProperty());
		root.prefHeightProperty().bind(scene.heightProperty());

		//Crete menu bar
		MenuBar menubar=new MenuBar();

		//create menu file
		Menu file=new Menu("_Connect");


		//create menu items for connection
		MenuItem open=new MenuItem("_Connect");
		MenuItem dis=new MenuItem("_Disconnect");





		//add items to file
	    file.getItems().addAll(open,dis);


        // create help menu 
        Menu about=new Menu("_Help");

        //create menu items for help 

        MenuItem report=new MenuItem("_Doc");
         

        //add items to menu help
        about.getItems().addAll(report);

        //create file chooser dialog
	    FileChooser chooser=new FileChooser();
	    chooser.setTitle("Open file");
	    //add filters
	    chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("ImageFiles","*.jpg","*.png"));

	    

	    //add menus to menubar
		menubar.getMenus().addAll(file,about);


		ImageView imageContainer=new ImageView(currentImage);
		imageContainer.setFitWidth(scene.getWidth());
        imageContainer.setPreserveRatio(true);
        imageContainer.setSmooth(true);
        //imageContainer.fitWidthProperty().bind(scene.widthProperty());

        
        //create select Button
        Button select =new Button("Select Image");
        select.getStyleClass().add("select-button");
        //add listener
        select.setOnAction((e)->{
          File fl=chooser.showOpenDialog(stage);
          if(fl!=null){
          	currentImage=fl.getAbsolutePath();
          	String image=currentImage.replace("\\","/");
          	image="file:///"+image.substring(3);
           System.out.println(image);
          imageContainer.setImage(new Image(image));
         
         }
   
        });

        //create Bottom to get result
        Button getResult=new Button("Classify");
        getResult.getStyleClass().add("result-button");



        //crete bottom bar
        HBox bottom =new HBox(select,getResult);
        bottom.getStyleClass().add("Bottom-container");
        bottom.setAlignment(Pos.CENTER);

        bottom.setSpacing(30.0);

        //crete text for result
        result=new Text("Dummy");

        //add listener on result button
        getResult.setOnAction((e)->{
        	if(currentImage.equals("select.png")){
        		result.setText("Select Image...");
        		Alert messingImage=new Alert(AlertType.NONE);
        		messingImage.setTitle("Messing Image");
        		messingImage.setContentText("Plaese select an Image if not selected.");
        		messingImage.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        		messingImage.showAndWait();
        		return;
        	}
        	if(inProcess){
        		result.setText("Previous Request is in process Plaese wait..");
        	}
        	result.setText("Processing...");
        	new Thread(()->{
        		String str="";
        		try{
        		if(socket!=null&&socket.isConnected()&&out!=null){
        		str=getProcessResult();
               final String strf=str;
                Platform.runLater(()->result.setText(strf));
              }else{
              	connectToTheServer();
              }
          }catch(Exception ex){
               ex.printStackTrace();
               final String strf=str;
                Platform.runLater(()->result.setText(strf));
          }
        	}).start();
              
        });


        open.setOnAction((e)->{
        	 connectToTheServer();

        });

        //crete vertical layout for bottom
        VBox bottomv=new VBox(result,bottom);
        bottomv.setAlignment(Pos.CENTER);


        

        //set image to center of root view
		root.setCenter(imageContainer);

		//set menubar to the top of root view
        root.setTop(menubar);

        //set bottom bar to the bottom of root view
		root.setBottom(bottomv);

        // set scene to the stage 
		stage.setScene(scene);



		// show the stage to the user
		stage.show();

	}
	public void connectToTheServer(){
		

		Platform.runLater(()->{
          dialog.setContentText("Plaese wait");
		 dialog.setOnCloseRequest((e)->{
		 	if(notConnected) e.consume();
		 });
		dialog.showAndWait();


		});
		shouldContinue=true;
		new Thread(()->{
		    String[] states={"Plaese wait.","Plaese wait..","Plaese wait..."};
		    int n=0;
			while(shouldContinue){
				final int i=n;
                Platform.runLater(()->dialog.setContentText(states[i]));
                n++;
                n%=3;
                try{
                Thread.sleep(1000);
            }catch(InterruptedException ex){
            	ex.printStackTrace();
            }
           }
		}).start();
		new Thread(()->{
        	if(socket==null||socket.isClosed()){
        	
        		tryToConnect();
        		
          }
         }).start();
	}

	public void tryToConnect(){
		try{
                  socket=new Socket(InetAddress.getLocalHost(),8888);
                   System.out.println("Connect to : "+socket.getInetAddress());
                   out=socket.getOutputStream();
                   out.write(currentImage.getBytes());
                   sc=new Scanner(socket.getInputStream());
                   sc.useDelimiter("`");
                     shouldContinue=false;

                     notConnected=false;
                     Platform.runLater(()->{
                        dialog.setContentText("Connected.");
                        
                     });
                     Thread.sleep(300);
                     Platform.runLater(()->dialog.close());
                     getProcessResult();
             }catch(Exception ex){
          	    ex.printStackTrace();
          	    notConnected=true;
          	    shouldContinue=false;
          	    Platform.runLater(()->{
                        dialog.setContentText("Soem error occurred.");
                        
                     });
          	   
           }
	}
	public String getProcessResult(){
		  inProcess=true;
		     String str="";
		     try{
			System.out.println("Sending.... : "+currentImage);
               out.write(currentImage.getBytes());
               str=sc.next();
               System.out.println("Recieved : "+str);
           }catch(Exception ex){
              ex.printStackTrace();
              socket=null;
              notConnected=true;
              Platform.runLater(()->{
              	dialog.setContentText("Somethong went wrong.\nConnection lost.\nRestart server.\nTry to connect to the server again.");
              	dialog.showAndWait();
              });
              System.out.println(str);
               
           }
           inProcess=false;
           return str;
               
               
	}
	public void stop(){

	}
}