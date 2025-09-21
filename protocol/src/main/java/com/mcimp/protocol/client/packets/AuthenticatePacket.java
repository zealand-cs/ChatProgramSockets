package com.mcimp.protocol.client.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.mcimp.protocol.client.ClientPacket;
import com.mcimp.protocol.client.ClientPacketId;

public class AuthenticatePacket extends ClientPacket {
    private final static ClientPacketId PACKET_ID = ClientPacketId.Authenticate;

    private AuthenticationType authType;
    private String username;
    private String password;

    public AuthenticatePacket(AuthenticationType authType, String username, String password) {
        super(PACKET_ID);
        this.authType = authType;
        this.username = username;
        this.password = password;
    }

    @Override
    protected void writeToStreamImpl(DataOutputStream stream) throws IOException {
        stream.writeByte(authType.toByte());

        stream.writeUTF(username);
        stream.writeUTF(password);
    }

    public static AuthenticatePacket readFromStream(DataInputStream stream) throws IOException {
        var authType = AuthenticationType.fromByte(stream.readByte());

        var username = stream.readUTF();
        var password = stream.readUTF();

        return new AuthenticatePacket(authType, username, password);
    }

    public AuthenticationType getAuthType() {
        return authType;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
