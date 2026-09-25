package coolguy.kutikiplayz.render;

import coolguy.kutikiplayz.extension.shader.CodeRenderLayer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.BufferAllocator;

import java.util.Optional;

@Environment(EnvType.CLIENT)
public class CodeVertexConsumerProvider implements VertexConsumerProvider {
    private final VertexConsumerProvider.Immediate parent;
    private final VertexConsumerProvider.Immediate plainDrawer = VertexConsumerProvider.immediate(new BufferAllocator(1536));

    public CodeVertexConsumerProvider(VertexConsumerProvider.Immediate parent) {
        this.parent = parent;
    }

    @Override public VertexConsumer getBuffer(RenderLayer renderLayer) {
        if (((CodeRenderLayer) renderLayer).code_breaker_origin$isCode()) {
            VertexConsumer vertexConsumer = this.plainDrawer.getBuffer(renderLayer);
            return new CodeVertexConsumer(vertexConsumer);
        } else {
            VertexConsumer vertexConsumer = this.parent.getBuffer(renderLayer);
            Optional<RenderLayer> optional = ((CodeRenderLayer) renderLayer).code_breaker_origin$getAffectedCodeVision();
            if (optional.isPresent()) {
                VertexConsumer vertexConsumer2 = this.plainDrawer.getBuffer(optional.get());
                return new CodeVertexConsumer(vertexConsumer2);
            } else {
                return vertexConsumer;
            }
        }
    }

    public void draw() {
        this.plainDrawer.draw();
    }

    @Environment(EnvType.CLIENT)
    record CodeVertexConsumer(VertexConsumer delegate) implements VertexConsumer {
        @Override public VertexConsumer vertex(float x, float y, float z) {
            this.delegate.vertex(x, y, z);
            return this;
        }

        @Override public VertexConsumer color(int red, int green, int blue, int alpha) { return this; }

        @Override public VertexConsumer texture(float u, float v) {
            this.delegate.texture(u, v);
            return this;
        }

        @Override public VertexConsumer overlay(int u, int v) { return this; }

        @Override public VertexConsumer light(int u, int v) { return this; }

        @Override public VertexConsumer normal(float x, float y, float z) { return this; }
    }
}
