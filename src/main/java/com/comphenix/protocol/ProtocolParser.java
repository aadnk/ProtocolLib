package com.comphenix.protocol;

interface ProtocolParser {
    PacketType.Protocol parseProtocol(String protocolValue);
}
