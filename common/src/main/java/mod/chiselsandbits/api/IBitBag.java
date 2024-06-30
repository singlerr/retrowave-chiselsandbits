package mod.chiselsandbits.api;


public interface IBitBag {

    /**
     * @return get max stack size of bits inside the bag.
     */
    int getBitbagStackSize();

    /**
     * @return how many slots contain bits.
     */
    int getSlotsUsed();
}
