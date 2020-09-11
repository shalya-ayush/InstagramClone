package com.example.instagramclone.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.HashMap;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private Context mContext;
    // Model class User that we have created
    private List<User> mUser;
    private boolean isFragment;
    private FirebaseUser firebaseUser;

    public UserAdapter(Context mContext, List<User> mUser, boolean isFragment) {
        this.mContext = mContext;
        this.mUser = mUser;
        this.isFragment = isFragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_items, parent, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final User user = mUser.get(position);
        holder.followButton.setVisibility(View.VISIBLE);
        holder.username.setText(user.getUsername());
        holder.userFullName.setText(user.getFullName());
        Picasso.get().load(user.getImageUrl()).placeholder(R.mipmap.ic_launcher).into(holder.userImage);
        isFollowed(user.getId(), holder.followButton);
        if (user.getId().equals(firebaseUser.getUid())) {
            holder.followButton.setVisibility(View.GONE);
        }
        holder.followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.followButton.getText().toString().equals("Follow")) {
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("Following").child(user.getId()).setValue(true);
                    addNotifications(user.getId());
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getId()).
                            child("Followers").child(firebaseUser.getUid()).setValue(true);


                } else {
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("Following").child(user.getId()).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getId()).
                            child("Followers").child(firebaseUser.getUid()).removeValue();


                }
            }
        });


    }

    private void isFollowed(final String id, final Button followButton) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                .child("Following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(id).exists()) {
                    followButton.setText("Following");
                } else {
                    followButton.setText("Follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addNotifications(String userId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("text", "started following you.");
        map.put("postId", "");
        map.put("isPost", false);
        FirebaseDatabase.getInstance().getReference().child("Notifications").child(firebaseUser.getUid()).push().setValue(map);
    }

    @Override
    public int getItemCount() {
        return mUser.size();     //it will return the  number of the user in the database;
    }

    // View Holder Class to link xml file with java class
    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView userImage;
        public TextView username;
        public TextView userFullName;
        public Button followButton;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.user_profile_image);
            username = itemView.findViewById(R.id.user_name);
            userFullName = itemView.findViewById(R.id.user_fullName);
            followButton = itemView.findViewById(R.id.btn_folow);
        }
    }

}
