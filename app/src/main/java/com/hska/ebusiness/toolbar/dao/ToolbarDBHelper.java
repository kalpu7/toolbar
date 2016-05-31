package com.hska.ebusiness.toolbar.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import com.hska.ebusiness.toolbar.model.Credentials;
import com.hska.ebusiness.toolbar.model.Offer;
import com.hska.ebusiness.toolbar.model.User;
import com.hska.ebusiness.toolbar.model.Rental;

import static com.hska.ebusiness.toolbar.dao.DatabaseSchema.BalanceEntry;
import static com.hska.ebusiness.toolbar.dao.DatabaseSchema.CredentialsEntry;
import static com.hska.ebusiness.toolbar.dao.DatabaseSchema.OfferEntry;
import static com.hska.ebusiness.toolbar.dao.DatabaseSchema.RentalEntry;
import static com.hska.ebusiness.toolbar.dao.DatabaseSchema.UserEntry;

public class ToolbarDBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 14;

    public static final String DATABASE_NAME = "toolbar.db";

    private final String TAG = this.getClass().getSimpleName();

    private static final String SQL_CREATE_TABLE_USER =
            "CREATE TABLE " + UserEntry.TABLE_NAME + " (" +
                    UserEntry._ID + " INTEGER PRIMARY KEY," +
                    UserEntry.COLUMN_NAME_USERNAME + " TEXT," +
                    UserEntry.COLUMN_NAME_EMAIL + " TEXT," +
                    UserEntry.COLUMN_NAME_STREET + " TEXT," +
                    UserEntry.COLUMN_NAME_ZIP_CODE + " TEXT," +
                    UserEntry.COLUMN_NAME_COUNTRY + " TEXT," +
                    UserEntry.COLUMN_NAME_PHONE + " TEXT," +
                    UserEntry.COLUMN_NAME_DESCRIPTION + " TEXT" +
                    ");";

    private static final String SQL_CREATE_TABLE_CREDENTIALS =
            "CREATE TABLE " + CredentialsEntry.TABLE_NAME + " (" +
                    CredentialsEntry._ID + " INTEGER PRIMARY KEY," +
                    CredentialsEntry.COLUMN_NAME_PASSWORD + " TEXT," +
                    CredentialsEntry.COLUMN_NAME_USER_FK + " INTEGER," +
                    "FOREIGN KEY (" + CredentialsEntry.COLUMN_NAME_USER_FK + ") REFERENCES " + UserEntry.TABLE_NAME + "(" + UserEntry._ID + ")" +
                    ")";

    private static final String SQL_CREATE_TABLE_BALANCE =
            "CREATE TABLE " + BalanceEntry.TABLE_NAME + " (" +
                    BalanceEntry._ID + " INTEGER PRIMARY KEY," +
                    BalanceEntry.COLUMN_NAME_AMOUNT + " INTEGER," +
                    BalanceEntry.COLUMN_NAME_USER_FK + " INTEGER," +
                    "FOREIGN KEY (" + BalanceEntry.COLUMN_NAME_USER_FK + ") REFERENCES " + UserEntry.TABLE_NAME + "(" + UserEntry._ID + ")" +
                    ")";

    private static final String SQL_CREATE_TABLE_OFFER =
            "CREATE TABLE " + OfferEntry.TABLE_NAME + " (" +
                    OfferEntry._ID + " INTEGER PRIMARY KEY," +
                    OfferEntry.COLUMN_NAME_NAME + " TEXT," +
                    OfferEntry.COLUMN_NAME_IMAGE + " TEXT," +
                    OfferEntry.COLUMN_NAME_DESCRIPTION + " TEXT," +
                    OfferEntry.COLUMN_NAME_ZIP_CODE + " TEXT," +
                    OfferEntry.COLUMN_NAME_PRICE + " INTEGER," +
                    OfferEntry.COLUMN_NAME_VALID_FROM + " INTEGER," +
                    OfferEntry.COLUMN_NAME_VALID_TO + " INTEGER," +
                    OfferEntry.COLUMN_NAME_LENDER_FK + " INTEGER, " +
                    "FOREIGN KEY (" + OfferEntry.COLUMN_NAME_LENDER_FK + ") REFERENCES " + UserEntry.TABLE_NAME + "(" + UserEntry._ID + ")" +
                    ");";


    private static final String SQL_CREATE_TABLE_RENTAL =
            "CREATE TABLE " + RentalEntry.TABLE_NAME + " (" +
                    RentalEntry._ID + " INTEGER PRIMARY KEY, " +
                    RentalEntry.COLUMN_NAME_FROM + " INTEGER, " +
                    RentalEntry.COLUMN_NAME_TO + " INTEGER, " +
                    RentalEntry.COLUMN_NAME_STATUS + " INTEGER, " +
                    RentalEntry.COLUMN_NAME_OFFER_FK + " INTEGER, " +
                    RentalEntry.COLUMN_NAME_HIRER_FK + " INTEGER, " +
                    RentalEntry.COLUMN_NAME_LENDER_FK + " INTEGER, " +
                    "FOREIGN KEY (" + RentalEntry.COLUMN_NAME_OFFER_FK + ") REFERENCES " + OfferEntry.TABLE_NAME + "(" + OfferEntry._ID + "), " +
                    "FOREIGN KEY (" + RentalEntry.COLUMN_NAME_HIRER_FK + ") REFERENCES " + UserEntry.TABLE_NAME + "(" + UserEntry._ID + "), " +
                    "FOREIGN KEY (" + RentalEntry.COLUMN_NAME_LENDER_FK + ") REFERENCES " + UserEntry.TABLE_NAME + "(" + UserEntry._ID + ")" +
                    ")";


    private static final String SQL_DROP_TABLE_USER = "DROP TABLE IF EXISTS " + UserEntry.TABLE_NAME + ";";
    private static final String SQL_DROP_TABLE_CREDENTIALS = "DROP TABLE IF EXISTS " + CredentialsEntry.TABLE_NAME + ";";
    private static final String SQL_DROP_TABLE_BALANCE = "DROP TABLE IF EXISTS " + BalanceEntry.TABLE_NAME + ";";
    private static final String SQL_DROP_TABLE_OFFER = "DROP TABLE IF EXISTS " + OfferEntry.TABLE_NAME + ";";
    private static final String SQL_DROP_TABLE_RENTAL = "DROP TABLE IF EXISTS " + RentalEntry.TABLE_NAME + ";";

    private static ToolbarDBHelper instance = null;
    private Context context;

    private ToolbarDBHelper(final Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public static ToolbarDBHelper getInstance(final Context context) {
        if(instance == null) {
            return new ToolbarDBHelper(context.getApplicationContext());
        }

        return instance;
    }

    public ToolbarDBHelper(final Context context, final String name,
                           final SQLiteDatabase.CursorFactory factory, final int version) {
        super(context, name, factory, version);
    }

    public Cursor findAllUsers() {
        Log.d(TAG, ": findAllUsers");
        final SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        final String sortOder = UserEntry.COLUMN_NAME_USERNAME + " ASC";
        queryBuilder.setTables(UserEntry.TABLE_NAME);
        return queryBuilder.query(getReadableDatabase(), null, null, null, null, null, sortOder);
    }

    public Cursor findOfferById(final long id) {
        Log.d( TAG, ": findOfferById " + id );

        final SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(OfferEntry.TABLE_NAME);
        return queryBuilder.query(getReadableDatabase(),
                new String[]{OfferEntry._ID, OfferEntry.COLUMN_NAME_NAME}, OfferEntry._ID + "=?",
                    new String[]{Long.toString(id)}, null, null, null);
    }

    //Neu
    public Cursor findAllRentalsToOffer(final long offerId){
        Log.d(TAG, ": findAllRentalsToOffer");

        final SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(RentalEntry.TABLE_NAME);
        return queryBuilder.query(getReadableDatabase(), null, RentalEntry.COLUMN_NAME_OFFER_FK
                + "=?", new String[]{Long.toString(offerId)}, null, null, null);
    }

    public long insertOffer(final Offer offer) {
        Log.d(TAG, ": insertOffer: " + offer.getName());

        final ContentValues values = getOfferValues(offer);
        return getWritableDatabase().insert(OfferEntry.TABLE_NAME, null, values);
    }

    public int updateOffer(final Offer offer) {
        Log.d(TAG, ": updateOffer: " + offer.getId());

        final String whereClause = OfferEntry._ID + "=?";
        final String[] whereArgs = new String[]{String.valueOf(offer.getId())};
        final ContentValues values = getOfferValues(offer);
        return getWritableDatabase().update(OfferEntry.TABLE_NAME, values, whereClause, whereArgs);
    }

    public long insertUser(final User user) {
        Log.d(TAG, ": insertUser: " + user.getUsername());

        final ContentValues values = getUserValues(user);
        return getWritableDatabase().insert(UserEntry.TABLE_NAME, null, values);
    }

    public long insertCredentials(final Credentials credentials) {
        Log.d(TAG, ": insertCredentials: " + credentials.getUserId());

        final ContentValues values = getCredentialValues(credentials);
        return getWritableDatabase().insert(CredentialsEntry.TABLE_NAME, null, values);
    }

    public Cursor findUserByUsername(final String username) {
        Log.d( TAG, ": findUserByUsername " + username );

        final SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(UserEntry.TABLE_NAME);
        return queryBuilder.query(getReadableDatabase(),
                null, UserEntry.COLUMN_NAME_USERNAME + "=?",
                new String[]{username}, null, null, null);
    }

    public Cursor findCredentialsByUserId(final long userId) {
        Log.d(TAG, ": findCredentialsByUserId " + userId);

        final SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(CredentialsEntry.TABLE_NAME);
        return queryBuilder.query(getReadableDatabase(),
                null, CredentialsEntry._ID + "=?",
                new String[]{Long.toString(userId)}, null, null, null);
    }

    public int deleteOffer(final Offer offer){
        Log.d(TAG, ": deleteOffer: " + offer.getName());

        final String whereClause = OfferEntry._ID + "=?";
        final String[] whereArgs = new String[]{String.valueOf(offer.getId())};
        final ContentValues values = getOfferValues(offer);
        return getWritableDatabase().delete(OfferEntry.TABLE_NAME, whereClause, whereArgs);
    }

    //Neu
    public long insertRental(final Rental rental) {
        Log.d(TAG, ": insertRental: " + rental.getOffer());

        final ContentValues values = getRentalValues(rental);
        return getWritableDatabase().insert(RentalEntry.TABLE_NAME, null, values);
    }

    private ContentValues getOfferValues(final Offer offer) {
        final ContentValues values = new ContentValues();
        values.put(OfferEntry.COLUMN_NAME_NAME, offer.getName());
        final String image = offer.getImage() == null ? "" : offer.getImage();
        values.put(OfferEntry.COLUMN_NAME_IMAGE, image);
        values.put(OfferEntry.COLUMN_NAME_DESCRIPTION, offer.getDescription());
        values.put(OfferEntry.COLUMN_NAME_ZIP_CODE, offer.getZipCode());
        values.put(OfferEntry.COLUMN_NAME_PRICE, offer.getPrice());
        values.put(OfferEntry.COLUMN_NAME_VALID_FROM, offer.getValidFrom());
        values.put(OfferEntry.COLUMN_NAME_VALID_TO, offer.getValidTo());
        values.put(OfferEntry.COLUMN_NAME_LENDER_FK, offer.getLender_fk());

        return values;
    }

    private ContentValues getUserValues(final User user) {
        final ContentValues values = new ContentValues();
        values.put(UserEntry.COLUMN_NAME_USERNAME, user.getUsername());
        values.put(UserEntry.COLUMN_NAME_EMAIL, user.getEmail());
        values.put(UserEntry.COLUMN_NAME_STREET, user.getStreet());
        values.put(UserEntry.COLUMN_NAME_ZIP_CODE, user.getZipCode());
        values.put(UserEntry.COLUMN_NAME_COUNTRY, user.getCountry());
        values.put(UserEntry.COLUMN_NAME_DESCRIPTION, user.getDescription());

        return values;
    }

    private ContentValues getCredentialValues(final Credentials credentials) {
        final ContentValues values = new ContentValues();
        values.put(CredentialsEntry.COLUMN_NAME_PASSWORD, credentials.getPassword());
        values.put(CredentialsEntry.COLUMN_NAME_USER_FK, credentials.getUserId());

        return  values;
    }

    //Neu
    private ContentValues getRentalValues(final Rental rental) {
        final ContentValues values = new ContentValues();
        values.put(RentalEntry.COLUMN_NAME_FROM, rental.getRentFrom());
        values.put(RentalEntry.COLUMN_NAME_TO, rental.getRentTo());
        values.put(RentalEntry.COLUMN_NAME_STATUS, rental.getStatus());
        values.put(RentalEntry.COLUMN_NAME_OFFER_FK, rental.getOffer());
        //values.put(RentalEntry.COLUMN_NAME_LENDER_FK, rental.getLender().getId());
        //values.put(RentalEntry.COLUMN_NAME_HIRER_FK, rental.getHirer().getId());

        return values;
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
        Log.d(TAG, ": onCreate Database with version" + db.getVersion());

        db.execSQL(SQL_CREATE_TABLE_USER);
        db.execSQL(SQL_CREATE_TABLE_CREDENTIALS);
        db.execSQL(SQL_CREATE_TABLE_BALANCE);
        db.execSQL(SQL_CREATE_TABLE_OFFER);
        db.execSQL(SQL_CREATE_TABLE_RENTAL);
    }

    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        Log.d(TAG, ": onUpgrade database from version" + oldVersion + " to" + newVersion);

        db.execSQL(SQL_DROP_TABLE_RENTAL);
        db.execSQL(SQL_DROP_TABLE_OFFER);
        db.execSQL(SQL_DROP_TABLE_BALANCE);
        db.execSQL(SQL_DROP_TABLE_CREDENTIALS);
        db.execSQL(SQL_DROP_TABLE_USER);

        db.execSQL(SQL_CREATE_TABLE_RENTAL);
        db.execSQL(SQL_CREATE_TABLE_OFFER);
        db.execSQL(SQL_CREATE_TABLE_BALANCE);
        db.execSQL(SQL_CREATE_TABLE_CREDENTIALS);
        db.execSQL(SQL_CREATE_TABLE_USER);

        onCreate(db);
    }

    @Override
    public void onDowngrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        Log.d(TAG, ": onDowngrade database from version" + newVersion + " to" + oldVersion);

        onUpgrade(db, oldVersion, newVersion);
    }
}

