package com.kosmonaut.tictactoe.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import com.kosmonaut.tictactoe.client.TicService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class TicServiceImpl extends RemoteServiceServlet implements TicService {

	private static final long serialVersionUID = 1L;

	private static final int EMPTY = 0;
	private static final int X = 1;
	private static final int O = 2;
	
	private static final int PLAY = 0;
	private static final int WIN = 1;
	private static final int LOSE = 2;
	private static final int DRAW = 3;
	
	private static final PersistenceManagerFactory PMF =
	      JDOHelper.getPersistenceManagerFactory("transactions-optional");
	
	private void createStats(){
	    PersistenceManager pm = getPersistenceManager();
	    try {
	      pm.makePersistent(new Stats());
	    } finally {
	      pm.close();
	    }
	}
	
	public void updateStats(Integer outcome){
	    PersistenceManager pm = getPersistenceManager();
	    try {
	      Query q = pm.newQuery(Stats.class);
	      List<Stats> stats = (List<Stats>) q.execute();
	      if(stats.isEmpty()){
	    	  createStats();
		      stats = (List<Stats>) q.execute();
	      }
	      if(outcome == WIN) stats.get(0).incWins();
	      else if(outcome == LOSE) stats.get(0).incLosses();
	      else if(outcome == DRAW) stats.get(0).incDraws();
	    } finally {
	      pm.close();
	    }
	}
	
	public Long[] getStats(){
	    PersistenceManager pm = getPersistenceManager();
	    Long[] result = new Long[3];
	    try {
	      Query q = pm.newQuery(Stats.class);
	      List<Stats> stats = (List<Stats>) q.execute();
	      if(stats.isEmpty()){
	    	  createStats();
		      stats = (List<Stats>) q.execute();
	      }
	      result[0] = stats.get(0).getWins();
	      result[1] = stats.get(0).getLosses();
	      result[2] = stats.get(0).getDraws();
	    } finally {
	      pm.close();
	    }
	    return result;
	}
	
	public Integer[] getComputerMove(Integer[][] board) {
		List<Integer[]> moves = getMoves(board);
		Integer[] response = {1,1};
		int bestutil = -100000000;
		for(Integer[] move : moves){
			Integer[][] brd = doAMove(move, board, O);
			int moveutil = minimax(brd, 1);
			if(moveutil > bestutil){
				bestutil = moveutil;
				response = move;
			}
		}
		return response;
	}
	
	private int minimax(Integer[][] board, int depth){
		boolean cpuTurn = (depth % 2) == 1;
		int state = getState(board);
		if(state == WIN || state == LOSE) {
			return 1000 * (cpuTurn ? 1 : -1);
		} else if (state == DRAW) {
			return 0;
		}
		int utility = -100000 * (cpuTurn ? -1 : 1);
		if(depth <= 10){
			for(Integer[] move : getMoves(board)){
				Integer[][] newboard = doAMove(move, board, (cpuTurn ? X : O) );
				if(cpuTurn){
					utility = Math.min(utility, minimax(newboard, depth+1));
				} else {
					utility = Math.max(utility, minimax(newboard, depth+1));
				}
			}
		}
		return utility;
	}
	
	private Integer[][] doAMove(Integer[] move, Integer[][] board, int player){
		Integer[][] brd = new Integer[3][3];
		for(int i = 0; i < board.length; i++){
			brd[i] = Arrays.copyOf(board[i], 3);
		}
		brd[move[0]][move[1]] = player;
		return brd;
	}
	
	public Integer getState(Integer[][] board) {
		// check rows
		int player = board[0][0];
		if(player != EMPTY && board[0][1] == player && board[0][2] == player){
			return player == X ? WIN : LOSE;
		}
		player = board[1][0];
		if(player != EMPTY && board[1][1] == player && board[1][2] == player){
			return player == X ? WIN : LOSE;
		}
		player = board[2][0];
		if(player != EMPTY && board[2][1] == player && board[2][2] == player){
			return player == X ? WIN : LOSE;
		}
		// check columns
		player = board[0][0];
		if(player != EMPTY && board[1][0] == player && board[2][0] == player){
			return player == X ? WIN : LOSE;
		}
		player = board[0][1];
		if(player != EMPTY && board[1][1] == player && board[2][1] == player){
			return player == X ? WIN : LOSE;
		}
		player = board[0][2];
		if(player != EMPTY && board[1][2] == player && board[2][2] == player){
			return player == X ? WIN : LOSE;
		}
		// check diags
		player = board[0][0];
		if(player != EMPTY && board[1][1] == player && board[2][2] == player){
			return player == X ? WIN : LOSE;
		}
		player = board[2][0];
		if(player != EMPTY && board[1][1] == player && board[0][2] == player){
			return player == X ? WIN : LOSE;
		}
		
		List<Integer[]> moves = getMoves(board);
		if(moves.size() == 0){
			return DRAW;
		}
		return PLAY;
	}
	
	
	private List<Integer[]> getMoves(Integer[][] board){
		List<Integer[]> moves = new ArrayList<Integer[]>();
		for(int i = 0; i<3; i++){
			for(int j = 0; j<3; j++){
				if(board[i][j] == EMPTY) {
					Integer[] t = new Integer[2];
					t[0] = i;
					t[1] = j;
					moves.add(t);
				}
			}
		}
		return moves;
	}
	private PersistenceManager getPersistenceManager() {
		return PMF.getPersistenceManager();
	}
}
