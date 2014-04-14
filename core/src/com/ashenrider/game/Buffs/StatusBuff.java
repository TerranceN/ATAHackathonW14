
package com.ashenrider.game.Buffs;

import com.ashenrider.game.Player;

public class StatusBuff extends Buff {
	// a do nothing buff that can be used to indicate a status
	
	public StatusBuff(Player p, float duration, Status status) {
		super(p, duration, status);
	}
	
	public void init() {
	}
	
	public void end() {
	}
}
