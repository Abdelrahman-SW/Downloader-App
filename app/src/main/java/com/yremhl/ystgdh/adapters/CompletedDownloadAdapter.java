package com.yremhl.ystgdh.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.yremhl.ystgdh.Models.CompletedDownload;
import com.yremhl.ystgdh.Utilites.Utilities;
import com.yremhl.ystgdh.databinding.CompletedDownloadItemBinding;

import java.util.ArrayList;
import java.util.List;

public class CompletedDownloadAdapter extends ListAdapter<CompletedDownload, RecyclerView.ViewHolder> {

    private final Context context;
    private final List<CompletedDownload> completedDownloads = new ArrayList<>();
    private final OnItemClicked onItemClicked ;

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

    private static final DiffUtil.ItemCallback<CompletedDownload> diffUtil = new DiffUtil.ItemCallback<CompletedDownload>() {
        @Override
        public boolean areItemsTheSame(@NonNull CompletedDownload oldItem, @NonNull CompletedDownload newItem) {
            return oldItem.getId() == newItem.getId() ;
        }

        @Override
        public boolean areContentsTheSame(@NonNull CompletedDownload oldItem, @NonNull CompletedDownload newItem) {
            return  false ;
        }
    };


    public CompletedDownloadAdapter(Context requireContext) {
        super(diffUtil);
        this.context = requireContext ;
        onItemClicked = (OnItemClicked) requireContext;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CompletedDownloadItemBinding binding = CompletedDownloadItemBinding.inflate(LayoutInflater.from(context));
        return new CompletedDownloadHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CompletedDownload completedDownload = getItem(position);
        ((CompletedDownloadHolder) holder).bind(completedDownload , context);
    }




//        @Override
//        public Filter getFilter() {
//            return filter ;
//        }

    public class CompletedDownloadHolder extends RecyclerView.ViewHolder{
        CompletedDownloadItemBinding binding ;
        public CompletedDownloadHolder(CompletedDownloadItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding ;
            binding.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClicked.onClick(getItem(getAdapterPosition()));
                }
            });
        }

        public void bind (CompletedDownload completedDownload , Context context) {
            binding.fileName.setText(completedDownload.getFileName());
            binding.totalTime.setText(completedDownload.getTotalTime());
            binding.downloadImg.setImageResource(Utilities.getImageResource(completedDownload.getType()));
            binding.size.setText(Utilities.getDownloadProcessString(completedDownload.getSize() , context));
        }
    }
    public interface OnItemClicked {
        void onClick (CompletedDownload completedDownload);
    }
}
