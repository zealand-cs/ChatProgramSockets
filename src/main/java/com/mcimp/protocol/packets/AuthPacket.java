package com.mcimp.protocol.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.mcimp.protocol.Packet;
import com.mcimp.protocol.PacketType;

public class AuthPacket extends Packet {
    private final static PacketType PACKET_TYPE = PacketType.Auth;

    private AuthType authType;
    private String username;
    private String password;

    public AuthPacket(AuthType authType, String username, String password) {
        super(PACKET_TYPE);
        this.authType = authType;
        this.username = username;
        this.password = password;
    }

    @Override
    public void writeToStream(DataOutputStream stream) throws IOException {
        super.writeToStream(stream);

        stream.writeByte(authType.toByte());

        stream.writeInt(username.length());
        stream.writeBytes(username);
        stream.writeInt(password.length());
        stream.writeBytes(password);
    }

    public static AuthPacket readFromStream(DataInputStream stream) throws IOException {
        var authType = AuthType.fromByte(stream.readByte());

        var usernameLength = stream.readInt();
        var username = new String(stream.readNBytes(usernameLength), StandardCharsets.UTF_8);

        var passwordLength = stream.readInt();
        var password = new String(stream.readNBytes(passwordLength), StandardCharsets.UTF_8);

        return new AuthPacket(authType, username, password);
    }

    public AuthType getAuthType() {
        return authType;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
