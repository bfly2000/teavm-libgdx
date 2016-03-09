
package org.teavm.gdx.files;

import org.teavm.gdx.TeaVMApplication;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;

/** TeaVM {@link Files} implementation. Supports only {@link FileType#Classpath} and {@link FileType#Internal} file types.
 * @author Alexey Andreev
 * @author MJ */
public class TeaVMFiles implements Files {
	/** @param path path to the file. Cannot be null.
	 * @param type has to be {@link FileType#Internal} or {@link FileType#Classpath}.
	 * @throws GdxRuntimeException if invalid file type is requested. */
	@Override
	public FileHandle getFileHandle (final String path, final FileType type) {
		return new TeaVMFileHandle(path, type);
	}

	@Override
	public FileHandle classpath (final String path) {
		return new TeaVMFileHandle(path, FileType.Classpath);
	}

	@Override
	public FileHandle internal (final String path) {
		return new TeaVMFileHandle(path, FileType.Internal);
	}

	@Override
	public FileHandle external (final String path) {
		throw new GdxRuntimeException("File type not supported: " + FileType.External);
	}

	@Override
	public FileHandle absolute (final String path) {
		throw new GdxRuntimeException("File type not supported: " + FileType.Absolute);
	}

	@Override
	public FileHandle local (final String path) {
		throw new GdxRuntimeException("File type not supported: " + FileType.Local);
	}

	@Override
	public String getExternalStoragePath () {
		TeaVMApplication.logUnsupported("External storage");
		return null;
	}

	@Override
	public boolean isExternalStorageAvailable () {
		return false;
	}

	@Override
	public String getLocalStoragePath () {
		TeaVMApplication.logUnsupported("Local storage");
		return null;
	}

	@Override
	public boolean isLocalStorageAvailable () {
		return false;
	}
}
