package com.algaworks.algashop.ordering.utility;

import org.junit.platform.commons.util.ResourceUtils;

import java.io.FileNotFoundException;
import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.isNull;

public class AlgaShopResourceUtils {

	public static String readContent(final String resourceName) {
		try (final var inputStream = ResourceUtils.class.getClassLoader().getResourceAsStream(resourceName)) {
			if (isNull(inputStream)) {
				throw new RuntimeException(new FileNotFoundException(resourceName));
			}
			return new String(inputStream.readAllBytes(), UTF_8);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
}