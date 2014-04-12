package com.ashenrider.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Fireball extends Projectile {	
	// frames per particle spawn
    float scale = 2.0f;
	private int particleTimer = 0;
    
    float animationTime = 0.0f;

    private static Animation staticAnim;

    private float FRAME_DURATION = 0.05f;
    private Animation anim;
    float angle;
    
	public Fireball(Vector2 initPosition, Vector2 direction, int playerNumber) {
		super(initPosition, direction, playerNumber);
        angle = direction.angle();
        boolean flipped = true;

        if (staticAnim == null) {
            TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("pack/fx.atlas"));
            TextureRegion[] frames = new TextureRegion[15];
            for (int i=0; i<15; i++) {
                frames[i] = atlas.findRegion("fire/fire-" + (i+1));
                if (flipped) {
                    frames[i].flip(true,  false);
                }
            }
            anim = new Animation(FRAME_DURATION, frames);
            staticAnim = anim;
        } else {
            anim = staticAnim;
        }
        
		size = new Vector2(16, 16);
        pos = initPosition.cpy().sub(size.cpy().scl(0.5f));

		
		// you cannot hurt yourself within the first fraction of a second that a shot is fired
		SPEED = 700.0f;
		speed = direction.nor().scl(SPEED);
		//GRAVITY = -300.0f;
		falls = false;
		
		BOUNCE_WALLS = false;
		BOUNCE_CEILING = false;
		groundBounces = 0;
		ELASTICITY = 0.7f;
		type = DeathSources.FIREBALL;
	}

	@Override
	public void destroy() {
		super.destroy();
        scene.addEntity(new Explosion(getCentre()), Scene.PARTICLE_LAYER);
	}
	
	@Override
	public void update(float dt) {
		super.update(dt);
        animationTime += dt;
		//Particle p = new GunParticle(getCentre(), speed.cpy().scl(-1), 1.0f, 0.4f);
		//scene.addEntity(p, Scene.PARTICLE_LAYER);
	}

    public TextureRegion getSprite() {
        return anim.getKeyFrame(animationTime, true);
    }
	
    @Override
    public void render(SpriteBatch batch) {
        TextureRegion frame = getSprite();
        float w = frame.getRegionWidth();
        float h = frame.getRegionHeight();
        float oX = 38;
        float oY = 24;
        batch.draw(frame, pos.x + size.x/2f -oX, pos.y + size.y/2f -oY, oX, oY, w, h, scale, scale, angle);
    }
}
