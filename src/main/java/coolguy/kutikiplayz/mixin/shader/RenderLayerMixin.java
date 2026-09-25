package coolguy.kutikiplayz.mixin.shader;

import coolguy.kutikiplayz.extension.shader.CodeMultiPhaseParameters;
import coolguy.kutikiplayz.extension.shader.CodeRenderLayer;
import coolguy.kutikiplayz.mixin.accessor.MultiPhaseParametersAccessor;
import coolguy.kutikiplayz.render.CodeRenderLayers;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexFormat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(RenderLayer.class)
public abstract class RenderLayerMixin implements CodeRenderLayer {
    @Override public boolean code_breaker_origin$isCode() { return false; }

    @Mixin(RenderLayer.MultiPhase.class)
    private static abstract class MultiPhaseMixin implements CodeRenderLayer {
        @Unique private Optional<RenderLayer> affectedCodeVision;
        @Unique private boolean isCode;

        @Inject(method = "<init>", at = @At("TAIL"))
        private void init(String name, VertexFormat vertexFormat, VertexFormat.DrawMode drawMode, int expectedBufferSize, boolean hasCrumbling, boolean translucent, RenderLayer.MultiPhaseParameters phases, CallbackInfo ci) {
            this.affectedCodeVision = ((CodeMultiPhaseParameters) phases).code_breaker_origin$getCodeVisionMode() == CodeRenderLayers.CodeVisionMode.AFFECTS_CODE
                    ? ((MultiPhaseParametersAccessor) phases).getTexture().getId().map(texture -> CodeRenderLayers.CODE_LAYER.apply(texture, ((MultiPhaseParametersAccessor) phases).getCull()))
                    : Optional.empty();
            this.isCode = ((CodeMultiPhaseParameters) phases).code_breaker_origin$getCodeVisionMode() == CodeRenderLayers.CodeVisionMode.IS_CODE;
        }

        @Override public Optional<RenderLayer> code_breaker_origin$getAffectedCodeVision() { return this.affectedCodeVision; }
        @Override public boolean code_breaker_origin$isCode() { return this.isCode; }
    }

    @Mixin(RenderLayer.MultiPhaseParameters.class)
    private static abstract class MultiPhaseParametersMixin implements CodeMultiPhaseParameters {
        @Unique CodeRenderLayers.CodeVisionMode codeVisionMode;
        @Override public CodeRenderLayers.CodeVisionMode code_breaker_origin$getCodeVisionMode() { return this.codeVisionMode; }
        @Override public void code_breaker_origin$setCodeVisionMode(CodeRenderLayers.CodeVisionMode codeVisionMode) { this.codeVisionMode = codeVisionMode; }

        @Mixin(RenderLayer.MultiPhaseParameters.Builder.class)
        private static abstract class MultiPhaseParametersBuilderMixin implements CodeMultiPhaseParameters {
            @Unique CodeRenderLayers.CodeVisionMode codeVisionMode;

            @Override public RenderLayer.MultiPhaseParameters.Builder code_breaker_origin$codeVision(boolean affectsCodeVision) {
                return this.code_breaker_origin$codeVision(affectsCodeVision ? CodeRenderLayers.CodeVisionMode.AFFECTS_CODE : CodeRenderLayers.CodeVisionMode.NONE);
            }
            @Override public RenderLayer.MultiPhaseParameters.Builder code_breaker_origin$codeVision(CodeRenderLayers.CodeVisionMode codeVisionMode) {
                this.codeVisionMode = codeVisionMode;
                return (RenderLayer.MultiPhaseParameters.Builder) (Object) this;
            }

            // Pretty much everything that should be able to glow should also be able to be in code vision.
            // I can make manual adjustments on a per-RenderLayer basis if need be.
            @Inject(method = "build(Z)Lnet/minecraft/client/render/RenderLayer$MultiPhaseParameters;", at = @At("RETURN"), cancellable = true)
            private void build(boolean affectsOutline, CallbackInfoReturnable<RenderLayer.MultiPhaseParameters> cir) {
                RenderLayer.MultiPhaseParameters parameters = cir.getReturnValue();
                ((CodeMultiPhaseParameters) parameters).code_breaker_origin$setCodeVisionMode(
                        this.codeVisionMode != null ? this.codeVisionMode :
                        affectsOutline ? CodeRenderLayers.CodeVisionMode.AFFECTS_CODE : CodeRenderLayers.CodeVisionMode.NONE);
                cir.setReturnValue(parameters);
            }

            @Inject(method = "build(Lnet/minecraft/client/render/RenderLayer$OutlineMode;)Lnet/minecraft/client/render/RenderLayer$MultiPhaseParameters;", at = @At("RETURN"), cancellable = true)
            private void build(RenderLayer.OutlineMode outlineMode, CallbackInfoReturnable<RenderLayer.MultiPhaseParameters> cir) {
                RenderLayer.MultiPhaseParameters parameters = cir.getReturnValue();
                ((CodeMultiPhaseParameters) parameters).code_breaker_origin$setCodeVisionMode(this.codeVisionMode);
                cir.setReturnValue(parameters);
            }
        }
    }
}
