package me.zeroeightsix.buildguide;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.util.math.Vec3d;

import static org.lwjgl.opengl.GL11.*;

public class Sphere extends Shape {

    private final double radius;

    public Sphere(Vec3d location, double radius) {
        super(location);
        this.radius = radius;
    }

    @Override
    void draw() {
        glTranslated(getLocation().x, getLocation().y, getLocation().z);
        final int lats = 100;
        final int longs = 100;

        int i, j;
        for(i = 0; i <= lats; i++) {
            double lat0 = Math.PI * (-0.5 + (double) (i - 1) / lats);
            double z0 = Math.sin(lat0);
            double zr0 = Math.cos(lat0);

            double lat1 = Math.PI * (-0.5 + (double) i / lats);
            double z1 = Math.sin(lat1);
            double zr1 = Math.cos(lat1);

            glBegin(GL_QUAD_STRIP);
            for(j = 0; j <= longs; j++) {
                double lng = 2 * Math.PI * (double) (j - 1) / longs;
                double x = Math.cos(lng);
                double y = Math.sin(lng);

                glNormal3d(x * zr0, y * zr0, z0);
                glVertex3d(radius * x * zr0, radius * y * zr0, radius * z0);
                glNormal3d(x * zr1, y * zr1, z1);
                glVertex3d(radius * x * zr1, radius * y * zr1, radius * z1);
            }
            glEnd();
        }
        glTranslated(-getLocation().x, -getLocation().y, -getLocation().z);
    }
}
