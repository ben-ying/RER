package com.yjh.rer.main.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.yjh.rer.R;
import com.yjh.rer.databinding.ItemRedEnvelopeBinding;
import com.yjh.rer.room.entity.RedEnvelope;
import com.yjh.rer.util.AlertUtils;

public class RedEnvelopeAdapter extends PagedListAdapter<
        RedEnvelope, RedEnvelopeAdapter.RedEnvelopeViewHolder> {

    private RedEnvelopeInterface mInterface;

    interface RedEnvelopeInterface {
        void delete(int reId);
    }

    RedEnvelopeAdapter(RedEnvelopeInterface redEnvelopeInterface) {
        super(DIFF_CALLBACK);
        this.mInterface = redEnvelopeInterface;
    }

    @NonNull
    @Override
    public RedEnvelopeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RedEnvelopeViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_red_envelope, parent, false));
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    public void onBindViewHolder(@NonNull RedEnvelopeViewHolder holder, int position) {
        RedEnvelope redEnvelope = getItem(position);
        holder.getBinding().setRedenvelope(redEnvelope);
        holder.getBinding().setHandler(holder);
//        holder.getBinding().executePendingBindings();
    }


    public class RedEnvelopeViewHolder extends RecyclerView.ViewHolder {
        private ItemRedEnvelopeBinding mBinding;

        ItemRedEnvelopeBinding getBinding() {
            return mBinding;
        }

        RedEnvelopeViewHolder(View itemView) {
            super(itemView);
            mBinding = ItemRedEnvelopeBinding.bind(itemView);
        }

        public void intent2DetailView(View v) {
        }

        public boolean showDeleteDialog(View view) {
            AlertUtils.showConfirmDialog(view.getContext(), R.string.delete_red_envelope_alert,
                    (dialogInterface, i) -> {
                        mInterface.delete(((RedEnvelope) view.getTag()).getRedEnvelopeId());
                    });
            return true;
        }
    }

    private static DiffUtil.ItemCallback<RedEnvelope> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<RedEnvelope>() {
        @Override
        public boolean areItemsTheSame(
                @NonNull RedEnvelope oldItem, @NonNull RedEnvelope newItem) {
            return oldItem.getRedEnvelopeId() == newItem.getRedEnvelopeId();
        }

        @Override
        public boolean areContentsTheSame(
                @NonNull RedEnvelope oldItem, @NonNull RedEnvelope newItem) {
            return oldItem.equals(newItem);
        }
    };
}
