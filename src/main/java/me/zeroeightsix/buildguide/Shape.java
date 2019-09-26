package me.zeroeightsix.buildguide;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.util.math.Vec3d;

public abstract class Shape {

    private Vec3d location;
    private int list = -1;

    public Shape(Vec3d location) {
        this.location = location;
    }

    abstract void draw();

    public Vec3d getLocation() {
        return location;
    }

    int getList() {
        return (list == -1 ? (list = GlStateManager.genLists(1)) : list);
    }

}
