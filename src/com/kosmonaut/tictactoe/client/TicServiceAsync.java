package com.kosmonaut.tictactoe.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface TicServiceAsync {

	void getComputerMove(Integer[][] board,
			AsyncCallback<Integer[]> asyncCallback);

	void getState(Integer[][] board, AsyncCallback<Integer> asyncCallback);

	void getStats(AsyncCallback<Long[]> asyncCallback);
	void updateStats(Integer outcome, AsyncCallback<Void> asyncCallback);
	

}
