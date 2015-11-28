package com.mbrlabs.mundus.ui.components;

import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisLabel;

/**
 * @author Marcus Brummer
 * @version 24-11-2015
 */
public class StatusBar extends Container {

    private HorizontalGroup group;

    private VisLabel verticesLabel;
    private VisLabel fpsLabel;

    public StatusBar() {
        super();
        setBackground(VisUI.getSkin().getDrawable("default-pane"));
        align(Align.right | Align.center);
        group = new HorizontalGroup();
        group.space(10);
        group.padRight(10);
        setActor(group);

        verticesLabel = new VisLabel();
        fpsLabel = new VisLabel();
        setFps(60);
        setVertexCount(0);

        group.addActor(verticesLabel);
        group.addActor(fpsLabel);
    }

    public void setFps(int fps) {
        this.fpsLabel.setText("fps: " + fps);
    }

    public void setVertexCount(long vertexCount) {
        this.verticesLabel.setText("vertices: " + vertexCount);
    }

}
