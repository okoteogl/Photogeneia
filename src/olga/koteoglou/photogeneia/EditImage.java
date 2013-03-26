package olga.koteoglou.photogeneia;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class EditImage extends Activity{

	//action id
	private static final int ID_PLUS    = 1;
	private static final int ID_X   = 2;
	private ImageView imageView;
	private Bitmap bitmap, bitmap_glow, bitmap_original;
	private Bitmap bitmap_grayscale, bitmap_sharpen;
	private Bitmap bitmap_cmR, bitmap_cmG, bitmap_cmB, bitmap_edge;
	private Uri imageUri;
	//	private static final String IMAGE_RESOURCE = "image-resource";
	//	private int image;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		boolean titled = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_edit_image);
		if(titled){
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title_bar);
		}


		QuickActionItem plusItem    = new QuickActionItem(ID_PLUS, getResources().getDrawable(R.drawable.plus3));
		QuickActionItem xItem     = new QuickActionItem(ID_X, getResources().getDrawable(R.drawable.x3));
		final QuickActionPopup quickActionPopup1 = new QuickActionPopup(this, QuickActionPopup.VERTICAL);
		//add action items into QuickActionPopup
		quickActionPopup1.addActionItem(plusItem);
		quickActionPopup1.addActionItem(xItem);



		imageView = (ImageView) findViewById(R.id.result);
		imageUri = getIntent().getData();
		//minimize size of bitmap by sampling down
		System.out.println("ImageUri = "+imageUri);
		BitmapFactory.Options options=new BitmapFactory.Options();
		options.inJustDecodeBounds = true;

		try {
			BitmapFactory.decodeFile(getRealPathFromURI(imageUri), options);
			Display display = getWindowManager().getDefaultDisplay();
			options.inSampleSize = Math.round(options.outWidth/display.getWidth());
			if (options.inSampleSize <= 1){options.inSampleSize = 1;}

			options.inJustDecodeBounds = false;
			bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri), null, options);
			bitmap_glow = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri), null, options);
			bitmap_original = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri), null, options);					
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		imageView.setImageBitmap(bitmap);


		//apply all filters
		try {
			bitmap_grayscale = ApplyFilter.applyFilter_grayscale(bitmap);
			bitmap_cmR = ApplyFilter.applyFilter_channelMixR(bitmap);
			bitmap_cmG = ApplyFilter.applyFilter_channelMixG(bitmap);
			bitmap_cmB = ApplyFilter.applyFilter_channelMixB(bitmap);
			bitmap_sharpen = ApplyFilter.applyFilter_sharpen(bitmap);
			bitmap_edge = ApplyFilter.applyFilter_edge(bitmap);
		} catch (FileNotFoundException e) {
			Toast.makeText(getApplicationContext(), "file not found", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		} catch (IOException e) {
			Toast.makeText(getApplicationContext(), "io exception", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}

		quickActionPopup1.setOnActionItemClickListener(new QuickActionPopup.OnActionItemClickListener() {           
			@Override
			public void onItemClick(QuickActionPopup source, int pos, int actionId) {               
				Bitmap bitmap_temp = null;
				//filtering items by id
				if (actionId == ID_PLUS) {
					try {
						bitmap_temp = ApplyFilter.applyFilter_glow(bitmap);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					bitmap = null;
					bitmap = bitmap_temp;
					imageView.setImageBitmap(bitmap);				

					//Toast.makeText(getApplicationContext(), "Mail clicked", Toast.LENGTH_SHORT).show();
				} else if (actionId == ID_X) {
					bitmap = null;
					bitmap = bitmap_glow;
					imageView.setImageBitmap(bitmap);
				} 
			}
		});

		//show on btn1
		ImageButton btn1 = (ImageButton) this.findViewById(R.id.title_bar_button3);
		btn1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				quickActionPopup1.show(v);
			}
		});



		((ImageButton) findViewById(R.id.title_bar_button2))
		.setOnClickListener(new OnClickListener() {
			Bitmap bitmap_temp = null;
			public void onClick(View arg0) {
				try {
					//bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
					bitmap_temp = rotateImage(bitmap);
					bitmap_glow = rotateImage(bitmap_glow);
					bitmap_grayscale = rotateImage(bitmap_grayscale);
					bitmap_cmR = rotateImage(bitmap_cmR);
					bitmap_cmG = rotateImage(bitmap_cmG);
					bitmap_cmB = rotateImage(bitmap_cmB);
					bitmap_sharpen = rotateImage(bitmap_sharpen);
					bitmap_edge = rotateImage(bitmap_edge);
				} catch (FileNotFoundException e) {
					System.out.println("File Not Found?");
					e.printStackTrace();
				} catch (IOException e) {
					System.out.println("IO Exception?");
					e.printStackTrace();
				}
				bitmap = null;
				bitmap = bitmap_temp; 
				imageView.setImageBitmap(bitmap);				
			}
		});

		((ImageButton) findViewById(R.id.title_bar_button4))
		.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {

				int imageNum = 0;
				File imagesFolder = new File(Environment.getExternalStorageDirectory()+File.separator+"DCIM", "@string/app_name");
				imagesFolder.mkdirs();
				String fileName = "image_" + String.valueOf(imageNum) + ".jpg";
				File output = new File(imagesFolder, fileName);
				while (output.exists()){
					imageNum++;
					fileName = "image_" + String.valueOf(imageNum) + ".jpg";
					output = new File(imagesFolder, fileName);
				}
				try {
					ByteArrayOutputStream bytes = new ByteArrayOutputStream();
					bitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
					FileOutputStream fo = new FileOutputStream(output);
					fo.write(bytes.toByteArray());
					fo.flush();
					fo.close();
					MediaScannerConnection.scanFile(EditImage.this, new String[]{output.getAbsolutePath()}, null, null);

				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		changeImageButtonBackground(R.id.Button1);
		((ImageButton) findViewById(R.id.Button1))
		.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				imageView.setImageBitmap(bitmap_original);
				bitmap = bitmap_original;
				changeImageButtonBackground(R.id.Button1);
			}});

		((ImageButton) findViewById(R.id.Button2))
		.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				imageView.setImageBitmap(bitmap_grayscale);	
				bitmap = bitmap_grayscale;
				changeImageButtonBackground(R.id.Button2);
			}
		});

		((ImageButton) findViewById(R.id.Button3))
		.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				imageView.setImageBitmap(bitmap_cmR);
				bitmap = bitmap_cmR;
				changeImageButtonBackground(R.id.Button3);
			}
		});

		((ImageButton) findViewById(R.id.Button4))
		.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				imageView.setImageBitmap(bitmap_cmG);
				bitmap = bitmap_cmG;
				changeImageButtonBackground(R.id.Button4);
			}
		});

		((ImageButton) findViewById(R.id.Button5))
		.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				imageView.setImageBitmap(bitmap_cmB);	
				bitmap = bitmap_cmB;
				changeImageButtonBackground(R.id.Button5);
			}
		});

		((ImageButton) findViewById(R.id.Button6))
		.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				imageView.setImageBitmap(bitmap_sharpen);
				bitmap = bitmap_sharpen;
				changeImageButtonBackground(R.id.Button6);
			}
		});

		((ImageButton) findViewById(R.id.Button7))
		.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				imageView.setImageBitmap(bitmap_edge);
				bitmap = bitmap_edge;
				changeImageButtonBackground(R.id.Button7);
			}
		});

	}

	private String getRealPathFromURI(Uri contentURI) {
		Cursor cursor = getContentResolver()
				.query(contentURI, null, null, null, null); 
		cursor.moveToFirst(); 
		int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA); 
		System.out.println("idx = "+idx);
		if (idx == -1){return new String("null");}

		return cursor.getString(idx); 
	}

	private void changeImageButtonBackground(int pressedButtonID)
	{
		findViewById(R.id.Button1).setBackgroundResource(R.drawable.custom_button_transparent);
		findViewById(R.id.Button2).setBackgroundResource(R.drawable.custom_button_transparent);
		findViewById(R.id.Button3).setBackgroundResource(R.drawable.custom_button_transparent);
		findViewById(R.id.Button4).setBackgroundResource(R.drawable.custom_button_transparent);
		findViewById(R.id.Button5).setBackgroundResource(R.drawable.custom_button_transparent);
		findViewById(R.id.Button6).setBackgroundResource(R.drawable.custom_button_transparent);
		findViewById(R.id.Button7).setBackgroundResource(R.drawable.custom_button_transparent);

		ImageButton temp = (ImageButton) findViewById(pressedButtonID);
		temp.setBackgroundResource(R.drawable.custom_button);
	}

	private Bitmap rotateImage(Bitmap bitmap) throws FileNotFoundException, IOException
	{
		Matrix matrix = new Matrix();
		matrix.postRotate(90);
		Bitmap bitmap_temp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),matrix, true);

		return bitmap_temp;
	}

	private int getImageOrientation(){
		final String[] imageColumns = { MediaStore.Images.Media._ID, MediaStore.Images.ImageColumns.ORIENTATION };
		final String imageOrderBy = MediaStore.Images.Media._ID+" DESC";
		Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				imageColumns, null, null, imageOrderBy);

		if(cursor.moveToFirst()){
			int orientation = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.ImageColumns.ORIENTATION));
			System.out.println("orientation==="+orientation);
			cursor.close();
			return orientation;
		} else {
			return 0;
		}
	}

	public static Bitmap getBitmapFromURL(String src) {
		try {
			URL url = new URL(src);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			Bitmap myBitmap = BitmapFactory.decodeStream(input);
			return myBitmap;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
