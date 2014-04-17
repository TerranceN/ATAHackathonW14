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

public class OrbParticle extends Entity {
    private static Texture ORB_PARTICLE = null;
    private Color orbColor;

    Texture img;
    private float lifeTime;
    
    private float TOTAL_TIME;
    private static final float FADE_TIME = 1.0f;
    private float initialAngle;
    private float rotationSpeed;
    
    private Player player;
    
    public OrbParticle(Player player, Color color, float duration) {
        super(player.getCentre());
        this.player = player;
        
        if (ORB_PARTICLE == null) {
            ORB_PARTICLE = Assets.manager.get("powerup.png", Texture.class);
        }
        orbColor = new Color(color); // copy the colour so that it can be modified to fade
        img = ORB_PARTICLE;

        size = new Vector2(20.0f, 20.0f);
        pos = pos.sub(size.cpy().scl(0.5f));
        falls = false;
        
        Random rand = new Random();
        initialAngle = rand.nextFloat() * 360;
        rotationSpeed = 120 + rand.nextFloat() * 120f;
        if (rand.nextBoolean()) {
            rotationSpeed = -rotationSpeed;
        }
        
        TOTAL_TIME = duration;
        lifeTime = TOTAL_TIME;
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        lifeTime -= dt;
        if(lifeTime <= 0 || !player.alive) {
            destroy();
        } else if (lifeTime < FADE_TIME) {
            orbColor.a = lifeTime / FADE_TIME;
        }
        float angle = initialAngle + rotationSpeed * lifeTime;
        Vector2 offset = new Vector2(48f, 0f).rotate(angle);
        pos = player.getCentre().add(offset).sub(size.cpy().scl(0.5f));
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.setColor(orbColor);
        batch.draw(img, pos.x, pos.y, size.x, size.y);
        batch.setColor(Color.WHITE);
    }
}
