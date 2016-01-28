package com.mbrlabs.mundus.commons.terrain;

import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.NumberUtils;

/**
 * @author Marcus Brummer
 * @version 28-01-2016
 */
public class TerrainTextureSplatAttribute extends Attribute {

    public static final String ATTRIBUTE_SPLAT0_ALIAS  = "splat0";
    public static final long ATTRIBUTE_SPLAT0 = register(ATTRIBUTE_SPLAT0_ALIAS);
    public static final String ATTRIBUTE_SPLAT1_ALIAS  = "splat1";
    public static final long ATTRIBUTE_SPLAT1 = register(ATTRIBUTE_SPLAT1_ALIAS);

    public TerrainTextureSplat splat;

    protected static long Mask = ATTRIBUTE_SPLAT0 | ATTRIBUTE_SPLAT1;

    /** Method to check whether the specified type is a valid DoubleAttribute type */
    public static Boolean is(final long type) {
        return (type & Mask) != 0;
    }

    public TerrainTextureSplatAttribute(long type, TerrainTextureSplat splat) {
        super(type);
        if (!is(type)) throw new GdxRuntimeException("Invalid type specified");
        this.splat = splat;
    }

    public TerrainTextureSplatAttribute(TerrainTextureSplatAttribute other) {
        super(other.type);
        if (!is(type)) throw new GdxRuntimeException("Invalid type specified");
        this.splat = other.splat;
    }

    protected TerrainTextureSplatAttribute(long type) {
        super(type);
        if (!is(type)) throw new GdxRuntimeException("Invalid type specified");
    }

    @Override
    public Attribute copy() {
        return new TerrainTextureSplatAttribute(this);
    }

    @Override
    public int hashCode() {
        final int prime = 7;
        final long v = NumberUtils.doubleToLongBits(splat.hashCode());
        return prime * super.hashCode() + (int)(v^(v>>>32));
    }

    @Override
    public int compareTo (Attribute o) {
        if (type != o.type) return type < o.type ? -1 : 1;
        TerrainTextureSplat otherValue = ((TerrainTextureSplatAttribute)o).splat;
        return splat.equals(otherValue) ? 0 : -1;
    }

}
