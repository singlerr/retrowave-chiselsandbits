package mod.chiselsandbits.registry;

import dev.architectury.registry.registries.RegistrySupplier;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import mod.chiselsandbits.ModBootstrap;
import mod.chiselsandbits.block.ChiseledBlock;
import mod.chiselsandbits.material.LegacyMaterials;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockBehaviour;

@Slf4j
@UtilityClass
public final class ModBlockRegistries {

    public final RegistrySupplier<ChiseledBlock> CHISELED_BLOCK = ModRegistrars.BLOCK_REGISTRAR.register(
            new ResourceLocation(ModBootstrap.MOD_ID, "chiseled_block"),
            () -> new ChiseledBlock(
                    "chiseled_block",
                    BlockBehaviour.Properties.of()
                            .strength(1.5f, 6f)
                            .isRedstoneConductor((blockState, blockGetter, blockPos) -> false)
                            .isValidSpawn((blockState, blockGetter, blockPos, object) -> false)
                            .isSuffocating((blockState, blockGetter, blockPos) -> false)
                            .noOcclusion()));

    public void initialize() {
        for (String material : LegacyMaterials.LEGACY_MATERIALS) {
            ModRegistrars.MATERIAL_TO_BLOCK_CONVERSIONS.put(
                    material,
                    ModRegistrars.BLOCK_REGISTRAR.register(
                            new ResourceLocation(ModBootstrap.MOD_ID, "chiseled_%s".formatted(material)),
                            () -> new ChiseledBlock(
                                    "chiseled_%s".formatted(material),
                                    BlockBehaviour.Properties.of()
                                            .strength(1.5f, 6f)
                                            .isRedstoneConductor((blockState, blockGetter, blockPos) -> false)
                                            .isValidSpawn((blockState, blockGetter, blockPos, object) -> false)
                                            .isSuffocating((blockState, blockGetter, blockPos) -> false)
                                            .noOcclusion())));
        }
        log.info("Loaded chiseled blocks for {} material types", LegacyMaterials.LEGACY_MATERIALS.size());
    }
}
