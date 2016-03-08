
package org.teavm.gdx.graphics.webgl;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.teavm.jso.webgl.WebGLRenderingContext;

import com.badlogic.gdx.utils.GdxRuntimeException;

/** Validates if {@link WebGLRenderingContext} has no errors after each GL call.
 * @author MJ */
public class DebugTeaVMGL20 extends TeaVMGL20 {
	public DebugTeaVMGL20 (final WebGLRenderingContext gl) {
		super(gl);
	}

	private void checkError () {
		int error = 0;
		if ((error = gl.getError()) != GL_NO_ERROR) {
			throw new GdxRuntimeException("GL error: " + error + ", " + Integer.toHexString(error));
		}
	}

	@Override
	public void glActiveTexture (final int texture) {
		super.glActiveTexture(texture);
		checkError();
	}

	@Override
	public void glBindTexture (final int target, final int texture) {
		super.glBindTexture(target, texture);
		checkError();
	}

	@Override
	public void glBlendFunc (final int sfactor, final int dfactor) {
		super.glBlendFunc(sfactor, dfactor);
		checkError();
	}

	@Override
	public void glClear (final int mask) {
		super.glClear(mask);
		checkError();
	}

	@Override
	public void glClearColor (final float red, final float green, final float blue, final float alpha) {
		super.glClearColor(red, green, blue, alpha);
		checkError();
	}

	@Override
	public void glClearDepthf (final float depth) {
		super.glClearDepthf(depth);
		checkError();
	}

	@Override
	public void glClearStencil (final int s) {
		super.glClearStencil(s);
		checkError();
	}

	@Override
	public void glColorMask (final boolean red, final boolean green, final boolean blue, final boolean alpha) {
		super.glColorMask(red, green, blue, alpha);
		checkError();
	}

	@Override
	public void glCompressedTexImage2D (final int target, final int level, final int internalformat, final int width,
		final int height, final int border, final int imageSize, final Buffer data) {
		super.glCompressedTexImage2D(target, level, internalformat, width, height, border, imageSize, data);
		checkError();
	}

	@Override
	public void glCompressedTexSubImage2D (final int target, final int level, final int xoffset, final int yoffset,
		final int width, final int height, final int format, final int imageSize, final Buffer data) {
		super.glCompressedTexSubImage2D(target, level, xoffset, yoffset, width, height, format, imageSize, data);
		checkError();
	}

	@Override
	public void glCopyTexImage2D (final int target, final int level, final int internalformat, final int x, final int y,
		final int width, final int height, final int border) {
		super.glCopyTexImage2D(target, level, internalformat, x, y, width, height, border);
		checkError();
	}

	@Override
	public void glCopyTexSubImage2D (final int target, final int level, final int xoffset, final int yoffset, final int x,
		final int y, final int width, final int height) {
		super.glCopyTexSubImage2D(target, level, xoffset, yoffset, x, y, width, height);
		checkError();
	}

	@Override
	public void glCullFace (final int mode) {
		super.glCullFace(mode);
		checkError();
	}

	@Override
	public void glDeleteTextures (final int n, final IntBuffer textures) {
		super.glDeleteTextures(n, textures);
		checkError();
	}

	@Override
	public void glDepthFunc (final int func) {
		super.glDepthFunc(func);
		checkError();
	}

	@Override
	public void glDepthMask (final boolean flag) {
		super.glDepthMask(flag);
		checkError();
	}

	@Override
	public void glDepthRangef (final float zNear, final float zFar) {
		super.glDepthRangef(zNear, zFar);
		checkError();
	}

	@Override
	public void glDisable (final int cap) {
		super.glDisable(cap);
		checkError();
	}

	@Override
	public void glDrawArrays (final int mode, final int first, final int count) {
		super.glDrawArrays(mode, first, count);
		checkError();
	}

	@Override
	public void glDrawElements (final int mode, final int count, final int type, final Buffer indices) {
		super.glDrawElements(mode, count, type, indices);
		checkError();
	}

