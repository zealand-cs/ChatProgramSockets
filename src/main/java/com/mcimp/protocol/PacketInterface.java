package com.mcimp.protocol;

import java.io.DataOutputStream;
import java.io.IOException;

public interface PacketInterface {
    void writeToStream(DataOutputStream stream) throws IOException;
}
