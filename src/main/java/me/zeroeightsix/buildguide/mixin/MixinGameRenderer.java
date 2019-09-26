package me.zeroeightsix.buildguide.mixin;

import me.zeroeightsix.buildguide.BuildGuide;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {

	@Shadow
	@Final
	private Camera camera;

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", ordinal = 13), method = "renderCenter")
	private void renderCenter(float float_1, long long_1, CallbackInfo info) {
		double x = camera.getPos().x;
		double y = camera.getPos().y;
		double z = camera.getPos().z;

		BuildGuide.render(x, y, z);
	}
}