	@Override
	public void glEnable (final int cap) {
		super.glEnable(cap);
		checkError();
	}

	@Override
	public void glFinish () {
		super.glFinish();
		checkError();
	}

	@Override
	public void glFlush () {
		super.glFlush();
		checkError();
	}

	@Override
	public void glFrontFace (final int mode) {
		super.glFrontFace(mode);
		checkError();
	}

	@Override
	public void glGenTextures (final int n, final IntBuffer textures) {
		super.glGenTextures(n, textures);
		checkError();
	}

	@Override
	public int glGetError () {
		return super.glGetError();
	}

	@Override
	public void glGetIntegerv (final int pname, final IntBuffer params) {
		super.glGetIntegerv(pname, params);
		checkError();
	}

	@Override
	public String glGetString (final int name) {
		return super.glGetString(name);
	}

	@Override
	public void glHint (final int target, final int mode) {
		super.glHint(target, mode);
		checkError();
	}

	@Override
	public void glLineWidth (final float width) {
		super.glLineWidth(width);
		checkError();
	}

	@Override
	public void glPixelStorei (final int pname, final int param) {
		super.glPixelStorei(pname, param);
		checkError();
	}

	@Override
	public void glPolygonOffset (final float factor, final float units) {
		super.glPolygonOffset(factor, units);
		checkError();
	}

	@Override
	public void glReadPixels (final int x, final int y, final int width, final int height, final int format, final int type,
		final Buffer pixels) {
		super.glReadPixels(x, y, width, height, format, type, pixels);
		checkError();
	}

	@Override
	public void glScissor (final int x, final int y, final int width, final int height) {
		super.glScissor(x, y, width, height);
		checkError();
	}

	@Override
	public void glStencilFunc (final int func, final int ref, final int mask) {
		super.glStencilFunc(func, ref, mask);
		checkError();
	}

	@Override
	public void glStencilMask (final int mask) {
		super.glStencilMask(mask);
		checkError();
	}

	@Override
	public void glStencilOp (final int fail, final int zfail, final int zpass) {
		super.glStencilOp(fail, zfail, zpass);
		checkError();
	}

	@Override
	public void glTexImage2D (final int target, final int level, final int internalformat, final int width, final int height,
		final int border, final int format, final int type, final Buffer pixels) {
		super.glTexImage2D(target, level, internalformat, width, height, border, format, type, pixels);
		checkError();
	}

	@Override
	public void glTexParameterf (final int target, final int pname, final float param) {
		super.glTexParameterf(target, pname, param);
		checkError();
	}

	@Override
	public void glTexSubImage2D (final int target, final int level, final int xoffset, final int yoffset, final int width,
		final int height, final int format, final int type, final Buffer pixels) {
		super.glTexSubImage2D(target, level, xoffset, yoffset, width, height, format, type, pixels);
		checkError();
	}

	@Override
	public void glViewport (final int x, final int y, final int width, final int height) {
		super.glViewport(x, y, width, height);
		checkError();
	}

	@Override
	public void glAttachShader (final int program, final int shader) {
		super.glAttachShader(program, shader);
		checkError();
	}

	@Override
	public void glBindAttribLocation (final int program, final int index, final String name) {
		super.glBindAttribLocation(program, index, name);
		checkError();
	}

	@Override
	public void glBindBuffer (final int target, final int buffer) {
		super.glBindBuffer(target, buffer);
		checkError();
	}

	@Override
	public void glBindFramebuffer (final int target, final int framebuffer) {
		super.glBindFramebuffer(target, framebuffer);
		checkError();
	}

	@Override
	public void glBindRenderbuffer (final int target, final int renderbuffer) {
		super.glBindRenderbuffer(target, renderbuffer);
		checkError();
	}

	@Override
	public void glBlendColor (final float red, final float green, final float blue, final float alpha) {
		super.glBlendColor(red, green, blue, alpha);
		checkError();
	}

	@Override
	public void glBlendEquation (final int mode) {
		super.glBlendEquation(mode);
		checkError();
	}

