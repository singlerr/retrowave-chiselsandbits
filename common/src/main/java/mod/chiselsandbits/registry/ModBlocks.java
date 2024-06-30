package mod.chiselsandbits.registry;

import static mod.chiselsandbits.registry.ModItemGroups.CHISELS_AND_BITS;

import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import mod.chiselsandbits.bitstorage.BlockBitStorage;
import mod.chiselsandbits.bitstorage.ItemBlockBitStorage;
import mod.chiselsandbits.bitstorage.ItemStackSpecialRendererBitStorage;
import mod.chiselsandbits.chiseledblock.BlockBitInfo;
import mod.chiselsandbits.chiseledblock.BlockChiseled;
import mod.chiselsandbits.chiseledblock.ItemBlockChiseled;
import mod.chiselsandbits.chiseledblock.MaterialType;
import mod.chiselsandbits.core.ChiselsAndBits;
import mod.chiselsandbits.printer.ChiselPrinterBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public final class ModBlocks {

    private static final DeferredRegister<Block> BLOCK_REGISTRAR =
            DeferredRegister.create(ChiselsAndBits.MODID, Registries.BLOCK);
    private static final DeferredRegister<Item> ITEM_REGISTRAR =
            DeferredRegister.create(ChiselsAndBits.MODID, Registries.ITEM);

    public static final Map<String, RegistrySupplier<BlockChiseled>> MATERIAL_TO_BLOCK_CONVERSIONS = Maps.newHashMap();
    public static final Map<String, RegistrySupplier<ItemBlockChiseled>> MATERIAL_TO_ITEM_CONVERSIONS =
            Maps.newHashMap();

    public static final RegistrySupplier<BlockBitStorage> BIT_STORAGE_BLOCK = BLOCK_REGISTRAR.register(
            "bit_storage",
            () -> new BlockBitStorage(BlockBehaviour.Properties.of()
                    .explosionResistance(6.0F)
                    .requiresCorrectToolForDrops()
                    .forceSolidOff()
                    .isValidSpawn((blockState, blockGetter, blockPos, object) -> false)
                    .isSuffocating((p_test_1_, p_test_2_, p_test_3_) -> false)
                    .isViewBlocking((p_test_1_, p_test_2_, p_test_3_) -> false)
            ));

    public static final RegistrySupplier<BlockItem> BIT_STORAGE_BLOCK_ITEM = ITEM_REGISTRAR.register(
            "bit_storage",
            () -> new ItemBlockBitStorage(
                    BIT_STORAGE_BLOCK.get(),
                    new Item.Properties()
                            .arch$tab(CHISELS_AND_BITS)
                            .setISTER(() -> ItemStackSpecialRendererBitStorage::new)));
    public static final RegistrySupplier<ChiselPrinterBlock> CHISEL_PRINTER_BLOCK = BLOCK_REGISTRAR.register(
            "chisel_printer",
            () -> new ChiselPrinterBlock(BlockBehaviour.Properties.of()
                    .strength(1.5f,6f)
                    .destroyTime(1)
                    .forceSolidOff()
                    .emissiveRendering((blockState, blockGetter, blockPos) -> false)
                    .isViewBlocking((p_test_1_, p_test_2_, p_test_3_) -> false)));

    public static final RegistrySupplier<BlockItem> CHISEL_PRINTER_ITEM = ITEM_REGISTRAR.register(
            "chisel_printer",
            () -> new BlockItem(ModBlocks.CHISEL_PRINTER_BLOCK.get(), new Item.Properties().arch$tab(CHISELS_AND_BITS)));

    public static final MaterialType[] VALID_CHISEL_MATERIALS = new MaterialType[] {
        new MaterialType("wood", "wood"),
        new MaterialType("rock", "rock"),
        new MaterialType("iron", "iron"),
        new MaterialType("cloth", "cloth"),
        new MaterialType("ice", "ice"),
        new MaterialType("packed_ice", "packed_ice"),
        new MaterialType("clay", "clay"),
        new MaterialType("glass", "glass"),
        new MaterialType("sand", "sand"),
        new MaterialType("ground", "ground"),
        new MaterialType("grass", "grass"),
        new MaterialType("snow", "snow"),
        new MaterialType("fluid", "fluid"),
        new MaterialType("leaves", "leaves"),
    };

    private ModBlocks() {
        throw new IllegalStateException("Tried to initialize: ModBlocks but this is a Utility class.");
    }

    public static void onModConstruction() {
        BLOCK_REGISTRAR.register();
        ITEM_REGISTRAR.register();

        Arrays.stream(VALID_CHISEL_MATERIALS).forEach(materialType -> {
            MATERIAL_TO_BLOCK_CONVERSIONS.put(
                    materialType.getType(),
                    BLOCK_REGISTRAR.register(
                            "chiseled" + materialType.getName(),
                            () -> new BlockChiseled(
                                    "chiseled_" + materialType.getName(),
                                    BlockBehaviour.Properties.of()
                                            .strength(1.5f, 6f)
                                            .isViewBlocking((p_test_1_, p_test_2_, p_test_3_) -> false)
                                            .emissiveRendering((blockState, blockGetter, blockPos) -> false)
                                            .forceSolidOff())));
            MATERIAL_TO_ITEM_CONVERSIONS.put(
                    materialType.getType(),
                    ITEM_REGISTRAR.register(
                            "chiseled" + materialType.getName(),
                            () -> new ItemBlockChiseled(
                                    MATERIAL_TO_BLOCK_CONVERSIONS
                                            .get(materialType.getType())
                                            .get(),
                                    new Item.Properties())));
        });
    }

    public static Map<String, RegistrySupplier<ItemBlockChiseled>> getMaterialToItemConversions() {
        return MATERIAL_TO_ITEM_CONVERSIONS;
    }

    public static Map<String, RegistrySupplier<BlockChiseled>> getMaterialToBlockConversions() {
        return MATERIAL_TO_BLOCK_CONVERSIONS;
    }

    public static MaterialType[] getValidChiselMaterials() {
        return VALID_CHISEL_MATERIALS;
    }

    @Nullable
    public static BlockState getChiseledDefaultState() {
        final Iterator<RegistrySupplier<BlockChiseled>> blockIterator =
                getMaterialToBlockConversions().values().iterator();
        if (blockIterator.hasNext()) return blockIterator.next().get().getDefaultState();

        return null;
    }

    public static BlockChiseled convertGivenStateToChiseledBlock(final BlockState state) {
        final Fluid f = BlockBitInfo.getFluidFromBlock(state.getBlock());
        return convertGivenMaterialToChiseledBlock(f != null ? Material.WATER : state.getMaterial());
    }

    public static BlockChiseled convertGivenMaterialToChiseledBlock(final Material material) {
        final RegistrySupplier<BlockChiseled> materialBlock =
                getMaterialToBlockConversions().get(material);
        return materialBlock != null ? materialBlock.get() : convertGivenMaterialToChiseledBlock(Material.ROCK);
    }

    public static RegistrySupplier<BlockChiseled> convertGivenStateToChiseledRegistryBlock(final BlockState state) {
        final Fluid f = BlockBitInfo.getFluidFromBlock(state.getBlock());
        return convertGivenMaterialToChiseledRegistryBlock(f != null ? Material.WATER : state.getMaterial());
    }

    public static RegistrySupplier<BlockChiseled> convertGivenMaterialToChiseledRegistryBlock(final Material material) {
        final RegistrySupplier<BlockChiseled> materialBlock =
                getMaterialToBlockConversions().get(material);
        return materialBlock != null ? materialBlock : convertGivenMaterialToChiseledRegistryBlock(Material.ROCK);
    }

    public static boolean convertMaterialTo(final Material source, final Material target) {
        final RegistrySupplier<BlockChiseled> sourceRegisteredObject =
                convertGivenMaterialToChiseledRegistryBlock(source);
        return getMaterialToBlockConversions().put(target, sourceRegisteredObject) != null;
    }
}
