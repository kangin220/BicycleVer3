package com.bicyle.bicycle.Board;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bicyle.bicycle.Data.DataManager;
import com.bicyle.bicycle.MainScreen;
import com.bicyle.bicycle.MapActivity;
import com.bicyle.bicycle.ProfDataSet;
import com.bicyle.bicycle.R;
import com.bicyle.bicycle.util.MyUtil;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;

public class BoardActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    ProfDataSet myProf;

    ListView listView;
    Toolbar mainToolbar;
    DrawerLayout dlDrawer;
    ActionBarDrawerToggle dtToggle;

    ListView boardListView;
    ListView searchListView;
    EditText searchEditText;
    TextView kindTV;
    TextView likeTV;
    Button writeBtn;
    Button searchBtn;
    Button refreshBtn;
    Button myBoardBtn;
    View writeBoardDialogView;
    ArrayList<BoardDTO> boardList = new ArrayList<>();
    BoardAdapter boardAdapter;
    ArrayList<BoardDTO> searchBoardList = new ArrayList<>();
    BoardAdapter searchBoardAdapter;
    boolean searchState = false; //search상태?
    int boardKindFilterControl = 0; //전체 : 0  ,
    int searchBoardKindFilterControl = 0;
    int boardLikeFilterControl = 0; //기본 : 0  , 추천순 : 1
    RadioButton categoryBtn0,categoryBtn1,categoryBtn2,categoryBtn3;
    int writeBoardKind = 0;
    @Override
    protected void onDestroy() {
        Log.d("BoardActivity", "onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        Intent intent = getIntent();
        myProf = (ProfDataSet) intent.getSerializableExtra("prof");

        boardListView = findViewById(R.id.boardListView);
        writeBtn = findViewById(R.id.board_writeBtn);
        searchBtn = findViewById(R.id.board_searchBtn);
        refreshBtn = findViewById(R.id.board_refreshBtn);
        myBoardBtn = findViewById(R.id.board_myBoardBtn);
        searchListView= findViewById(R.id.searchListView);
        searchEditText=findViewById(R.id.board_searchEditText);
        kindTV = findViewById(R.id.board_kindTV);kindTV.setText("종류 : 전체 : 전체");
        likeTV = findViewById(R.id.board_LikeTV);


        DataManager.getInstance().firebaseDatabase = FirebaseDatabase.getInstance();
        DataManager.getInstance().databaseReference = DataManager.getInstance().firebaseDatabase.getReference();
        boardAdapter=new BoardAdapter(getApplicationContext(),R.layout.boardlist_row, boardList); //전체게시판
        boardListView.setAdapter(boardAdapter);
        searchBoardAdapter=new BoardAdapter(getApplicationContext(),R.layout.boardlist_row, searchBoardList); //검색된게시판
        searchListView.setAdapter(searchBoardAdapter);

        searchBtn.setOnClickListener(this);
        myBoardBtn.setOnClickListener(this);
        writeBtn.setOnClickListener(this);
        refreshBtn.setOnClickListener(this);
        kindTV.setOnClickListener(this);
        likeTV.setOnClickListener(this);

        Log.d("BoardActivity", "onCreate");

        mainToolbar = (Toolbar) findViewById(R.id.mainToolbar);
        dlDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        setSupportActionBar(mainToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        dtToggle = new ActionBarDrawerToggle(this, dlDrawer, R.string.app_name, R.string.app_name);
        dlDrawer.addDrawerListener(dtToggle);

        final String[] mainFuncList = getResources().getStringArray(R.array.naviBoardMenu);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, mainFuncList);

        listView = (ListView) findViewById(R.id.drawer_menulist);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch(position) {
                    case 0:
                        Intent mainIntent = new Intent(getApplicationContext(), MainScreen.class);
                        mainIntent.putExtra("prof", myProf);
                        startActivity(mainIntent);
                        break;
                    case 1:
                        Intent mapIntent = new Intent(getApplicationContext(), MapActivity.class);
                        mapIntent.putExtra("prof", myProf);
                        startActivity(mapIntent);
                        break;
                    case 2:
                        Toast.makeText(getApplicationContext(), "동호회선택", Toast.LENGTH_SHORT).show();
                        break;
                }
                dlDrawer.closeDrawer(Gravity.LEFT);
            }
        });

        //board의 변화 감지
        DataManager.getInstance().databaseReference.child("board").addChildEventListener(new ChildEventListener() {
            /*
            기존 하위 항목마다 한 번씩 발생한 후 지정된 경로에 하위 항목이 새로 추가될 때마다 다시 발생합니다.
            새 하위 항목의 데이터를 포함하는 스냅샷이 이벤트 콜백에 전달됩니다.
            정렬을 위해 이전 하위 항목의 키를 포함하는 두 번째 인수도 전달됩니다.

             */
            @Override // 리스트아이템을 검색하거나 아이템의 추가가 있을 때 수신
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Log.d("BoardActivity", "onChildAdded");
                Log.d("BoardActivity", dataSnapshot.toString());
                BoardDTO boardData = dataSnapshot.getValue(BoardDTO.class);//
                boardData.setKey( dataSnapshot.getKey());


                boardList.add(boardData);


                Collections.sort(boardList,DataManager.getInstance().descendingBoard);
                boardAdapter.notifyDataSetChanged(); //adapter 갱신



            }

            @Override // 아이템의 변화가 있을 때 수신
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d("BoardActivity", "onChildChanged");
                Log.d("BoardActivity", dataSnapshot.toString());
                BoardDTO boardData = dataSnapshot.getValue(BoardDTO.class);//

                for(BoardDTO board : boardList)
                {
                    if(board.getKey().equals( dataSnapshot.getKey())) //key가 같은지?
                    {
                        board.setLikeNum(boardData.getLikeNum());
                        break;
                    }
                }

                Collections.sort(boardList,DataManager.getInstance().descendingBoard);
                boardAdapter.notifyDataSetChanged(); //adapter 갱신
            }

            @Override // 아이템이 삭제되었을때 수신
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d("BoardActivity", "onChildRemoved");
                Log.d("BoardActivity", dataSnapshot.toString());
                BoardDTO boardData = dataSnapshot.getValue(BoardDTO.class);//
                boardData.setKey( dataSnapshot.getKey());
                //Log.d("BoardActivity", ""+boardData.getKey()+boardData.getWriter()+boardData.getDate()+boardData.getTitle());
                for(BoardDTO board : boardList)
                {
                    if(board.getKey().equals(boardData.getKey())) //key가 같은지?
                    {
                        boardList.remove(board);
                        break;
                    }
                }

                for(BoardDTO board : searchBoardList)
                {
                    if(board.getKey().equals(boardData.getKey())) //key가 같은지?
                    {
                        searchBoardList.remove(board);
                        break;
                    }
                }

                searchBoardAdapter.notifyDataSetChanged(); //adapter 갱신

                boardKindRefreshFilter();
            }

            @Override //순서 변경시 수신
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.d("BoardActivity", "onChildMoved");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("BoardActivity", "onCancelled");

            }
        });

        //board 클릭시 item띄우기
        boardListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BoardDTO curItem= (BoardDTO)boardAdapter.getItem(position);

                Intent intent= new Intent(getApplicationContext(), BoardDetailActivity.class);
                intent.putExtra("board",curItem);
                startActivity(intent);



            }
        });
        //searchBoard 클릭시 item띄우기
        searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BoardDTO curItem= (BoardDTO)boardAdapter.getItem(position);


                Intent intent= new Intent(getApplicationContext(), BoardDetailActivity.class);
                intent.putExtra("board",curItem);
                startActivity(intent);


            }
        });


    }

    public void boardKindFilter(boolean searchState)
    {
        if(searchState) //search한 상태의 listview
        {
            if(searchBoardKindFilterControl==4)
            {
                kindTV = findViewById(R.id.board_kindTV);kindTV.setText("종류 : 검색 : 전체");
                searchBoardAdapter.getFilter(1).filter("");
                searchBoardKindFilterControl=0;
            }
            else
            {   kindTV.setText("종류 : 검색 : "+MyUtil.getBoardKind(searchBoardKindFilterControl));
                searchBoardAdapter.getFilter(1).filter(searchBoardKindFilterControl+"");
                searchBoardKindFilterControl++;
            }




        }
        else //기본 전체 listview
        {
            if(boardKindFilterControl==4)
            {
                kindTV = findViewById(R.id.board_kindTV);kindTV.setText("종류 : 전체 : 전체");
                boardAdapter.getFilter(1).filter("");
                boardKindFilterControl=0;
            }
            else
            {
                kindTV.setText("종류 : 전체 : "+MyUtil.getBoardKind(boardKindFilterControl));
                boardAdapter.getFilter(1).filter(boardKindFilterControl+"");
                boardKindFilterControl++;
            }



        }
    }

    //삭제, 글쓰기, 되었을때 list를 다시 refresh해줘야한다. 이경우 notify로는 안됨. list자체를 다시 세팅해줘야함.
    public void boardKindRefreshFilter()
    {
        if(searchState) //search한 상태의 listview
        {
            if(searchBoardKindFilterControl==0)
            {

                searchBoardAdapter.getFilter(1).filter("");
            }
            else
            {
                searchBoardAdapter.getFilter(1).filter((searchBoardKindFilterControl-1)+"");

            }




        }
        else //기본 전체 listview
        {
            if(boardKindFilterControl==0)
            {
                boardAdapter.getFilter(1).filter("");

            }
            else
            {

                boardAdapter.getFilter(1).filter((boardKindFilterControl-1)+"");

            }



        }
    }

    @Override
    public void onClick(View v) {

        switch(v.getId())
        {
            //검색
            case R.id.board_searchBtn :
                boardListView.setVisibility(View.GONE);
                searchListView.setVisibility(View.VISIBLE);
                String text= searchEditText.getText().toString();

                searchBoardList.clear();
                for(BoardDTO board : boardList)
                {
                    if(board.getTitle().contains(text) || board.getBody().contains(text)) //제목과 내용에 포함되어있는지?
                    {
                        searchBoardList.add(board);
                    }
                }

                searchBoardAdapter.notifyDataSetChanged(); //adapter 갱신
                searchState= true;

                kindTV = findViewById(R.id.board_kindTV);kindTV.setText("종류 : 검색 : 전체");
                searchBoardAdapter.getFilter(1).filter("");
                searchBoardKindFilterControl=0;


                InputMethodManager mInputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

                mInputMethodManager.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
                break;
            //내가쓴글
            case R.id.board_myBoardBtn :
                boardListView.setVisibility(View.GONE);
                searchListView.setVisibility(View.VISIBLE);

                searchBoardList.clear();
                for(BoardDTO board : boardList)
                {
                    if(board.getWriter().contains(DataManager.getInstance().userName)) //userName과 일치하는 board
                    {
                        searchBoardList.add(board);
                    }
                }
                searchBoardAdapter.notifyDataSetChanged(); //adapter 갱신
                searchState= true;

                kindTV = findViewById(R.id.board_kindTV);kindTV.setText("종류 : 검색 : 전체");
                searchBoardAdapter.getFilter(1).filter("");
                searchBoardKindFilterControl=0;


                break;
            //작성
            case R.id.board_writeBtn :
                //dialog 화면 view 객체
                writeBoardDialogView =View.inflate(BoardActivity.this,R.layout.writeboarddialog,null);

                final EditText titleET= writeBoardDialogView.findViewById(R.id.titleET);


                categoryBtn0=(RadioButton)writeBoardDialogView.findViewById(R.id.categoryBtn0); categoryBtn0.setOnCheckedChangeListener(this);
                categoryBtn1=(RadioButton)writeBoardDialogView.findViewById(R.id.categoryBtn1); categoryBtn1.setOnCheckedChangeListener(this);
                categoryBtn2=(RadioButton)writeBoardDialogView.findViewById(R.id.categoryBtn2); categoryBtn2.setOnCheckedChangeListener(this);
                categoryBtn3=(RadioButton)writeBoardDialogView.findViewById(R.id.categoryBtn3); categoryBtn3.setOnCheckedChangeListener(this);
                final  EditText bodyET= writeBoardDialogView.findViewById(R.id.bodyET);



                //AlertDialog 생성
                AlertDialog.Builder builder=new AlertDialog.Builder(BoardActivity.this);

                //옵션 설정
                builder.setTitle("게시글 작성");
                //builder.setIcon(android.R.drawable.ic_menu_save);
                builder.setView(writeBoardDialogView); //dialogview를 builder에 붙임

                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        //여기서 push함
                        BoardDTO boardExample = new BoardDTO(titleET.getText().toString(),DataManager.getInstance().userName, writeBoardKind,
                                bodyET.getText().toString(), MyUtil.getDate(), 0);
                        DataManager.getInstance().databaseReference.child("board").push().setValue(boardExample); //board에 data push
                        boardAdapter.notifyDataSetChanged(); //adapter 갱신

                        boardKindRefreshFilter();
                    }
                });
                builder.setNegativeButton("취소",null);

                //보여주기
                builder.show();
                break;

            //새로고침
            case R.id.board_refreshBtn :
                boardListView.setVisibility(View.VISIBLE);
                searchListView.setVisibility(View.GONE);
                searchState= false;
                kindTV = findViewById(R.id.board_kindTV);kindTV.setText("종류 : 전체 : 전체");
                boardAdapter.getFilter(1).filter("");
                boardKindFilterControl=0;
                break;


            //종류 별 필터링
            case R.id.board_kindTV :
                boardKindFilter(searchState);
                break;


            //추천수 필터링
            case R.id.board_LikeTV :
                if(searchState) //search한 상태의 listview
                {
                    if(boardLikeFilterControl ==0) //기본상태 -> 추천순으로
                    {
                        searchBoardAdapter.getFilter(0).filter("true");
                        boardLikeFilterControl =1;
                    }
                    else
                    {
                        searchBoardAdapter.getFilter(0).filter("false");
                        boardLikeFilterControl =0;
                    }

                }
                else //기본 전체 listview
                {
                    if(boardLikeFilterControl ==0) //기본상태 -> 추천순으로
                    {
                        boardAdapter.getFilter(0).filter("true");
                        boardLikeFilterControl =1;
                    }
                    else
                    {
                        boardAdapter.getFilter(0).filter("false");
                        boardLikeFilterControl =0;
                    }
                }

                break;
        }




    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked==true)
        {
            switch (buttonView.getId())
            {
                case R.id.categoryBtn0:
                    writeBoardKind=0;
                    categoryBtn0.setChecked(true);
                    categoryBtn1.setChecked(false);
                    categoryBtn2.setChecked(false);
                    categoryBtn3.setChecked(false);
                    break;
                case R.id.categoryBtn1:
                    writeBoardKind=1;
                    categoryBtn1.setChecked(true);
                    categoryBtn0.setChecked(false);
                    categoryBtn2.setChecked(false);
                    categoryBtn3.setChecked(false);
                    break;
                case R.id.categoryBtn2:
                    writeBoardKind=2;
                    categoryBtn2.setChecked(true);
                    categoryBtn1.setChecked(false);
                    categoryBtn0.setChecked(false);
                    categoryBtn3.setChecked(false);
                    break;
                case R.id.categoryBtn3:
                    writeBoardKind=3;
                    categoryBtn3.setChecked(true);
                    categoryBtn1.setChecked(false);
                    categoryBtn2.setChecked(false);
                    categoryBtn0.setChecked(false);
                    break;
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_func_menu, menu);
        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        dtToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        dtToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (dtToggle.onOptionsItemSelected(item)) {
            return true;
        }
//        switch (item.getItemId()) {
//            case R.id.mapsFunc:
//                Toast.makeText(getApplicationContext(), "동호회선택", Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.clubFunc:
//                Toast.makeText(getApplicationContext(), "동호회선택", Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.commuFunc:
//                Toast.makeText(getApplicationContext(), "커뮤니티선택", Toast.LENGTH_SHORT).show();
//                break;
//        }
        return super.onOptionsItemSelected(item);
    }
}