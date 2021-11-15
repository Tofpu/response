package io.tofpu.response.util.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class MyConfiguration {
    @Setting
    private final GeneralCategory generalCategory = new GeneralCategory();

    public GeneralCategory getGeneralCategory() {
        return generalCategory;
    }
}
