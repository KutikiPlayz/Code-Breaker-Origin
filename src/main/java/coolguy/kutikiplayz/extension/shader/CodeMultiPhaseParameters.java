package coolguy.kutikiplayz.extension.shader;

import coolguy.kutikiplayz.render.CodeRenderLayers;
import net.minecraft.client.render.RenderLayer;

public interface CodeMultiPhaseParameters {
    CodeRenderLayers.CodeVisionMode code_breaker_origin$getCodeVisionMode();
    void code_breaker_origin$setCodeVisionMode(CodeRenderLayers.CodeVisionMode codeVisionMode);

    RenderLayer.MultiPhaseParameters.Builder code_breaker_origin$codeVision(boolean affectsCodeVision);
    RenderLayer.MultiPhaseParameters.Builder code_breaker_origin$codeVision(CodeRenderLayers.CodeVisionMode codeVisionMode);
}
