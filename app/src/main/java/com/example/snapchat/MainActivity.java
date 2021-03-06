package com.example.snapchat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Base64;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.Preferences;
import com.example.maja.snapchat.R;
import com.example.snapchat.utils.ImageHolder;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.ExecutionException;

public class MainActivity extends FragmentActivity {

    private Button btnCamera;
    private Button btnSendSnap;
    private Button btnEditSnap;
    private ImageView capturedImage;
    private Preferences preferences;
    private GestureDetectorCompat gestureObject;
    private MainActivity thisInstance;
    private boolean imageExists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = Preferences.getInstance(this);
        thisInstance = this;
        setContentView(R.layout.activity_main);
        btnCamera = (Button) findViewById(R.id.btnCamera);
        btnSendSnap = (Button) findViewById(R.id.btnSendSnap);
        btnEditSnap = (Button) findViewById(R.id.btnEditSnap);
        capturedImage = (ImageView) findViewById(R.id.capturedImage);
        gestureObject = new GestureDetectorCompat(this, new LearnGesture());

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });

        btnEditSnap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editImage(capturedImage);
            }
        });

        btnSendSnap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageExists == false) {
                    Toast.makeText(thisInstance, "Halo! Najpierw zrob zdjecie! ;)", Toast.LENGTH_SHORT).show();
                    return;
                }
                EncodeImage encoder = new EncodeImage(capturedImage);
                String img = null;
                try {
                    img = encoder.execute().get();
                    ImageHolder.setImage(img);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                if (img == null) {
                    Toast.makeText(getApplicationContext(), "encoding failed", Toast.LENGTH_LONG).show();
                    return;
                }
                selectFriends();
            }
        });


    }

    private void selectFriends() {
        Intent intent = new Intent(MainActivity.this, SelectFriendsToSnapActivity.class);
        startActivity(intent);
        finish();
    }

    private String encodeImage(ImageView capturedImage) {
        capturedImage.buildDrawingCache();
        Bitmap bitmap = capturedImage.getDrawingCache();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] b = stream.toByteArray();
        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encodedImage;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 0);
    }

    private void editImage(ImageView capturedImage) {
        Intent intent = new Intent(MainActivity.this, EditSnapActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bitmap bp = (Bitmap) data.getExtras().get("data");
            capturedImage.setImageBitmap(bp);
            imageExists = true;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.gestureObject.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    ;

    class LearnGesture extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            if (e2.getX() > e1.getX()) {

                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(MainActivity.this, FriendsActivity.class);
                startActivity(intent);

            }
            return true;

        }

        ;

    }

    class EncodeImage extends AsyncTask<Void, Void, String> {

        private ImageView imageView;
        private String image;

        public EncodeImage(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected String doInBackground(Void... params) {
            image = encodeImage(imageView);
            return image;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }
    }

}
