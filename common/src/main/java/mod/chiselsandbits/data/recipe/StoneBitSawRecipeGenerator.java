package mod.chiselsandbits.data.recipe;

import com.ldtteam.datagenerators.recipes.RecipeIngredientJson;
import com.ldtteam.datagenerators.recipes.RecipeIngredientKeyJson;
import java.io.IOException;
import mod.chiselsandbits.core.ChiselsAndBits;
import mod.chiselsandbits.registry.ModItems;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.Tags;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = ChiselsAndBits.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class StoneBitSawRecipeGenerator extends AbstractRecipeGenerator {
    @SubscribeEvent
    public static void dataGeneratorSetup(final GatherDataEvent event) {
        event.getGenerator().addProvider(new StoneBitSawRecipeGenerator(event.getGenerator()));
    }

    private StoneBitSawRecipeGenerator(final DataGenerator generator) {
        super(generator, ModItems.ITEM_BIT_SAW_STONE.get());
    }

    @Override
    protected void generate() throws IOException {
        addShapedRecipe(
                "sss",
                "stt",
                "   ",
                "s",
                new RecipeIngredientKeyJson(new RecipeIngredientJson(
                        Tags.Items.RODS_WOODEN.getName().toString(), true)),
                "t",
                new RecipeIngredientKeyJson(
                        new RecipeIngredientJson(Tags.Items.STONE.getName().toString(), true)));
    }
}
