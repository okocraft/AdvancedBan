package me.leoko.advancedban.utils.floodgate;

public class FloodgateHook {
    public Floodgate hook() {
        try {
            return new Floodgate();
        } catch (NoClassDefFoundError | IllegalStateException e) {
            return null;
        }
    }
}