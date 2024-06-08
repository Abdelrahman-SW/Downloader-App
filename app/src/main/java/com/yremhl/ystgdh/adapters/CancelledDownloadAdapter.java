package com.yremhl.ystgdh.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.yremhl.ystgdh.Models.CancelledDownload;
import com.yremhl.ystgdh.Utilites.Utilities;
import com.yremhl.ystgdh.databinding.CancelledDownloadItemBinding;

import java.util.ArrayList;
import java.util.List;

public class CancelledDownloadAdapter extends ListAdapter<CancelledDownload, RecyclerView.ViewHolder> {

    private final Context context;
    private final List<CancelledDownload> cancelledDownload = new ArrayList<>();
    private final CancelledDownloadAdapter.OnItemClicked onItemClicked ;

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

    private static final DiffUtil.ItemCallback<CancelledDownload> diffUtil = new DiffUtil.ItemCallback<CancelledDownload>() {
        @Override
        public boolean areItemsTheSame(@NonNull CancelledDownload oldItem, @NonNull CancelledDownload newItem) {
            return oldItem.getId() == newItem.getId() ;
        }

        @Override
        public boolean areContentsTheSame(@NonNull CancelledDownload oldItem, @NonNull CancelledDownload newItem) {
            return  false ;
        }
    };


    public CancelledDownloadAdapter(Context requireContext) {
        super(diffUtil);
        this.context = requireContext ;
        onItemClicked = (CancelledDownloadAdapter.OnItemClicked) requireContext;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CancelledDownloadItemBinding binding = CancelledDownloadItemBinding.inflate(LayoutInflater.from(context));
        return new CancelledDownloadAdapter.CancelledDownloadHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CancelledDownload cancelledDownload = getItem(position);
        ((CancelledDownloadAdapter.CancelledDownloadHolder) holder).bind(cancelledDownload , context);
    }




//        @Override
//        public Filter getFilter() {
//            return filter ;
//        }

    public class CancelledDownloadHolder extends RecyclerView.ViewHolder {
        CancelledDownloadItemBinding binding ;
        public CancelledDownloadHolder(CancelledDownloadItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding ;
            binding.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClicked.onClick(getItem(getAdapterPosition()));
                }
            });
        }

        public void bind (CancelledDownload cancelledDownload , Context context) {
            binding.fileName.setText(cancelledDownload.getFileName());
            binding.size.setText(Utilities.getDownloadProcessString(cancelledDownload.getSize() , context));
            binding.downloadImg.setImageResource(Utilities.getImageResource(cancelledDownload.getType()));
        }
    }
    public interface OnItemClicked {
        void onClick (CancelledDownload cancelledDownload);
    }
}
