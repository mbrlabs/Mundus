package com.mbrlabs.mundus.commons.utils;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import org.junit.Test;

import static org.junit.Assert.*;

public class MathUtilsTest {
    @Test
    public void barryCentric() throws Exception {
        Vector3 a = new Vector3(1.0f, 0.0f, 0.0f);
        Vector3 b = new Vector3(0.0f, 1.0f, 0.0f);
        Vector3 c = new Vector3(0.0f, 0.0f, 1.0f);
        Vector2 pos = new Vector2(0.0f, 0.0f);

        assertEquals(1.0f, MathUtils.barryCentric(a, b, c, pos), 0.0f);
    }

    @Test
    public void dst() throws Exception {
        assertEquals(1.0f, MathUtils.dst(1.0f, 1.0f, 2.0f, 1.0f), 0.0f);
    }

    @Test
    public void angle() throws Exception {
        assertEquals(45.0f, MathUtils.angle(0.0f,0.0f,1.0f,1.0f), 0.0f);
    }

}