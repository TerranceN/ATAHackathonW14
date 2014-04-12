package com.ashenrider.game;

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

public class RespawnParticle extends Entity {
	private static Texture RESPAWN_PARTICLE = null;
	private Color respawnTint;

	Texture img;
    private float lifeTime;
    
    private static final float TOTAL_TIME = 1.2f;
    private static final float FADE_TIME = 0.4f;
    private static final float MIN_SCALE = 0.6f;
    private static final float MAX_SCALE = 2.0f;
    private static final float ROTATION = 225.0f; // negative is clockwise
    private Player player;
    
    public RespawnParticle(Player player) {
        super(player.getCentre());
        this.player = player;
    	
		if (RESPAWN_PARTICLE == null) {
			RESPAWN_PARTICLE = new Texture("respawn.png");
			//0xFFF76BFF
		}
		respawnTint = new Color(0xFFF6ADFF); //RRGGBBAA
		img = RESPAWN_PARTICLE;

        size = new Vector2(16.0f, 16.0f);
        pos = pos.sub(size.cpy().scl(0.5f));
        
        lifeTime = TOTAL_TIME;
        falls = false;
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        lifeTime -= dt;
        if(lifeTime <= 0) {
            destroy();
        } else if (lifeTime < FADE_TIME) {
            respawnTint.a = lifeTime / FADE_TIME;
        }
        pos = player.getCentre().sub(size.cpy().scl(0.5f));
    }

    @Override
    public void render(SpriteBatch batch) {
        float angle = 45 + ROTATION * lifeTime/TOTAL_TIME;
        float scale = MIN_SCALE + (MAX_SCALE - MIN_SCALE) * lifeTime/TOTAL_TIME;
		batch.setColor(respawnTint);
        float w = img.getWidth();
        float h = img.getHeight();
        float oX = w/2;
        float oY = h/2;
        batch.draw(img, pos.x + size.x/2f - oX, pos.y + size.y/2f - oY, oX, oY, w, h, scale, scale, angle, 0, 0, (int) w, (int) h, false, false);
		batch.setColor(Color.WHITE);
    }
}
