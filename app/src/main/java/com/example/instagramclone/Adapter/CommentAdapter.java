package com.example.instagramclone.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagramclone.HomeActivity;
import com.example.instagramclone.Model.Comment;
import com.example.instagramclone.Model.User;
import com.example.instagramclone.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private Context mContext;
    private List<Comment> mComment;
    String postId;

    private FirebaseUser firebaseUser;

    public CommentAdapter(Context mContext, List<Comment> mComment, String postId) {
        this.mContext = mContext;
        this.mComment = mComment;
        this.postId = postId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.comments_items, parent, false);
        return new CommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final Comment comment = mComment.get(position);
        holder.commentText.setText(comment.getComment());
        FirebaseDatabase.getInstance().getReference().child("Users").child(comment.getAuthor()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                holder.profileName.setText(user.getUsername());
                if (user.getImageUrl().equals("default")) {
                    holder.profileImage.setImageResource(R.drawable.ic_color_account);
                } else {
                    Picasso.get().load(user.getImageUrl()).into(holder.profileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.commentText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, HomeActivity.class);
                intent.putExtra("authorId", comment.getAuthor());
                mContext.startActivity(intent);
            }
        });
        holder.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, HomeActivity.class);
                intent.putExtra("authorId", comment.getAuthor());
                mContext.startActivity(intent);

            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (comment.getAuthor().endsWith(firebaseUser.getUid())) {
                    final AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                    alertDialog.setTitle("Are you sure to delete this comment?");
                    alertDialog.setButton(alertDialog.BUTTON_NEUTRAL, "NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.setButton(alertDialog.BUTTON_POSITIVE, "YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, int which) {
                            FirebaseDatabase.getInstance().getReference().child("Comments").child(postId).child(comment.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(mContext, "Comment has been deleted", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                }
                            });
                        }
                    });
                    alertDialog.show();

                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mComment.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView profileImage;
        public TextView profileName;
        public TextView commentText;
        public ImageView like;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profileImage);
            profileName = itemView.findViewById(R.id.profileName);
            commentText = itemView.findViewById(R.id.comment_text);
            like = itemView.findViewById(R.id.like_comment);
        }
    }
}
