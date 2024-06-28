package mod.chiselsandbits.registry;

import dev.architectury.registry.registries.RegistrySupplier;
import lombok.experimental.UtilityClass;
import mod.chiselsandbits.ModBootstrap;
import mod.chiselsandbits.block.entity.ChiseledBlockEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;

@UtilityClass
public class ModBlockEntityRegistries {

    public final RegistrySupplier<BlockEntityType<ChiseledBlockEntity>> CHISELED_BLOCK_ENTITY =
            ModRegistrars.BLOCK_ENTITY_REGISTRAR.register(
                    new ResourceLocation(ModBootstrap.MOD_ID, "chiseled_block_entity"),
                    () -> BlockEntityType.Builder.of(ChiseledBlockEntity::new, ModBlockRegistries.CHISELED_BLOCK.get())
                            .build(null));
}
