package com.example.instagramclone.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagramclone.Fragments.PostDetailFragment;
import com.example.instagramclone.Fragments.ProfileFragment;
import com.example.instagramclone.Model.Notifications;
import com.example.instagramclone.Model.Post;
import com.example.instagramclone.Model.User;
import com.example.instagramclone.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private Context mContext;
    private List<Notifications> mNotifications;

    public NotificationAdapter(Context mContext, List<Notifications> mNotifications) {
        this.mContext = mContext;
        this.mNotifications = mNotifications;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.notifications_items, parent, false);
        return new NotificationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Notifications notifications = mNotifications.get(position);
        holder.notificationText.setText(notifications.getText());
        getUser(holder.profileImage, holder.profileName, notifications.getUserId());
        if (notifications.isPost()) {
            holder.postImage.setVisibility(View.VISIBLE);
            getPostImage(holder.postImage, notifications.getPostId());
        } else {
            holder.postImage.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notifications.isPost()) {
                    mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit().putString("postId", notifications.getPostId()).apply();
                    ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PostDetailFragment()).commit();
                } else {
                    mContext.getSharedPreferences("PROFILE", Context.MODE_PRIVATE).edit().putString("profileId", notifications.getUserId()).apply();
                    ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return mNotifications.size();
    }

    private void getUser(final ImageView profileImage, final TextView profileName, String userId) {
        FirebaseDatabase.getInstance().getReference().child("Users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user.getImageUrl().equals("default")) {
                    profileImage.setImageResource(R.drawable.ic_account);
                } else {
                    Picasso.get().load(user.getImageUrl()).into(profileImage);
                }
                profileName.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getPostImage(final ImageView postImage, String postId) {
        FirebaseDatabase.getInstance().getReference().child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Post post = snapshot.getValue(Post.class);
                Picasso.get().load(post.getImageUrl()).placeholder(R.drawable.ic_launcher_foreground).into(postImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView profileImage;
        public TextView profileName;
        public TextView notificationText;
        public ImageView postImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.notification_image);
            profileName = itemView.findViewById(R.id.notification_name);
            notificationText = itemView.findViewById(R.id.notification_text);
            postImage = itemView.findViewById(R.id.notification_post_image);

        }
    }
}
