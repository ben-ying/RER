package com.yjh.rer.room.entity;


import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

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
    public static final String FIELD_INSERTED_TIME = "inserted_time";
    public static final String FIELD_PAGE = "page";

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
    // Added in database start
    @ColumnInfo(name = FIELD_INSERTED_TIME)
    private long insertedTime = System.currentTimeMillis();
    @ColumnInfo(name = FIELD_PAGE)
    private int page;
    // Added in database end


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

    public long getInsertedTime() {
        return insertedTime;
    }

    public void setInsertedTime(long insertedTime) {
        this.insertedTime = insertedTime;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getLabel() {
        return "From: " + moneyFrom + ", Money: " + money +
                ", Remark: " + remark + ", Page: " + page + "\n";
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        RedEnvelope redEnvelope = (RedEnvelope) obj;
        if (getMoneyInt() != redEnvelope.getMoneyInt()) {
            return false;
        }
        if (!getMoneyFrom().equals(redEnvelope.getMoneyFrom())) {
            return false;
        }
        if (getType() != redEnvelope.getType()) {
            return false;
        }
        if (!getRemark().equals(redEnvelope.getRemark())) {
            return false;
        }
        if (getUserId() != redEnvelope.getUserId()) {
            return false;
        }

        return redEnvelope.getRedEnvelopeId() == getRedEnvelopeId()
                && redEnvelope.getCreated().equals(getCreated());
    }
}
