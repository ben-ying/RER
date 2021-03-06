package com.yjh.rer.room.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import static com.yjh.rer.room.entity.RedEnvelope.TABLE_NAME;

@Entity(tableName = TABLE_NAME)
public class RedEnvelope {
    public static final int INVALID_ID = -1;
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
        return Integer.valueOf(money);
    }

    public String getCreatedDate() {
        return created == null ? "" : created.split(" ")[0];
    }

    public RedEnvelope() {
    }

    @Ignore
    public RedEnvelope(int redEnvelopeId) {
        this.redEnvelopeId = redEnvelopeId;
    }

    @Ignore
    public RedEnvelope(String money, String moneyFrom, String remark) {
        this.redEnvelopeId = INVALID_ID;
        this.money = money;
        this.moneyFrom = moneyFrom;
        this.remark = remark;
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

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
