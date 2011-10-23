package com.kosmonaut.tictactoe.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("TicService")
public interface TicService extends RemoteService {
	
	Integer[] getComputerMove(Integer[][] coords);
	Integer getState(Integer[][] board);
	Long[] getStats();
	void updateStats(Integer outcome);
	
	
	
	/**
	 * Utility class for simplifying access to the instance of async service.
	 */
	public static class Util {
		private static TicServiceAsync instance;
		public static TicServiceAsync getInstance(){
			if (instance == null) {
				instance = GWT.create(TicService.class);
			}
			return instance;
		}
	}
}
