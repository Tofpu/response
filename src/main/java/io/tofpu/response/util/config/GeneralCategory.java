package io.tofpu.response.util.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class GeneralCategory {
    @Setting("enable-debug-messages")
    @Comment("For debug purposes, keep this disabled unless instructed otherwise")
    private boolean enableDebugMessages = false;

    public boolean isDebugMessagesEnabled() {
        return this.enableDebugMessages;
    }

    public void setDebugMessage(final boolean enableDebugMessages) {
        this.enableDebugMessages = enableDebugMessages;
    }

    @Setting("enable-papi-support")
    @Comment("If enabled, it'll allow you to use PlaceholderAPI placeholders")
    private boolean enablePapiSupport = true;

    public boolean isPAPISupportEnabled() {
        return this.enablePapiSupport;
    }

    public void setPAPISupport(final boolean enablePapiSupport) {
        this.enablePapiSupport = enablePapiSupport;
    }
}
