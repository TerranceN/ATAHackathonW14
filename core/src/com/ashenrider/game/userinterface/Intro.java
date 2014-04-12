package com.ashenrider.game.userinterface;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Intro {

    float scale = 1.0f;
    float animationTime = 0.0f;

    private static Animation staticAnim;
    private static SpriteBatch batch;
    private static int NUM_FRAMES;

    private float FRAME_DURATION = 0.025f;
    private Animation anim;
    
    float angle;

    public Intro() {       
        if (staticAnim == null) {
            TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("pack/intro.atlas"));
            TextureRegion[] frames = new TextureRegion[181];
            NUM_FRAMES = frames.length;
            for (int i=0; i<NUM_FRAMES; i++) {
                frames[i] = atlas.findRegion(String.format("intro-%05d", i));
            }
            anim = new Animation(FRAME_DURATION, frames);
            staticAnim = anim;

        	batch = new SpriteBatch();
        } else {
            anim = staticAnim;
        }
    }

    public TextureRegion getSprite() {
        return anim.getKeyFrame(animationTime, false);
    }

    public void update(float dt) {
        animationTime += dt;
    }

    public void skip() {
    	animationTime = FRAME_DURATION * NUM_FRAMES;
    }
    
    public boolean isFinished() {
    	return animationTime > FRAME_DURATION * NUM_FRAMES;
    }
    
    public void render() {
        TextureRegion frame = getSprite();
        batch.begin();
        batch.draw(frame, 0, 0);
        batch.end();
    }
}
