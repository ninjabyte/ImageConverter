package microcat.converter;

import java.awt.image.BufferedImage;

import org.lwjgl.opengl.GL11;

import net.quantuminfinity.engine.texture.Texture16;
import net.quantuminfinity.engine.texture.TextureLoader;

public class TextureGenerator
{	
	public static int genTex(int width, int height)
	{
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		for (int x = 0; x < image.getWidth(); x++)
			for (int y = 0; y < image.getHeight(); y++)
				image.setRGB(x, y, 0x00FF0000);
		
		return TextureLoader.loadTexture(image, GL11.GL_NEAREST, GL11.GL_RGBA8);
	}
	
	public static int genTex16(int width, int height)
	{
		Texture16 image = new Texture16(width, height);
		for (int x = 0; x < image.getWidth(); x++)
			for (int y = 0; y < image.getHeight(); y++)
				image.setPixel(x, y, Texture16.getRGBA(0xFFFF, 0, 0, 0));
		
		return image.getGLTexture();
	}
}
