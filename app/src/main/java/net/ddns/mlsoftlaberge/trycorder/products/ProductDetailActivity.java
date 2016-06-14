package net.ddns.mlsoftlaberge.trycorder.products;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import net.ddns.mlsoftlaberge.trycorder.R;

/*
 * ProductDetailActivity allows to enter a new product item 
 * or to change an existing
 */
public class ProductDetailActivity extends Activity {
	private Spinner mCategory;
	private EditText mUpcText;
	private EditText mTitleText;
	private EditText mBodyText;

	private Uri productUri;

    // buttons of control

    private ImageButton mBacktopButton;
    private Button mBackButton;
    private Button mSaveButton;

    @Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.product_edit);

		mCategory = (Spinner) findViewById(R.id.category);
		mUpcText = (EditText) findViewById(R.id.product_edit_upc);
		mTitleText = (EditText) findViewById(R.id.product_edit_name);
		mBodyText = (EditText) findViewById(R.id.product_edit_description);

        // the search button
        mBacktopButton = (ImageButton) findViewById(R.id.backtop_button);
        mBacktopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonsound();
                buttonback();
            }
        });
        // the search button
        mBackButton = (Button) findViewById(R.id.back_button);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonsound();
                buttonback();
            }
        });
        // the search button
        mSaveButton = (Button) findViewById(R.id.button_save_product);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonsound();
                buttonsave();
            }
        });

        // get extra info from intent
        Bundle extras = getIntent().getExtras();

		// Check from the saved Instance
		productUri = (bundle == null) ? null : (Uri) bundle.getParcelable(MyProductContentProvider.CONTENT_ITEM_TYPE);

		// Or passed from the other activity
		if (extras != null) {
			productUri = extras.getParcelable(MyProductContentProvider.CONTENT_ITEM_TYPE);
		}
		if (productUri!=null) {
			fillData(productUri);
		}

    }

    private void buttonsound() {
        MediaPlayer mediaPlayer = MediaPlayer.create(getBaseContext(), R.raw.keyok2);
        mediaPlayer.start(); // no need to call prepare(); create() does that for you
    }

    private void buttonback() {
        finish();
    }

    private void buttonsave() {
        if (TextUtils.isEmpty(mTitleText.getText().toString())) {
            makeToast();
        } else {
            setResult(RESULT_OK);
            finish();
        }
    }

    // permits this activity to hide status and action bars, and proceed full screen
//	@Override
//	public void onWindowFocusChanged(boolean hasFocus) {
//		super.onWindowFocusChanged(hasFocus);
//		if (hasFocus) {
//			getWindow().getDecorView().setSystemUiVisibility(
//					View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//							| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//							| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//							| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//							| View.SYSTEM_UI_FLAG_FULLSCREEN
//							| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//		}
//	}

	private void fillData(Uri uri) {
		String[] projection = { ProductTable.COLUMN_NAME,ProductTable.COLUMN_UPC,
				ProductTable.COLUMN_DESCRIPTION, ProductTable.COLUMN_CATEGORY };
		Cursor cursor = getContentResolver().query(uri, projection, null, null,
				null);
		if (cursor != null) {
			cursor.moveToFirst();
			String category = cursor.getString(cursor
					.getColumnIndexOrThrow(ProductTable.COLUMN_CATEGORY));

			for (int i = 0; i < mCategory.getCount(); i++) {

				String s = (String) mCategory.getItemAtPosition(i);
				if (s.equalsIgnoreCase(category)) {
					mCategory.setSelection(i);
				}
			}

			mUpcText.setText(cursor.getString(cursor
					.getColumnIndexOrThrow(ProductTable.COLUMN_UPC)));
			mTitleText.setText(cursor.getString(cursor
					.getColumnIndexOrThrow(ProductTable.COLUMN_NAME)));
			mBodyText.setText(cursor.getString(cursor
					.getColumnIndexOrThrow(ProductTable.COLUMN_DESCRIPTION)));

			// Always close the cursor
			cursor.close();
		}
	}

	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		saveState();
		outState.putParcelable(MyProductContentProvider.CONTENT_ITEM_TYPE, productUri);
	}

	@Override
	protected void onPause() {
		super.onPause();
		saveState();
	}

	private void saveState() {
		String category = (String) mCategory.getSelectedItem();
		String upc = mUpcText.getText().toString();
		String name = mTitleText.getText().toString();
		String description = mBodyText.getText().toString();

		// Only save if either name or description
		// is available

		if (description.length() == 0 && name.length() == 0) {
			return;
		}

		ContentValues values = new ContentValues();
		values.put(ProductTable.COLUMN_CATEGORY, category);
		values.put(ProductTable.COLUMN_UPC, upc);
		values.put(ProductTable.COLUMN_NAME, name);
		values.put(ProductTable.COLUMN_DESCRIPTION, description);

		if (productUri == null) {
			// New product
			productUri = getContentResolver().insert(
					MyProductContentProvider.CONTENT_URI, values);
		} else {
			// Update product
			getContentResolver().update(productUri, values, null, null);
		}
	}

	private void makeToast() {
		Toast.makeText(ProductDetailActivity.this, "Please maintain a name",
				Toast.LENGTH_LONG).show();
	}

}
