package com.yjh.rer.room.entity;

import android.arch.lifecycle.ViewModel;
import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import static com.yjh.rer.room.entity.RedEnvelope.TABLE_NAME;

@Entity(tableName = TABLE_NAME)
public class RedEnvelope extends ViewModel {
    public static final String TABLE_NAME = "red_envelopes";
    public static final String FIELD_RED_ENVELOPE_ID = "red_envelope_id";
    public static final String FIELD_USER_ID = "user_id";
    public static final String FIELD_TYPE = "money_type";
    public static final String FIELD_MONEY = "money";
    public static final String FIELD_MONEY_FROM = "money_from";
    public static final String FIELD_REMARK = "remark";
    public static final String FIELD_CREATED = "created";

    @PrimaryKey
    @SerializedName(FIELD_RED_ENVELOPE_ID)
    @ColumnInfo(name = FIELD_RED_ENVELOPE_ID)
    private int redEnvelopeId;
    @SerializedName(FIELD_USER_ID)
    @ColumnInfo(name = FIELD_USER_ID)
    private int userId;
    @SerializedName(FIELD_TYPE)
    @ColumnInfo(name = FIELD_TYPE)
    private int type;
    @SerializedName(FIELD_MONEY)
    @ColumnInfo(name = FIELD_MONEY)
    private String money;
    @SerializedName(FIELD_MONEY_FROM)
    @ColumnInfo(name = FIELD_MONEY_FROM)
    private String moneyFrom;
    @SerializedName(FIELD_REMARK)
    @ColumnInfo(name = FIELD_REMARK)
    private String remark;
    @SerializedName(FIELD_CREATED)
    @ColumnInfo(name = FIELD_CREATED)
    private String created;

    public int getMoneyInt() {
        try {
            return Integer.valueOf(money);
        } catch (Exception e) {
            return 0;
        }
    }

    public String getCreatedDate() {
        return created == null ? "" : created.split(" ")[0];
    }


    public int getRedEnvelopeId() {
        return redEnvelopeId;
    }

    public void setRedEnvelopeId(int redEnvelopeId) {
        this.redEnvelopeId = redEnvelopeId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getMoneyFrom() {
        return moneyFrom;
    }

    public void setMoneyFrom(String moneyFrom) {
        this.moneyFrom = moneyFrom;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }
}
