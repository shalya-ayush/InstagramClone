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

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private Context mContext;
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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        User users = mUser.get(position);
        holder.followBtn.setVisibility(View.VISIBLE);
        holder.userName.setText(users.getUsername());
        holder.fullName.setText(users.getFullname());
        isFollowed(users.getId(), holder.followBtn);
        if (users.getId().equals(firebaseUser.getUid())) {
            holder.followBtn.setVisibility(View.GONE);
        }

    }

    private void isFollowed(final String id, final Button followBtn) {
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("Following");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(id).exists()) {
                    followBtn.setText("Following");
                } else {
                    followBtn.setText("Follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mUser.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage;
        TextView userName;
        TextView fullName;
        Button followBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.user_image);
            userName = itemView.findViewById(R.id.user_name);
            fullName = itemView.findViewById(R.id.user_fullName);
            followBtn = itemView.findViewById(R.id.btn_folow);

        }
    }
}
