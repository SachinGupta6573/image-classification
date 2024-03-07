import javafx.stage.Stage;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.image.ImageView;
import javafx.geometry.Pos;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import java.io.File;
public class Image extends Application{

	public static void main(String[] args) {
		launch(args);
	}
	public void init(){


	}
	public void start(Stage stage){
		File currentImage=new File("file:///Users/lenovo/Desktop/d4.jpg");
		BorderPane root=new BorderPane();

		Scene scene=new Scene(root,600,500);
		scene.getStylesheets().add("image.css");

		//Crete menu bar
		MenuBar menubar=new MenuBar();

		//create menu file
		Menu file=new Menu("File");


		//create menu items
		MenuItem open=new MenuItem("Open");



		//add items to file
	    file.getItems().add(open);


        //create file chooser dialog
	    FileChooser chooser=new FileChooser();
	    chooser.setTitle("Open file");
	    //add filters
	    chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("ImageFiles","*.jpg","*.png"));

	    //add menus to menubar
		menubar.getMenus().add(file);


		ImageView imageContainer=new ImageView(currentImage.getAbsolutePath());
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
          imageContainer.setImage(new Image(fl));
          currentImage=fl;
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
        Text result=new Text("Dummy");

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
	public void stop(){

	}
}