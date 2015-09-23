package microcat.converter;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

public class GLWindowContext
{
	private final long windowID;
	int w, h;
	
	public GLWindowContext(int width, int height)
	{
		if (GLFW.glfwInit() != GL11.GL_TRUE )
			throw new IllegalStateException("Unable to initialize GLFW");
		GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GL11.GL_FALSE);
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GL11.GL_FALSE);
		GLFW.glfwWindowHint(GLFW.GLFW_ACCUM_RED_BITS, 8);
		GLFW.glfwWindowHint(GLFW.GLFW_ACCUM_GREEN_BITS, 8);
		GLFW.glfwWindowHint(GLFW.GLFW_ACCUM_BLUE_BITS, 8);
		GLFW.glfwWindowHint(GLFW.GLFW_ACCUM_ALPHA_BITS, 8);
		
		windowID = GLFW.glfwCreateWindow(width, height, "", 0, 0);
		if (windowID == 0)
			throw new RuntimeException("Failed to create the GLFW window");
		
		GLFW.glfwMakeContextCurrent(windowID);
		GLContext.createFromCurrent();
		
		w = width;
		h = height;
	}
	
	public void update()
	{
		GLFW.glfwSwapBuffers(windowID);
		GLFW.glfwPollEvents();
	}
	
	public void close()
	{
		GLFW.glfwDestroyWindow(windowID);
		GLFW.glfwTerminate();
	}
	
	public int getWidth()
	{
		return w;
	}
	
	public int getHeight()
	{
		return h;
	}
}
