package com.yjh.rer;

import static org.junit.Assert.assertEquals;

import com.yjh.rer.data.room.entity.RedEnvelope;

import org.junit.Test;

public class RedEnvelopeTest {

    @Test
    public void getMoneyDouble_parsesDecimal() {
        RedEnvelope redEnvelope = new RedEnvelope();
        redEnvelope.setMoney("12.34");

        assertEquals(12.34, redEnvelope.getMoneyDouble(), 0.0001);
    }

    @Test
    public void getMoneyDouble_parsesInteger() {
        RedEnvelope redEnvelope = new RedEnvelope();
        redEnvelope.setMoney("42");

        assertEquals(42.0, redEnvelope.getMoneyDouble(), 0.0001);
    }

    @Test
    public void getMoneyDouble_invalidReturnsZero() {
        RedEnvelope redEnvelope = new RedEnvelope();
        redEnvelope.setMoney("not-a-number");

        assertEquals(0.0, redEnvelope.getMoneyDouble(), 0.0001);
    }

    @Test
    public void getCreatedDate_handlesNull() {
        RedEnvelope redEnvelope = new RedEnvelope();
        redEnvelope.setCreated(null);

        assertEquals("", redEnvelope.getCreatedDate());
    }

    @Test
    public void getCreatedDate_splitsDateTime() {
        RedEnvelope redEnvelope = new RedEnvelope();
        redEnvelope.setCreated("2026-01-28 10:20:30");

        assertEquals("2026-01-28", redEnvelope.getCreatedDate());
    }

    @Test
    public void getCreatedDate_keepsDateOnly() {
        RedEnvelope redEnvelope = new RedEnvelope();
        redEnvelope.setCreated("2026-01-28");

        assertEquals("2026-01-28", redEnvelope.getCreatedDate());
    }
}
