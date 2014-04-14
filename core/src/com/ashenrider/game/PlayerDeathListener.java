package com.ashenrider.game;

import com.ashenrider.game.Entities.Player;

public interface PlayerDeathListener {
    public void onPlayerDeath(Player aggressor, Player victim, int deathSource);
}
