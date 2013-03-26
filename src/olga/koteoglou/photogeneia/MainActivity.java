package olga.koteoglou.photogeneia;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;

public class MainActivity extends Activity {

	private static final int SELECT_PICTURE = 0;
	private static final int IMAGE_CAPTURE = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		boolean titled = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_main);
		if(titled){
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title_bar_main);
		}
		
		((ImageButton) findViewById(R.id.button1))
		.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {

				// in onCreate or any event where your want the user to
				// select a file
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);

				//check if there is app to receive intent
				PackageManager packageManager = getPackageManager();
				List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
				@SuppressWarnings("unused")
				boolean isIntentSafe = activities.size() > 0;

				startActivityForResult(Intent.createChooser(intent,
						"Select Picture"), SELECT_PICTURE);
			}
		});

		((ImageButton) findViewById(R.id.button2))
		.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Intent cameraIntent = new Intent(
						android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(cameraIntent, IMAGE_CAPTURE);
			}
		});
	}


	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == RESULT_OK) {
			if (requestCode == SELECT_PICTURE) {
				Uri selectedImageUri = data.getData();
				Intent intent = new Intent(this, EditImage.class);
				intent.setType("/*image");
				intent.setData(selectedImageUri);
				intent.putExtra("EditMode", false);
				startActivity(intent);
			}
			else if (requestCode == IMAGE_CAPTURE)
			{
				//File dir = Environment .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                //File output = new File(dir, "camerascript.png");
                //String cPath = output.getAbsolutePath();
                Intent cameraIntent = new Intent(this, EditImage.class);
				cameraIntent.setType("/*image");
				cameraIntent.setData(data.getData());
				//if edit mode is 1, intent is from camera
				cameraIntent.putExtra("EditMode", true);
                startActivity(cameraIntent);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
}
