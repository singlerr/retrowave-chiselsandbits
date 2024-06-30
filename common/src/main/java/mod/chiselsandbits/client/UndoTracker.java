package mod.chiselsandbits.client;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import mod.chiselsandbits.chiseledblock.data.VoxelBlobStateReference;
import mod.chiselsandbits.core.ChiselsAndBits;
import mod.chiselsandbits.core.ClientSide;
import mod.chiselsandbits.helpers.ActingPlayer;
import mod.chiselsandbits.interfaces.ICacheClearable;
import mod.chiselsandbits.network.packets.PacketUndo;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class UndoTracker implements ICacheClearable {

    private static final UndoTracker instance = new UndoTracker();

    public static UndoTracker getInstance() {
        return instance;
    }

    public UndoTracker() {
        ChiselsAndBits.getInstance().addClearable(this);
    }

    private int level = -1; // the current undo level.
    private boolean recording = true; // is the system currently recording?
    private boolean grouping = false; // is a group active?
    private boolean hasCreatedGroup = false; // did we add an item yet?

    private final List<UndoStep> undoLevels = new ArrayList<UndoStep>();

    // errors produced by operations are accumulated for display.
    private final Set<String> errors = new HashSet<String>();

    /**
     * capture stack trace from whoever opened the undo group, for display
     * later.
     */
    private RuntimeException groupStarted;

    public void add(
            final World world,
            final BlockPos pos,
            final VoxelBlobStateReference before,
            final VoxelBlobStateReference after) {
        // servers don't track undo's
        if (pos != null && world != null && world.isRemote && recording) {
            if (undoLevels.size() > level && !undoLevels.isEmpty()) {
                final int end = Math.max(-1, level);
                for (int x = undoLevels.size() - 1; x > end; --x) {
                    undoLevels.remove(x);
                }
            }

            if (undoLevels.size()
                    > ChiselsAndBits.getConfig().getClient().maxUndoLevel.get()) {
                undoLevels.remove(0);
            }

            if (level >= undoLevels.size()) {
                level = undoLevels.size() - 1;
            }

            if (grouping && hasCreatedGroup) {
                final UndoStep current = undoLevels.get(undoLevels.size() - 1);
                final UndoStep newest = new UndoStep(world.getDimensionKey().getRegistryName(), pos, before, after);
                undoLevels.set(undoLevels.size() - 1, newest);
                newest.next = current;
                return;
            }

            undoLevels.add(new UndoStep(world.getDimensionKey().getRegistryName(), pos, before, after));
            hasCreatedGroup = true;
            level = undoLevels.size() - 1;
        }
    }

    public void undo() {
        if (level > -1) {
            final UndoStep step = undoLevels.get(level);
            final PlayerEntity who = ClientSide.instance.getPlayer();

            if (correctWorld(who, step) && step.after != null && step.before != null) {
                final ActingPlayer testPlayer = ActingPlayer.testingAs(who, Hand.MAIN_HAND);
                final boolean result = replayChanges(testPlayer, step, true, false);

                if (result) {
                    final ActingPlayer player = ActingPlayer.actingAs(who, Hand.MAIN_HAND);
                    if (replayChanges(player, step, true, true)) {
                        level--;
                    }
                }

                displayError();
            }
        } else {
            ClientSide.instance
                    .getPlayer()
                    .sendMessage(new TranslationTextComponent("mod.chiselsandbits.result.nothing_to_undo"), null);
        }
    }

    public void redo() {
        if (level + 1 < undoLevels.size()) {
            final UndoStep step = undoLevels.get(level + 1);
            final PlayerEntity who = ClientSide.instance.getPlayer();

            if (correctWorld(who, step)) {
                final ActingPlayer testPlayer = ActingPlayer.testingAs(who, Hand.MAIN_HAND);
                final boolean result = replayChanges(testPlayer, step, false, false);

                if (result) {
                    final ActingPlayer player = ActingPlayer.actingAs(who, Hand.MAIN_HAND);
                    if (replayChanges(player, step, false, true)) {
                        level++;
                    }
                }

                displayError();
            }
        } else {
            ClientSide.instance
                    .getPlayer()
                    .sendMessage(new TranslationTextComponent("mod.chiselsandbits.result.nothing_to_redo"), null);
        }
    }

    private boolean replayChanges(
            final ActingPlayer player,
            UndoStep step,
            final boolean backwards,
            final boolean spawnItemsAndCommitWorldChanges) {
        boolean done = false;

        while (step != null
                && replaySingleAction(
                        player,
                        step.pos,
                        backwards ? step.after : step.before,
                        backwards ? step.before : step.after,
                        spawnItemsAndCommitWorldChanges)) {
            step = step.next;
            if (step == null) {
                done = true;
            }
        }

        return done;
    }

    private boolean correctWorld(final PlayerEntity player, final UndoStep step) {
        return player.getEntityWorld().getDimensionKey().getRegistryName().equals(step.dimensionId);
    }

    private boolean replaySingleAction(
            final ActingPlayer player,
            final BlockPos pos,
            final VoxelBlobStateReference before,
            final VoxelBlobStateReference after,
            final boolean spawnItemsAndCommitWorldChanges) {
        try {
            recording = false;
            final PacketUndo packet = new PacketUndo(pos, before, after);
            if (packet.preformAction(player, spawnItemsAndCommitWorldChanges)) {
                ChiselsAndBits.getNetworkChannel().sendToServer(packet);
                return true;
            }

            return false;
        } finally {
            recording = true;
        }
    }

    public boolean ignorePlayer(final PlayerEntity player) {
        return player.getEntityWorld() == null || !player.getEntityWorld().isRemote;
    }

    public void beginGroup(final PlayerEntity player) {
        if (ignorePlayer(player)) {
            // don't touch this stuff if your a server.
            return;
        }

        if (grouping) {
            throw new RuntimeException("Opening a new group, previous group already started.", groupStarted);
        }

        // capture stack...
        groupStarted = new RuntimeException("Group was not closed properly.");
        groupStarted.fillInStackTrace();

        grouping = true;
        hasCreatedGroup = false;
    }

    public void endGroup(final PlayerEntity player) {
        if (ignorePlayer(player)) {
            // don't touch this stuff if your a server.
            return;
        }

        if (!grouping) {
            throw new RuntimeException("Closing undo group, but no undogroup was started.");
        }

        groupStarted = null;
        grouping = false;
    }

    @OnlyIn(Dist.CLIENT)
    private void displayError() {
        for (final String err : errors) {
            ClientSide.instance.getPlayer().sendMessage(new TranslationTextComponent(err), null);
        }

        errors.clear();
    }

    public void addError(final ActingPlayer player, final String string) {
        // servers don't care about this...
        if (!player.isReal() && player.getWorld().isRemote) {
            errors.add(string);
        }
    }

    public void onNetworkUpdate(final VoxelBlobStateReference beforeUpdate, final VoxelBlobStateReference afterUpdate) {
        this.undoLevels.forEach(step -> {
            step.onNetworkUpdate(beforeUpdate, afterUpdate);
        });
    }

    @Override
    public void clearCache() {
        level = -1;
        undoLevels.clear();
    }
}
