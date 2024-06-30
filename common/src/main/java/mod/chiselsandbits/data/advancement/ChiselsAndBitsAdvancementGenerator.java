package mod.chiselsandbits.data.advancement;

import java.util.function.Consumer;
import mod.chiselsandbits.core.ChiselsAndBits;
import mod.chiselsandbits.registry.ModBlocks;
import mod.chiselsandbits.registry.ModItems;
import mod.chiselsandbits.registry.ModTags;
import mod.chiselsandbits.utils.Constants;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.criterion.*;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = ChiselsAndBits.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ChiselsAndBitsAdvancementGenerator extends AbstractAdvancementGenerator {
    @SubscribeEvent
    public static void dataGeneratorSetup(final GatherDataEvent event) {
        event.getGenerator().addProvider(new ChiselsAndBitsAdvancementGenerator(event.getGenerator()));
    }

    private ChiselsAndBitsAdvancementGenerator(final DataGenerator generator) {
        super(generator, ChiselsAndBitsAdvancementGenerator::build);
    }

    private static void build(Consumer<Advancement> register) {
        Advancement root = Advancement.Builder.builder()
                .withDisplay(
                        ModItems.ITEM_CHISEL_DIAMOND.get(),
                        new TranslationTextComponent("mod.chiselsandbits.advancements.root.title"),
                        new TranslationTextComponent("mod.chiselsandbits.advancements.root.description"),
                        new ResourceLocation("textures/gui/advancements/backgrounds/stone.png"),
                        FrameType.CHALLENGE,
                        true,
                        true,
                        true)
                .withCriterion(
                        "chisel",
                        InventoryChangeTrigger.Instance.forItems(new ItemPredicate(
                                ModTags.Items.CHISEL,
                                null,
                                MinMaxBounds.IntBound.UNBOUNDED,
                                MinMaxBounds.IntBound.UNBOUNDED,
                                new EnchantmentPredicate[0],
                                new EnchantmentPredicate[0],
                                null,
                                NBTPredicate.ANY)))
                .register(register, Constants.MOD_ID + ":chiselsandbits/root");

        Advancement findChiselables = Advancement.Builder.builder()
                .withParent(root)
                .withDisplay(
                        ModItems.ITEM_MAGNIFYING_GLASS.get(),
                        new TranslationTextComponent("mod.chiselsandbits.advancements.find-chiselables.title"),
                        new TranslationTextComponent("mod.chiselsandbits.advancements.find-chiselables.description"),
                        new ResourceLocation("textures/gui/advancements/backgrounds/stone.png"),
                        FrameType.TASK,
                        true,
                        true,
                        true)
                .withCriterion(
                        "magnifier_glass",
                        InventoryChangeTrigger.Instance.forItems(ModItems.ITEM_MAGNIFYING_GLASS.get()))
                .register(register, Constants.MOD_ID + ":chiselsandbits/find_chiselables");

        Advancement collectBits = Advancement.Builder.builder()
                .withParent(root)
                .withDisplay(
                        ModItems.ITEM_BIT_BAG_DEFAULT.get(),
                        new TranslationTextComponent("mod.chiselsandbits.advancements.collect-bits.title"),
                        new TranslationTextComponent("mod.chiselsandbits.advancements.collect-bits.description"),
                        new ResourceLocation("textures/gui/advancements/backgrounds/stone.png"),
                        FrameType.TASK,
                        true,
                        true,
                        true)
                .withCriterion(
                        "bit_bag",
                        InventoryChangeTrigger.Instance.forItems(new ItemPredicate(
                                ModTags.Items.BIT_BAG,
                                null,
                                MinMaxBounds.IntBound.UNBOUNDED,
                                MinMaxBounds.IntBound.UNBOUNDED,
                                new EnchantmentPredicate[0],
                                new EnchantmentPredicate[0],
                                null,
                                NBTPredicate.ANY)))
                .register(register, Constants.MOD_ID + ":chiselsandbits/collect_bits");

        Advancement makeTank = Advancement.Builder.builder()
                .withParent(root)
                .withDisplay(
                        ModBlocks.BIT_STORAGE_BLOCK_ITEM.get(),
                        new TranslationTextComponent("mod.chiselsandbits.advancements.make-tank.title"),
                        new TranslationTextComponent("mod.chiselsandbits.advancements.make-tank.description"),
                        new ResourceLocation("textures/gui/advancements/backgrounds/stone.png"),
                        FrameType.TASK,
                        true,
                        true,
                        true)
                .withCriterion(
                        "bit_tank", InventoryChangeTrigger.Instance.forItems(ModBlocks.BIT_STORAGE_BLOCK_ITEM.get()))
                .register(register, Constants.MOD_ID + ":chiselsandbits/make_tank");
    }

    @Override
    public String getName() {
        return "Chisels and bits default advancement generator";
    }
}
