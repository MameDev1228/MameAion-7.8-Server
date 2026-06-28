/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 *
 *  Aion-Lightning is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Aion-Lightning is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details. *
 *  You should have received a copy of the GNU General Public License
 *  along with Aion-Lightning.
 *  If not, see <http://www.gnu.org/licenses/>.
 */

package com.aionemu.commons.scripting.impl.javacompiler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import javax.tools.SimpleJavaFileObject;

/**
 * In-memory class file used by the runtime script compiler.
 *
 * The old implementation extended com.sun.tools.javac.file.BaseFileObject,
 * which is an internal javac class and is not available on modern JDKs. This
 * version uses the public javax.tools API so the source can compile on JDK 25.
 *
 * @author SoulKeeper
 */
public class BinaryClass extends SimpleJavaFileObject {

	private final String name;
	private final ByteArrayOutputStream baos = new ByteArrayOutputStream();
	private Class<?> definedClass;

	protected BinaryClass(String name) {
		super(URI.create("byte:///" + name.replace('.', '/') + Kind.CLASS.extension), Kind.CLASS);
		this.name = name;
	}

	@Override
	public String getName() {
		return name + Kind.CLASS.extension;
	}

	@Override
	public InputStream openInputStream() throws IOException {
		return new ByteArrayInputStream(baos.toByteArray());
	}

	@Override
	public OutputStream openOutputStream() throws IOException {
		return baos;
	}

	@Override
	public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public long getLastModified() {
		return 0;
	}

	@Override
	public boolean delete() {
		return false;
	}

	public String inferBinaryName() {
		return name;
	}

	public byte[] getBytes() {
		return baos.toByteArray();
	}

	public Class<?> getDefinedClass() {
		return definedClass;
	}

	public void setDefinedClass(Class<?> definedClass) {
		this.definedClass = definedClass;
	}

	public String getShortName() {
		return this.name;
	}

	@Override
	public boolean equals(Object arg0) {
		if (arg0 instanceof BinaryClass) {
			return ((BinaryClass) arg0).name.equals(this.name);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.name.hashCode();
	}
}
