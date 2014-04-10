package com.ashenrider.game;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Explosion extends Entity {

    private float lifeTime = 5.0f;

    float scale = 3.0f;
    float animationTime = 0.0f;

    private float FRAME_DURATION = 0.025f;
    private Animation anim;
    
    float angle;

    public Explosion(Vector2 initPosition) {
        super(initPosition);
        Random rand = new Random();
        angle = 360 * rand.nextFloat();
        boolean flipped = false;
        
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("pack/fx.atlas"));
        TextureRegion[] frames = new TextureRegion[43];
        for (int i=0; i<43; i++) {
            frames[i] = atlas.findRegion("explosion/explosion-" + (i+1));
            if (flipped) {
            	frames[i].flip(true,  false);
            }
        }
        anim = new Animation(FRAME_DURATION, frames);

        size = new Vector2(16.0f, 16.0f).scl(scale);
        pos = initPosition.cpy().sub(size.cpy().scl(0.5f));
        
        lifeTime = FRAME_DURATION * 44;
        falls = false;
    }

    public TextureRegion getSprite() {
        return anim.getKeyFrame(animationTime, false);
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        animationTime += dt;
        lifeTime -= dt;
        if(lifeTime < 0) {
            destroy();
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        TextureRegion frame = getSprite();
        float w = frame.getRegionWidth();
        float h = frame.getRegionHeight();
        float oX = 26; // flip this if flipped
        float oY = 30;
        batch.draw(frame, pos.x + size.x/2f -oX, pos.y + size.y/2f -oY, oX, oY, w, h, scale, scale, angle);
    }
}
