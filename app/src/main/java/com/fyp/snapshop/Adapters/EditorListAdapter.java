package com.fyp.snapshop.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.fyp.snapshop.Models.Model;
import com.fyp.snapshop.R;

import java.util.ArrayList;
import java.util.List;



public class EditorListAdapter extends RecyclerView.Adapter<EditorListAdapter.viewholder>  {

    List<Model> data1;
    public boolean showShimmer =true;
    int SHIMMER_ITEM= 10;
    Context context;

    ArrayList<Double> ratingoints=new ArrayList<>();
    OnClickListner listner;


    public EditorListAdapter(List<Model> data1, Context context
            ,ArrayList<Double> ratingoints
    ) {
        this.data1 = data1;
        this.context = context;
//        this.ratingpoints=ratingpoints;
        this.ratingoints=ratingoints;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.editor_list,parent,false);
        viewholder viewHolder=new viewholder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder,final int position) {



        if(showShimmer){
            holder.shimmerFrameLayout.startShimmer();

        }
        else {

            final Model model = data1.get(position);
            holder.shimmerFrameLayout.stopShimmer();
            holder.shimmerFrameLayout.setShimmer(null);
            Glide.with(context).load(model.getImage()).apply(RequestOptions.circleCropTransform()).into(holder.image);
            holder.image.setBackground(null);
            holder.t1.setText(model.getName());
            holder.t1.setBackground(null);
            holder.t2.setText(model.getEmail());
            holder.t2.setBackground(null);
            holder.ratingBar.setVisibility(View.VISIBLE);
            if(ratingoints.get(position)==0.0){
                holder.ratingBar.setVisibility(View.GONE);
            }else {
                holder.ratingBar.setRating(ratingoints.get(position).floatValue());
            }
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listner.onClick(position,model);
                }
            });


        }


    }

    @Override
    public int getItemCount() {
        return showShimmer?SHIMMER_ITEM:data1.size();
    }

    public class viewholder extends RecyclerView.ViewHolder{

        TextView t1,t2;
        ImageView image;
        ShimmerFrameLayout shimmerFrameLayout;
        RatingBar ratingBar;
        CardView cardView;

        public viewholder(@NonNull View itemView) {
            super(itemView);

            shimmerFrameLayout = itemView.findViewById(R.id.shimmer);
            t1=itemView.findViewById(R.id.textView);
            t2=itemView.findViewById(R.id.textView2);
            image=itemView.findViewById(R.id.imageView);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            cardView = itemView.findViewById(R.id.cardview);


        }
    }

       public interface OnClickListner{

        public void onClick(int position, Model model);
        //ModelShowTutor modelShowTutor it is model
    }


    public void setOnClickListener(OnClickListner listner){
        this.listner = listner;
    }

}