	@Override
	public void glBlendEquationSeparate (final int modeRGB, final int modeAlpha) {
		super.glBlendEquationSeparate(modeRGB, modeAlpha);
		checkError();
	}

	@Override
	public void glBlendFuncSeparate (final int srcRGB, final int dstRGB, final int srcAlpha, final int dstAlpha) {
		super.glBlendFuncSeparate(srcRGB, dstRGB, srcAlpha, dstAlpha);
		checkError();
	}

	@Override
	public void glBufferData (final int target, final int size, final Buffer data, final int usage) {
		super.glBufferData(target, size, data, usage);
		checkError();
	}

	@Override
	public void glBufferSubData (final int target, final int offset, final int size, final Buffer data) {
		super.glBufferSubData(target, offset, size, data);
		checkError();
	}

	@Override
	public int glCheckFramebufferStatus (final int target) {
		return super.glCheckFramebufferStatus(target);
	}

	@Override
	public void glCompileShader (final int shader) {
		super.glCompileShader(shader);
		checkError();
	}

	@Override
	public int glCreateProgram () {
		final int program = super.glCreateProgram();
		checkError();
		return program;
	}

	@Override
	public int glCreateShader (final int type) {
		final int shader = super.glCreateShader(type);
		checkError();
		return shader;
	}

	@Override
	public void glDeleteBuffers (final int n, final IntBuffer buffers) {
		super.glDeleteBuffers(n, buffers);
		checkError();
	}

	@Override
	public void glDeleteFramebuffers (final int n, final IntBuffer framebuffers) {
		super.glDeleteFramebuffers(n, framebuffers);
		checkError();
	}

	@Override
	public void glDeleteProgram (final int program) {
		super.glDeleteProgram(program);
		checkError();
	}

	@Override
	public void glDeleteRenderbuffers (final int n, final IntBuffer renderbuffers) {
		super.glDeleteRenderbuffers(n, renderbuffers);
		checkError();
	}

	@Override
	public void glDeleteShader (final int shader) {
		super.glDeleteShader(shader);
		checkError();
	}

	@Override
	public void glDetachShader (final int program, final int shader) {
		super.glDetachShader(program, shader);
		checkError();
	}

	@Override
	public void glDisableVertexAttribArray (final int index) {
		super.glDisableVertexAttribArray(index);
		checkError();
	}

	@Override
	public void glDrawElements (final int mode, final int count, final int type, final int indices) {
		super.glDrawElements(mode, count, type, indices);
		checkError();
	}

	@Override
	public void glEnableVertexAttribArray (final int index) {
		super.glEnableVertexAttribArray(index);
		checkError();
	}

	@Override
	public void glFramebufferRenderbuffer (final int target, final int attachment, final int renderbuffertarget,
		final int renderbuffer) {
		super.glFramebufferRenderbuffer(target, attachment, renderbuffertarget, renderbuffer);
		checkError();
	}

	@Override
	public void glFramebufferTexture2D (final int target, final int attachment, final int textarget, final int texture,
		final int level) {
		super.glFramebufferTexture2D(target, attachment, textarget, texture, level);
		checkError();
	}

	@Override
	public void glGenBuffers (final int n, final IntBuffer buffers) {
		super.glGenBuffers(n, buffers);
		checkError();
	}

	@Override
	public void glGenerateMipmap (final int target) {
		super.glGenerateMipmap(target);
		checkError();
	}

	@Override
	public void glGenFramebuffers (final int n, final IntBuffer framebuffers) {
		super.glGenFramebuffers(n, framebuffers);
		checkError();
	}

	@Override
	public void glGenRenderbuffers (final int n, final IntBuffer renderbuffers) {
		super.glGenRenderbuffers(n, renderbuffers);
		checkError();
	}

	@Override
	public String glGetActiveAttrib (final int program, final int index, final IntBuffer size, final Buffer type) {
		final String attrib = super.glGetActiveAttrib(program, index, size, type);
		checkError();
		return attrib;
	}

