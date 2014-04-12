package com.ashenrider.game;

public interface PlayerDeathListener {
    public void onPlayerDeath(Player aggressor, Player victim, int deathSource);
}
