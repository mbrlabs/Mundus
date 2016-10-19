/*
 * Copyright (c) 2016. See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mbrlabs.mundus.editor.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BoxShapeBuilder;
import com.badlogic.gdx.math.Vector3;

/**
 * @author Marcus Brummer
 * @version 24-11-2015
 */
public class UsefulMeshs {

    private static final MeshPartBuilder.VertexInfo v0 = new MeshPartBuilder.VertexInfo();
    private static final MeshPartBuilder.VertexInfo v1 = new MeshPartBuilder.VertexInfo();

    public static Model createAxes() {
        final float GRID_MIN = -10f;
        final float GRID_MAX = 10f;
        final float GRID_STEP = 1f;
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        MeshPartBuilder builder = modelBuilder.part("grid", GL20.GL_LINES,
                VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorUnpacked, new Material());
        builder.setColor(Color.LIGHT_GRAY);
        for (float t = GRID_MIN; t <= GRID_MAX; t += GRID_STEP) {
            builder.line(t, 0, GRID_MIN, t, 0, GRID_MAX);
            builder.line(GRID_MIN, 0, t, GRID_MAX, 0, t);
        }
        builder = modelBuilder.part("axes", GL20.GL_LINES,
                VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorUnpacked, new Material());
        builder.setColor(Color.RED);
        builder.line(0, 0, 0, 100, 0, 0);
        builder.setColor(Color.GREEN);
        builder.line(0, 0, 0, 0, 100, 0);
        builder.setColor(Color.BLUE);
        builder.line(0, 0, 0, 0, 0, 100);
        return modelBuilder.end();
    }

    public static Model createArrowStub(Material mat, Vector3 from, Vector3 to) {
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        MeshPartBuilder meshBuilder;
        // line
        meshBuilder = modelBuilder.part("line", GL20.GL_LINES,
                VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorUnpacked, mat);
        meshBuilder.line(from.x, from.y, from.z, to.x, to.y, to.z);
        // stub
        Node node = modelBuilder.node();
        node.translation.set(to.x, to.y, to.z);
        meshBuilder = modelBuilder.part("stub", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal, mat);
        BoxShapeBuilder.build(meshBuilder, 2, 2, 2);
        return modelBuilder.end();
    }

    public static Model torus(Material mat, float width, float height, int divisionsU, int divisionsV) {

        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        MeshPartBuilder builder = modelBuilder.part("torus", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position, mat);
        // builder.setColor(Color.LIGHT_GRAY);

        MeshPartBuilder.VertexInfo curr1 = v0.set(null, null, null, null);
        curr1.hasUV = curr1.hasNormal = false;
        curr1.hasPosition = true;

        MeshPartBuilder.VertexInfo curr2 = v1.set(null, null, null, null);
        curr2.hasUV = curr2.hasNormal = false;
        curr2.hasPosition = true;
        short i1, i2, i3 = 0, i4 = 0;

        int i, j, k;
        double s, t, twopi;
        twopi = 2 * Math.PI;

        for (i = 0; i < divisionsV; i++) {
            for (j = 0; j <= divisionsU; j++) {
                for (k = 1; k >= 0; k--) {
                    s = (i + k) % divisionsV + 0.5;
                    t = j % divisionsU;

                    curr1.position.set(
                            (float) ((width + height * Math.cos(s * twopi / divisionsV))
                                    * Math.cos(t * twopi / divisionsU)),
                            (float) ((width + height * Math.cos(s * twopi / divisionsV))
                                    * Math.sin(t * twopi / divisionsU)),
                            (float) (height * Math.sin(s * twopi / divisionsV)));
                    k--;
                    s = (i + k) % divisionsV + 0.5;
                    curr2.position.set(
                            (float) ((width + height * Math.cos(s * twopi / divisionsV))
                                    * Math.cos(t * twopi / divisionsU)),
                            (float) ((width + height * Math.cos(s * twopi / divisionsV))
                                    * Math.sin(t * twopi / divisionsU)),
                            (float) (height * Math.sin(s * twopi / divisionsV)));
                    // curr2.uv.set((float) s, 0);
                    i1 = builder.vertex(curr1);
                    i2 = builder.vertex(curr2);
                    builder.rect(i4, i2, i1, i3);
                    i4 = i2;
                    i3 = i1;
                }
            }
        }

        return modelBuilder.end();
    }

}
