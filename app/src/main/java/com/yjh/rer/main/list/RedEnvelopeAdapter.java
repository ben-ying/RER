package com.yjh.rer.main.list;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yjh.rer.R;
import com.yjh.rer.databinding.ItemRedEnvelopeBinding;
import com.yjh.rer.room.entity.RedEnvelope;
import com.yjh.rer.util.AlertUtils;

import java.util.List;

public class RedEnvelopeAdapter extends RecyclerView.Adapter<
        RedEnvelopeAdapter.RedEnvelopeViewHolder> {

    private Context mContext;
    private List<RedEnvelope> mRedEnvelopes;
    private TextView mTotalTextView;
    private RedEnvelopeInterface mInterface;

    interface RedEnvelopeInterface {
        void delete(int reId);
    }

    RedEnvelopeAdapter(Context context, List<RedEnvelope> redEnvelopes,
                       TextView totalTextView, RedEnvelopeInterface redEnvelopeInterface) {
        this.mContext = context;
        this.mRedEnvelopes = redEnvelopes;
        this.mTotalTextView = totalTextView;
        this.mInterface = redEnvelopeInterface;
    }

    public void setData(List<RedEnvelope> redEnvelopes) {
        this.mRedEnvelopes = redEnvelopes;
        notifyDataSetChanged();
    }

    @Override
    public RedEnvelopeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemRedEnvelopeBinding binding = ItemRedEnvelopeBinding.inflate(
                LayoutInflater.from(mContext), parent, false);
        return new RedEnvelopeViewHolder(binding);
    }


    @Override
    public void onBindViewHolder(RedEnvelopeViewHolder holder, int position) {
        final RedEnvelope redEnvelope = mRedEnvelopes.get(position);
        holder.binding.tvFrom.setText(redEnvelope.getMoneyFrom());
        holder.binding.tvDatetime.setText(redEnvelope.getCreatedDate());
        holder.binding.tvRemark.setText(redEnvelope.getRemark());
        holder.binding.tvMoney.setText(String.format(mContext.getString(R.string.red_envelope_yuan),
                redEnvelope.getMoneyDouble()));
        holder.binding.contentLayout.setTag(redEnvelope);
    }

    @Override
    public int getItemCount() {
        return mRedEnvelopes.size();
    }

    class RedEnvelopeViewHolder extends RecyclerView.ViewHolder {
        ItemRedEnvelopeBinding binding;

        RedEnvelopeViewHolder(ItemRedEnvelopeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            
            // 设置点击监听器
            binding.contentLayout.setOnClickListener(v -> intent2DetailView(v));
            binding.contentLayout.setOnLongClickListener(v -> showDeleteDialog(v));
        }

        void intent2DetailView(View v) {
            // 空实现
        }

        boolean showDeleteDialog(View view) {
            AlertUtils.showConfirmDialog(mContext, R.string.delete_red_envelope_alert,
                    (dialogInterface, i) -> {
                        mInterface.delete(((RedEnvelope) view.getTag()).getRedEnvelopeId());
                    });
            return true;
        }
    }
}
