package com.se.parametric;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Reflection;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javax.swing.JOptionPane;
import com.se.parametric.dba.ParaQueryUtil;
import com.se.parametric.dto.GrmUserDTO;

/**
 * 
 * @web http://zoranpavlovic.blogspot.com/
 */
public class Login extends Application
{

	String userName, password;
	MainWindow frame;
	public static void main(String[] args)
	{
		launch(args);
	}

	@Override
	public void start(final Stage primaryStage)
	{
		primaryStage.setTitle("Login Form");

		BorderPane bp = new BorderPane();
		bp.setPadding(new Insets(10, 50, 50, 50));

		HBox hb = new HBox();
		hb.setPadding(new Insets(20, 20, 20, 30));

		GridPane gridPane = new GridPane();
		GridPane gridPane2 = new GridPane();
		gridPane.setPadding(new Insets(20, 20, 20, 20));
		gridPane.setHgap(2);
		gridPane.setVgap(2);

		Label lblUserName = new Label("           Username    ");
		final TextField txtUserName = new TextField();
		txtUserName.setText("Ayman Mohammad");
		Label lblPassword = new Label("           Password    ");
		Label label = new Label("             ");
		final PasswordField pf = new PasswordField();
		pf.setText("123456");
		Button btnLogin = new Button("Login");
		final Label lblMessage = new Label();

		// Adding Nodes to GridPane layout
		gridPane.add(lblUserName, 0, 0);
		gridPane.add(txtUserName, 1, 0);
		gridPane.add(lblPassword, 0, 1);
		gridPane.add(pf, 1, 1);
		gridPane2.add(label, 0, 0);
		gridPane2.add(btnLogin, 1, 0);
		gridPane.add(gridPane2, 1, 3);
		// gridPane.add(btnLogin, 1, 2);
		gridPane.add(lblMessage, 1, 2);
		// Reflection for gridPane
		Reflection r = new Reflection();
		r.setFraction(0.99f);
		gridPane.setEffect(r);
		txtUserName.setEffect(r);
		pf.setEffect(r);
		// DropShadow effect
		DropShadow dropShadow = new DropShadow();
		dropShadow.setOffsetX(5);
		dropShadow.setOffsetY(5);
		// Adding text and DropShadow effect to it
		Text text = new Text("Parametric Automation");
		text.setFont(Font.font("Courier New", FontWeight.BOLD, 24));
		text.setEffect(dropShadow);
		// Adding text to HBox
		hb.getChildren().add(text);
		// Add ID's to Nodes
		bp.setId("bp");
		gridPane.setId("root");
		btnLogin.setId("btnLogin");
		text.setId("text");

		btnLogin.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event)
			{
				Loading loading = new Loading();
				Thread thread = new Thread(loading);
				thread.start();
				userName = txtUserName.getText().toString();
				password = pf.getText().toString();
				GrmUserDTO grmUser = ParaQueryUtil.checkUser(userName, password);
				if(grmUser == null)
				{
					JOptionPane.showMessageDialog(null, "User Name or Password is Error");
				}
				else
				{

					 frame = new MainWindow();
					frame.setVisible(true);
					primaryStage.hide();
					Runtime.getRuntime().gc();
					frame.init(grmUser);
				}
				thread.stop();
				loading.frame.dispose();
				while(true){
					if(frame!=null)
					{	
						frame.updateFlags();						
						try
						{
							Thread.sleep(50000);
						}catch(InterruptedException e)
						{
							e.printStackTrace();
						}
					}
					}
			}
		});

		bp.setTop(hb);
		bp.setCenter(gridPane);
		Scene scene = new Scene(bp);
		scene.getStylesheets().add(getClass().getClassLoader().getResource("Login.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.show();
	
	}
}