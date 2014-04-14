package com.ashenrider.game.Buffs;

import com.ashenrider.game.Player;

public class DashBuff extends Buff {
	
	public DashBuff(Player p, float duration) {
		super(p, duration, Status.DASHING);
	}
	
	public void init() {
		player.falls = false;
	}
	
	public void end() {
		player.falls = true;
	}
}
