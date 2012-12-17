package com.comphenix.protocol.packets;

import com.comphenix.protocol.Packets;
import com.comphenix.protocol.events.PacketContainer;

/**
 * Represents a packet that wraps the raw packet data as strongly typed getters and setters.
 * 
 * @author Kristian
 */
public class WrappedPacket {
	// The packet we will be modifying
	protected PacketContainer rawPacket;
	
	/**
	 * Constructs a new strongly typed wrapper for the given packet.
	 * @param rawPacket - container for raw packet data.
	 * @param packetID - the packet ID we expect.
	 */
	public WrappedPacket(PacketContainer rawPacket, int packetID) {
		// Make sure we're given a valid packet
		if (rawPacket == null)
			throw new IllegalArgumentException("Packet container cannot be NULL.");
		if (rawPacket.getID() != packetID)
			throw new IllegalArgumentException(
					rawPacket.getHandle() + " is not a packet " + Packets.getDeclaredName(packetID) + "(" + packetID + ")");
		
		this.rawPacket = rawPacket;
	}

	/**
	 * Retrieve the underlying packet container that yields access to the raw packet data.
	 * @return A container for the raw packet data.
	 */
	public PacketContainer getRawPacket() {
		return rawPacket;
	}
	
	/**
	 * Retrieve the ID of this packet.
	 * @return The current packet ID.
	 */
	public int getID() {
		return rawPacket.getID();
	}
}
