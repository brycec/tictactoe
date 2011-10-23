package com.kosmonaut.tictactoe.server;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Stats {
	
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long id;
	@Persistent
	private Long wins = 0L;
	@Persistent
	private Long losses = 0L;
	@Persistent
	private Long draws = 0L;
  
	public void incWins() {
		this.wins = wins + 1;
	}
	public Long getWins() {
		return wins;
	}
	public void incLosses() {
		this.losses = losses + 1;
	}
	public Long getLosses() {
		return losses;
	}
	public void incDraws() {
		this.draws = draws + 1;
	}
	public Long getDraws() {
		return draws;
	}
	public Long getId() {
		return id;
	}
	  
}
