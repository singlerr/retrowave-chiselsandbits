package mod.chiselsandbits.registry;

import com.google.common.base.Suppliers;
import com.google.common.collect.Maps;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import dev.architectury.registry.registries.RegistrySupplier;
import java.util.Map;
import java.util.function.Supplier;
import lombok.experimental.UtilityClass;
import mod.chiselsandbits.ModBootstrap;
import mod.chiselsandbits.block.ChiseledBlock;
import mod.chiselsandbits.item.ChiseledBlockItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

@UtilityClass
public class ModRegistrars {

    public final Supplier<RegistrarManager> REGISTRAR_MANAGER =
            Suppliers.memoize(() -> RegistrarManager.get(ModBootstrap.MOD_ID));

    public final Registrar<Block> BLOCK_REGISTRAR = REGISTRAR_MANAGER.get().get(Registries.BLOCK);

    public final Registrar<Item> ITEM_REGISTRAR = REGISTRAR_MANAGER.get().get(Registries.ITEM);

    public final Registrar<BlockEntityType<?>> BLOCK_ENTITY_REGISTRAR =
            REGISTRAR_MANAGER.get().get(Registries.BLOCK_ENTITY_TYPE);

    public final Map<String, RegistrySupplier<ChiseledBlock>> MATERIAL_TO_BLOCK_CONVERSIONS = Maps.newHashMap();

    public final Map<String, RegistrySupplier<ChiseledBlockItem>> MATERIAL_TO_ITEM_CONVERSIONS = Maps.newHashMap();
}
