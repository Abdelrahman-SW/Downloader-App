package com.yremhl.ystgdh.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.yremhl.ystgdh.Models.ActiveDownload;
import com.yremhl.ystgdh.R;
import com.yremhl.ystgdh.Utilites.Utilities;
import com.yremhl.ystgdh.databinding.ActiveDownloadsItemBinding;

import java.util.ArrayList;
import java.util.List;

public class ActiveDownloadsAdapter extends ListAdapter<ActiveDownload, RecyclerView.ViewHolder> {

        private final Context context;
        private final List<ActiveDownload> activeDownloads = new ArrayList<>();
        OnPausePlayBtnClicked onPausePlayBtnClicked ;
        private OnItemClicked onItemClicked ;
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

        private static final DiffUtil.ItemCallback<ActiveDownload> diffUtil = new DiffUtil.ItemCallback<ActiveDownload>() {
            @Override
            public boolean areItemsTheSame(@NonNull ActiveDownload oldItem, @NonNull ActiveDownload newItem) {
                return false ;
            }

            @Override
            public boolean areContentsTheSame(@NonNull ActiveDownload oldItem, @NonNull ActiveDownload newItem) {
                return  false ;
            }
        };


    public ActiveDownloadsAdapter(Context requireContext) {
            super(diffUtil);
            this.context = requireContext ;
            onPausePlayBtnClicked = (OnPausePlayBtnClicked) requireContext ;
            onItemClicked = (OnItemClicked) requireContext;
     }

        @NonNull
        @Override
        public ActiveDownloadHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ActiveDownloadsItemBinding binding = ActiveDownloadsItemBinding.inflate(LayoutInflater.from(context));
            return new ActiveDownloadHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ActiveDownload activeDownload = getItem(position);
            ((ActiveDownloadHolder) holder).bind(activeDownload , context);
        }




//        @Override
//        public Filter getFilter() {
//            return filter ;
//        }

        public class ActiveDownloadHolder extends RecyclerView.ViewHolder{
            ActiveDownloadsItemBinding binding ;
            public ActiveDownloadHolder(ActiveDownloadsItemBinding binding) {
                super(binding.getRoot());
                this.binding = binding ;

                binding.pausePlayBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            onPausePlayBtnClicked.onClick(getItem(getAdapterPosition()));
                        }
                        catch (Exception exception) {
                            Log.i("ab_do" , exception.getMessage());
                        }
                    }
                });

                binding.root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            onItemClicked.onItemClick(getItem(getAdapterPosition()));
                        }catch (Exception exception) {
                            Log.i("ab_do" , exception.getMessage());
                        }
                    }
                });

                binding.downloadProgress.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        return true;
                    }
                });
        }

        public void bind (ActiveDownload activeDownload , Context context) {
              binding.fileName.setText(activeDownload.getFileName());
              binding.downloadProgress.setProgress((int) activeDownload.getProgress());
              binding.downloadSpeed.setText(Utilities.getSpeedTextFromStatue(context , activeDownload , binding.downloadSpeed));
              binding.downloaded.setText (String.format(context.getString(R.string.process) , Utilities.getDownloadProcessString(activeDownload.getDownloaded() , context) , Utilities.getDownloadProcessString(activeDownload.getSize() , context)));
              binding.downloadImg.setImageResource(Utilities.getImageResource(activeDownload.getType()));
              if (activeDownload.isPaused()) {
                  binding.pausePlayBtn.setImageResource(R.drawable.ic_play__3_);
              }
              else {
                  binding.pausePlayBtn.setImageResource(R.drawable.ic_pause);
              }
              if (activeDownload.isWaiting()) {
                  binding.downloaded.setVisibility(View.GONE);
                  binding.pausePlayBtn.setVisibility(View.GONE);
                  binding.downloadProgress.setVisibility(View.GONE);
              }
              else {
                  binding.downloaded.setVisibility(View.VISIBLE);
                  binding.pausePlayBtn.setVisibility(View.VISIBLE);
                  binding.downloadProgress.setVisibility(View.VISIBLE);
              }
        }
    }
    public interface OnPausePlayBtnClicked {
        void onClick (ActiveDownload activeDownload);
    }
    public interface OnItemClicked {
        void onItemClick (ActiveDownload activeDownload);
    }
}
