package com.ichmed.trinketeers.util.texturesystem;

import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glTexParameteri;

/**
 * The class to use for saving textures of all kinds.
 * Use a codec to instatiate it.
 * @author marcel
 * @see TextureCodecRegistry
 */
public abstract class Texture {

	private int textype = 0;
	private int id = 0;
	
	protected Texture(int id, int textype){
		this.id = id;
		this.textype = textype;
	}

	/**
	 * binds the texture in the current OpenGL context
	 */
	public void bind() {
		glBindTexture(textype, id);
		glTexParameteri(textype, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(textype, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
	}

	/**
	 * destroys the texture from the graphics card. This should
	 * only be done if it is really not needed anymore or the
	 * graphics memory needs to be cleaned. 
	 */
	public void destruct() {
		glDeleteTextures(id);
	}
	
}
