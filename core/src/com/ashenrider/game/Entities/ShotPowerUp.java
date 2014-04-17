package com.ashenrider.game.Entities;

import com.ashenrider.game.Scene;
import com.ashenrider.game.Buffs.Buff;
import com.ashenrider.game.Buffs.SpeedBuff;
import com.ashenrider.game.Buffs.StatusBuff;
import com.ashenrider.game.Buffs.Buff.Status;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import java.util.List;

public class ShotPowerUp extends PowerUp {

    private static final float RESPAWN_TIME = 10.0f;
    private static final float BUFF_DURATION = 7.0f;
    private static final String TEXTURE_LOCATION = "powerup.png";

    public ShotPowerUp(List<Vector2> respawns) {
        super(respawns, RESPAWN_TIME, TEXTURE_LOCATION, new Color(0.8f, 0.1f, 0.1f, 1));
    }

    @Override
    public void onPickup(Player player) {
        super.onPickup(player);
        player.addBuff(new StatusBuff(player, BUFF_DURATION, Buff.Status.MULTI_SHOT));
        scene.addEntity(new OrbParticle(player, colorTint, BUFF_DURATION), Scene.PARTICLE_LAYER);
    }
}
