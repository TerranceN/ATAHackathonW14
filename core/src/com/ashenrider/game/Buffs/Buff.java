package com.ashenrider.game.Buffs;

import com.ashenrider.game.Entities.Player;

public abstract class Buff {
    // Some buffs stack (you can have 2 speed boosts)
    // others are unique (being invulnerable twice merely refreshes the duration)
    public enum Status {
        INVULNERABLE, DASHING, LAND_STUN, MULTI_SHOT //unused: RESPAWNING
    }
    
    public float duration;
    public boolean finished = false;
    protected Player player;
    public Status status = null;
    
    public Buff(Player p, float duration, Status status) {
        this.duration = duration;
        this.player = p;
        this.status = status;
    }
    
    public void update(float dt) {
        duration -= dt;
        if (duration <= 0.0f && !finished) {
            end();
            finished = true;
        }
    }
    
    public abstract void init();
    
    public abstract void end();
}
