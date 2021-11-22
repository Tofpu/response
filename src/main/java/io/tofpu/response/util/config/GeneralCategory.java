package io.tofpu.response.util.config;

import org.bukkit.Bukkit;
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

    @Setting("automatic-response")
    @Comment("If enabled, the plugin will automatically attempt to respond to a message " +
            "that is associated with any of the loaded responses")
    private boolean automaticResponse = false;

    public boolean isAutomaticReplyEnabled() {
        return this.automaticResponse;
    }

    public GeneralCategory setAutomaticResponse(final boolean automaticResponse) {
        this.automaticResponse = automaticResponse;
        return this;
    }

    @Setting("server-name")
    @Comment("Your server name. this will be used as a prefix by the " +
            "automatic-response feature.")
    private String serverName = Bukkit.getServerName();

    public String getServerName() {
        return serverName;
    }

    public GeneralCategory setServerName(final String serverName) {
        this.serverName = serverName;
        return this;
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
