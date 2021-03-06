package com.hska.ebusiness.toolbar.util;

import android.database.Cursor;
import android.util.Log;

import com.hska.ebusiness.toolbar.dao.DatabaseSchema;
import com.hska.ebusiness.toolbar.model.Offer;

public class OfferMapper {

    public static Offer map(final Cursor cursor) {
        Offer offer = new Offer();

        Log.d(OfferMapper.class.getSimpleName(), "Offer: " + String.valueOf(cursor.getColumnIndex(DatabaseSchema.OfferEntry.COLUMN_NAME_LENDER_FK)));

        offer.setId(cursor.getLong(cursor.getColumnIndex(DatabaseSchema.OfferEntry._ID)));
        offer.setName(cursor.getString(cursor.getColumnIndex(DatabaseSchema.OfferEntry.COLUMN_NAME_NAME)));
        offer.setImage(cursor.getString(cursor.getColumnIndex(DatabaseSchema.OfferEntry.COLUMN_NAME_IMAGE)));
        offer.setDescription(cursor.getString(cursor.getColumnIndex(DatabaseSchema.OfferEntry.COLUMN_NAME_DESCRIPTION)));
        offer.setZipCode(cursor.getString(cursor.getColumnIndex(DatabaseSchema.OfferEntry.COLUMN_NAME_ZIP_CODE)));
        offer.setPrice(Long.parseLong(cursor.getString(cursor.getColumnIndex(DatabaseSchema.OfferEntry.COLUMN_NAME_PRICE))));
        offer.setValidFrom(cursor.getLong(cursor.getColumnIndex(DatabaseSchema.OfferEntry.COLUMN_NAME_VALID_FROM)));
        offer.setValidTo(cursor.getLong(cursor.getColumnIndex(DatabaseSchema.OfferEntry.COLUMN_NAME_VALID_TO)));
        offer.setLender(cursor.getLong(cursor.getColumnIndex(DatabaseSchema.OfferEntry.COLUMN_NAME_LENDER_FK)));

        return offer;
    }

}
