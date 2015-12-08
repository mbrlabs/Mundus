package com.mbrlabs.mundus.core;

import com.mbrlabs.mundus.terrain.brushes.Brush;
import com.mbrlabs.mundus.terrain.brushes.SphereBrush;

/**
 * @author Marcus Brummer
 * @version 08-12-2015
 */
public class Brushes {

    public SphereBrush sphereBrush;

    private Brush activeBrush;

    public Brushes() {
        this.sphereBrush = new SphereBrush();
    }

    public void activate(Brush brush) {
        this.activeBrush = brush;
        Mundus.input.setCurrentToolInput(brush.getInputProcessor());
    }

    public void deactivate() {
        if(this.activeBrush != null) {
            Mundus.input.removeProcessor(activeBrush.getInputProcessor());
            activeBrush = null;
        }
    }

    public Brush getActiveBrush() {
        return this.activeBrush;
    }

}
