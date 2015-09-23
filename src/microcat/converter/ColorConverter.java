package microcat.converter;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.ARBFragmentShader;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import net.quantuminfinity.engine.gl.fbo.PingPongFBO;
import net.quantuminfinity.engine.gl.shader.ShaderProgram;
import net.quantuminfinity.engine.gl.util.GLMU;
import net.quantuminfinity.engine.io.GLWindow;
import net.quantuminfinity.engine.math.vector.Vector2;
import net.quantuminfinity.engine.texture.TextureLoader;

public class ColorConverter
{	
	static int SAMPLER_ID = 0, IMG_ID = 1;
	
	BufferedImage pal, img;
	int imgWidth, imgHeight, imgTex, palTex;
	String ofile;
	
	GLWindow w;
	PingPongFBO fbo;
	ShaderProgram conv, outp;
	
	public ColorConverter(String ifile, String ofile) throws IOException
	{
		pal = ImageIO.read(ColorConverter.class.getResource("/assets/palette.png"));
		
		img = ImageIO.read(new File(ifile));
		imgWidth = img.getWidth();
		imgHeight = img.getHeight();
		
		this.ofile = ofile;
	}
	
	public void genImage()
	{
		init();
		work();
		close();
	}

	private void init()
	{
		w = new GLWindow(imgWidth, imgHeight, "", false);
		GLMU.initGL2D(w.getWidth(), w.getHeight());
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glClearColor(0, 0, 0, 1);
		
		fbo = new PingPongFBO(1, new Vector2(w.getWidth(), w.getHeight()), GL11.GL_RGBA8);
		fbo.setData(TextureGenerator.genTex16(w.getWidth(), w.getHeight()));
		
		System.out.println("Creating conversion shader...");
		conv = new ShaderProgram()
		{{
			addShader("pkg:/shaders/shader.vert", ARBVertexShader.GL_VERTEX_SHADER_ARB);
			addShader("pkg:/shaders/conv.frag", ARBFragmentShader.GL_FRAGMENT_SHADER_ARB);
			compile();
		}};
		
		System.out.println("Creating output shader...");
		outp = new ShaderProgram()
		{{
			addShader("pkg:/shaders/shader.vert", ARBVertexShader.GL_VERTEX_SHADER_ARB);
			addShader("pkg:/shaders/outp.frag", ARBFragmentShader.GL_FRAGMENT_SHADER_ARB);
			compile();
		}};
		
		conv.bind();
		conv.setUniform("uData", SAMPLER_ID);
		conv.setUniform("uImg", IMG_ID);
		conv.setUniform("uResolution", w.getWidth(), w.getHeight());
		conv.release();
		
		outp.bind();
		outp.setUniform("uData", SAMPLER_ID);
		outp.setUniform("uPal", IMG_ID);
		outp.setUniform("uResolution", w.getWidth(), w.getHeight());
		outp.release();
		
		imgTex = TextureLoader.loadTexture(img, GL11.GL_NEAREST, GL11.GL_RGBA8);
		palTex = TextureLoader.loadTexture(pal, GL11.GL_NEAREST, GL11.GL_RGBA8);
	}
	
	private void work()
	{
		for (int i=0; i<pal.getWidth()*pal.getHeight(); i++)
		{
			int rgb = pal.getRGB(i%pal.getWidth(), i/pal.getWidth());
			float r = (rgb >> 16 & 0xFF) / 256f;
			float g = (rgb >>  8 & 0xFF) / 256f;
			float b = (rgb >>  0 & 0xFF) / 256f;
			
			fbo.bind();
			GL13.glActiveTexture(GL13.GL_TEXTURE0 + SAMPLER_ID); GL11.glBindTexture(GL11.GL_TEXTURE_2D, fbo.getData(0));
			GL13.glActiveTexture(GL13.GL_TEXTURE0 + IMG_ID); GL11.glBindTexture(GL11.GL_TEXTURE_2D, imgTex);
			conv.bind();
			conv.setUniform("uColor", r, g, b);
			conv.setUniform("uColorID", i/256f);
			conv.setUniform("uColorUV", (float)(i%pal.getWidth())/pal.getWidth(), (float)(i/pal.getWidth())/pal.getHeight());
			
			GL11.glBegin(GL11.GL_QUADS);
			{
				GL11.glTexCoord2f(0.0f, 0.0f); GL11.glVertex2f(0, 0);
				GL11.glTexCoord2f(1.0f, 0.0f); GL11.glVertex2f(w.getWidth(), 0);
				GL11.glTexCoord2f(1.0f, 1.0f); GL11.glVertex2f(w.getWidth(), w.getHeight());
				GL11.glTexCoord2f(0.0f, 1.0f); GL11.glVertex2f(0, w.getHeight());
			}
			GL11.glEnd();
			
			conv.release();
			fbo.release();
			w.update();
		}
		
		while (!w.isCloseRequested())
		{
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
			GL11.glLoadIdentity();
			
			GL13.glActiveTexture(GL13.GL_TEXTURE0); GL11.glBindTexture(GL11.GL_TEXTURE_2D, fbo.getData(0));
			GL13.glActiveTexture(GL13.GL_TEXTURE0 + IMG_ID); GL11.glBindTexture(GL11.GL_TEXTURE_2D, palTex);
			outp.bind();
			GL11.glBegin(GL11.GL_QUADS);
				GL11.glVertex2f(0, 0);
				GL11.glVertex2f(w.getWidth(), 0);
				GL11.glVertex2f(w.getWidth(), w.getHeight());
				GL11.glVertex2f(0, w.getHeight());
			GL11.glEnd();
			outp.release();
			
			w.update();
		}
	}
	
	private void close()
	{
		GL11.glDeleteTextures(imgTex);
		GL11.glDeleteTextures(palTex);
		outp.destroy();
		conv.destroy();
		fbo.destroy();
		w.close();
	}
		
	public static void main(String[] args)
	{
		try
		{
			new ColorConverter("earth.jpg","").genImage();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
