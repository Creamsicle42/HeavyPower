package com.creamsicle42.heavypower.misc;

import net.minecraft.nbt.CompoundTag;

import java.util.Arrays;
import java.util.List;

public class SerializationHelper {

    private static double DOUBLE_STORAGE_FACTOR = 1000.0;

    private static final String GRID_WIDTH_TAG = "GridWidth";
    private static final String GRID_HEIGHT_TAG = "GridHeight";
    private static final String GRID_VALUES_TAG = "GridValues";


    public static CompoundTag gridToTag(double[][] grid, CompoundTag tag) {
        if (grid == null || grid.length == 0) {
            tag.putInt(GRID_HEIGHT_TAG, 0);
            tag.putInt(GRID_WIDTH_TAG, 0);
            tag.putLongArray(GRID_VALUES_TAG, List.of(new Long[0]));
            return tag;
        }
        tag.putInt(GRID_HEIGHT_TAG, grid.length);
        tag.putInt(GRID_WIDTH_TAG, grid[0].length);
        tag.putLongArray(GRID_VALUES_TAG,
                Arrays.stream(grid)
                        .flatMapToDouble(Arrays::stream)
                        .mapToLong(d -> (long)(DOUBLE_STORAGE_FACTOR * d))
                        .toArray()
        );

        return tag;
    }

    public static double[][] tagToGrid(CompoundTag tag) {
        int width = tag.getInt(GRID_WIDTH_TAG);
        int height = tag.getInt(GRID_HEIGHT_TAG);

        double[][] out = new double[height][width];
        long[] values = tag.getLongArray(GRID_VALUES_TAG);

        for (int i = 0; i < values.length; i++) {
            out[i / width][i % width] = (double)values[i] / DOUBLE_STORAGE_FACTOR;
        }

        return out;
    }
}
