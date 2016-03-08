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

package org.teavm.gdx.files;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.teavm.jso.dom.html.HTMLImageElement;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;

/** Default implementation of {@link FileHandle} for TeaVM application. Does not support any modifying operations (write, copy,
 * delete).
 * @author Alexey Andreev
 * @author MJ */
public class TeaVMFileHandle extends AbstractFileHandle {
	public static final FSEntry ROOT = new FSEntry();

	public TeaVMFileHandle (final String path) {
		this(path, FileType.Internal);
	}

	public TeaVMFileHandle (final String fileName, final FileType type) {
		super(fileName, type);
		if (type != FileType.Internal && type != FileType.Classpath) {
			throw new GdxRuntimeException("FileType '" + type + "' not supported by TeaVM backend.");
		}
	}

	@Override
	public InputStream read () {
		final FSEntry entry = entry();
		if (entry == null || entry.data == null) {
			throwFileDoesNotExistError();
		}
		return new ByteArrayInputStream(entry.data);
	}

	/** @return {@link FSEntry} for the selected file. */
	public FSEntry entry () {
		FSEntry entry = ROOT;
		for (final String part : split()) {
			entry = entry.childEntries.get(part);
			if (entry == null) {
			break;
			}
		}
		return entry;
	}

	protected String[] split () {
		final String file = path();
		final List<String> result = new ArrayList<>();
		int index = 0;
		while (index < file.length()) {
			final int next = file.indexOf('/', index);
			if (next == -1) {
			break;
			}
			addPart(index, next, result);
			index = next + 1;
		}
		addPart(index, file.length(), result);
		return result.toArray(new String[result.size()]);
	}

	private void addPart (final int index, final int next, final List<String> result) {
		final String part = path().substring(index, next);
		if (!part.isEmpty() && !part.equals(".")) {
			if (part.equals("..")) {
			result.remove(result.size() - 1);
			} else {
			result.add(part);
			}
		}
	}

	@Override
	public BufferedInputStream read (final int bufferSize) {
		return new BufferedInputStream(read(), bufferSize);
	}

	@Override
	public Reader reader () {
		return new InputStreamReader(read());
	}

	@Override
	public String readString () {
		return new String(readBytes());
	}

	@Override
	public byte[] readBytes () {
		final FSEntry entry = entry();
		if (entry == null || entry.data == null) {
			throwFileDoesNotExistError();
		}
		return Arrays.copyOf(entry.data, entry.data.length);
	}

	@Override
	public int readBytes (final byte[] bytes, final int offset, int size) {
		final FSEntry entry = entry();
		if (entry == null || entry.data == null) {
			throwFileDoesNotExistError();
		}
		size = Math.min(size, entry.data.length);
		System.arraycopy(entry.data, 0, bytes, offset, size);
		return size;
	}

	@Override
	public FileHandle[] list () {
		final FSEntry entry = entry();
		if (entry == null) {
			throwFileDoesNotExistError();
		}
		final FileHandle[] result = new FileHandle[entry.childEntries.size()];
		int index = 0;
		for (final String childName : entry.childEntries.keySet()) {
			result[index++] = new TeaVMFileHandle(path() + "/" + childName, type);
		}
		return result;
	}

	@Override
	public FileHandle[] list (final String suffix) {
		final FSEntry entry = entry();
		if (entry == null) {
			throwFileDoesNotExistError();
		}
		final FileHandle[] result = new FileHandle[entry.childEntries.size()];
		int index = 0;
		for (final String childName : entry.childEntries.keySet()) {
			if (childName.endsWith(suffix)) {
			result[index++] = new TeaVMFileHandle(path() + "/" + childName, type);
			}
		}
		return index == result.length ? result : Arrays.copyOf(result, index);
	}

	@Override
	public boolean isDirectory () {
		final FSEntry entry = entry();
		return entry != null && entry.data == null;
	}

	@Override
	public FileHandle child (final String name) {
		return new TeaVMFileHandle(getChildPath(name), type);
	}

	@Override
	public FileHandle parent () {
		return new TeaVMFileHandle(getParentPath(), type);
	}

	@Override
	public FileHandle sibling (final String name) {
		return new TeaVMFileHandle(getSiblingPath(name), type);
	}

	@Override
	public boolean exists () {
		return entry() != null;
	}

	@Override
	public long length () {
		final FSEntry entry = entry();
		return entry != null && entry.data != null ? entry.data.length : null;
	}

	@Override
	public long lastModified () {
		final FSEntry entry = entry();
		return entry != null ? entry.lastModified : null;
	}

	protected void throwFileDoesNotExistError () {
		throw new GdxRuntimeException(path() + " does not exist.");
	}

	public static class FSEntry {
		public final Map<String, FSEntry> childEntries = new HashMap<>();
		public byte[] data;
		public long lastModified;
		public boolean directory;
		public HTMLImageElement imageElem;
	}
}
