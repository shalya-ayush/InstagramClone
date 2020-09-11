package com.example.instagramclone.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagramclone.CommentActivity;
import com.example.instagramclone.Fragments.PostDetailFragment;
import com.example.instagramclone.Fragments.ProfileFragment;
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

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private Context mContext;
    private List<Post> mPost;
    private FirebaseUser firebaseUser;

    public PostAdapter(Context mContext, List<Post> mPost) {
        this.mContext = mContext;
        this.mPost = mPost;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_posts, parent, false);
        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Post post = mPost.get(position);
        Picasso.get().load(post.getImageUrl()).into(holder.postImage);
        holder.caption.setText(post.getDescription());
        FirebaseDatabase.getInstance().getReference().child("Users").child(post.getAuthor()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user.getImageUrl().equals("default")) {
                    holder.profileImage.setImageResource(R.drawable.ic_color_account);
                } else {
                    Picasso.get().load(user.getImageUrl()).into(holder.profileImage);

                }
                holder.profileName.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        isLiked(post.getPostId(), holder.like);
        noOfLikes(post.getPostId(), holder.numberOfLikes);
        isSaved(post.getPostId(), holder.save);
// To add the like feature
        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If  image is not already liked then on click it will be liked
                if (holder.like.getTag().equals("like")) {
                    FirebaseDatabase.getInstance().getReference().child("Likes").
                            child(post.getPostId()).child(firebaseUser.getUid()).setValue(true);
                    addNotifications(post.getPostId(), post.getAuthor());

                }
                // If image is already liked then it will be removed from liked
                else {
                    FirebaseDatabase.getInstance().getReference().child("Likes").
                            child(post.getPostId()).child(firebaseUser.getUid()).removeValue();

                }
            }
        });

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CommentActivity.class);
                intent.putExtra("postId", post.getPostId());
                intent.putExtra("authorId", post.getAuthor());
                mContext.startActivity(intent);

            }
        });
        // on click listener on save button   (this functionality is not working)
        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.save.getTag().equals("save")) {
                    FirebaseDatabase.getInstance().getReference().child("Saves").
                            child(post.getPostId()).child(firebaseUser.getUid()).setValue(true);
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Saves").
                            child(post.getPostId()).child(firebaseUser.getUid()).removeValue();
                }
            }
        });
        // method to show profile when a user click on anybody's profile
        holder.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.getSharedPreferences("PROFILE", Context.MODE_PRIVATE).edit().putString("profileId", post.getAuthor()).apply();
                ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
            }
        });
        // method to show post when a user click on post
        holder.postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit().putString("postId", post.getPostId()).apply();
                ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PostDetailFragment()).commit();
            }
        });


    }

    // Method to store notifications into the database
    private void addNotifications(String postId, String author) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("userId", author);
        map.put("text", " you liked the post");
        map.put("postId", postId);
        map.put("isPost", true);
        FirebaseDatabase.getInstance().getReference().child("Notifications").child(firebaseUser.getUid()).push().setValue(map);
    }

    //Method to check image has been saved or not
    private void isSaved(final String postId, final ImageView save) {
        FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(postId).exists()) {
                    save.setImageResource(R.drawable.ic_bookmark);
                    save.setTag("saved");

                } else {
                    save.setImageResource(R.drawable.ic_bookmark);
                    save.setTag("save");
                }
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

    //Method to check image is liked or not
    private void isLiked(String postId, final ImageView imageView) {
        FirebaseDatabase.getInstance().getReference().child("Likes").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(firebaseUser.getUid()).exists()) {

                    imageView.setImageResource(R.drawable.ic_favorite);
                    imageView.setTag("liked");
                } else {
                    imageView.setImageResource(R.drawable.ic_love);
                    imageView.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView profileImage;
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

    // Method to count no of likes
    private void noOfLikes(String postId, final TextView textView) {
        FirebaseDatabase.getInstance().getReference().child("Likes").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                textView.setText(snapshot.getChildrenCount() + " likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
