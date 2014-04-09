package com.ashenrider.game;

public abstract class Input {
	public abstract class InputAxis {	
		// return a value between -1 and 1
		public abstract float getValue();
	}

	public abstract class InputButton {
		// return a boolean if a key is pressed
		public abstract boolean isDown();
	}	
}
