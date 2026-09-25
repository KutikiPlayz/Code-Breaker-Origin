package coolguy.kutikiplayz.mixin.power;

import com.llamalad7.mixinextras.sugar.Local;
import coolguy.kutikiplayz.extension.power.CodeHudRender;
import io.github.apace100.apoli.power.type.HudRendered;
import io.github.apace100.apoli.screen.PowerHudRenderer;
import io.github.apace100.apoli.util.HudRender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Mixin(PowerHudRenderer.class)
public abstract class PowerHudRendererMixin {
    @Unique private final Random random = Random.create();

    @Unique private final Map<HudRendered, Float> alphaMap = new HashMap<>();

    @Redirect(method = "lambda$render$4", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIII)V"))
    private void specialRender(DrawContext context, Identifier texture, int x, int y, int u, int v, int width, int height, @Local HudRendered hudRendered, @Local HudRender hudRender) {
        context.getMatrices().push();

        float shakeThreshold = ((CodeHudRender) hudRender).code_breaker_origin$getShakeThreshold();
        float shakeIntensity = ((CodeHudRender) hudRender).code_breaker_origin$getMaxShakeIntensity();
        if (shakeIntensity > 0f) {
            float shakeProgress = MathHelper.clampedMap(hudRendered.getFill(), shakeThreshold, 1f, 0f, 1f);
            float intensity = shakeProgress * shakeIntensity;
            context.getMatrices().translate(
                    random.nextGaussian() * intensity,
                    random.nextGaussian() * intensity,
                    0f
            );
        }

        if (((CodeHudRender) hudRender).code_breaker_origin$shouldFade()) {
            boolean shouldRender = hudRender.shouldRender(MinecraftClient.getInstance().player);
            float delta = MinecraftClient.getInstance().getRenderTickCounter().getLastFrameDuration();

            int fadeInTime = ((CodeHudRender) hudRender).code_breaker_origin$getFadeInTime();
            int fadeOutTime = ((CodeHudRender) hudRender).code_breaker_origin$getFadeOutTime();

            this.alphaMap.putIfAbsent(hudRendered, 0f);
            if (shouldRender) {
                float fadeIn = fadeInTime > 0 ? delta / fadeInTime : 1f;
                this.alphaMap.put(hudRendered, MathHelper.clamp(this.alphaMap.get(hudRendered) + fadeIn, 0f, 1f));
            } else {
                float fadeOut = fadeOutTime > 0 ? delta / fadeOutTime : 1f;
                this.alphaMap.put(hudRendered, MathHelper.clamp(this.alphaMap.get(hudRendered) - fadeOut, 0f, 1f));
            }

            float alpha = this.alphaMap.get(hudRendered);
            if (alpha > 0f) {
                context.drawTexturedQuad(
                        texture,
                        x, x + width,
                        y, y + height,
                        0,
                        u / 256f, (u + width) / 256f,
                        v / 256f, (v + height) / 256f,
                        1f, 1f, 1f, alpha
                );
            }

            ArrayList<HudRendered> notRendered = new ArrayList<>();
            this.alphaMap.forEach((hud, opacity) -> {
                if (opacity <= 0f)
                    notRendered.add(hud);
            });
            for (HudRendered hud : notRendered)
                this.alphaMap.remove(hud);
        } else {
            context.drawTexture(texture, x, y, u, v, width, height);
        }

        context.getMatrices().pop();
    }

    @Redirect(method = "lambda$render$4", at = @At(value = "INVOKE", target = "Ljava/util/concurrent/atomic/AtomicInteger;getAndAdd(I)I"))
    private int specialRender2(AtomicInteger integer, int delta, @Local HudRendered hudRendered, @Local HudRender hudRender) {
        if (((CodeHudRender) hudRender).code_breaker_origin$shouldFade() && !this.alphaMap.containsKey(hudRendered))
            return integer.get();
        return integer.getAndAdd(delta);
    }
}
