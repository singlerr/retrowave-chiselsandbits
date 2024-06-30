package mod.chiselsandbits.data.recipe;

import com.ldtteam.datagenerators.recipes.RecipeIngredientJson;
import com.ldtteam.datagenerators.recipes.RecipeIngredientKeyJson;
import java.io.IOException;
import mod.chiselsandbits.core.ChiselsAndBits;
import mod.chiselsandbits.registry.ModItems;
import mod.chiselsandbits.utils.Constants;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = ChiselsAndBits.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MagnifyingGlassRecipeGenerator extends AbstractRecipeGenerator {

    @SubscribeEvent
    public static void dataGeneratorSetup(final GatherDataEvent event) {
        event.getGenerator().addProvider(new MagnifyingGlassRecipeGenerator(event.getGenerator()));
    }

    private MagnifyingGlassRecipeGenerator(final DataGenerator generator) {
        super(generator, ModItems.ITEM_MAGNIFYING_GLASS.get());
    }

    @Override
    protected void generate() throws IOException {
        addShapedRecipe(
                "cg ",
                "s  ",
                "   ",
                "c",
                new RecipeIngredientKeyJson(
                        new RecipeIngredientJson(new ResourceLocation(Constants.MOD_ID, "chisel").toString(), true)),
                "g",
                new RecipeIngredientKeyJson(
                        new RecipeIngredientJson(Tags.Items.GLASS.getName().toString(), true)),
                "s",
                new RecipeIngredientKeyJson(new RecipeIngredientJson(
                        Tags.Items.RODS_WOODEN.getName().toString(), true)));
    }
}
