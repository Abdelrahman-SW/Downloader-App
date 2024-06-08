package com.yremhl.ystgdh.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.yremhl.ystgdh.Models.ScheduleDownload;
import com.yremhl.ystgdh.Utilites.Utilities;
import com.yremhl.ystgdh.databinding.ScheduleDownloadItemBinding;

import java.util.ArrayList;
import java.util.List;

public class ScheduleDownloadAdapter extends ListAdapter<ScheduleDownload, RecyclerView.ViewHolder> {

    private final Context context;
    private final List<ScheduleDownload> scheduleDownloads = new ArrayList<>();
    private final ScheduleDownloadAdapter.OnItemClicked onItemClicked ;

//        private final Filter filter = new Filter() {
//            @Override
//            protected FilterResults performFiltering(CharSequence constraint) {
//                List <MusicModel> MusicFilters = new ArrayList<>() ;
//                if (constraint == null || constraint.length()==0) {
//                    MusicFilters.clear();
//                    // Search Result Not Found // user exit the search view
//                    SearchTxt = "" ;
//                    MusicFilters.addAll(musics) ;
//                }
//                else {
//                    MusicFilters.clear();
//                    SearchTxt  = constraint.toString().toLowerCase().trim() ;
//                    for (MusicModel music : musics) {
//                        if (music.getTitle().toLowerCase().trim().contains(SearchTxt)
//                                || music.getArtist().toLowerCase().trim().contains(SearchTxt) ) {
//                            MusicFilters.add(music) ;
//                        }
//                    }
//                }
//                FilterResults filterResults = new FilterResults() ;
//                filterResults.values = MusicFilters ;
//                return filterResults ;
//            }
//
//            @Override
//            protected void publishResults(CharSequence constraint, FilterResults results) {
//                Full_data = false ;
//                Log.i("ab_do" , "size " + ((List<MusicModel>) results.values).size() );
//                submitList((List<MusicModel>) results.values);
//            }
//        };

//        @Override
//        public void submitList(List<MusicModel> values) {
//            if (values!=null) {
//                super.submitList(values);
//                if (Full_data) {
//                    musics.clear();
//                    musics.addAll(values);
//                }
//                notifyDataSetChanged();
//            }
//        }

    //private String SearchTxt = "";
    //public boolean Full_data = false;

    private static final DiffUtil.ItemCallback<ScheduleDownload> diffUtil = new DiffUtil.ItemCallback<ScheduleDownload>() {
        @Override
        public boolean areItemsTheSame(@NonNull ScheduleDownload oldItem, @NonNull ScheduleDownload newItem) {
            return oldItem.getId() == newItem.getId() ;
        }

        @Override
        public boolean areContentsTheSame(@NonNull ScheduleDownload oldItem, @NonNull ScheduleDownload newItem) {
            return  false ;
        }
    };


    public ScheduleDownloadAdapter(Context requireContext) {
        super(diffUtil);
        this.context = requireContext ;
        onItemClicked = (ScheduleDownloadAdapter.OnItemClicked) requireContext;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ScheduleDownloadItemBinding binding = ScheduleDownloadItemBinding.inflate(LayoutInflater.from(context));
        return new ScheduleDownloadAdapter.ScheduleDownloadHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ScheduleDownload scheduleDownload = getItem(position);
        ((ScheduleDownloadAdapter.ScheduleDownloadHolder) holder).bind(scheduleDownload , context);
    }




//        @Override
//        public Filter getFilter() {
//            return filter ;
//        }

    public class ScheduleDownloadHolder extends RecyclerView.ViewHolder{
        ScheduleDownloadItemBinding binding ;
        public ScheduleDownloadHolder(ScheduleDownloadItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding ;
            binding.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClicked.onClick(getItem(getAdapterPosition()));
                }
            });
//            AnimatedVectorDrawableCompat loading_img_animated = AnimatedVectorDrawableCompat.create(context , R.drawable.anim_time) ;
//            binding.loading.setImageDrawable(loading_img_animated);
//            if (loading_img_animated!=null)
//                loading_img_animated.start();
        }

        public void bind (ScheduleDownload scheduleDownload , Context context) {
              binding.fileName.setText(scheduleDownload.getFileName());
              binding.downloadImg.setImageResource(Utilities.getImageResource(scheduleDownload.getType()));
              binding.time.setText(Utilities.FormatToTime(scheduleDownload.getCalendar() , (AppCompatActivity) context));
              binding.date.setText(Utilities.FormatToDate(scheduleDownload.getCalendar()));

        }
    }
    public interface OnItemClicked {
        void onClick (ScheduleDownload scheduleDownload);
    }
}
