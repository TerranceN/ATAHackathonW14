package com.ashenrider.game.Entities;

import java.util.Random;

import com.ashenrider.game.Assets;
import com.ashenrider.game.Buffs.Buff;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;

public class SwingParticle extends Entity {
    private static Texture SWING_PARTICLE = null;
    private Color col;

    Texture img;
    private float lifeTime;
    
    private float TOTAL_TIME = 0.25f;
    private static final float FADE_TIME = 0.2f;
    private float angle;
    
    private float scaleX;
    private float scaleY;
    
    private Player player;
    
    public SwingParticle(Player player, float initAngle) {
        super(player.getCentre());
        this.player = player;
        
        if (SWING_PARTICLE == null) {
            SWING_PARTICLE = Assets.manager.get("swing1.png", Texture.class);
        }
        img = SWING_PARTICLE;
        col = new Color(1f, 1f, 1f, 1f);
        size = new Vector2(0.0f, 0.0f);
        // the hit shape is unrelated to the size vector. Instead, it should be an angle range with distance a function of angle
        falls = false;
        
        lifeTime = TOTAL_TIME;
        angle = initAngle;
        scaleX = player.scale;
        scaleY = Math.cos(angle) > 0 ? scaleX : -scaleX; // the arc has a top and a bottom
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        lifeTime -= dt;
        if(lifeTime <= 0 || !player.alive) {
            destroy();
        } else if (lifeTime < FADE_TIME) {
            col.a = lifeTime / FADE_TIME;
        }
        pos = player.getCentre();
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.setColor(col);
        float w = img.getWidth();
        float h = img.getHeight();
        float oX = w/2;
        float oY = h/2;
        batch.draw(img, pos.x + size.x/2f - oX, pos.y + size.y/2f - oY, oX, oY, w, h, scaleX, scaleY, angle, 0, 0, (int) w, (int) h, false, false);

        batch.setColor(Color.WHITE);
    }
}
