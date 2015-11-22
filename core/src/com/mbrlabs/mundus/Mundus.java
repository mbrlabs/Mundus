package com.mbrlabs.mundus;

import com.badlogic.gdx.Game;
import com.kotcrab.vis.ui.VisUI;
import com.mbrlabs.mundus.ui.screens.TestScreen;

public class Mundus extends Game {
	
	@Override
	public void create () {
        VisUI.load();
        setScreen(new TestScreen());
	}

	@Override
	public void render () {
        super.render();
	}

    @Override
    public void dispose() {
        super.dispose();
        VisUI.dispose();
    }


}
