package me.zeroeightsix.buildguide;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.github.cottonmc.clientcommands.ArgumentBuilders;
import io.github.cottonmc.clientcommands.ClientCommandPlugin;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class BuildGuide implements ClientCommandPlugin, ModInitializer {

	private static Shape[] shapes = new Shape[100];
	private static int[] lists = new int[0];
	private static boolean dirty = false;
	private static List<Integer> toDestroy = new ArrayList<>();
	private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.locate.failed"));

    public static void render(double x, double y, double z) {
		if (dirty) {
			redraw();
			dirty = false;
		}

		GlStateManager.pushMatrix();
		GlStateManager.translated(-x, -y, -z);
		GlStateManager.color4f(1, 1, 1, 0.3f);
		GlStateManager.enableAlphaTest();
		GlStateManager.enableBlend();
		GlStateManager.disableCull();
		GlStateManager.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
		for (int list : lists) {
			glCallList(list);
		}
		GlStateManager.popMatrix();

    }

	private static void redraw() {
    	List<Integer> lists = new ArrayList<>();

    	for (int i : toDestroy) {
    		glDeleteLists(i, 1);
		}
    	toDestroy.clear();

    	for (Shape shape : shapes) {
    		if (shape == null) continue;
			int list = shape.getList();
			lists.add(list);
			glNewList(list, GL_COMPILE);
			shape.draw();
			glEndList();
		}
    	BuildGuide.lists = lists.stream().mapToInt(integer -> integer).toArray();
    }

	@Override
	public void registerCommands(CommandDispatcher<CottonClientCommandSource> dispatcher) {
		dispatcher.register(ArgumentBuilders.literal("buildguide")
				.then(ArgumentBuilders.literal("add")
						.then(ArgumentBuilders.literal("sphere")
								.then(ArgumentBuilders.argument("radius", DoubleArgumentType.doubleArg())
										.executes(context -> {
											try {
												int id = pushShape(new Sphere(MinecraftClient.getInstance().player.getPos(), context.getArgument("radius", Double.class)));
												context.getSource().sendFeedback(new LiteralText("Created sphere with id " + id + "."));
											} catch (IllegalStateException e) {
												throw new SimpleCommandExceptionType(new LiteralText(e.getMessage())).create();
											}
											return 1;
										})
								)
						)
				)
				.then(ArgumentBuilders.literal("remove")
						.then(ArgumentBuilders.argument("id", IntegerArgumentType.integer())
								.executes(context -> {
									int id = context.getArgument("id", Integer.class);
									Shape shape;

									if (id > 100 || (shape = shapes[id]) == null) {
										throw new SimpleCommandExceptionType(new LiteralText("No such shape with that id.")).create();
									}

									toDestroy.add(shape.getList());
									shapes[id] = null;
									dirty = true;
									context.getSource().sendFeedback(new LiteralText("Shape removed!"));
									return 1;
								})
						)
				));
	}

	@Override
	public void onInitialize() {
		System.out.println("Build guide initialised!");
	}

	private int pushShape(Shape shape) {
		for (int i = 0; i < shapes.length; i++) {
			if (shapes[i] == null) {
				shapes[i] = shape;
				dirty = true;
				return i;
			}
		}
		throw new IllegalStateException("Too many shapes");
	}
}
