package com.comphenix.protocol;

import com.comphenix.protocol.PacketType.Protocol;
import com.comphenix.protocol.PacketType.Sender;
import com.comphenix.protocol.events.ConnectionSide;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Range;

import java.util.*;

class PacketTypeParser {

	public static final Range<Integer> DEFAULT_MAX_RANGE = Range.closed(0, 255);
	
	private Sender connectionSide = null;
	private Protocol protocol = null;


	private Map<Protocol, ProtocolParser> protocolParsers = new HashMap<>();
	{
		protocolParsers.put(Protocol.HANDSHAKING, new HandshakingProtocolParser());
		protocolParsers.put(Protocol.LOGIN, new LoginProtocolParser());
		protocolParsers.put(Protocol.PLAY, new PlayProtocolParser());
		protocolParsers.put(Protocol.STATUS, new StatusProtocolParser());
	}

	public Set<PacketType> parseTypes(Deque<String> arguments, Range<Integer> defaultRange) {
		final Set<PacketType> result = new HashSet<>();
		connectionSide = null;
		protocol = null;

		// Find these first
		while (connectionSide == null) {
			String arg = arguments.poll();

			// Attempt to parse a side or protocol first
			if (connectionSide == null) {
				ConnectionSide connection = parseSide(arg);

				if (connection != null) {
					connectionSide = connection.getSender();
					continue;
				}
			}
			if (protocol == null) {
				ProtocolParser protocolParser = protocolParsers.get(protocolParsers.get(arg));
				if (protocolParser != null) {
					protocol = protocolParser.parseProtocol(arg);
					continue;
				}
			}
			throw new IllegalArgumentException("Specify connection side (CLIENT or SERVER).");
		}


		// Then we move on to parsing IDs (named packet types soon to come)
		List<Range<Integer>> ranges = RangeParser.getRanges(arguments, DEFAULT_MAX_RANGE);

		// And finally, parse packet names if we have a protocol
		if (protocol != null) {
			for (Iterator<String> it = arguments.iterator(); it.hasNext(); ) {
				String name = it.next().toUpperCase(Locale.ENGLISH);
				Collection<PacketType> names = PacketType.fromName(name);
				
				for (PacketType type : names) {
					if (type.getProtocol() == protocol && type.getSender() == connectionSide) {
						result.add(type);
						it.remove();
					}
				}
			}
		}
		
		// Supply a default integer range
		if (ranges.isEmpty() && result.isEmpty()) {
			ranges = Collections.singletonList(defaultRange);
		}
		
		for (Range<Integer> range : ranges) {
			for (Integer id : ContiguousSet.create(range, DiscreteDomain.integers())) {
				// Deprecated packets
				if (protocol == null) {
					if (PacketType.hasLegacy(id)) {
						result.add(PacketType.findLegacy(id, connectionSide));
					}
				} else {
					if (PacketType.hasCurrent(protocol, connectionSide, id)) {
						result.add(PacketType.findCurrent(protocol, connectionSide, id));
					}
				}
			}
		}
		return result;
	}
	
	/**
	 * Retrieve the last parsed protocol.
	 * @return Last protocol.
	 */
	public Protocol getLastProtocol() {
		return protocol;
	}
	
	/**
	 * Retrieve the last sender.
	 * @return Last sender.
	 */
	public Sender getLastSide() {
		return connectionSide;
	}
	
	/**
	 * Parse a connection sides from a string.
	 * @param text - the possible connection side.
	 * @return The connection side, or NULL if not found.
	 */
	public ConnectionSide parseSide(String text) {
		if (text == null)
			return null;
		String candidate = text.toLowerCase();
		
		// Parse the side gracefully
		if ("client".startsWith(candidate))
			return ConnectionSide.CLIENT_SIDE;
		else if ("server".startsWith(candidate))
			return ConnectionSide.SERVER_SIDE;
		else
			return null;
	}

	class HandshakingProtocolParser implements ProtocolParser {
		public Protocol parseProtocol(String protocolValue) {
			if (protocolValue == null) return null;
			if (protocolValue.equalsIgnoreCase("handshake") || protocolValue.equalsIgnoreCase("handshaking")) {
				return Protocol.HANDSHAKING;
			}
			return null;
		}
	}
	class LoginProtocolParser implements ProtocolParser {
		public Protocol parseProtocol(String protocolValue) {
			if (protocolValue == null) return null;
			if (protocolValue.equalsIgnoreCase("login")) {
				return Protocol.LOGIN;
			}
			return null;
		}
	}

	class PlayProtocolParser implements ProtocolParser {
		public Protocol parseProtocol(String protocolValue) {
			if (protocolValue == null) return null;
			if (protocolValue.equalsIgnoreCase("play") || protocolValue.equalsIgnoreCase("game")) {
				return Protocol.PLAY;
			}
			return null;
		}
	}

	class StatusProtocolParser implements ProtocolParser {
		public Protocol parseProtocol(String protocolValue) {
			if (protocolValue == null) return null;
			if (protocolValue.equalsIgnoreCase("status")) {
				return Protocol.STATUS;
			}
			return null;
		}
	}
}


