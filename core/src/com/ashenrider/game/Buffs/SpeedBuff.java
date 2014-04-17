package com.ashenrider.game.Buffs;

import com.ashenrider.game.Entities.Player;

public class SpeedBuff extends Buff {
    private float speedDiff;
    
    public SpeedBuff(Player p, float duration, float speedDiff) {
        super(p, duration, null);
        this.speedDiff = speedDiff;
    }
    
    public void init() {
        player.speedMult += speedDiff;
    }
    
    public void end() {
        player.speedMult -= speedDiff;
    }
}
