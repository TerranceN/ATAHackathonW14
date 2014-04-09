

package com.ashenrider.game.Input;

import com.ashenrider.game.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class MouseAxis extends InputAxis {
	Entity relativeE;
	OrthographicCamera camera;
	boolean xAxis;
	
	public MouseAxis(Entity e, OrthographicCamera cam, boolean useX) {
		xAxis = useX;
		relativeE = e;
		camera = cam;
	}

	@Override
	public float getValue() {
		Vector3 mouse3 = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
		mouse3 = camera.unproject(mouse3);
		Vector2 mouse = new Vector2(mouse3.x, mouse3.y);
		mouse.sub(relativeE.pos);
		mouse.nor();
		if (xAxis)
			return mouse.x;
		else
			return mouse.y;
	}
}