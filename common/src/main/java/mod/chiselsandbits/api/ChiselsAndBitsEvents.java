package mod.chiselsandbits.api;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;

public interface ChiselsAndBitsEvents {

    Event<BlockBitModification> BLOCK_BIT_MODIFICATION = EventFactory.createLoop();

    Event<BlockBitPostModification> BLOCK_BIT_POST_MODIFICATION = EventFactory.createLoop();

    Event<FullBlockRestoration> FULL_BLOCK_RESTORATION = EventFactory.createLoop();
}
