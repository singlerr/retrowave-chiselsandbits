package mod.chiselsandbits.data.recipe;

import com.ldtteam.datagenerators.recipes.RecipeIngredientJson;
import com.ldtteam.datagenerators.recipes.RecipeIngredientKeyJson;
import java.io.IOException;
import java.util.Objects;
import mod.chiselsandbits.core.ChiselsAndBits;
import mod.chiselsandbits.registry.ModBlocks;
import mod.chiselsandbits.registry.ModTags;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.Tags;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = ChiselsAndBits.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ChiselPrinterRecipeGenerator extends AbstractRecipeGenerator {
    @SubscribeEvent
    public static void dataGeneratorSetup(final GatherDataEvent event) {
        event.getGenerator().addProvider(new ChiselPrinterRecipeGenerator(event.getGenerator()));
    }

    private ChiselPrinterRecipeGenerator(final DataGenerator generator) {
        super(generator, ModBlocks.CHISEL_PRINTER_BLOCK.get());
    }

    @Override
    protected void generate() throws IOException {
        addShapedRecipe(
                " c ",
                "t t",
                "sss",
                "s",
                new RecipeIngredientKeyJson(new RecipeIngredientJson(
                        Objects.requireNonNull(Blocks.SMOOTH_STONE_SLAB.getRegistryName())
                                .toString(),
                        false)),
                "t",
                new RecipeIngredientKeyJson(new RecipeIngredientJson(
                        Tags.Items.RODS_WOODEN.getName().toString(), true)),
                "c",
                new RecipeIngredientKeyJson(
                        new RecipeIngredientJson(ModTags.Items.CHISEL.getName().toString(), true)));
    }
}
