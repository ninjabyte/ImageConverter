package microcat.converter;

import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class ColorConverter
{
	int[] pal, src, dst;
	int width, height;
	
	public ColorConverter(String ifile, String ofile) throws IOException
	{
		BufferedImage palImg = ImageIO.read(ColorConverter.class.getResource("/assets/palette.png"));
		BufferedImage srcImg = ImageIO.read(new File(ifile));
		
		width = srcImg.getWidth();
		height = srcImg.getHeight();
		
		pal = toPixelArray(palImg);
		src = toPixelArray(srcImg);
		dst = new int[src.length]; // the alpha component of the dst image is used to store the color's index on the palette.
	}
	
	public void genImage()
	{
		long startTime = System.nanoTime();
		
		for (int i=0; i<src.length; i++)
			dst[i] = getClosestColor(src[i]);
		
		long endTime = System.nanoTime();
		long cTime = (endTime - startTime) / 1000000;
		
		System.out.println("Conversion took " + cTime + " ms");
		
		show(toImage(dst, width, height));
	}
	
	private void show(BufferedImage img)
	{
		JFrame frame = new JFrame();
		frame.getContentPane().setLayout(new FlowLayout());
		frame.getContentPane().add(new JLabel(new ImageIcon(img)));
		frame.pack();
		frame.setVisible(true);
	}
	
	private int getClosestColor(int color)
	{
		int closestDst = Integer.MAX_VALUE, col = 0, dst;

		for (int i=0; i<pal.length; i++)
			if ((dst = ColorUtil.getDifferenceSq(color, pal[i])) < closestDst)
			{
				closestDst = dst;
				col = i;
			}
		
		return ColorUtil.setAlpha(pal[col], col);
	}
	
	private int[] toPixelArray(BufferedImage img)
	{
		byte[] pixels = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
		int[] data = new int[img.getWidth() * img.getHeight()];
		boolean hasAlpha = img.getAlphaRaster() != null;
		
		if (hasAlpha)
			for (int i=0; i<data.length; i++)
				data[i] = ColorUtil.asColor(pixels[i*4+3] & 0xFF, pixels[i*4+2] & 0xFF, pixels[i*4+1] & 0xFF, 0);
		else
			for (int i=0; i<data.length; i++)
				data[i] = ColorUtil.asColor(pixels[i*3+2] & 0xFF, pixels[i*3+1] & 0xFF, pixels[i*3] & 0xFF, 0);
		
		return data;
	}
	
	private BufferedImage toImage(int[] pixels, int width, int height)
	{
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		int[] data = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();
		System.arraycopy(pixels, 0, data, 0, pixels.length);
		return img;
	}
	
	public static void main(String[] args)
	{
		try
		{
			new ColorConverter("spectrum.png","").genImage();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
