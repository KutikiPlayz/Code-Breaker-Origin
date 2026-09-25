package coolguy.kutikiplayz.mixin.power.night_owl;

import coolguy.kutikiplayz.extension.power.CodePreventSleepPowerType;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.type.PreventSleepPowerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.SleepManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SleepManager.class)
public abstract class SleepManagerMixin {
    @Redirect(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;isSpectator()Z"))
    private boolean ignoreCertainPlayers(ServerPlayerEntity player) {
        boolean shouldBeIgnored = PowerHolderComponent.getPowerTypes(player, PreventSleepPowerType.class)
                .stream().anyMatch((powerType) -> ((CodePreventSleepPowerType) powerType).code_breaker_origin$shouldIgnoreForSleepCount());
        return player.isSpectator() || shouldBeIgnored;
    }
}