	@Override
	public String glGetActiveUniform (final int program, final int index, final IntBuffer size, final Buffer type) {
		final String uniform = super.glGetActiveUniform(program, index, size, type);
		checkError();
		return uniform;
	}

	@Override
	public void glGetAttachedShaders (final int program, final int maxcount, final Buffer count, final IntBuffer shaders) {
		super.glGetAttachedShaders(program, maxcount, count, shaders);
		checkError();
	}

	@Override
	public int glGetAttribLocation (final int program, final String name) {
		final int loc = super.glGetAttribLocation(program, name);
		checkError();
		return loc;
	}

	@Override
	public void glGetBooleanv (final int pname, final Buffer params) {
		super.glGetBooleanv(pname, params);
		checkError();
	}

	@Override
	public void glGetBufferParameteriv (final int target, final int pname, final IntBuffer params) {
		super.glGetBufferParameteriv(target, pname, params);
		checkError();
	}

	@Override
	public void glGetFloatv (final int pname, final FloatBuffer params) {
		super.glGetFloatv(pname, params);
		checkError();
	}

	@Override
	public void glGetFramebufferAttachmentParameteriv (final int target, final int attachment, final int pname,
		final IntBuffer params) {
		super.glGetFramebufferAttachmentParameteriv(target, attachment, pname, params);
		checkError();
	}

	@Override
	public void glGetProgramiv (final int program, final int pname, final IntBuffer params) {
		super.glGetProgramiv(program, pname, params);
		checkError();
	}

	@Override
	public String glGetProgramInfoLog (final int program) {
		final String info = super.glGetProgramInfoLog(program);
		checkError();
		return info;
	}

	@Override
	public void glGetRenderbufferParameteriv (final int target, final int pname, final IntBuffer params) {
		super.glGetRenderbufferParameteriv(target, pname, params);
		checkError();
	}

	@Override
	public void glGetShaderiv (final int shader, final int pname, final IntBuffer params) {
		super.glGetShaderiv(shader, pname, params);
		checkError();
	}

	@Override
	public String glGetShaderInfoLog (final int shader) {
		final String info = super.glGetShaderInfoLog(shader);
		checkError();
		return info;
	}

	@Override
	public void glGetShaderPrecisionFormat (final int shadertype, final int precisiontype, final IntBuffer range,
		final IntBuffer precision) {
		super.glGetShaderPrecisionFormat(shadertype, precisiontype, range, precision);
		checkError();
	}

	@Override
	public void glGetTexParameterfv (final int target, final int pname, final FloatBuffer params) {
		super.glGetTexParameterfv(target, pname, params);
		checkError();
	}

	@Override
	public void glGetTexParameteriv (final int target, final int pname, final IntBuffer params) {
		super.glGetTexParameteriv(target, pname, params);
		checkError();
	}

	@Override
	public void glGetUniformfv (final int program, final int location, final FloatBuffer params) {
		super.glGetUniformfv(program, location, params);
		checkError();
	}

	@Override
	public void glGetUniformiv (final int program, final int location, final IntBuffer params) {
		super.glGetUniformiv(program, location, params);
		checkError();
	}

	@Override
	public int glGetUniformLocation (final int program, final String name) {
		final int loc = super.glGetUniformLocation(program, name);
		checkError();
		return loc;
	}

	@Override
	public void glGetVertexAttribfv (final int index, final int pname, final FloatBuffer params) {
		super.glGetVertexAttribfv(index, pname, params);
		checkError();
	}

	@Override
	public void glGetVertexAttribiv (final int index, final int pname, final IntBuffer params) {
		super.glGetVertexAttribiv(index, pname, params);
		checkError();
	}

	@Override
	public void glGetVertexAttribPointerv (final int index, final int pname, final Buffer pointer) {
		super.glGetVertexAttribPointerv(index, pname, pointer);
		checkError();
	}

	@Override
	public boolean glIsBuffer (final int buffer) {
		final boolean res = super.glIsBuffer(buffer);
		checkError();
		return res;
	}

