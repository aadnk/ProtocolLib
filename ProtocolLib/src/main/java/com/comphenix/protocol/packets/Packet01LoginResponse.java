package com.comphenix.protocol.packets;

import org.bukkit.WorldType;

import com.comphenix.protocol.Packets;
import com.comphenix.protocol.events.PacketContainer;

/**
 * Wrapper class for packet 1 (hex: 01)
 * <p>
 * Server -> Client
 * 
 * @author Kristian
 */
public class Packet01LoginResponse extends WrappedPacket {
	// Note that the order differs in memory from how the fields are transmitted on the wire.
	/**
	 * The ID of this packet.
	 */
	public static int ID = Packets.Server.LOGIN;
	
	/**
	 * Constructs a strongly typed wrapper for a packet of ID 1.
	 * @param rawPacket
	 */
	public Packet01LoginResponse(PacketContainer rawPacket) {
		super(rawPacket, ID);
	}
	
	/**
	 * Retrieve the player's entity ID.
	 * @return Player's entity ID.
	 */
	public int getEntityID() {
		return rawPacket.getIntegers().read(0);
	}
	
	/**
	 * Set the player's entity ID.
	 * @param entityID - the new entity ID.
	 */
	public void setEntityID(int entityID) {
		rawPacket.getIntegers().write(0, entityID);
	}
	
	/**
	 * Retrieve the type of world where the player will be spawned.
	 * @return Type of the world
	 */
	public WorldType getWorldType() {
		return rawPacket.getWorldTypeModifier().read(0);
	}
	
	/**
	 * Set the type of world where the player will be spawned.
	 * @param type - new world type.
	 */
	public void setWorldType(WorldType type) {
		rawPacket.getWorldTypeModifier().write(0, type);
	}
	

	// TODO: ADD MORE FIELDS
}
