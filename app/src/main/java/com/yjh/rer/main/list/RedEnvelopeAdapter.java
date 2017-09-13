package com.yjh.rer.main.list;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.yjh.rer.R;
import com.yjh.rer.room.entity.RedEnvelope;
import com.yjh.rer.util.AlertUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemLongClick;
import butterknife.OnLongClick;

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
        return new RedEnvelopeViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_red_envelope, parent, false));
    }


    @Override
    public void onBindViewHolder(RedEnvelopeViewHolder holder, int position) {
        final RedEnvelope redEnvelope = mRedEnvelopes.get(position);
        holder.fromTextView.setText(redEnvelope.getMoneyFrom());
        holder.dateTextView.setText(redEnvelope.getCreatedDate());
        holder.moneyTextView.setText(String.format(mContext.getString(R.string.red_envelope_yuan),
                redEnvelope.getMoneyInt()) + ", " + redEnvelope.getRemark());
        holder.rootView.setTag(redEnvelope);
    }

    @Override
    public int getItemCount() {
        return mRedEnvelopes.size();
    }

    class RedEnvelopeViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.content_layout)
        View rootView;
        @BindView(R.id.tv_from)
        TextView fromTextView;
        @BindView(R.id.tv_datetime)
        TextView dateTextView;
        @BindView(R.id.tv_money)
        TextView moneyTextView;

        RedEnvelopeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.content_layout)
        void intent2DetailView(View v) {
        }

        @OnLongClick(R.id.content_layout)
        boolean showDeleteDialog(View view) {
            AlertUtils.showConfirmDialog(mContext, R.string.delete_red_envelope_alert,
                    (dialogInterface, i) -> {
                        mInterface.delete(((RedEnvelope) view.getTag()).getRedEnvelopeId());
                    });
            return true;
        }
    }
}