	@Override
	public boolean glIsEnabled (final int cap) {
		final boolean res = super.glIsEnabled(cap);
		checkError();
		return res;
	}

	@Override
	public boolean glIsFramebuffer (final int framebuffer) {
		final boolean res = super.glIsFramebuffer(framebuffer);
		checkError();
		return res;
	}

	@Override
	public boolean glIsProgram (final int program) {
		final boolean res = super.glIsProgram(program);
		checkError();
		return res;
	}

	@Override
	public boolean glIsRenderbuffer (final int renderbuffer) {
		final boolean res = super.glIsRenderbuffer(renderbuffer);
		checkError();
		return res;
	}

	@Override
	public boolean glIsShader (final int shader) {
		final boolean res = super.glIsShader(shader);
		checkError();
		return res;
	}

	@Override
	public boolean glIsTexture (final int texture) {
		final boolean res = super.glIsTexture(texture);
		checkError();
		return res;
	}

	@Override
	public void glLinkProgram (final int program) {
		super.glLinkProgram(program);
		checkError();
	}

	@Override
	public void glReleaseShaderCompiler () {
		super.glReleaseShaderCompiler();
		checkError();
	}

	@Override
	public void glRenderbufferStorage (final int target, final int internalformat, final int width, final int height) {
		super.glRenderbufferStorage(target, internalformat, width, height);
		checkError();
	}

	@Override
	public void glSampleCoverage (final float value, final boolean invert) {
		super.glSampleCoverage(value, invert);
		checkError();
	}

	@Override
	public void glShaderBinary (final int n, final IntBuffer shaders, final int binaryformat, final Buffer binary,
		final int length) {
		super.glShaderBinary(n, shaders, binaryformat, binary, length);
		checkError();
	}

	@Override
	public void glShaderSource (final int shader, final String source) {
		super.glShaderSource(shader, source);
		checkError();
	}

	@Override
	public void glStencilFuncSeparate (final int face, final int func, final int ref, final int mask) {
		super.glStencilFuncSeparate(face, func, ref, mask);
		checkError();
	}

	@Override
	public void glStencilMaskSeparate (final int face, final int mask) {
		super.glStencilMaskSeparate(face, mask);
		checkError();
	}

	@Override
	public void glStencilOpSeparate (final int face, final int fail, final int zfail, final int zpass) {
		super.glStencilOpSeparate(face, fail, zfail, zpass);
		checkError();
	}

	@Override
	public void glTexParameterfv (final int target, final int pname, final FloatBuffer params) {
		super.glTexParameterfv(target, pname, params);
		checkError();
	}

	@Override
	public void glTexParameteri (final int target, final int pname, final int param) {
		super.glTexParameteri(target, pname, param);
		checkError();
	}

	@Override
	public void glTexParameteriv (final int target, final int pname, final IntBuffer params) {
		super.glTexParameteriv(target, pname, params);
		checkError();
	}

	@Override
	public void glUniform1f (final int location, final float x) {
		super.glUniform1f(location, x);
		checkError();
	}

	@Override
	public void glUniform1fv (final int location, final int count, final FloatBuffer v) {
		super.glUniform1fv(location, count, v);
		checkError();
	}

	@Override
	public void glUniform1i (final int location, final int x) {
		super.glUniform1i(location, x);
		checkError();
	}

	@Override
	public void glUniform1iv (final int location, final int count, final IntBuffer v) {
		super.glUniform1iv(location, count, v);
		checkError();
	}

	@Override
	public void glUniform2f (final int location, final float x, final float y) {
		super.glUniform2f(location, x, y);
		checkError();
	}

	@Override
	public void glUniform2fv (final int location, final int count, final FloatBuffer v) {
		super.glUniform2fv(location, count, v);
		checkError();
	}

	@Override
	public void glUniform2i (final int location, final int x, final int y) {
		super.glUniform2i(location, x, y);
		checkError();
	}

	@Override
	public void glUniform2iv (final int location, final int count, final IntBuffer v) {
		super.glUniform2iv(location, count, v);
		checkError();
	}

