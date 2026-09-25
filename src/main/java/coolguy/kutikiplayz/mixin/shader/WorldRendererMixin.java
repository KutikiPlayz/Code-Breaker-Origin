package coolguy.kutikiplayz.mixin.shader;

import com.google.gson.JsonSyntaxException;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import coolguy.kutikiplayz.extension.shader.CodeBufferBuilderStorage;
import coolguy.kutikiplayz.extension.shader.CodeLivingEntity;
import coolguy.kutikiplayz.extension.shader.CodeWorldRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.render.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin implements CodeWorldRenderer {
    @Shadow @Final private static Logger LOGGER;
    @Shadow @Final private MinecraftClient client;
    @Shadow @Final private BufferBuilderStorage bufferBuilders;

    @Unique @Nullable private Framebuffer codeEntitiesFramebuffer;
    @Unique @Nullable private PostEffectProcessor codeEntitiesPostProcessor;

    @Inject(method = "close", at = @At("HEAD"))
    private void close(CallbackInfo ci) {
        if (this.codeEntitiesPostProcessor != null)
            this.codeEntitiesPostProcessor.close();
    }

    @Inject(method = "reload(Lnet/minecraft/resource/ResourceManager;)V", at = @At("HEAD"))
    private void reload(ResourceManager manager, CallbackInfo ci) {
        this.loadCodeEntitiesPostProcessor();
    }

    @Unique private void loadCodeEntitiesPostProcessor() {
        if (this.codeEntitiesPostProcessor != null)
            this.codeEntitiesPostProcessor.close();

        Identifier shader = Identifier.ofVanilla("shaders/post/entity_code.json");

        try {
            this.codeEntitiesPostProcessor = new PostEffectProcessor(
                this.client.getTextureManager(), this.client.getResourceManager(), this.client.getFramebuffer(), shader
            );
            this.codeEntitiesPostProcessor.setupDimensions(this.client.getWindow().getFramebufferWidth(), this.client.getWindow().getFramebufferHeight());
            this.codeEntitiesFramebuffer = this.codeEntitiesPostProcessor.getSecondaryTarget("final");
        } catch (IOException ioException) {
            LOGGER.warn("Failed to load shader: {}", shader, ioException);
            this.codeEntitiesPostProcessor = null;
            this.codeEntitiesFramebuffer = null;
        } catch (JsonSyntaxException jsonSyntaxException) {
            LOGGER.warn("Failed to parse shader: {}", shader, jsonSyntaxException);
            this.codeEntitiesPostProcessor = null;
            this.codeEntitiesFramebuffer = null;
        }
    }

    @Override public void code_breaker_origin$drawCodeEntitiesFramebuffer() {
        if (this.canDrawCodeEntities()) {
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(
                GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE
            );
            this.codeEntitiesFramebuffer.draw(this.client.getWindow().getFramebufferWidth(), this.client.getWindow().getFramebufferHeight(), false);
            RenderSystem.disableBlend();
            RenderSystem.defaultBlendFunc();
        }
    }

    @Unique private boolean canDrawCodeEntities() {
        return !this.client.gameRenderer.isRenderingPanorama()
                && this.codeEntitiesFramebuffer != null
                && this.codeEntitiesPostProcessor != null
                && this.client.player != null;
    }

    @Inject(method = "onResized", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;scheduleTerrainUpdate()V", shift = At.Shift.AFTER))
    private void onResized(int width, int height, CallbackInfo ci) {
        if (this.codeEntitiesPostProcessor != null)
            this.codeEntitiesPostProcessor.setupDimensions(width, height);
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;canDrawEntityOutlines()Z", ordinal = 0))
    private void render(RenderTickCounter tickCounter, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, Matrix4f matrix4f2, CallbackInfo ci) {
        if (this.canDrawCodeEntities()) {
            this.codeEntitiesFramebuffer.clear(MinecraftClient.IS_SYSTEM_MAC);
            this.codeEntitiesFramebuffer.copyDepthFrom(this.client.getFramebuffer());
            this.client.getFramebuffer().beginWrite(false);
        }
    }

    @Unique private boolean hasCodeEntities;
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;applyModelViewMatrix()V", ordinal = 0, shift = At.Shift.AFTER))
    private void render2(RenderTickCounter tickCounter, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, Matrix4f matrix4f2, CallbackInfo ci) {
        this.hasCodeEntities = false;
    }

    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;renderEntity(Lnet/minecraft/entity/Entity;DDDFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;)V"), index = 6)
    private VertexConsumerProvider render3(VertexConsumerProvider vertexConsumers, @Local Entity entity) {
        if (this.canDrawCodeEntities() && entity instanceof LivingEntity livingEntity && ((CodeLivingEntity) livingEntity).code_breaker_origin$inCodeVision()) {
            this.hasCodeEntities = true;
            return ((CodeBufferBuilderStorage) this.bufferBuilders).code_breaker_origin$getCodeVertexConsumers();
        }

        return vertexConsumers;
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/BufferBuilderStorage;getOutlineVertexConsumers()Lnet/minecraft/client/render/OutlineVertexConsumerProvider;", ordinal = 1))
    private void render4(RenderTickCounter tickCounter, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, Matrix4f matrix4f2, CallbackInfo ci) {
        ((CodeBufferBuilderStorage) this.bufferBuilders).code_breaker_origin$getCodeVertexConsumers().draw();
        if (this.hasCodeEntities) {
            this.codeEntitiesPostProcessor.render(tickCounter.getLastFrameDuration());
            this.client.getFramebuffer().beginWrite(false);
        }
    }

    @Override public @Nullable Framebuffer code_breaker_origin$getCodeEntitiesFramebuffer() {
        return this.codeEntitiesFramebuffer;
    }
}
