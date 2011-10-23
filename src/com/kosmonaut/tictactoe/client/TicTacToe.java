package com.kosmonaut.tictactoe.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class TicTacToe implements EntryPoint {
	
	private static final int EMPTY = 0;
	private static final int X = 1;
	private static final int O = 2;
	
	private static final int PLAY = 0;
	private static final int WIN = 1;
	private static final int LOSE = 2;
	private static final int DRAW = 3;
	
	private static final String EMPTY_TEXT = "&nbsp;";
	private static final String X_TEXT = "X";
	private static final String O_TEXT = "O";
	
	private final TicServiceAsync ticService = GWT.create(TicService.class);
	
	final HTML[][] spaces = {{new HTML(EMPTY_TEXT), new HTML(EMPTY_TEXT), new HTML(EMPTY_TEXT)},
			   {new HTML(EMPTY_TEXT), new HTML(EMPTY_TEXT), new HTML(EMPTY_TEXT)},
			   {new HTML(EMPTY_TEXT), new HTML(EMPTY_TEXT), new HTML(EMPTY_TEXT)}};
	final Integer[][] board = {{EMPTY, EMPTY, EMPTY},
					   		{EMPTY, EMPTY, EMPTY},
					   		{EMPTY, EMPTY, EMPTY}};
	
	final HTML[] stats = {  new HTML(""), new HTML(""), new HTML("")};
	
	private int gameState = PLAY;
	private boolean myTurn = true;
	
	private final Anchor computerGo = new Anchor("let computer go first");
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		RootPanel.get().setStyleName("body");
		
		final FocusPanel boardPanel = new FocusPanel();
		
		RootPanel.get("board").add(boardPanel);
		boardPanel.setSize("300px", "300px");
		Grid boardGrid = new Grid(3, 3);
		boardPanel.setWidget(boardGrid);
		boardGrid.setSize("300px", "300px");
		boardGrid.setCellSpacing(0);
		boardGrid.setStyleName("grid");
		boardGrid.setWidget(0, 0, spaces[0][0]);
		boardGrid.setWidget(0, 1, spaces[0][1]);
		boardGrid.setWidget(0, 2, spaces[0][2]);
		boardGrid.setWidget(1, 0, spaces[1][0]);
		boardGrid.setWidget(1, 1, spaces[1][1]);
		boardGrid.setWidget(1, 2, spaces[1][2]);
		boardGrid.setWidget(2, 0, spaces[2][0]);
		boardGrid.setWidget(2, 1, spaces[2][1]);
		boardGrid.setWidget(2, 2, spaces[2][2]);

		Grid statsGrid = new Grid(2,3);
		statsGrid.setSize("300px", "50px");
		statsGrid.setWidget(0, 0, new HTML("wins:"));
		statsGrid.setWidget(0, 1, new HTML("losses:"));
		statsGrid.setWidget(0, 2, new HTML("draws:"));
		stats[0].setStyleName("statsdigits");
		stats[1].setStyleName("statsdigits");
		stats[2].setStyleName("statsdigits");
		statsGrid.setWidget(1, 0, stats[0]);
		statsGrid.setWidget(1, 1, stats[1]);
		statsGrid.setWidget(1, 2, stats[2]);
		RootPanel.get("stats").add(statsGrid);
		
		final Anchor resetButton = new Anchor("reset");
		resetButton.addClickHandler(
				new ClickHandler() {
					public void onClick(ClickEvent event){
						resetGame();
					}
				});
		resetButton.setStyleName("a");
		RootPanel.get("header").add(resetButton);
		
		computerGo.setStyleName("a");
		computerGo.addClickHandler(
				new ClickHandler() {
					public void onClick(ClickEvent event){
						myTurn = false;
						setDialogue("Hmm... I'm thinking...");
						doComputerTurn();
					}
				});
		
		resetGame();

		class BoardHandler implements ClickHandler, MouseMoveHandler {
			int lastx = 0;
			int lasty = 0;
			public void onMouseMove(MouseMoveEvent event){
				int x = ( event.getRelativeY(boardPanel.getElement()) / 100 ) ;
				int y = ( event.getRelativeX(boardPanel.getElement()) / 100 ) ;
				x = (x > 2 || x < 0) ? 0 : x;
				y = (y > 2 || y < 0) ? 0 : y;
				
				drawBoard();
				
				if(board[x][y] == EMPTY){
					spaces[x][y].setHTML("X");
					spaces[x][y].setStyleName("hoverpiece");
				}
				if(lastx != x || lasty != y){
					lastx = x;
					lasty = y;
				}
			}
			
			public void onClick(ClickEvent event) {
				if(board[lastx][lasty] == EMPTY && myTurn && gameState == PLAY){
					myTurn = false;
					board[lastx][lasty] = X;

					setDialogue("Hmm... I'm thinking...");
					drawBoard();
					updateState();
				}
			}
		}

		BoardHandler handler = new BoardHandler();
		boardPanel.addMouseMoveHandler(handler);
		boardPanel.addClickHandler(handler);
		
	}
	
	private void updateState() {
		ticService.getState(board,
				new AsyncCallback<Integer>() {
				public void onSuccess(Integer response){
					gameState = response;
					if(gameState == PLAY && myTurn == false){
						doComputerTurn();
					}else if(gameState == LOSE){
						setDialogue("You lose! :(");
					}else if(gameState == WIN){
						setDialogue("You win! :D");
					}else if(gameState == DRAW){
						setDialogue("Cat's game!");
					}
					
					// gameover happened, so update stats
					if(gameState != PLAY){
						updateStats(gameState);
					}
				}

				public void onFailure(Throwable e){
					setDebug("<h1>oops!</h1>" + e.toString());
					gameState = DRAW;
				}
			});
	}

	private void doComputerTurn(){
		
		ticService.getComputerMove(board,
				new AsyncCallback<Integer[]>() {
				public void onSuccess(Integer[] response){
					cpuMove(response[0], response[1]);
					myTurn = true;
					updateState();
					setDialogue("Your turn");
				}
				
				public void onFailure(Throwable e){
					setDebug("<h1>oops!</h1>" + e.toString());
				}
			});
		
	}


	
	protected void cpuMove(int x, int y) {
		board[x][y] = O;
		drawBoard();
	}
	
	private void updateStats(int gameResult) {
		ticService.updateStats(gameResult,
				new AsyncCallback<Void>() {
				public void onSuccess(Void v){
					getAndShowStats();
				}
				public void onFailure(Throwable e){
					setDebug("<h1>oops!</h1>" + e.toString());
				}
			});
		
	}
	
	private void getAndShowStats() {
		ticService.getStats(
				new AsyncCallback<Long[]>() {
				public void onSuccess(Long[] response){
					Long wins = response[0];
					Long losses = response[1];
					Long draws = response[2];
					stats[0].setHTML(wins.toString());
					stats[1].setHTML(losses.toString());
					stats[2].setHTML(draws.toString());
				}
				public void onFailure(Throwable e){
					setDebug("<h1>oops!</h1>" + e.toString());
				}
			});
		
	}

	private void drawBoard(){
		for(int i = 0; i<3; i++){
			for(int j = 0; j<3; j++){
				if(board[i][j] == EMPTY){
					spaces[i][j].setHTML(EMPTY_TEXT);
					spaces[i][j].setStyleName("empty");
				}else if(board[i][j] == X){
					spaces[i][j].setHTML(X_TEXT);
					spaces[i][j].setStyleName("piece");
				}else if(board[i][j] == O){
					spaces[i][j].setHTML(O_TEXT);
					spaces[i][j].setStyleName("piece");
				}
			}
		}
	}
	
	private void resetGame(){
		for(int i = 0; i<3; i++){
			for(int j = 0; j<3; j++){
				board[i][j] = EMPTY;
			}
		}
		myTurn = true;
		gameState = PLAY;
		drawBoard();
		
		FlowPanel diag = new FlowPanel();
		diag.add(computerGo);
		
		RootPanel.get("dialogue").clear();
		RootPanel.get("dialogue").add(diag);
		getAndShowStats();
	}
	
	private void setDialogue(String msg){
		RootPanel.get("dialogue").clear();
		RootPanel.get("dialogue").add(new HTML(msg));
	}
	private void setDebug(String msg){
		RootPanel.get("debug").clear();
		RootPanel.get("debug").add(new HTML(msg));
	}
}