	@Override
	public void glUniform3f (final int location, final float x, final float y, final float z) {
		super.glUniform3f(location, x, y, z);
		checkError();
	}

	@Override
	public void glUniform3fv (final int location, final int count, final FloatBuffer v) {
		super.glUniform3fv(location, count, v);
		checkError();
	}

	@Override
	public void glUniform3i (final int location, final int x, final int y, final int z) {
		super.glUniform3i(location, x, y, z);
		checkError();
	}

	@Override
	public void glUniform3iv (final int location, final int count, final IntBuffer v) {
		super.glUniform3iv(location, count, v);
		checkError();
	}

	@Override
	public void glUniform4f (final int location, final float x, final float y, final float z, final float w) {
		super.glUniform4f(location, x, y, z, w);
		checkError();
	}

	@Override
	public void glUniform4fv (final int location, final int count, final FloatBuffer v) {
		super.glUniform4fv(location, count, v);
		checkError();
	}

	@Override
	public void glUniform4i (final int location, final int x, final int y, final int z, final int w) {
		super.glUniform4i(location, x, y, z, w);
		checkError();
	}

	@Override
	public void glUniform4iv (final int location, final int count, final IntBuffer v) {
		super.glUniform4iv(location, count, v);
		checkError();
	}

	@Override
	public void glUniformMatrix2fv (final int location, final int count, final boolean transpose, final FloatBuffer value) {
		super.glUniformMatrix2fv(location, count, transpose, value);
		checkError();
	}

	@Override
	public void glUniformMatrix3fv (final int location, final int count, final boolean transpose, final FloatBuffer value) {
		super.glUniformMatrix3fv(location, count, transpose, value);
		checkError();
	}

	@Override
	public void glUniformMatrix4fv (final int location, final int count, final boolean transpose, final FloatBuffer value) {
		super.glUniformMatrix4fv(location, count, transpose, value);
		checkError();
	}

	@Override
	public void glUseProgram (final int program) {
		super.glUseProgram(program);
		checkError();
	}

	@Override
	public void glValidateProgram (final int program) {
		super.glValidateProgram(program);
		checkError();
	}

	@Override
	public void glVertexAttrib1f (final int indx, final float x) {
		super.glVertexAttrib1f(indx, x);
		checkError();
	}

	@Override
	public void glVertexAttrib1fv (final int indx, final FloatBuffer values) {
		super.glVertexAttrib1fv(indx, values);
		checkError();
	}

	@Override
	public void glVertexAttrib2f (final int indx, final float x, final float y) {
		super.glVertexAttrib2f(indx, x, y);
		checkError();
	}

	@Override
	public void glVertexAttrib2fv (final int indx, final FloatBuffer values) {
		super.glVertexAttrib2fv(indx, values);
		checkError();
	}

	@Override
	public void glVertexAttrib3f (final int indx, final float x, final float y, final float z) {
		super.glVertexAttrib3f(indx, x, y, z);
		checkError();
	}

	@Override
	public void glVertexAttrib3fv (final int indx, final FloatBuffer values) {
		super.glVertexAttrib3fv(indx, values);
		checkError();
	}

	@Override
	public void glVertexAttrib4f (final int indx, final float x, final float y, final float z, final float w) {
		super.glVertexAttrib4f(indx, x, y, z, w);
		checkError();
	}

	@Override
	public void glVertexAttrib4fv (final int indx, final FloatBuffer values) {
		super.glVertexAttrib4fv(indx, values);
		checkError();
	}

	@Override
	public void glVertexAttribPointer (final int indx, final int size, final int type, final boolean normalized, final int stride,
		final Buffer ptr) {
		super.glVertexAttribPointer(indx, size, type, normalized, stride, ptr);
		checkError();
	}

	@Override
	public void glVertexAttribPointer (final int indx, final int size, final int type, final boolean normalized, final int stride,
		final int ptr) {
		super.glVertexAttribPointer(indx, size, type, normalized, stride, ptr);
		checkError();
	}
}
