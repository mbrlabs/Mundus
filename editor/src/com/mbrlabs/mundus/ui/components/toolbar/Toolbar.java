package com.mbrlabs.mundus.ui.components.toolbar;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.VisUI;

/**
 * @author Marcus Brummer
 * @version 24-11-2015
 */
public class Toolbar extends Container {

    private HorizontalGroup group;

    public Toolbar() {
        super();
        setBackground(VisUI.getSkin().getDrawable("default-pane"));
        align(Align.left | Align.center);
        group = new HorizontalGroup();
        setActor(group);
    }

    public void addItem(Actor actor) {
        group.addActor(actor);
    }

}
