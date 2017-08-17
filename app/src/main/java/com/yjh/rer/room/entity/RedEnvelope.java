package com.yjh.rer.room.entity;

import android.arch.lifecycle.ViewModel;
import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import static com.yjh.rer.room.entity.RedEnvelope.TABLE_NAME;

@Entity(tableName = TABLE_NAME)
public class RedEnvelope extends ViewModel {
    public static final String TABLE_NAME = "red_envelopes";
    public static final String FIELD_RED_ENVELOPE_ID = "red_envelopes";
    public static final String FIELD_USER_ID = "user_id";
    public static final String FIELD_TYPE = "type";
    public static final String FIELD_MONEY = "money";
    public static final String FIELD_MONEY_FROM = "money_from";
    public static final String FIELD_REMARK = "remark";
    public static final String FIELD_CREATED = "created";

    @PrimaryKey
    @ColumnInfo(name = FIELD_RED_ENVELOPE_ID)
    private int redEnvelopeId;
    @ColumnInfo(name = FIELD_USER_ID)
    private int userId;
    @ColumnInfo(name = FIELD_TYPE)
    private int type;
    @ColumnInfo(name = FIELD_MONEY)
    private String money;
    @ColumnInfo(name = FIELD_MONEY_FROM)
    private String moneyFrom;
    @ColumnInfo(name = FIELD_REMARK)
    private String remark;
    @ColumnInfo(name = FIELD_CREATED)
    private String created;

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
