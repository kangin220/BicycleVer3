package com.bicyle.bicycle.Board;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bicyle.bicycle.Data.DataManager;
import com.bicyle.bicycle.R;
import com.bicyle.bicycle.util.MyUtil;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BoardDetailActivity extends AppCompatActivity implements View.OnClickListener {
    ListView replyListView;
    BoardDTO board;
    View writeReplyDialogView;
    ArrayList<ReplyDTO> replyList = new ArrayList<>();
    ArrayList<LikeDTO> likeList = new ArrayList<>();

    TextView kind_titleTV;
    TextView writer_date_likeTV;
    TextView bodyTV;


    Button replyWriteBtn;
    Button boardDeleteBtn;
    Button likeBtn ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_detail);

        kind_titleTV=findViewById(R.id.board_detail_boardKind_titleTV);
        writer_date_likeTV=findViewById(R.id.board_detail_writer_date_likeTV);
        bodyTV=findViewById(R.id.board_detail_bodyTV);

        replyWriteBtn=findViewById(R.id.replyWriteBtn);
        boardDeleteBtn = findViewById(R.id.board_detail_deleteBtn);
        likeBtn = findViewById(R.id.board_detail_likeBtn);



        replyListView= findViewById(R.id.replyListView);
        boardDeleteBtn.setOnClickListener(this);
        replyWriteBtn.setOnClickListener(this); //댓글입력
        likeBtn.setOnClickListener(this);

        final ReplyAdapter replyAdapter;





        board= (BoardDTO) getIntent().getSerializableExtra("board");



        kind_titleTV=findViewById(R.id.board_detail_boardKind_titleTV);
        writer_date_likeTV=findViewById(R.id.board_detail_writer_date_likeTV);;
        bodyTV=findViewById(R.id.board_detail_bodyTV);

        //내용 세팅
        kind_titleTV.setText("["+ MyUtil.getBoardKind(board.getBoardKind())+"]   "+ board.getTitle());
        writer_date_likeTV.setText(board.getWriter()+"\n"+board.getDate()+"  : 추천 "+board.getLikeNum());
        bodyTV.setText(board.getBody());

        //자신의 글일때만 삭제버튼보임
        if(board.getWriter().equals(DataManager.getInstance().userName))
            boardDeleteBtn.setVisibility(View.VISIBLE);
        else
            boardDeleteBtn.setVisibility(View.GONE);


        replyAdapter=new ReplyAdapter(BoardDetailActivity.this,R.layout.replylist_row, replyList);
        replyListView.setAdapter(replyAdapter);


        //리플클릭시삭제가능
        replyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final ReplyDTO curItem = (ReplyDTO)replyAdapter.getItem(position);

                //dialog 생성, key 비교후 삭제
                if(DataManager.getInstance().userName.equals(curItem.getWriter()))
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(BoardDetailActivity.this);
                    //옵션 설정
                    builder.setTitle("댓글삭제");
                    builder.setMessage("정말 삭제하시겠습니까?");
                    //AlertDialog 모양 설정 => 확인/취소 버튼 포함하는 AlertDialog
                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            DataManager.getInstance().databaseReference.child("board").child(board.getKey()).child("reply").child(curItem.getKey()).removeValue();
                            Toast.makeText(BoardDetailActivity.this,"삭제되었습니다.",Toast.LENGTH_LONG).show();
                        }
                    });
                    builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });

                    // AlertDialog 보이기
                    builder.show();
                }
                else
                {
                    Toast.makeText(BoardDetailActivity.this,DataManager.getInstance().userName+"은 "+board.getWriter()+"의 댓글을 삭제할 수 없습니다.",Toast.LENGTH_LONG).show();
                }
            }
        });


        //like 좋아요 list를 받아옴


        DataManager.getInstance().databaseReference.child("board").child(board.getKey()).child("like").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d("BoardDeatilActivity", "like-addChildEventListener");
                Log.d("BoardDeatilActivity", dataSnapshot.getValue().toString());
                Log.d("BoardDeatilActivity", dataSnapshot.getKey());
                //LikeDTO likeData = dataSnapshot.getValue(LikeDTO.class);//
                likeList.add(new LikeDTO(dataSnapshot.getValue().toString()));



            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d("BoardDeatilActivity", "like-onChildChanged");
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Log.d("BoardDeatilActivity", "like-onChildRemoved");
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d("BoardDeatilActivity", "like-onChildMoved");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("BoardDeatilActivity", "like-onCancelled");
            }
        });



        //board의 변화 감지
        DataManager.getInstance().databaseReference.child("board").child(board.getKey()).child("reply").addChildEventListener(new ChildEventListener() {

            @Override // 리스트아이템을 검색하거나 아이템의 추가가 있을 때 수신
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Log.d("BoardDeatilActivity", "reply-addChildEventListener");
                Log.d("BoardDeatilActivity", dataSnapshot.getValue().toString());
                Log.d("BoardDeatilActivity", dataSnapshot.getKey());
                ReplyDTO replyData = dataSnapshot.getValue(ReplyDTO.class);//
                replyData.setKey( dataSnapshot.getKey());
                replyList.add(replyData);
                Collections.sort(replyList,DataManager.getInstance().descendingReply);
                replyAdapter.notifyDataSetChanged(); //adapter 갱신
            }

            @Override // 아이템의 변화가 있을 때 수신
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d("BoardDeatilActivity", "reply-onChildChanged");
            }

            @Override // 아이템이 삭제되었을때 수신
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d("BoardDeatilActivity", "reply-onChildRemoved");
                Log.d("BoardDeatilActivity", dataSnapshot.toString());
                ReplyDTO replyData = dataSnapshot.getValue(ReplyDTO.class);//
                replyData.setKey( dataSnapshot.getKey());
                //Log.d("BoardActivity", ""+boardData.getKey()+boardData.getWriter()+boardData.getDate()+boardData.getTitle());
                for(ReplyDTO reply : replyList)
                {
                    if(reply.getKey().equals(replyData.getKey())) //key가 같은지?
                    {
                        replyList.remove(reply);
                        break;
                    }
                }

                Collections.sort(replyList,DataManager.getInstance().descendingReply);
                replyAdapter.notifyDataSetChanged(); //adapter 갱신



            }

            @Override //순서 변경시 수신
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.d("BoardDeatilActivity", "onChildMoved");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("BoardDeatilActivity", "onCancelled");

            }
        });

    }

    //리플작성
    public void writeReply()
    {
        //dialog 화면 view 객체
        writeReplyDialogView =View.inflate(BoardDetailActivity.this,R.layout.writereplydialog,null);

        final EditText writeReplyDialog_editText= writeReplyDialogView.findViewById(R.id.writeReplyDialog_editText);

        //AlertDialog 생성
        AlertDialog.Builder builder=new AlertDialog.Builder(BoardDetailActivity.this);

        //옵션 설정
        builder.setTitle("댓글 작성");
        //builder.setIcon(android.R.drawable.ic_menu_save);
        builder.setView(writeReplyDialogView); //dialogview를 builder에 붙임

        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                //여기서 push함
                ReplyDTO replyExample= new ReplyDTO(MyUtil.getDate(),writeReplyDialog_editText.getText().toString().trim(),DataManager.getInstance().userName);
                DataManager.getInstance().databaseReference.child("board").child(board.getKey()).child("reply").push().setValue(replyExample); //board에 data push


            }
        });
        builder.setNegativeButton("취소",null);

        //보여주기
        builder.show();




    }

    //게시글 삭제
    public void deleteBoard()
    {
        if(DataManager.getInstance().userName.equals(board.getWriter()))
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(BoardDetailActivity.this);
            //옵션 설정
            builder.setTitle("게시글삭제");
            builder.setMessage("정말 삭제하시겠습니까?");
            //AlertDialog 모양 설정 => 확인/취소 버튼 포함하는 AlertDialog
            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    DataManager.getInstance().databaseReference.child("board").child(board.getKey()).removeValue();
                    Toast.makeText(BoardDetailActivity.this,"삭제되었습니다.",Toast.LENGTH_LONG).show();
                    BoardDetailActivity.this.finish();
                }
            });
            builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });

            // AlertDialog 보이기
            builder.show();


        }
        else
        {
            Toast.makeText(BoardDetailActivity.this,DataManager.getInstance().userName+"은 "+board.getWriter()+"의 글을 삭제할 수 없습니다.",Toast.LENGTH_LONG).show();
        }


    }
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.board_detail_deleteBtn: //삭제버튼
                deleteBoard();
                break;
            case R.id.replyWriteBtn : //작성버튼
                writeReply();
                break;
            case R.id.board_detail_likeBtn: //추천버튼
                boolean flag = false;

                for(int i=0; i<likeList.size(); i++)
                {
                    if(likeList.get(i).getuId().equals(DataManager.getInstance().userName))
                    {
                        Toast.makeText(BoardDetailActivity.this,"이미 추천하셨습니다.",Toast.LENGTH_SHORT).show();
                        flag= true;
                    }
                }
                if(!flag)
                {
                    Toast.makeText(BoardDetailActivity.this,"추천했습니다.",Toast.LENGTH_SHORT).show();
                    LikeDTO likeData= new LikeDTO(DataManager.getInstance().userName);
                    DataManager.getInstance().databaseReference.child("board").child(board.getKey()).child("like").setValue(likeData); //board에 data push

                    Map<String, Object> taskMap = new HashMap<String, Object>();
                    taskMap.put("likeNum", (board.getLikeNum()+1));
                    DataManager.getInstance().databaseReference.child("board").child(board.getKey()).updateChildren(taskMap);
                    writer_date_likeTV.setText(board.getWriter()+"\n"+board.getDate()+"  : 추천 "+(board.getLikeNum()+1));
                }

                //update해야함.
                break;
        }


    }
}