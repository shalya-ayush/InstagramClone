package com.example.instagramclone.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagramclone.Model.Post;
import com.example.instagramclone.Model.User;
import com.example.instagramclone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private Context mContext;
    private List<Post> mPost;
    private FirebaseUser firebaseUser;

    public PostAdapter(Context mContext, List<Post> mPost) {
        this.mContext = mContext;
        this.mPost = mPost;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_posts, parent, false);
        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Post post = mPost.get(position);
        Picasso.get().load(post.getImageUrl()).into(holder.postImage);
        holder.caption.setText(post.getDescription());
        FirebaseDatabase.getInstance().getReference().child("Users").child(post.getAuthor()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user.getImageUrl().equals("default")) {
                    holder.profileImage.setImageResource(R.drawable.ic_person);
                } else {
                    Picasso.get().load(user.getImageUrl()).into(holder.profileImage);

                }
                holder.profileName.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return mPost.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView profileImage;
        public ImageView postImage;
        public ImageView like;
        public ImageView comment;
        public ImageView share;
        public ImageView save;
        public ImageView more;

        public TextView profileName;
        public TextView numberOfLikes;
        public TextView caption;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.home_profileImage);
            postImage = itemView.findViewById(R.id.home_postImage);
            like = itemView.findViewById(R.id.home_like_sign);
            comment = itemView.findViewById(R.id.home_comments_sign);
            share = itemView.findViewById(R.id.home_share_sign);
            save = itemView.findViewById(R.id.home_save_sign);
            more = itemView.findViewById(R.id.home_moreSign);

            profileName = itemView.findViewById(R.id.home_fullName);
            numberOfLikes = itemView.findViewById(R.id.home_number_of_likes);
            caption = itemView.findViewById(R.id.home_caption);

        }
    }
}
