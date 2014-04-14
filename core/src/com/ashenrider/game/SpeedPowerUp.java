package com.ashenrider.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import java.util.List;

public class SpeedPowerUp extends PowerUp {

    private static final float RESPAWN_TIME = 5.0f;
    private static final float BUFF_DURATION = 4.0f;
    private static final String TEXTURE_LOCATION = "powerup.png";

    public SpeedPowerUp(List<Vector2> respawns) {
        super(respawns, RESPAWN_TIME, TEXTURE_LOCATION, new Color(0.0f, 1, 0.25f, 1));
    }

    @Override
    public void onPickup(Player player) {
        super.onPickup(player);
        player.onSpeedBoost(0.5f, BUFF_DURATION);
    }
}
