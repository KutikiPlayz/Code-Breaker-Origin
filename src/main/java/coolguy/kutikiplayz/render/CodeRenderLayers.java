package coolguy.kutikiplayz.render;

import coolguy.kutikiplayz.extension.CodeMultiPhaseParameters;
import coolguy.kutikiplayz.extension.CodeWorldRenderer;
import coolguy.kutikiplayz.mixin.accessor.MultiPhaseAccessor;
import coolguy.kutikiplayz.mixin.accessor.MultiPhaseParametersAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.function.BiFunction;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class CodeRenderLayers {
    static final RenderPhase.ShaderProgram CODE_PROGRAM = new RenderPhase.ShaderProgram(CodeGameRenderer::getRenderTypeCodeProgram);

    static final RenderPhase.Target CODE_TARGET = new RenderPhase.Target(
            "code_target",
            () -> ((CodeWorldRenderer) MinecraftClient.getInstance().worldRenderer).code_breaker_origin$getCodeEntitiesFramebuffer().beginWrite(false),
            () -> MinecraftClient.getInstance().getFramebuffer().beginWrite(false)
    );


    public static Identifier getTextureFromRenderLayer(RenderLayer renderLayer) {
        if (!(renderLayer instanceof RenderLayer.MultiPhase multiPhase)) return null;
        return ((MultiPhaseParametersAccessor) ((MultiPhaseAccessor) multiPhase).phases())
                .getTexture().getId().orElse(null);
    }

    public static final BiFunction<Identifier, RenderPhase.Cull, RenderLayer> CODE_LAYER = Util.memoize(
            (texture, culling) -> RenderLayer.of(
                    "code",
                    VertexFormats.POSITION_TEXTURE,
                    VertexFormat.DrawMode.QUADS,
                    1536,
                    ((CodeMultiPhaseParameters) RenderLayer.MultiPhaseParameters.builder())
                            .code_breaker_origin$codeVision(CodeVisionMode.IS_CODE)
                            .program(CODE_PROGRAM)
                            .texture(new RenderPhase.Texture(texture, false, false))
                            .cull(culling)
                            .target(CODE_TARGET)
                            .build(RenderLayer.OutlineMode.NONE)
            )
    );

    public static RenderLayer getEntityCutoutNoCode(Identifier texture) {
        return ENTITY_CUTOUT_NO_CODE.apply(texture);
    }

    private static final Function<Identifier, RenderLayer> ENTITY_CUTOUT_NO_CODE = Util.memoize(
            texture -> {
                RenderLayer.MultiPhaseParameters multiPhaseParameters = ((CodeMultiPhaseParameters) RenderLayer.MultiPhaseParameters.builder())
                        .code_breaker_origin$codeVision(false)
                        .program(RenderPhase.ENTITY_CUTOUT_PROGRAM)
                        .texture(new RenderPhase.Texture(texture, false, false))
                        .transparency(RenderPhase.NO_TRANSPARENCY)
                        .lightmap(RenderPhase.ENABLE_LIGHTMAP)
                        .overlay(RenderPhase.ENABLE_OVERLAY_COLOR)
                        .build(true);
                return RenderLayer.of("entity_cutout_no_code", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 1536, true, false, multiPhaseParameters);
            }
    );

    public static RenderLayer getEntityCutoutNoCullNoCode(Identifier texture) {
        return ENTITY_CUTOUT_NO_CULL_NO_CODE.apply(texture);
    }

    private static final Function<Identifier, RenderLayer> ENTITY_CUTOUT_NO_CULL_NO_CODE = Util.memoize(
            texture -> {
                RenderLayer.MultiPhaseParameters multiPhaseParameters = ((CodeMultiPhaseParameters) RenderLayer.MultiPhaseParameters.builder())
                        .code_breaker_origin$codeVision(false)
                        .program(RenderPhase.ENTITY_CUTOUT_NONULL_PROGRAM)
                        .texture(new RenderPhase.Texture(texture, false, false))
                        .transparency(RenderPhase.NO_TRANSPARENCY)
                        .cull(RenderPhase.DISABLE_CULLING)
                        .lightmap(RenderPhase.ENABLE_LIGHTMAP)
                        .overlay(RenderPhase.ENABLE_OVERLAY_COLOR)
                        .build(true);
                return RenderLayer.of("entity_cutout_no_cull_no_code", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 1536, true, false, multiPhaseParameters);
            }
    );


    @Environment(EnvType.CLIENT)
    public enum CodeVisionMode {
        NONE("none"),
        IS_CODE("is_code"),
        AFFECTS_CODE("affects_code");

        private final String name;

        CodeVisionMode(final String name) { this.name = name; }

        @Override public String toString() { return this.name; }
    }
}
