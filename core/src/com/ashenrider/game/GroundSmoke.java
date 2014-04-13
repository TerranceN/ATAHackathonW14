package com.ashenrider.game;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class GroundSmoke extends Entity {

    private float lifeTime;

    float scale = 2.5f;
    float animationTime = 0.0f;

    private float FRAME_DURATION = 0.025f;
    private static Animation staticAnim = null;
    private Animation anim;
    
    boolean flipped;

    public GroundSmoke(Vector2 initPosition, boolean isRight) {
        super(initPosition);
        flipped = isRight;

        if (staticAnim == null) {
            TextureAtlas atlas = Assets.manager.get("pack/fx.atlas", TextureAtlas.class);
            TextureRegion[] frames = new TextureRegion[18];
            for (int i=0; i<18; i++) {
                frames[i] = atlas.findRegion("smoke/smoke-" + (i+1));
            }
            anim = new Animation(FRAME_DURATION, frames);
            staticAnim = anim;
        } else {
            anim = staticAnim;
        }
        
        size = new Vector2(52.0f, 12.0f).scl(scale);
        if (isRight) {
        	pos = initPosition.cpy();
        } else {
        	pos = initPosition.cpy().sub(size.x, 0.0f);
        }
        
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
        float w = 52;//frame.getRegionWidth();
        float h = 52;//frame.getRegionHeight();
        float oX = 26;
        float oY = 0;
        if (flipped) {
            batch.draw(frame, pos.x + size.x/2f -oX, pos.y, oX, oY, w, h, -scale, scale, 0.0f);
        } else {
            batch.draw(frame, pos.x + size.x/2f -oX, pos.y, oX, oY, w, h, scale, scale, 0.0f);
        }
    }
}
