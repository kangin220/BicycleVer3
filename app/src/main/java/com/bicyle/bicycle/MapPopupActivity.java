package com.bicyle.bicycle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class MapPopupActivity extends Activity {
    TextView txtText;
    Button no_button;
    private String add;
    private double latitude;
    private double longitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.mappopup_activity);

        //UI 객체생성
        txtText = (TextView)findViewById(R.id.map_popup_Text);
        no_button = (Button)findViewById(R.id.map_route_no_button);

        //데이터 가져오기
        Intent intent = getIntent();
        String data = intent.getStringExtra("data");
        String mode_flag = intent.getStringExtra("mode_flag");
        if(!data.equals("해당 결과가 없습니다")) {
            latitude = intent.getDoubleExtra("lat",0);
            longitude = intent.getDoubleExtra("long",0);
            if (mode_flag.equals("도착지")) {
                Log.d("route_test","팝업 도착지");
                no_button.setVisibility(View.VISIBLE);
                add = "_dest";
            }
            if (mode_flag.equals("출발지")) {
                Log.d("route_test","팝업 출발지");
                no_button.setVisibility(View.VISIBLE);
                add = "_source";
            }
        }
        txtText.setText(data);
    }

    //확인 버튼 클릭
    public void mOnYes(View v){
        //데이터 전달하기
        Intent intent = new Intent();
        intent.putExtra("result", "OK"+add);
        intent.putExtra("lat",latitude);
        intent.putExtra("long",longitude);
        setResult(RESULT_OK, intent);
        Log.d("route_test", add+"확인버튼 누름. lat: "+latitude+" long: "+longitude);
        //액티비티(팝업) 닫기
        finish();
    }

    public void mOnNo(View v){
        Intent intent = new Intent();
        intent.putExtra("result", "NO"+add);
        setResult(RESULT_OK, intent);
        Log.d("route_test", add+"취소버튼 누름");

        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }
}