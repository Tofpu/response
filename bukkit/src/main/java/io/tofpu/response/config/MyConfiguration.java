package io.tofpu.response.config;

import io.tofpu.response.config.category.GeneralCategory;
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
