package com.example.instagramclone;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagramclone.Adapter.CommentAdapter;
import com.example.instagramclone.Model.Comment;
import com.example.instagramclone.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentActivity extends AppCompatActivity {
    FirebaseUser firebaseUser;
    private RecyclerView recyclerView;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;
    private CircleImageView profileImage;
    private EditText addComments;
    private TextView postComments;
    private String postId;
    private String authorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        postId = intent.getStringExtra("postId");
        authorId = intent.getStringExtra("authorId");
        setContentView(R.layout.activity_comment);
        profileImage = findViewById(R.id.comment_profileImage);
        addComments = findViewById(R.id.add_comments);
        postComments = findViewById(R.id.post_comments);
        recyclerView = findViewById(R.id.comments_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(getApplicationContext(), commentList, postId);
        recyclerView.setAdapter(commentAdapter);
//        getSupportActionBar().hide();
        Toolbar toolbar = findViewById(R.id.comments_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        postComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(addComments.getText().toString())) {
                    Toast.makeText(CommentActivity.this, "Add some comment", Toast.LENGTH_SHORT).show();
                } else {
                    putComments();
                }

            }
        });
        getImage();
        getComments();
    }

    private void getComments() {
        FirebaseDatabase.getInstance().getReference().child("Comments").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Comment comment = dataSnapshot.getValue(Comment.class);
                    commentList.add(comment);

                }
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void putComments() {
        HashMap<String, Object> map = new HashMap<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Comments").child(postId);
        String id = ref.push().getKey();
        map.put("id", id);
        map.put("comment", addComments.getText().toString());
        map.put("author", firebaseUser.getUid());
        addComments.setText("");
        ref.child(postId).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(CommentActivity.this, "Comment Added", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CommentActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void getImage() {
        FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user.getImageUrl().equals("default")) {
                    profileImage.setImageResource(R.drawable.ic_person);
                } else {
                    Picasso.get().load(user.getImageUrl()).into(profileImage);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}