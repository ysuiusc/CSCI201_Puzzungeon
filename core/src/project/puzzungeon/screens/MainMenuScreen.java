package project.puzzungeon.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import project.puzzungeon.Client;
import project.puzzungeon.Puzzungeon;
import project.server.LoginRegister;
import project.server.Password;
import project.server.Username;

//First screen; 

public class MainMenuScreen implements Screen{

	Puzzungeon game; //reference to the game
	private Stage stage;
	
	private Boolean displayDialog;
	private Dialog gameFullDialog;
	private Dialog connectionFailDialog;
	
	//constructor
	public MainMenuScreen(Puzzungeon game) {
		this.game = game;
		stage = new Stage();
		Gdx.input.setInputProcessor(stage);
		displayDialog = false;
	}
	
	//construct stage
	@Override
	public void show() {
		
		//simple layout:
		//       Game Title
		//
		//login   new user   guest
		//
		//                    exit
		
		//create the actors
		Label gameTitle = new Label("Puzzungeon", game.skin);
		
		TextButton loginButton = new TextButton("Login", game.skin, "default");
			loginButton.addListener(new ClickListener(){
				@Override 
		            public void clicked(InputEvent event, float x, float y){
						game.setScreen(new LoginScreen(game));
		            }
		        });
		
		TextButton newUserButton = new TextButton("New User", game.skin, "default");
			newUserButton.addListener(new ClickListener(){
				@Override 
	            	public void clicked(InputEvent event, float x, float y){
						game.setScreen(new RegisterScreen(game));
	            	}
	        	});
		TextButton guestButton = new TextButton("Login as Guest", game.skin, "default");
					guestButton.addListener(new ClickListener() {
						@Override
						public void clicked(InputEvent event, float x, float y) {
							game.client.clientUsername = "Guest";
              
							if(!game.client.connectState) {
								//set up connection to the server
								System.out.println("Trying to connect...");
								if(!game.client.connect()) {
									System.out.println("Unable to connect to the server");
									displayDialog = true;
									game.client = new Client(game.serverAddress, game.serverPort);
								}
								else {
									game.client.sendUsername(new Username("guest"));
									game.client.sendPassword(new Password("guest"));
									game.client.sendLoginRegister(new LoginRegister("guest"));
									displayDialog = true;
								}
							}
							else {
								game.client.sendUsername(new Username("guest"));
								game.client.sendPassword(new Password("guest"));
								game.client.sendLoginRegister(new LoginRegister("guest"));
								displayDialog = true;
							}
						}
					});
		TextButton exitButton = new TextButton("Exit", game.skin, "default");
			exitButton.addListener(new ClickListener(){
				@Override 
				public void clicked(InputEvent event, float x, float y){
					Gdx.app.exit();
					System.exit(0);
				}
			});
			
			
		gameFullDialog = new Dialog("Game is full.", game.skin, "dialog") {
			    public void result(Object obj) {}};

		gameFullDialog.text("We already have 2 players.");
		gameFullDialog.button("Got it", false); //sends "false" as the result
		
		
		connectionFailDialog = new Dialog("Connection failed", game.skin, "dialog") {
		    public void result(Object obj) {}};

		connectionFailDialog.text("Couldn't connect to the server");
		connectionFailDialog.button("Got it", false); //sends "false" as the result
			
		
		//use vg and hg to group the actors now. changes should be made to make it look better
		VerticalGroup vg = new VerticalGroup();
		vg.setFillParent(true);
		vg.addActor(gameTitle);
		
		HorizontalGroup buttonRow1 = new HorizontalGroup();
		buttonRow1.addActor(loginButton);
		buttonRow1.addActor(newUserButton);
		buttonRow1.addActor(guestButton);
		vg.addActor(buttonRow1);
		
		HorizontalGroup buttonRow2 = new HorizontalGroup();
		buttonRow2.addActor(exitButton);
		vg.addActor(buttonRow2);
		
		//add actors onto the stage
		stage.addActor(vg);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act(Gdx.graphics.getDeltaTime());
		checkClientLoginState();
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
		
	}

	@Override
	public void hide() {
		
	}

	@Override
	public void dispose() {

	}
	
	public void checkClientLoginState() {

		if(displayDialog == true) {
			
			if(game.client.loginState) {
				game.setScreen(new WaitingScreen(game));
			}
			
			if(!game.client.loginState) {
				if(game.client.loginStateMessage.equals("Game is Full.")) {
					game.client.loginStateMessage = "";
					gameFullDialog.show(stage);
					displayDialog = false;
				}
				if(!game.client.connectState) {
					connectionFailDialog.show(stage);
					displayDialog = false;
				}
			}
		}
	}
	
}
