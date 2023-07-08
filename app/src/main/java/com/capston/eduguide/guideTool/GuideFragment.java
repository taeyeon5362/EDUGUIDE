package com.capston.eduguide.guideTool;


import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.capston.eduguide.MainActivity;
import com.capston.eduguide.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

/*
 * 가이드 띄우기, 가이드 관리, 가이드 - 파이어베이스 연동 등을 처리함
 *
 * 필드 : 가이드 박스 개수, 가이드 구성 인터페이스 저장 구조, 파이어베이스 인스턴스
 * 생성 시 : 인터페이스 저장 구조 초기화, 이벤트 리스너 부착, 고정 모드 활성화 여부 결정
 *
 * 메소드 :
 * 터치 이벤트 - 키워드 작성,
 * 길게 누르기 이벤트 - 설명글 작성,
 * 고정 모드 - addButton 비활성화,
 * 가이드 데이터베이스 저장
 * 가이드 데이터베이스 조회
 * */

public class GuideFragment extends Fragment {


    //가이드 박스 개수
    static int guideMaxNum = 18;
    static int guideMinNum = 9;

    public boolean isDestroyed = false;
    static String postId; //게시글 아이디

    private View view;
    private Vector<Button> guideBoxViews = new Vector<>(guideMaxNum); // 가이드 박스
    private Vector<Button> guideLineViews = new Vector<>(guideMaxNum-1); // 라인
    private Vector<ImageButton> guideAddButtons = new Vector<>(guideMaxNum-guideMinNum); // 추가 버튼
    private HashMap<Integer, String> boxInfos = new HashMap<>(guideMaxNum); // 가이드 박스 설명글

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(); // 파이어베이스 DB
    DatabaseReference guideDatabaseReference = firebaseDatabase.getReference("guide"); // 가이드 DB

    public static GuideFragment newInstance(int param1){
        GuideFragment fg = new GuideFragment();
        Bundle args = new Bundle();
        args.putInt("param1", param1);
        fg.setArguments(args);
        return fg; }

    /*
    public static GuideFragment newInstance(int param1, String postId) {
        GuideFragment fg = new GuideFragment();
        Bundle args = new Bundle();
        args.putInt("param1", param1);
        args.putString("postId", postId);
        fg.setArguments(args);
        return fg; }*/



        //생성자
    public GuideFragment(){
        if(!MainActivity.getCurrentMenu().equals("posting"))  {
            Toast.makeText(getActivity(), "ERROR 잘못된 접근, 생성 시 postId 필요", Toast.LENGTH_SHORT).show();
            Log.d("GuideFragment", "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! GuideFragment 생성 오류!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! 게시물 작성 모드가 아닐 경우 생성 시 postID 값을 넘여줘야만 박스를 개수만큼 생성할 수 있음!!!!!!!!!!!!!!!!!");
            //this.postId = error; //잘못된 접근 시 임시로 넣을 게시물 아이디 값 필요
        }
    } //postID 없이 생성할 경우, 가이드 박스를 가이드 박스 데이터 개수 만큼 배치할 수 없음
    public GuideFragment(String feedId) {
        this.postId = feedId; //게시글 아이디
        //setGuideData(postId);
    }

    public void onStart(){
        super.onStart();
        if(isDestroyed){
            //setGuideData(postId);
            isDestroyed = false;
        }
    }

