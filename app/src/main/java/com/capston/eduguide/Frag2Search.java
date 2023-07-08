package com.capston.eduguide;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capston.eduguide.post.CommentSimple;
import com.capston.eduguide.search.SearchAdapter;
import com.capston.eduguide.search.SearchItem;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Frag2Search extends Fragment{ //implements SearchAdapter.OnItemClickListenr {

    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    private SearchView searchView;
    private SearchAdapter adapter;

    String userEmail;
    String userName;

    Integer userGrade;

    public Frag2Search() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag2_search, container, false);
        //firebase 초기화
        databaseReference = FirebaseDatabase.getInstance().getReference();

        //recyclerView 초기화
        recyclerView=view.findViewById(R.id.recycle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter=new SearchAdapter(getContext(),new SearchAdapter.OnItemClickListener(){
            public void onItemClick(SearchItem searchItem){
                Frag2Search.this.onItemClick(searchItem);
            }
        });
        recyclerView.setAdapter(adapter);

        //searchView 초기화
        searchView = view.findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String query){
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText){
                search(newText);
                return true;
            }
        });
        Bundle bundle = getArguments();
        if (userEmail == null){
            if(bundle.getString("userEmail")!= null) {
                userEmail = bundle.getString("userEmail");
                callUserName();
            }
        }

        return view;
    }
    public void onItemClick(SearchItem searchItem){

        Fragment fragment = new CommentSimple();

        Bundle bundle = new Bundle();
        bundle.putString("feedId",searchItem.getPostId());
        bundle.putString("title_text",searchItem.getTitle());
        bundle.putString("tag_text",searchItem.getTag());
        bundle.putString("main_text",searchItem.getDescription());
        bundle.putString("feedUser_name", searchItem.getUserId());
        bundle.putInt("feedUser_grade",searchItem.getGrade());
        bundle.putString("userEmail",userEmail);
        bundle.putInt("userGrade",userGrade);
        bundle.putString("userName",userName);
        fragment.setArguments(bundle);

        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.main_frame,fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    private void search(String query){
        //firebase에서 검색어와 일치하는 태그값을 찾아 검색결과를 가져옴
        if (query.isEmpty()) {
            adapter.setSearchItems(new ArrayList<>());
            return;
        }

        databaseReference.child("post")
                .orderByChild("tag")
                .addListenerForSingleValueEvent(new ValueEventListener(){
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot){
                        List<SearchItem> searchItems=new ArrayList<>();
                        for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                            //태그가 일치하는 경우
                            String tag = snapshot.child("tag").getValue(String.class);
                            if(tag!=null&& tag.contains(query.toLowerCase())){
                                String title=snapshot.child("title").getValue(String.class);
                                String postId=snapshot.getKey();
                                String description = snapshot.child("text").getValue(String.class);
                                String userId = snapshot.child("userName").getValue(String.class);
                                Integer grade=snapshot.child("grade").getValue(Integer.class);

                                SearchItem searchItem=new SearchItem(postId,title,tag,description,userId,grade);
                                searchItems.add(searchItem);
                            }
                        }

                        adapter.setSearchItems(searchItems);


                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError){
                        Log.e("Frag2Search","onCancelled",databaseError.toException());
                    }
                });
    }
    public void callUserName(){

        if(userEmail != null){
            databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        HashMap<String, String> value = (HashMap<String, String>)dataSnapshot.getValue();
                        if(userEmail.equals(value.get("email"))){
                            userName = value.get("name");
                            userGrade = Integer.parseInt(value.get("grade"));

                        }
                    }
                    if(userName == null){
                        userName = "";

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else{
            userName = "";
        }
    }
}