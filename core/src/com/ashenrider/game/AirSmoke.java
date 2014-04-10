package com.ashenrider.game;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class AirSmoke extends Entity {

    private float lifeTime;

    float scale = 2.5f;
    float animationTime = 0.0f;

    private float FRAME_DURATION = 0.025f;
    private Animation anim;
    
    float angle;
    boolean flipped;

    public AirSmoke(Vector2 initPosition, float angle, boolean isUpsideDown) {
        super(initPosition);
        this.angle = angle;
        flipped = isUpsideDown;

        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("pack/fx.atlas"));
        TextureRegion[] frames = new TextureRegion[18];
        for (int i=0; i<18; i++) {
            frames[i] = atlas.findRegion("smoke/smoke-" + (i+1));
            //if (flipped) {
            //	frames[i].flip(true,  false);
            //}
        }
        anim = new Animation(FRAME_DURATION, frames);

        size = new Vector2(8.0f, 8.0f).scl(scale);
    	pos = initPosition.cpy().sub(size.cpy().scl(0.5f));
        //if (isRight) {
        //} else {
        //	pos = initPosition.cpy().sub(size.x, 0.0f);
        //}
        
        lifeTime = FRAME_DURATION * 18;
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
        float w = 52;
        float h = 52;
        float oX = 52;
        float oY = 0;
        if (flipped) {
            batch.draw(frame, pos.x + size.x/2, pos.y + size.y/2, oX, oY, w, h, scale, -scale, 0.0f);
        } else {
            batch.draw(frame, pos.x + size.x/2, pos.y + size.y/2, oX, oY, w, h, scale, scale, 0.0f);
        }
    }
}