    //뷰가 destroy될 경우 변수를 true로 변경(체크용)
    public void onDestroyView() {
        super.onDestroyView();
        isDestroyed = true;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = null;  // onCreateView() 호출 전 초기화

        if (getArguments() != null) {
            int param1 = getArguments().getInt("param1");
            String postId = getArguments().getString("postId");
            if (postId != null) {
                this.postId = postId;
                //setGuideData(postId);
            } else {
                Toast.makeText(getActivity(), "ERROR 잘못된 접근, 생성 시 postId 필요", Toast.LENGTH_SHORT).show();
                Log.d("GuideFragment", "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! GuideFragment 생성 오류!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! 게시물 작성 모드가 아닐 경우 생성 시 postID 값을 넘여줘야만 박스를 개수만큼 생성할 수 있음!!!!!!!!!!!!!!!!!");
            }
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if(view == null) { view = inflater.inflate(R.layout.guide_guidetool, container, false); initGuide(); } // view 초기화

        //isDestroyed 발생 시 데이터 재설정
        if(isDestroyed){
            //setGuideData(postId);
        }

        //메뉴 검사
        if(MainActivity.getCurrentMenu().equals("posting"))  { setPostingMode(); } //게시글 작성 모드 호출
        else {Log.d("Guide", "보기 모드 호출 전"); setViewMode(); } //보기 모드 호출

        return view;
    }

    // 가이드 인터페이스 초기화 메소드
    private void initGuide() {

        //가이드박스 벡터에 저장
        guideBoxViews.add((Button) view.findViewById(R.id.guideBox1));
        guideBoxViews.add((Button) view.findViewById(R.id.guideBox2));
        guideBoxViews.add((Button) view.findViewById(R.id.guideBox3));
        guideBoxViews.add((Button) view.findViewById(R.id.guideBox4));
        guideBoxViews.add((Button) view.findViewById(R.id.guideBox5));
        guideBoxViews.add((Button) view.findViewById(R.id.guideBox6));
        guideBoxViews.add((Button) view.findViewById(R.id.guideBox7));
        guideBoxViews.add((Button) view.findViewById(R.id.guideBox8));
        guideBoxViews.add((Button) view.findViewById(R.id.guideBox9));
        guideBoxViews.add((Button) view.findViewById(R.id.guideBox10)); // 추가, index9
        guideBoxViews.add((Button) view.findViewById(R.id.guideBox11));
        guideBoxViews.add((Button) view.findViewById(R.id.guideBox12));
        guideBoxViews.add((Button) view.findViewById(R.id.guideBox13));
        guideBoxViews.add((Button) view.findViewById(R.id.guideBox14));
        guideBoxViews.add((Button) view.findViewById(R.id.guideBox15));
        guideBoxViews.add((Button) view.findViewById(R.id.guideBox16));
        guideBoxViews.add((Button) view.findViewById(R.id.guideBox17));
        guideBoxViews.add((Button) view.findViewById(R.id.guideBox18));

        //라인 벡터에 저장
        guideLineViews.add((Button) view.findViewById(R.id.line1));
        guideLineViews.add((Button) view.findViewById(R.id.line2));
        guideLineViews.add((Button) view.findViewById(R.id.line3));
        guideLineViews.add((Button) view.findViewById(R.id.line4));
        guideLineViews.add((Button) view.findViewById(R.id.line5));
        guideLineViews.add((Button) view.findViewById(R.id.line6));
        guideLineViews.add((Button) view.findViewById(R.id.line7));
        guideLineViews.add((Button) view.findViewById(R.id.line8));
        guideLineViews.add((Button) view.findViewById(R.id.line9)); // 추가, index8
        guideLineViews.add((Button) view.findViewById(R.id.line10));
        guideLineViews.add((Button) view.findViewById(R.id.line11));
        guideLineViews.add((Button) view.findViewById(R.id.line12));
        guideLineViews.add((Button) view.findViewById(R.id.line13));
        guideLineViews.add((Button) view.findViewById(R.id.line14));
        guideLineViews.add((Button) view.findViewById(R.id.line15));
        guideLineViews.add((Button) view.findViewById(R.id.line16));
        guideLineViews.add((Button) view.findViewById(R.id.line17));

        //추가버튼 벡터에 저장
        guideAddButtons.add((ImageButton) view.findViewById(R.id.addButton0));
        guideAddButtons.add((ImageButton) view.findViewById(R.id.addButton1));
        guideAddButtons.add((ImageButton) view.findViewById(R.id.addButton2));
        guideAddButtons.add((ImageButton) view.findViewById(R.id.addButton3));
        guideAddButtons.add((ImageButton) view.findViewById(R.id.addButton4));
        guideAddButtons.add((ImageButton) view.findViewById(R.id.addButton5));
        guideAddButtons.add((ImageButton) view.findViewById(R.id.addButton6));
        guideAddButtons.add((ImageButton) view.findViewById(R.id.addButton7));
        guideAddButtons.add((ImageButton) view.findViewById(R.id.addButton8));
    }

    private void setPostingMode() {
        guideAddButtons.get(0).setVisibility(View.VISIBLE); //추가 버튼 보이게 함

        //addButton에 이벤트리스너 달기
        Iterator<ImageButton> addbuttonIt = guideAddButtons.iterator();
        while (addbuttonIt.hasNext()) {
            ImageButton addButton = addbuttonIt.next();
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickAddButton(v);
                }
            });
        }

        //guideBox에 이벤트리스너 달기
        Iterator<Button> guideboxIt = guideBoxViews.iterator();
        while (guideboxIt.hasNext()) {
            Button guideBox = guideboxIt.next();
            guideBox.setOnClickListener(new View.OnClickListener() { //짧게 터치 이벤트
                @Override
                public void onClick(View v) {
                    onClickPostingModeGuide(v);
                }
            });

            guideBox.setOnLongClickListener(new View.OnLongClickListener() { //길게 누르기 이벤트
                @Override
                public boolean onLongClick(View v) { onLongClickPostingModeGuide(v); return true; }
            });
        }
    }

    private void setViewMode() {
        guideAddButtons.get(0).setVisibility(View.GONE); //추가 버튼 보이지 않게 함
        setGuideData(postId); //데이터 설정

        //짧게 터치 이벤트리스너 달기
        Iterator<Button> guideboxIt = guideBoxViews.iterator();
        while (guideboxIt.hasNext()) {
            Button guideBox = guideboxIt.next();
            guideBox.setOnClickListener(new View.OnClickListener() { //짧게 터치 이벤트
                @Override
                public void onClick(View v) {
                    //설명글 창 띄우기
                    View guideDialogView = View.inflate(getContext(), R.layout.guide_guidebox_inform, null);
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
                    dialogBuilder.setView(guideDialogView);
                    final AlertDialog informDialog = dialogBuilder.create();

                    //팝업창 제목 수정하기
                    ((TextView) guideDialogView.findViewById(R.id.titleKeyword)).setText(">ㅁ<// 이 단계에서는 : ");

                    //팝업창 설명 부분 수정하기
                    MultiAutoCompleteTextView guideInformTextView = (MultiAutoCompleteTextView) guideDialogView.findViewById(R.id.guideInform);
                    guideInformTextView.setText(boxInfos.get(guideBoxViews.indexOf(v)));
                    guideInformTextView.setEnabled(false);

                    //팝업창 등록 버튼 보이지 않게 하기
                    guideDialogView.findViewById(R.id.editInform).setVisibility(View.INVISIBLE);

                    informDialog.show();
                }
            });
        }
    }

    //PostingMode에서 guideBox의 터치 이벤트
    public void onClickPostingModeGuide(View view) {
        //키워드 입력 창 띄우기
        View guideDialogView = View.inflate(getContext(), R.layout.guide_guidebox_keyword, null);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        dialogBuilder.setView(guideDialogView);
        final AlertDialog keywordDialog = dialogBuilder.create();
        keywordDialog.show();

        //키워드 입력 저장 설정
        guideDialogView.findViewById(R.id.editKeyword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = String.valueOf(((EditText)guideDialogView.findViewById(R.id.guideKeyword)).getText());
                if( !(keyword.trim().isEmpty() || keyword.equals("키워드")) ) ((Button) view).setText(keyword);
                keywordDialog.dismiss();
            }
        });
    }

    //PostingMode에서 guideBox의 길게 누르기 이벤트
    public void onLongClickPostingModeGuide(View view) {
        //설명글 입력창 띄우기
        View guideDialogView = View.inflate(getContext(), R.layout.guide_guidebox_inform, null);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        dialogBuilder.setView(guideDialogView);
        final AlertDialog informDialog = dialogBuilder.create();
        informDialog.show();

        //설명글 입력 저장 설정
        guideDialogView.findViewById(R.id.editInform).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String boxInfo = String.valueOf(((MultiAutoCompleteTextView)guideDialogView.findViewById(R.id.guideInform)).getText()); // 설명글
                int indexKey = guideBoxViews.indexOf((Button) view); // 가이드박스의 인덱스 번호
                boxInfos.put(indexKey, boxInfo); // 설명글 해시맵에 저장
                informDialog.dismiss();
            }
        });
    }

    //addButtion의 터치 이벤트
    public void onClickAddButton(View view) {
        int indexAdd = guideAddButtons.indexOf((ImageButton) view); //클릭한 addButton의 인덱스
        view.setVisibility(View.GONE); //클릭한 addButton 없애기
        guideBoxViews.get(guideMinNum+indexAdd).setVisibility(View.VISIBLE); //guideBox 보이게 하기
        guideLineViews.get(guideMinNum + indexAdd - 1).setVisibility(View.VISIBLE); //line 보이게 하기
        if(indexAdd == guideMaxNum-guideMinNum-1) return; //마지막 addbutton일 경우 뒷부분 생략
        guideAddButtons.get(++indexAdd).setVisibility(View.VISIBLE); //다음 addButton 보이게 하기
    }

    //파이어베이스에 가이드 데이터 저장
    public void regGuideData(String postId) {
        Log.d("GuideItem", "regGuideContent called. postId: " + postId);

        List<GuideBoxItem> guideBoxItems = new LinkedList<GuideBoxItem>() {}; //가이드 박스 리스트
        Iterator<Button> guideboxIt = guideBoxViews.iterator();
        Log.d("regGuideData", String.valueOf(guideBoxViews.isEmpty()));

        while (guideboxIt.hasNext()) {
            Log.d("regGuideData","guideboxIt 순차 검색");
            Button guideBox = guideboxIt.next();

            // 가이드박스 키워드 가져오기
            String keyword = String.valueOf(guideBox.getText());
            Log.d("regGuideData", "keyword = " + keyword);
            if (keyword.trim().isEmpty() || keyword.matches("\\d+단계")) {
                Log.d("regGuideData", "키워드 저장 조건에 맞지 않아 스킵됨");
                continue; // "n단계" 형식이 아닌 경우 저장 건너뛰기
            }

            //가이드박스 설명글 가져오기
            int indexKey = guideBoxViews.indexOf(guideBox);
            String boxInfo;
            if(boxInfos.get(indexKey)==null) //미입력 상태인 경우
                boxInfo = "내용없음";
            else
                boxInfo = boxInfos.get(indexKey);

            Log.d("regGuideData",keyword+boxInfo+"////////////////////////////////");
            guideBoxItems.add(new GuideBoxItem(keyword, boxInfo)); //리스트에 가이드박스 넣기
        }

        GuideItem guideItem = new GuideItem(postId, guideBoxItems); // 가이드 객체 생성
        Log.d("",String.valueOf(guideItem.getGuideBoxList()));
        guideDatabaseReference.child(postId).setValue(guideItem); //가이드 객체 DB에 넣기
        Log.d("Guide", "GuideItem registered successfully.");

        guideDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d("Guide", "GuideBoxes registered successfully.");
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) { }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    //파이어베이스에서 가이드 데이터 가져오기
    private void setGuideData(String postId) {
        this.postId = postId;
        guideDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange (DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) { return; };

                DataSnapshot guideSnapshot;
                int numGuideBoxes;
                try { // 데이터가 존재할 경우 처리
                    guideSnapshot = dataSnapshot.child(postId).child("guideBoxList");
                    numGuideBoxes = (int) guideSnapshot.getChildrenCount();
                } catch (NullPointerException e) { // 데이터가 없을 경우 처리
                    Log.d("setGuideData", "데이터가 존재하지 않습니다.");
                    return;
                }
                Log.d("numGuideBoxes", "numGuideBoxes = "+ numGuideBoxes + "///////// postId =" + postId);

                //가이드 박스 개수 맞추기
                    int addNum = numGuideBoxes - guideMinNum;
                    if(addNum > 0) {
                        for(int i = 1; i <= addNum; i++) {
                            Log.d("numGuideBoxes", "i = "+i);
                            Log.d("numGuideBoxes", "guideBoxViews capacity= "+guideBoxViews.capacity());
                            Log.d("numGuideBoxes", "guideBoxViews size = "+guideBoxViews.size());
                            Log.d("numGuideBoxes", "guideLineViews capacity = "+guideLineViews.capacity());
                            Log.d("numGuideBoxes", "guideLineViews size = "+guideLineViews.size());
                            guideBoxViews.get(guideMinNum+i-1).setVisibility(View.VISIBLE); //guideBox 보이게 하기 //@guideBox10부터, index9
                            guideLineViews.get(guideMinNum+i-2).setVisibility(View.VISIBLE); //line 보이게 하기 //@line9부터, index8
                            Log.d("numGuideBoxes", "for문 VISIBLE 수행완료");
                        }
                    }

                    //데이터 가져와서 띄우기
                    Iterator<Button> guideboxIt = guideBoxViews.iterator();
                    int index = 0;
                    while (guideboxIt.hasNext()) {
                        Button guideBox = guideboxIt.next();
                        guideBox.setText(guideSnapshot.child(String.valueOf(index)).child("keyword").getValue(String.class)); //박스에 키워드 표시
                        boxInfos.put(index, guideSnapshot.child(String.valueOf(index)).child("boxInfo").getValue(String.class)); //해시맵에 설명글 저장
                        index++;
                    }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 에러 처리
                Toast.makeText(getActivity(), "데이터 읽기 실패: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}

