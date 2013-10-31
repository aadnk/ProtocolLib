/*
 *  ProtocolLib - Bukkit server library that allows access to the Minecraft protocol.
 *  Copyright (C) 2012 Kristian S. Stangeland
 *
 *  This program is free software; you can redistribute it and/or modify it under the terms of the 
 *  GNU General Public License as published by the Free Software Foundation; either version 2 of 
 *  the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *  See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with this program; 
 *  if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 
 *  02111-1307 USA
 */

package com.comphenix.protocol.injector;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.comphenix.protocol.Packets;
import com.comphenix.protocol.injector.packet.PacketRegistry;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.reflect.compiler.BackgroundCompiler;
import com.comphenix.protocol.reflect.compiler.CompileListener;
import com.comphenix.protocol.reflect.compiler.CompiledStructureModifier;
import com.comphenix.protocol.utility.MinecraftReflection;

/**
 * Caches structure modifiers.
 * @author Kristian
 */
public class StructureCache {
	// Structure modifiers
	private static ConcurrentMap<Integer, StructureModifier<Object>> structureModifiers = 
			new ConcurrentHashMap<Integer, StructureModifier<Object>>();
	
	private static Set<Integer> compiling = new HashSet<Integer>();
	
	/**
	 * Creates an empty Minecraft packet of the given ID.
	 * @param id - packet ID.
	 * @return Created packet.
	 */
	public static Object newPacket(int id) {
		try {
			return PacketRegistry.getPacketClassFromID(id, true).newInstance();
		} catch (InstantiationException e) {
			return null;
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Access denied.", e);
		}
	}
	
	/**
	 * Retrieve a cached structure modifier for the given packet id.
	 * @param id - packet ID.
	 * @return A structure modifier.
	 */
	public static StructureModifier<Object> getStructure(int id) {
		// Compile structures by default
		return getStructure(id, true);
	}
	
	/**
	 * Retrieve a cached structure modifier given a packet type.
	 * @param packetType - packet type.
	 * @return A structure modifier.
	 */
	public static StructureModifier<Object> getStructure(Class<?> packetType) {
		// Compile structures by default
		return getStructure(packetType, true);
	}
	
	/**
	 * Retrieve a cached structure modifier given a packet type.
	 * @param packetType - packet type.
	 * @param compile - whether or not to asynchronously compile the structure modifier.
	 * @return A structure modifier.
	 */
	public static StructureModifier<Object> getStructure(Class<?> packetType, boolean compile) {
		// Get the ID from the class
		return getStructure(PacketRegistry.getPacketID(packetType), compile);
	}
	
	/**
	 * Retrieve a cached structure modifier for the given packet id.
	 * @param id - packet ID.
	 * @param compile - whether or not to asynchronously compile the structure modifier.
	 * @return A structure modifier.
	 */
	public static StructureModifier<Object> getStructure(int id, boolean compile) {
		StructureModifier<Object> result = structureModifiers.get(id);

		// We don't want to create this for every lookup
		if (result == null) {
			// Use the vanilla class definition
			final Class<?> type = PacketRegistry.getPacketClassFromID(id, true);
			final StructureModifier<Object> value = new StructureModifier<Object>(getRemappedFields(type, id), type, true, true);
			
			result = structureModifiers.putIfAbsent(id, value);
			
			// We may end up creating multiple modifiers, but we'll agree on which to use
			if (result == null) {
				result = value;
			}
		}
		
		// Automatically compile the structure modifier
		if (compile && !(result instanceof CompiledStructureModifier)) {
			// Compilation is many orders of magnitude slower than synchronization
			synchronized (compiling) {
				final int idCopy = id;
				final BackgroundCompiler compiler = BackgroundCompiler.getInstance();
				
				if (!compiling.contains(id) && compiler != null) {
					compiler.scheduleCompilation(result, new CompileListener<Object>() {
						@Override
						public void onCompiled(StructureModifier<Object> compiledModifier) {
							structureModifiers.put(idCopy, compiledModifier);
						}
					});
					compiling.add(id);
				}
			}
		}
		
		return result;
	}
	
	private static List<Field> getRemappedFields(Class<?> targetType, int id) {
		List<Field> fields = StructureModifier.getFields(targetType, MinecraftReflection.getPacketClass());
		
		if (id == Packets.Server.NAMED_ENTITY_SPAWN) {
			int first = getIndex(fields, String.class, 0);
			int second = getIndex(fields, String.class, 1);
			
			// Swap around string #1 and #2
			if (first >= 0 && second >= 0) {
				Collections.swap(fields, first, second);
			}
		}
		return fields;
	}
	
	/**
	 * Retrieve the index of the n'th field of the given type.
	 * @param fields - the fields.
	 * @param type - the type to look for.
	 * @param skip - the number of fields of the correct type to skip. 
	 * @return The index of the field, or -1.
	 */
	private static int getIndex(List<Field> fields, Class<?> type, int skip) {
		for (int i = 0; i < fields.size(); i++) {
			if (fields.get(i).getType().equals(type) && (skip-- <= 0)) {
				return i;
			}
		}
		return -1;
	}
}
