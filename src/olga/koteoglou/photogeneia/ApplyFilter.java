package olga.koteoglou.photogeneia;

import java.io.FileNotFoundException;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;

import com.jabistudio.androidjhlabs.filter.ChannelMixFilter;
import com.jabistudio.androidjhlabs.filter.ContrastFilter;
import com.jabistudio.androidjhlabs.filter.EdgeFilter;
import com.jabistudio.androidjhlabs.filter.GlowFilter;
import com.jabistudio.androidjhlabs.filter.GrayscaleFilter;
import com.jabistudio.androidjhlabs.filter.SharpenFilter;
import com.jabistudio.androidjhlabs.filter.util.AndroidUtils;

public class ApplyFilter{
	
	public ApplyFilter(){}

	public static Bitmap applyFilter_contrast(Bitmap bitmap) throws FileNotFoundException, IOException{

		ContrastFilter filter = new ContrastFilter();
		filter.setBrightness(1.1f);
		filter.setContrast(1.1f);

		int[] src = AndroidUtils.bitmapToIntArray(bitmap);
		int[] src2 = filter.filter(src, bitmap.getWidth(), bitmap.getHeight());
		Bitmap bitmap_temp = Bitmap.createBitmap(src2, bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);

		return bitmap_temp;
	}
	
	public static Bitmap applyFilter_glow(Bitmap bitmap) throws FileNotFoundException, IOException{

		GlowFilter filter2 = new GlowFilter();
		filter2.setAmount(0.03f);

		int[] src = AndroidUtils.bitmapToIntArray(bitmap);
		filter2.filter(src, bitmap.getWidth(), bitmap.getHeight());
		Bitmap bitmap_temp = Bitmap.createBitmap(src, bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		return bitmap_temp;
	}
	
	public static Bitmap applyFilter_sharpen(Bitmap bitmap) throws FileNotFoundException, IOException{

		SharpenFilter filter = new SharpenFilter();
		int[] src = AndroidUtils.bitmapToIntArray(bitmap);
		for (int i = 0; i < 3; i++)
		{
			src = filter.filter(src, bitmap.getWidth(), bitmap.getHeight());
		}
		Bitmap bitmap_temp = Bitmap.createBitmap(src, bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);

		return bitmap_temp;
	}
	
	public static Bitmap applyFilter_grayscale(Bitmap bitmap) throws FileNotFoundException, IOException{

		Bitmap bitmap_temp = bitmap.copy(Config.ARGB_8888, true);
		GrayscaleFilter filter = new GrayscaleFilter();
		for (int y = 0; y < bitmap.getHeight(); y++) {
			for (int x = 0; x < bitmap.getWidth(); x++) {
				bitmap_temp.setPixel(x, y, filter.filterRGB(x,y,bitmap.getPixel(x, y)));
			}
		}
		return bitmap_temp;
	}
	
	public static Bitmap applyFilter_channelMixR(Bitmap bitmap) throws FileNotFoundException, IOException{

		Bitmap bitmap_temp = bitmap.copy(Config.ARGB_8888, true);
		ChannelMixFilter filter = new ChannelMixFilter();
		filter.setIntoR(127);filter.setIntoG(0);filter.setIntoB(0);
		filter.setBlueGreen(0);filter.setGreenRed(0);filter.setRedBlue(0);
		for (int y = 0; y < bitmap.getHeight(); y++) {
			for (int x = 0; x < bitmap.getWidth(); x++) {
				bitmap_temp.setPixel(x, y, filter.filterRGB(x,y,bitmap.getPixel(x, y)));
			}
		}
		return bitmap_temp;
	}
	
	public static Bitmap applyFilter_channelMixG(Bitmap bitmap) throws FileNotFoundException, IOException{

		Bitmap bitmap_temp = bitmap.copy(Config.ARGB_8888, true);
		ChannelMixFilter filter = new ChannelMixFilter();
		filter.setIntoR(0);filter.setIntoG(127);filter.setIntoB(0);
		filter.setBlueGreen(0);filter.setGreenRed(0);filter.setRedBlue(0);
		for (int y = 0; y < bitmap.getHeight(); y++) {
			for (int x = 0; x < bitmap.getWidth(); x++) {
				bitmap_temp.setPixel(x, y, filter.filterRGB(x,y,bitmap.getPixel(x, y)));
			}
		}
		return bitmap_temp;
	}
	
	public static Bitmap applyFilter_channelMixB(Bitmap bitmap) throws FileNotFoundException, IOException{

		Bitmap bitmap_temp = bitmap.copy(Config.ARGB_8888, true);
		ChannelMixFilter filter = new ChannelMixFilter();
		filter.setIntoR(0);filter.setIntoG(0);filter.setIntoB(127);
		filter.setBlueGreen(0);filter.setGreenRed(0);filter.setRedBlue(0);
		for (int y = 0; y < bitmap.getHeight(); y++) {
			for (int x = 0; x < bitmap.getWidth(); x++) {
				bitmap_temp.setPixel(x, y, filter.filterRGB(x,y,bitmap.getPixel(x, y)));
			}
		}
		return bitmap_temp;
	}
	
	public static Bitmap applyFilter_edge(Bitmap bitmap) throws FileNotFoundException, IOException{

		EdgeFilter filter = new EdgeFilter();
		int[] src = AndroidUtils.bitmapToIntArray(bitmap);
		src = filter.filter(src, bitmap.getWidth(), bitmap.getHeight());
		Bitmap bitmap_temp = Bitmap.createBitmap(src, bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);

		return bitmap_temp;
	}
}