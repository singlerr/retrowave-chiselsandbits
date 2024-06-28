package mod.chiselsandbits.registry;

import lombok.experimental.UtilityClass;
import mod.chiselsandbits.ModBootstrap;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

@UtilityClass
public class ModTagRegistries {

    public static void initialize() {
        Items.init();
        Blocks.init();
    }

    public final class Items {
        private static void init() {}

        public static TagKey<Item> CHISEL = tag("chisel");
        public static TagKey<Item> BIT_BAG = tag("bit_bag");

        private static TagKey<Item> tag(String name) {

            return ItemTags.bind(ModBootstrap.MOD_ID + ":" + name);
        }
    }

    public final class Blocks {
        private static void init() {}

        public static TagKey<Block> FORCED_CHISELABLE = tag("chiselable/forced");
        public static TagKey<Block> BLOCKED_CHISELABLE = tag("chiselable/blocked");

        private static TagKey<Block> tag(String name) {
            return BlockTags.create(ModBootstrap.MOD_ID + ":" + name);
        }
    }
}
