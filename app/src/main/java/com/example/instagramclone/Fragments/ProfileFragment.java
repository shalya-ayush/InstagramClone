package com.example.instagramclone.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagramclone.Adapter.PhotoAdapter;
import com.example.instagramclone.MainActivity;
import com.example.instagramclone.Model.Post;
import com.example.instagramclone.Model.User;
import com.example.instagramclone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {
    CircleImageView profileImage;
    TextView userName;
    TextView userFullName;
    ImageView options;
    TextView posts;
    TextView followers;
    TextView following;
    Button editProfile, logout;
    ImageButton userPosts;
    ImageButton savedPosts;
    FirebaseUser firebaseUser;
    String profileId;

    // For RecyclerView
    private RecyclerView recyclerView;
    private PhotoAdapter photoAdapter;
    private List<Post> myPosts;

    private RecyclerView savedPostRecyclerView;
    private PhotoAdapter postAdapter;
    private List<Post> mySavedPosts;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        //hooks
        profileImage = view.findViewById(R.id.profile_image);
        userName = view.findViewById(R.id.user_name);
        userFullName = view.findViewById(R.id.user_full_name);
        options = view.findViewById(R.id.hamburger_menu);
        posts = view.findViewById(R.id.no_of_posts);
        logout = view.findViewById(R.id.logout);
        followers = view.findViewById(R.id.no_of_followers);
        following = view.findViewById(R.id.no_of_following);
        editProfile = view.findViewById(R.id.edit_profile);
        userPosts = view.findViewById(R.id.my_posts);
        savedPosts = view.findViewById(R.id.saved_posts);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        recyclerView = view.findViewById(R.id.posts_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        myPosts = new ArrayList<>();
        photoAdapter = new PhotoAdapter(getContext(), myPosts);
        recyclerView.setAdapter(photoAdapter);

        savedPostRecyclerView = view.findViewById(R.id.saved_posts_recycler_view);
        savedPostRecyclerView.setHasFixedSize(true);
        savedPostRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mySavedPosts = new ArrayList<>();
        postAdapter = new PhotoAdapter(getContext(), mySavedPosts);
        savedPostRecyclerView.setAdapter(postAdapter);
        profileId = firebaseUser.getUid();
        String data = getContext().getSharedPreferences("PROFILE", Context.MODE_PRIVATE).getString("profileId", "none");
        if (data.equals("none")) {
            profileId = firebaseUser.getUid();
            logout.setVisibility(View.VISIBLE);
        } else {
            profileId = data;
            logout.setVisibility(View.GONE);
            checkFollowingStatus();
        }
        userInfo();
        getFollowersAndFollowingCount();
        getPostsCount();
        getMyPosts();
        getSavedPosts();
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btnText = editProfile.getText().toString();
                if (btnText.equals("Edit Profile")) {
                    // Go to Edit Profile;
                } else {
                    if (btnText.equals("Follow")) {
                        FirebaseDatabase.getInstance().getReference().child("Follow").
                                child(firebaseUser.getUid()).child("Following").child(profileId).setValue(true);
                        FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId).
                                child("Following").child(firebaseUser.getUid()).setValue(true);

                    } else {
                        FirebaseDatabase.getInstance().getReference().child("Follow").
                                child(firebaseUser.getUid()).child("Following").child(profileId).removeValue();
                        FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId).
                                child("Following").child(firebaseUser.getUid()).removeValue();

                    }
                }
            }
        });
        userPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savedPostRecyclerView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        });
        savedPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savedPostRecyclerView.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(getContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setClass(getActivity(), MainActivity.class);
                getActivity().startActivity(intent);
            }
        });
        return view;
    }

    private void getSavedPosts() {
        final List<String> savedIds = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    savedIds.add(dataSnapshot.getKey());

                }
                FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot1) {
                        mySavedPosts.clear();
                        for (DataSnapshot dataSnapshot1 : snapshot1.getChildren()) {
                            Post post = dataSnapshot1.getValue(Post.class);
                            for (String ids : savedIds) {
                                if (post.getPostId().equals(ids)) {
                                    mySavedPosts.add(post);
                                }
                            }
                        }
                        postAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMyPosts() {
        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myPosts.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    if (post.getAuthor().equals(profileId)) {
                        myPosts.add(post);
                    }
                }
                Collections.reverse(myPosts);
                photoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkFollowingStatus() {
        FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("Following").
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child(profileId).exists()) {
                            editProfile.setText("Following");
                        } else {
                            editProfile.setText("Follow");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void getPostsCount() {
        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int counter = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    if (post.getAuthor().equals(profileId)) {
                        counter++;
                    }
                }
                posts.setText(String.valueOf(counter));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFollowersAndFollowingCount() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId);
        ref.child("Followers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followers.setText("" + snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ref.child("Following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                following.setText("" + snapshot.getChildrenCount());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void userInfo() {
        FirebaseDatabase.getInstance().getReference().child("Users").child(profileId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Picasso.get().load(user.getImageUrl()).into(profileImage);
                userName.setText(user.getUsername());
                userFullName.setText(user.getFullName());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}