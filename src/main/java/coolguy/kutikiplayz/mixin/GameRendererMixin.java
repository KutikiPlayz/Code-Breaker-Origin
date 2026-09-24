package coolguy.kutikiplayz.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.util.Pair;
import coolguy.kutikiplayz.extension.CodeWorldRenderer;
import coolguy.kutikiplayz.render.CodeGameRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.resource.ResourceFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Shadow @Final MinecraftClient client;

    @Inject(method = "loadPrograms", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;clearPrograms()V"))
    private void loadPrograms(ResourceFactory factory, CallbackInfo ci, @Local(ordinal = 1) List<Pair<ShaderProgram, Consumer<ShaderProgram>>> list2) {
        try {
            list2.add(Pair.of(new ShaderProgram(factory, "rendertype_code", VertexFormats.POSITION_TEXTURE), CodeGameRenderer::setRenderTypeCodeProgram));
        } catch (IOException ioException) {
            list2.forEach(pair -> pair.getFirst().close());
            throw new RuntimeException("could not reload shaders", ioException);
        }
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;drawEntityOutlinesFramebuffer()V"))
    private void render(RenderTickCounter tickCounter, boolean tick, CallbackInfo ci) {
        ((CodeWorldRenderer) this.client.worldRenderer).code_breaker_origin$drawCodeEntitiesFramebuffer();
    }
}
