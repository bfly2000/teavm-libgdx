/*
 *  Copyright 2015 Alexey Andreev.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.teavm.gdx.plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.teavm.javascript.RenderingContext;
import org.teavm.vm.BuildTarget;
import org.teavm.vm.spi.RendererListener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SequenceWriter;

/** @author Alexey Andreev */
public class AssetsCopier implements RendererListener {
	private RenderingContext context;
	private final FileDescriptor rootFileDescriptor = new FileDescriptor();
	private final ObjectMapper mapper = new ObjectMapper();
	private final ObjectWriter writer = mapper.writerFor(FileDescriptor.class);

	@Override
	public void begin (final RenderingContext context, final BuildTarget buildTarget) throws IOException {
		this.context = context;
	}

	@Override
	public void complete () throws IOException {
		final String dirName = context.getProperties().getProperty("teavm.libgdx.genAssetsDirectory", "");
		if (!dirName.isEmpty()) {
			final File dir = new File(dirName);
			dir.mkdirs();
			copyClasspathAssets(dir);
			createFSDescriptor(dir);
		} else {
			createFSDescriptor(null);
		}
	}

	@SuppressWarnings("resource")
	private void copyClasspathAssets (final File dir) throws IOException {
		final Enumeration<URL> resources = context.getClassLoader().getResources("META-INF/teavm-libgdx/classpath-assets");
		final Set<String> resourcesToCopy = new HashSet<>();
		while (resources.hasMoreElements()) {
			final URL resource = resources.nextElement();
			final InputStream input = resource.openStream();
			if (input == null) {
			continue;
			}
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"))) {
			while (true) {
				String line = reader.readLine();
				if (line == null) {
					break;
				}
				line = line.trim();
				if (line.isEmpty() || line.startsWith("#")) {
					continue;
				}
				resourcesToCopy.add(line);
			}
			}
		}

		for (final String resourceToCopy : resourcesToCopy) {
			final File resource = new File(dir, resourceToCopy);
			if (resource.exists()) {
			final URL url = context.getClassLoader().getResource(resourceToCopy);
			if (url != null && url.getProtocol().equals("file")) {
				try {
					final File sourceFile = new File(url.toURI());
					if (sourceFile.exists() && sourceFile.length() == resource.length()
						&& sourceFile.lastModified() == resource.lastModified()) {
						continue;
					}
				} catch (final URISyntaxException e) {
					// fall back to usual resource copying
				}
			}
			}
			final InputStream input = context.getClassLoader().getResourceAsStream(resourceToCopy);
			if (input == null) {
			continue;
			}
			resource.getParentFile().mkdirs();
			IOUtils.copy(input, new FileOutputStream(resource));
		}
	}

	private void createFSDescriptor (File dir) throws IOException {
		final String path = context.getProperties().getProperty("teavm.libgdx.fsJsonPath", "");
		if (path.isEmpty()) {
			return;
		}
		if (dir != null) {
			processFile(dir, rootFileDescriptor);
		}

		final String dirName = context.getProperties().getProperty("teavm.libgdx.warAssetsDirectory", "");
		if (!dirName.isEmpty()) {
			dir = new File(dirName);
			processFile(dir, rootFileDescriptor);
		}

		try (FileOutputStream output = new FileOutputStream(new File(path))) {
			writeJsonFS(output);
		}
	}

	private void processFile (final File file, final FileDescriptor desc) {
		desc.setName(file.getName());
		desc.setDirectory(file.isDirectory());
		if (file.isDirectory()) {
			for (final File child : file.listFiles()) {
			final FileDescriptor childDesc = new FileDescriptor();
			processFile(child, childDesc);
			desc.getChildFiles().add(childDesc);
			}
		}
	}

	private void writeJsonFS (final OutputStream output) throws IOException {
		try (SequenceWriter seqWriter = writer.writeValues(output)) {
			boolean first = true;
			output.write((byte)'[');
			for (final FileDescriptor desc : rootFileDescriptor.getChildFiles()) {
			if (!first) {
				output.write((byte)',');
			}
			first = false;
			seqWriter.write(desc);
			}
			output.write((byte)']');
			seqWriter.flush();
		}
	}
}
