

package com.ashenrider.game.Input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

public class MouseAxis extends InputAxis {
	boolean xAxis;
	public MouseAxis(boolean useX) {
		xAxis = useX;
	}

	@Override
	public float getValue() {
		Vector2 mouse = new Vector2(Gdx.input.getX(), Gdx.input.getY());
		mouse.nor();
		if (xAxis)
			return mouse.x;
		else
			return mouse.y;
	}
}