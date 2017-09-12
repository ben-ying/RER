package com.yjh.rer.main.list;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yjh.rer.R;
import com.yjh.rer.room.entity.RedEnvelope;
import com.yjh.rer.util.AlertUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RedEnvelopeAdapter extends RecyclerView.Adapter<
        RedEnvelopeAdapter.RedEnvelopeViewHolder> implements View.OnClickListener {

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
        holder.rootView.setOnClickListener(this);
        holder.rootView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertUtils.showConfirmDialog(mContext, R.string.delete_red_envelope_alert,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mInterface.delete(redEnvelope.getRedEnvelopeId());
                            }
                        });
                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.content_layout:
//                Intent intent = new Intent(mContext, RedEnvelopeActivity.class);
//                ((Activity) mContext).startActivityForResult(
//                        intent, Constants.RED_ENVELOPE_EDIT_REQUEST);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mRedEnvelopes.size();
    }

    class RedEnvelopeViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.content_layout) View rootView;
        @BindView(R.id.tv_from) TextView fromTextView;
        @BindView(R.id.tv_datetime) TextView dateTextView;
        @BindView(R.id.tv_money) TextView moneyTextView;

        RedEnvelopeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
