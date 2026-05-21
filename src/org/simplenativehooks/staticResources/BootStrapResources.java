package org.simplenativehooks.staticResources;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

public class BootStrapResources {

	private static final Set<AbstractBootstrapResource> BOOTSTRAP_RESOURCES;

	private static final NativeHookBootstrapResources nativeHookResources;

	static {

		/*********************************************************************************/
		BOOTSTRAP_RESOURCES = new HashSet<>();

		nativeHookResources = new NativeHookBootstrapResources();
		BOOTSTRAP_RESOURCES.add(nativeHookResources);
	}

	public static void extractResources() throws IOException, URISyntaxException {
		for (AbstractBootstrapResource resource : BOOTSTRAP_RESOURCES) {
			resource.extractResources();
		}
	}

	private BootStrapResources() {}
}
