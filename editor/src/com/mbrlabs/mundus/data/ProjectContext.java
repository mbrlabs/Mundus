package com.mbrlabs.mundus.data;

import com.badlogic.gdx.utils.Disposable;
import com.mbrlabs.mundus.World;
import com.mbrlabs.mundus.data.home.ProjectRef;

/**
 * @author Marcus Brummer
 * @version 28-11-2015
 */
public class ProjectContext implements Disposable {

    private ProjectRef ref;
    private World world;

    public ProjectContext() {
        ref = null;
        world = new World();
    }

    public ProjectContext(ProjectRef ref, World world) {
        this.ref = ref;
        this.world = world;
    }

    public ProjectRef getRef() {
        return ref;
    }

    public void setRef(ProjectRef ref) {
        this.ref = ref;
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    @Override
    public void dispose() {
        world.dispose();
    }

}
