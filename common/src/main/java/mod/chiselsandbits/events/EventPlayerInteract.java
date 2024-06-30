package mod.chiselsandbits.events;

import java.util.WeakHashMap;
import mod.chiselsandbits.chiseledblock.BlockBitInfo;
import mod.chiselsandbits.core.ClientSide;
import mod.chiselsandbits.items.ItemChisel;
import mod.chiselsandbits.items.ItemChiseledBit;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Disable breaking blocks when using a chisel / bit, some items break too fast
 * for the other code to prevent which is where this comes in.
 *
 * This manages survival chisel actions, creative some how skips this and calls
 * onBlockStartBreak on its own, but when in creative this is called on the
 * server... which still needs to be canceled or it will break the block.
 *
 * The whole things, is very strange.
 */
public class EventPlayerInteract {

    private static WeakHashMap<PlayerEntity, Boolean> serverSuppressEvent = new WeakHashMap<PlayerEntity, Boolean>();

    public static void setPlayerSuppressionState(final PlayerEntity player, final boolean state) {
        if (state) {
            serverSuppressEvent.put(player, state);
        } else {
            serverSuppressEvent.remove(player);
        }
    }

    @SubscribeEvent
    public void interaction(final LeftClickBlock event) {
        if (event.getPlayer() != null && event.getUseItem() != Event.Result.DENY) {
            final ItemStack is = event.getItemStack();
            final boolean validEvent = event.getPos() != null && event.getWorld() != null;
            if (is != null
                    && (is.getItem() instanceof ItemChisel || is.getItem() instanceof ItemChiseledBit)
                    && validEvent) {
                final BlockState state = event.getWorld().getBlockState(event.getPos());
                if (BlockBitInfo.canChisel(state)) {
                    if (event.getWorld().isRemote) {
                        // this is called when the player is survival -
                        // client side.
                        is.getItem().onBlockStartBreak(is, event.getPos(), event.getPlayer());
                    }

                    // cancel interactions vs chiseable blocks, creative is
                    // magic.
                    event.setCanceled(true);
                }
            }
        }

        testInteractionSupression(event, event.getUseItem());
    }

    @SubscribeEvent
    public void interaction(final RightClickBlock event) {
        testInteractionSupression(event, event.getUseItem());
    }

    private void testInteractionSupression(final PlayerInteractEvent event, final Event.Result useItem) {
        // client is dragging...
        if (event.getWorld().isRemote) {
            if (ClientSide.instance.getStartPos() != null) {
                event.setCanceled(true);
            }
        }

        // server is supressed.
        if (!event.getWorld().isRemote && event.getEntity() != null && useItem != Event.Result.DENY) {
            if (serverSuppressEvent.containsKey(event.getPlayer())) {
                event.setCanceled(true);
            }
        }
    }
}
