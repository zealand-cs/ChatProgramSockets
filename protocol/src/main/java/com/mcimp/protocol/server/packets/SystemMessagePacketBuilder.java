package com.mcimp.protocol.server.packets;

public class SystemMessagePacketBuilder {
    private SystemMessageLevel level;
    private SystemMessageScope scope;
    private String text;

    // Scopes

    public SystemMessagePacketBuilder global() {
        scope = SystemMessageScope.Global;
        return this;
    }

    public SystemMessagePacketBuilder room() {
        scope = SystemMessageScope.Room;
        return this;
    }

    public SystemMessagePacketBuilder user() {
        scope = SystemMessageScope.User;
        return this;
    }

    public SystemMessagePacketBuilder error() {
        level = SystemMessageLevel.Error;
        return this;
    }

    // Levels

    public SystemMessagePacketBuilder pure() {
        level = SystemMessageLevel.Pure;
        return this;
    }

    public SystemMessagePacketBuilder info() {
        level = SystemMessageLevel.Info;
        return this;
    }

    public SystemMessagePacketBuilder success() {
        level = SystemMessageLevel.Success;
        return this;
    }

    public SystemMessagePacketBuilder warn() {
        level = SystemMessageLevel.Warning;
        return this;
    }

    // Text

    public SystemMessagePacketBuilder text(String text) {
        this.text = text;
        return this;
    }

    // Build

    public SystemMessagePacket build() {
        assert scope != null && level != null && text != null;
        if (scope == null || level == null || text == null) {
            // Here we throw since we should not continue when a developer
            // makes such a dumb mistake
            throw new RuntimeException("developer mistake. Scope, level AND text should be set.");
        }

        return new SystemMessagePacket(scope, level, text);
    }
}
