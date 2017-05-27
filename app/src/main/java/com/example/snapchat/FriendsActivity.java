package com.example.snapchat;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.Preferences;
import com.example.maja.snapchat.R;
import com.example.snapchat.api.Api;
import com.example.snapchat.database.DatabaseHelper;
import com.example.snapchat.database.model.Friend;
import com.example.snapchat.database.model.User;
import com.example.snapchat.dto.FriendDto;
import com.example.snapchat.dto.UserDto;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by maja on 14.05.17.
 */

public class FriendsActivity extends AppCompatActivity {

    private Preferences preferences;
    private FriendsActivity thisInstance;
    private GestureDetectorCompat gestureObject;

    private ListView friendsListView;
    private Button btnAddFriend;
    private EditText friendEmail;
    private Button btnDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = Preferences.getInstance(this);
        thisInstance = this;

        setContentView(R.layout.activity_friends_list);
        friendsListView = (ListView) findViewById(R.id.friends_list_view);
        btnAddFriend = (Button) findViewById(R.id.add_friend_button);
        gestureObject = new GestureDetectorCompat(this, new FriendsActivity.LearnGesture());

        btnAddFriend.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                final Dialog dialog = new Dialog(thisInstance);
                dialog.setContentView(R.layout.dialog_add_friend);
                friendEmail = (EditText) dialog.findViewById(R.id.add_friend_editText);
                btnDialog = (Button) dialog.findViewById(R.id.dialog_add_friend_button);

                btnDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FriendDto friendDto = new FriendDto(preferences.getEmail(),friendEmail.getText().toString());

                        Api.getInstance().addFriend(friendDto)
                          .enqueue(new Callback<UserDto>() {
                            @Override
                            public void onResponse(Call<UserDto> call, Response<UserDto> response) {
                                try {
                                    if (response.code()  < 200 || response.code() >= 300) {
                                        Toast.makeText(thisInstance, "Nie dodano znajomego!", Toast.LENGTH_SHORT).show();
                                        this.onFailure(call, new Throwable("Niepoprawne dane logowania"));
                                    } else {
                                        Toast.makeText(thisInstance, "Dodano użytkownika!", Toast.LENGTH_SHORT).show();
                                        this.onFailure(call, new Throwable("nie wiem jaki bald"));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(Call<UserDto> call, Throwable t) {
                                Log.d(FriendsActivity.class.getSimpleName(), "Error in adding a friend(): " + t.getLocalizedMessage());
                            }
                        });
                        dialog.dismiss();
                    }

                });
                dialog.show();
            }
        }
        );

    };

    @Override
    public boolean onTouchEvent(MotionEvent event){
        this.gestureObject.onTouchEvent(event);
        return super.onTouchEvent(event);
    };



    class LearnGesture extends GestureDetector.SimpleOnGestureListener{

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY){

            if(e2.getX() > e1.getX()){

                Intent intent = new Intent(FriendsActivity.this, MainActivity.class);
                startActivity(intent);
            }
            return true;

        };

    }

}