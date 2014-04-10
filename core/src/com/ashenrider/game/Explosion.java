package com.ashenrider.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Explosion extends Entity {

    private float lifeTime = 5.0f;

    float scale = 2.0f;
    float animationTime = 0.0f;

    private float FRAME_DURATION = 0.03f;
    private Animation anim;
    
    float angle;

    public Explosion(Vector2 initPosition) {
        super(initPosition);
        angle = 0.0f;//direction.angle();
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
        batch.draw(frame, pos.x, pos.y, w/2, h/2, w, h, scale, scale, angle);
    }
}
