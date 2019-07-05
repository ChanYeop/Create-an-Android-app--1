package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.myapplication.MESSAGE";
    private int REQUEST_VOTE = 1; //변수 선언
    public static final int REQUEST_IMAGE_CAPTURE = 2;
    private Bitmap imageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void sendMessage(View view) { //함수 선언
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = findViewById(R.id.editText); //EditText를 참조하여 받음
        String message = editText.getText().toString(); // 변수 선언 후 editText를 string 형식으로 받음
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent); //activity를 실행
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_VOTE) {
            if (resultCode == RESULT_OK) {
                String result = data.getStringExtra(VoteActivity.EXTRA_RESULT); //vote activity에 있는 결과 값 받기
                TextView textView = findViewById(R.id.textView);
                textView.setText(result);
            } else { // RESULT_CANCEL
                Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show(); // 만약 실패했을 시 찗은 메세지로 밑에 failed를 출력
            }
        }

        else if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                Bundle extras = data.getExtras(); // data 키 값을 받아온다.
                Bitmap imageBitmap = (Bitmap) extras.get("data"); //원본 형태로 받기
                ImageView imageView = findViewById(R.id.imageView); //이미지를 표현해준 imageview의 참조 값 불러오기
                imageView.setImageBitmap(imageBitmap);
                String path =
                        getExternalFilesDir(Environment.DIRECTORY_PICTURES)+"/Share.png"; //파일 저장할 경로를 설정해준다.
                File file=new File(path); //file의 객체 생성
                FileOutputStream out; //
                try {
                    out = new FileOutputStream(file); //string에서 설정한 파일 경로와 파일 이름으로 파일 객체를 생성해준다.
                    imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, out); //compress를 이용하여 png파일로 변환 후 FileOutputStream의 객체에 써준다.
                    out.flush(); //데이터를 보내준다.
                    out.close(); // 객체를 닫는다.
                } catch (Exception e) {
                    e.printStackTrace();
                }
                EditText editText = findViewById(R.id.editText);
                String message = editText.getText().toString();

                //String path =
                //        getExternalFilesDir(Environment.DIRECTORY_PICTURES)+"/Share.png";
                //File file=new File(path);
                //FileOutputStream out;

                Uri bmpUri = FileProvider.getUriForFile(MainActivity.this,
                        "com.example.myapplication.fileprovider", file); // FileProvier을 통해 권한 설정 후 Uri 객체에 저장

                Intent shareintent = new Intent();
                shareintent.setAction((Intent.ACTION_SEND)); //인텐트의 작업에 대한 설정
                shareintent.putExtra(Intent.EXTRA_SUBJECT,message);
                shareintent.putExtra(Intent.EXTRA_TEXT,message);
                shareintent.setType("*/*");
                shareintent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                startActivity(Intent.createChooser(shareintent, "send"));
            } else { // RESULT_CANCEL
                Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show(); // 만약 실패했을 시 밑에 failed를 출력
            }
        }
    }

    public void startVote(View view) {
        Intent intent = new Intent ( this, VoteActivity.class);
        startActivityForResult(intent, REQUEST_VOTE); // activity를 통해 어떠한 결과값 받기

    }


    public void takeScreenshot(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) { // 카메라 앱이 있는지 확인
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }

    }

    public void shareMessageWithIntent(View view) {
// Create the text message with a string
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        EditText editText = findViewById(R.id.editText);
        String textMessage = editText.getText().toString();
        sendIntent.putExtra(Intent.EXTRA_TEXT, textMessage);
        sendIntent.setType("text/plain");
// Verify that the intent will resolve to an activity
        if (sendIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(sendIntent);
        }
    }

}
