package com.comphenix.protocol.wrappers;

import com.comphenix.protocol.BukkitInitialization;
import com.comphenix.protocol.wrappers.WrappedServerPing.CompressedImage;
import com.google.common.io.Resources;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import static org.junit.jupiter.api.Assertions.*;

public class WrappedServerPingTest {

	@BeforeAll
	public static void initializeBukkit() {
		BukkitInitialization.initializeAll();
	}

	@Test
	public void test() {
		try {
			CompressedImage tux = CompressedImage.fromPng(Resources.getResource("tux.png").openStream());
			byte[] original = tux.getDataCopy();

			WrappedServerPing serverPing = new WrappedServerPing();
			serverPing.setMotD("Hello, this is a test.");
			serverPing.setPlayersOnline(5);
			serverPing.setPlayersMaximum(10);
			serverPing.setVersionName("Minecraft 123");
			serverPing.setVersionProtocol(4);
			serverPing.setFavicon(tux);

			assertEquals(5, serverPing.getPlayersOnline());
			assertEquals(10, serverPing.getPlayersMaximum());
			assertEquals("Minecraft 123", serverPing.getVersionName());
			assertEquals(4, serverPing.getVersionProtocol());

			assertArrayEquals(original, serverPing.getFavicon().getData());

			CompressedImage copy = CompressedImage.fromBase64Png(Base64Coder.encodeLines(tux.getData()));
			assertArrayEquals(copy.getData(), serverPing.getFavicon().getData());
		} catch (Throwable ex) {
			if (ex.getCause() instanceof SecurityException) {
				// There was a global package seal for a while, but not anymore
				System.err.println("Encountered a SecurityException, update your Spigot jar!");
			} else {
				fail("Encountered an exception testing ServerPing", ex);
			}
		}
	}
}
