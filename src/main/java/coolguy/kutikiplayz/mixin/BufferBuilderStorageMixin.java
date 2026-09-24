package coolguy.kutikiplayz.mixin;

import coolguy.kutikiplayz.extension.CodeBufferBuilderStorage;
import coolguy.kutikiplayz.render.CodeVertexConsumerProvider;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.VertexConsumerProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BufferBuilderStorage.class)
public abstract class BufferBuilderStorageMixin implements CodeBufferBuilderStorage {
    @Shadow @Final private VertexConsumerProvider.Immediate entityVertexConsumers;

    @Unique private CodeVertexConsumerProvider codeVertexConsumers;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(int maxBlockBuildersPoolSize, CallbackInfo ci) {
        this.codeVertexConsumers = new CodeVertexConsumerProvider(this.entityVertexConsumers);
    }

    @Override public CodeVertexConsumerProvider code_breaker_origin$getCodeVertexConsumers() {
        return this.codeVertexConsumers;
    }
}
