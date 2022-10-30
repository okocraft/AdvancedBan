package me.leoko.advancedban.utils.floodgate;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.SimpleFloodgateApi;
import org.geysermc.floodgate.config.FloodgateConfig;

public final class Floodgate {

    private final FloodgateApi floodgate;
    private final FloodgateConfig floodgateConfig;

    Floodgate() {
        this.floodgate = Optional.ofNullable(FloodgateApi.getInstance())
                .orElseThrow(() -> new IllegalStateException("Floodgate is not installed"));
        try {
            Field configField = SimpleFloodgateApi.class.getDeclaredField("config");
            configField.setAccessible(true);
            floodgateConfig = (FloodgateConfig) configField.get(floodgate);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalStateException("Floodgate is not installed");
        }
    }

    private String toGamertag(String javaName) {
        javaName = javaName.substring(floodgateConfig.getUsernamePrefix().length());
        return floodgateConfig.isReplaceSpaces()
                ? javaName.replaceAll("_", " ")
                : javaName;
    }

    public UUID getPlayerUUID(String name) {
        try {
            return floodgate.getUuidFor(toGamertag(name)).get(1000L, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getPlayerName(UUID uuid) {
        try {
            String prefix = floodgateConfig.getUsernamePrefix();
            String name = floodgate.getGamertagFor(uuid.getLeastSignificantBits()).get(1000L, TimeUnit.MILLISECONDS);
            if (name.length() - prefix.length() > 16) {
                name = name.substring(0, name.length() - prefix.length());
            }
            if (floodgateConfig.isReplaceSpaces()) {
                name = name.replaceAll(" ", "_");
            }
            return prefix + name;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}