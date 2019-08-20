package com.yjh.rer.main.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

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

    @NonNull
    @Override
    public RedEnvelopeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RedEnvelopeViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_red_envelope, parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull RedEnvelopeViewHolder holder, int position) {
        final RedEnvelope redEnvelope = mRedEnvelopes.get(position);
        holder.setText(redEnvelope);
    }

    @Override
    public int getItemCount() {
        return mRedEnvelopes.size();
    }

    public class RedEnvelopeViewHolder extends RecyclerView.ViewHolder {
        private ItemRedEnvelopeBinding mBinding;

        RedEnvelopeViewHolder(View itemView) {
            super(itemView);
            mBinding = DataBindingUtil.bind(itemView);
            if (mBinding != null) {
                mBinding.setHandler(this);
            }
        }

        public void setText(RedEnvelope redEnvelope) {
            mBinding.tvFrom.setText(redEnvelope.getMoneyFrom());
            mBinding.tvDatetime.setText(redEnvelope.getCreatedDate());
            mBinding.tvMoney.setText(String.format(mContext.getString(R.string.red_envelope_yuan),
                    redEnvelope.getMoneyInt(), redEnvelope.getRemark()));
            mBinding.getRoot().setTag(redEnvelope);
        }

        public void intent2DetailView(View v) {
        }

        public boolean showDeleteDialog(View view) {
            AlertUtils.showConfirmDialog(mContext, R.string.delete_red_envelope_alert,
                    (dialogInterface, i) -> {
                        mInterface.delete(((RedEnvelope) view.getTag()).getRedEnvelopeId());
                    });
            return true;
        }
    }
}
