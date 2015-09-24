package microcat.converter;

public class ColorUtil
{
	public static final int BIT_ALPHA = 24;
	public static final int BIT_RED = 16;
	public static final int BIT_GREEN = 8;
	public static final int BIT_BLUE = 0;
	
	public static int asColor(int r, int g, int b, int a)
	{
		return  (clampColor(a) << BIT_ALPHA) | (clampColor(r) << BIT_RED) | (clampColor(g) << BIT_GREEN) | (clampColor(b) << BIT_BLUE);
	}
	
	public static int getAlpha(int c)
	{
		return getColorByte(c, BIT_ALPHA);
	}
	
	public static int getRed(int c)
	{
		return getColorByte(c, BIT_RED);
	}
	
	public static int getGreen(int c)
	{
		return getColorByte(c, BIT_GREEN);
	}
	
	public static int getBlue(int c)
	{
		return getColorByte(c, BIT_BLUE);
	}
	
	public static int setAlpha(int c, int a)
	{
		return c | (clampColor(a) << BIT_ALPHA);
	}
	
	public static int setRed(int c, int a)
	{
		return c | (clampColor(a) << BIT_RED);
	}
	
	public static int setGreen(int c, int a)
	{
		return c | (clampColor(a) << BIT_GREEN);
	}
	
	public static int setBlue(int c, int a)
	{
		return c | (clampColor(a) << BIT_BLUE);
	}
	
	public static int add(int c0, int c1)
	{
		return asColor(getRed(c0) + getRed(c1), getGreen(c0) + getGreen(c1), getBlue(c0) + getBlue(c1), getAlpha(c0) + getAlpha(c1));
	}
	
	public static int sub(int c0, int c1)
	{
		return asColor(getRed(c0) - getRed(c1), getGreen(c0) - getGreen(c1), getBlue(c0) - getBlue(c1), getAlpha(c0) - getAlpha(c1));
	}
	
	public static int mul(int c0, float m)
	{
		return asColor((int)(getRed(c0)*m), (int)(getGreen(c0)*m), (int)(getBlue(c0)*m), (int)(getAlpha(c0)*m));
	}
	
	public static int getDifferenceSq(int c0, int c1)
	{
		int r = getRed(c0) - getRed(c1);
		int g = getGreen(c0) - getGreen(c1);
		int b = getBlue(c0) - getBlue(c1);
		return r*r + g*g + b*b;
	}
	
	private static int getColorByte(int c, int bit)
	{
		return c >> bit & 0xFF;
	}
	
	private static int clampColor(int c)
	{
		return c > 0xFF ? 0xFF : c < 0 ? 0 : c;
	}
}
