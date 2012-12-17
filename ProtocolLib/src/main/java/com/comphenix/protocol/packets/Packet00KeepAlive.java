package com.comphenix.protocol.packets;

import com.comphenix.protocol.Packets;
import com.comphenix.protocol.events.PacketContainer;

/**
 * Wrapper class for packet 0 (hex: 00)
 * <p>
 * Server <-> Client
 * 
 * @author Kristian
 */
public class Packet00KeepAlive extends WrappedPacket {
	/**
	 * The ID of this packet.
	 */
	// Packets.Client.KEEP_ALIVE is equivalent, but we will always use Server as a convention
	public static int ID = Packets.Server.KEEP_ALIVE;
	
	/**
	 * Constructs a strongly typed wrapper for a packet of ID 0.
	 * @param rawPacket
	 */
	public Packet00KeepAlive(PacketContainer rawPacket) {
		super(rawPacket, ID);
	}
	
	/**
	 * A random ID that the server will transmit to the client and expect back.
	 * <p>
	 * If the client doesn't respond within 1200 game ticks with the same random ID, the server
	 * will assume the client is in an overloaded state (generally unreachable) and sever the 
	 * connection. The client may do the same if it doesn't recieve this packet.
	 * 
	 * @return The random ID that is echoed by the client.
	 */
	public int getRandomID() {
		return rawPacket.getIntegers().read(0);
	}
	
	/**
	 * Set a random ID that the server will transmit to the client and expect back.
	 * <p>
	 * If the client doesn't respond within 1200 game ticks with the same random ID, the server
	 * will assume the client is in an overloaded state (generally unreachable) and sever the 
	 * connection. The client may do the same if it doesn't recieve this packet.
	 * 
	 * @param value - the new random ID stored in this packet.
	 */
	public void setRandomID(int value) {
		rawPacket.getIntegers().write(0, value);
	}
}
