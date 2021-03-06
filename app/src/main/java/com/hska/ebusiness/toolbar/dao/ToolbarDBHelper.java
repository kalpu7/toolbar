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
import com.hska.ebusiness.toolbar.model.Rental;
import com.hska.ebusiness.toolbar.model.User;

import org.joda.time.DateTime;

import static com.hska.ebusiness.toolbar.dao.DatabaseSchema.BalanceEntry;
import static com.hska.ebusiness.toolbar.dao.DatabaseSchema.CredentialsEntry;
import static com.hska.ebusiness.toolbar.dao.DatabaseSchema.OfferEntry;
import static com.hska.ebusiness.toolbar.dao.DatabaseSchema.RentalEntry;
import static com.hska.ebusiness.toolbar.dao.DatabaseSchema.UserEntry;

public class ToolbarDBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "toolbar.db";

    private final String TAG = this.getClass().getSimpleName();

    private static final String SQL_CREATE_TABLE_USER =
            "CREATE TABLE " + UserEntry.TABLE_NAME + " (" +
                    UserEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    UserEntry.COLUMN_NAME_USERNAME + " TEXT," +
                    UserEntry.COLUMN_NAME_EMAIL + " TEXT," +
                    UserEntry.COLUMN_NAME_STREET + " TEXT," +
                    UserEntry.COLUMN_NAME_ZIP_CODE + " TEXT," +
                    UserEntry.COLUMN_NAME_COUNTRY + " TEXT," +
                    UserEntry.COLUMN_NAME_DESCRIPTION + " TEXT" +
                    ");";

    private static final String SQL_CREATE_TABLE_CREDENTIALS =
            "CREATE TABLE " + CredentialsEntry.TABLE_NAME + " (" +
                    CredentialsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    CredentialsEntry.COLUMN_NAME_PASSWORD + " TEXT," +
                    CredentialsEntry.COLUMN_NAME_USER_FK + " INTEGER," +
                    "FOREIGN KEY (" + CredentialsEntry.COLUMN_NAME_USER_FK + ") REFERENCES " + UserEntry.TABLE_NAME + "(" + UserEntry._ID + ")" +
                    ")";

    private static final String SQL_CREATE_TABLE_BALANCE =
            "CREATE TABLE " + BalanceEntry.TABLE_NAME + " (" +
                    BalanceEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    BalanceEntry.COLUMN_NAME_AMOUNT + " INTEGER," +
                    BalanceEntry.COLUMN_NAME_USER_FK + " INTEGER," +
                    "FOREIGN KEY (" + BalanceEntry.COLUMN_NAME_USER_FK + ") REFERENCES " + UserEntry.TABLE_NAME + "(" + UserEntry._ID + ")" +
                    ")";

    private static final String SQL_CREATE_TABLE_OFFER =
            "CREATE TABLE " + OfferEntry.TABLE_NAME + " (" +
                    OfferEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
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
                    RentalEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
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
        if (instance == null) {
            return new ToolbarDBHelper(context.getApplicationContext());
        }

        return instance;
    }

    public ToolbarDBHelper(final Context context, final String name,
                           final SQLiteDatabase.CursorFactory factory, final int version) {
        super(context, name, factory, version);
    }

    /**
     * Get all rentals of an offer
     *
     * @param offerId of the requested offer
     * @return a cursor that contains all rentals
     */
    public Cursor findAllRentalsToOffer(final long offerId) {
        Log.d(TAG, "findAllRentalsToOffer: " + offerId);

        final SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(RentalEntry.TABLE_NAME);
        return queryBuilder.query(getReadableDatabase(), null, RentalEntry.COLUMN_NAME_OFFER_FK
                + "=?", new String[]{Long.toString(offerId)}, null, null, null);
    }

    /**
     * Get all offers that belong to a specific zip code
     *
     * @param zip in which shall be looked for offers
     * @return a cursor that contains all requested offers
     */
    public Cursor findOfferByZIP(String zip) {
        Log.d(TAG, "findOfferByZIP " + zip);

        final SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(OfferEntry.TABLE_NAME);
        return queryBuilder.query(getReadableDatabase(),
                new String[]{OfferEntry._ID, OfferEntry.COLUMN_NAME_NAME, OfferEntry.COLUMN_NAME_IMAGE, OfferEntry.COLUMN_NAME_DESCRIPTION,
                        OfferEntry.COLUMN_NAME_ZIP_CODE, OfferEntry.COLUMN_NAME_PRICE, OfferEntry.COLUMN_NAME_VALID_FROM,
                        OfferEntry.COLUMN_NAME_VALID_TO, OfferEntry.COLUMN_NAME_LENDER_FK}, OfferEntry.COLUMN_NAME_ZIP_CODE + "=?",
                new String[]{zip}, null, null, null);
    }

    /**
     * Inserts a offer into db
     *
     * @param offer that shall be inserted
     * @param db    the SQLiteDatabase
     * @return the affected rows
     */
    public long insertOffer(final Offer offer, final SQLiteDatabase db) {
        Log.d(TAG, "insertOffer: " + offer.getName());

        final ContentValues values = getOfferValues(offer);
        return db.insert(OfferEntry.TABLE_NAME, null, values);
    }

    /**
     * Updates a given offer
     *
     * @param offer that shall be updated
     * @return the affected rows
     */
    public int updateOffer(final Offer offer) {
        Log.d(TAG, "updateOffer: " + offer.getId());

        final String whereClause = OfferEntry._ID + "=?";
        final String[] whereArgs = new String[]{String.valueOf(offer.getId())};
        final ContentValues values = getOfferValues(offer);
        return getWritableDatabase().update(OfferEntry.TABLE_NAME, values, whereClause, whereArgs);
    }

    /**
     * Insert a user object into the database
     *
     * @param user User object
     * @return long value.
     */
    public long insertUser(final User user, final SQLiteDatabase db) {
        Log.d(TAG, "insertUser: " + user.getUsername());

        final ContentValues values = getUserValues(user);
        return db.insert(UserEntry.TABLE_NAME, null, values);
    }

    /**
     * Insert user credentials into the database
     *
     * @param credentials Credentials object
     * @param db          the SQLiteDatabase
     * @return affected rows
     */
    public long insertCredentials(final Credentials credentials, final SQLiteDatabase db) {
        Log.d(TAG, "insertCredentials: " + credentials.getUserId());

        final ContentValues values = getCredentialValues(credentials);
        return db.insert(CredentialsEntry.TABLE_NAME, null, values);
    }

    /**
     * Find a user regarding the username in the database.
     *
     * @param username that shall be looked for
     * @return Cursor object
     */
    public Cursor findUserByUsername(final String username) {
        Log.d(TAG, "findUserByUsername: " + username);

        final SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(UserEntry.TABLE_NAME);
        return queryBuilder.query(getReadableDatabase(),
                null, UserEntry.COLUMN_NAME_USERNAME + "=?",
                new String[]{username}, null, null, null);
    }

    /**
     * Find credentials regarding the userId in the database.
     *
     * @param userId Id of the User.
     * @return Cursor object
     */
    public Cursor findCredentialsByUserId(final long userId) {
        Log.d(TAG, "findCredentialsByUserId: " + userId);

        final SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(CredentialsEntry.TABLE_NAME);
        return queryBuilder.query(getReadableDatabase(),
                null, CredentialsEntry._ID + "=?",
                new String[]{Long.toString(userId)}, null, null, null);
    }

    /**
     * Delete a given offer
     *
     * @param offer that shall be deleted
     * @return the affected rows
     */
    public int deleteOffer(final Offer offer) {
        Log.d(TAG, "Delete offer: " + offer.getId());

        final String whereClause = OfferEntry._ID + "=?";
        final String[] whereArgs = new String[]{String.valueOf(offer.getId())};
        final ContentValues values = getOfferValues(offer);
        return getWritableDatabase().delete(OfferEntry.TABLE_NAME, whereClause, whereArgs);
    }

    /**
     * Insert a rental into the database
     *
     * @param rental that shall be inserted
     * @param db     the SQLiteDatabase
     * @return the affected rows
     */
    public long insertRental(final Rental rental, final SQLiteDatabase db) {
        Log.d(TAG, "Insert rental: " + rental.getOffer());

        final ContentValues values = getRentalValues(rental);
        return db.insert(RentalEntry.TABLE_NAME, null, values);
    }

    /**
     * Get the values of an offer
     *
     * @param offer object which values are requested
     * @return the values of the requested offer
     */
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
        values.put(OfferEntry.COLUMN_NAME_LENDER_FK, offer.getLender());

        return values;
    }

    /**
     * Get the values of an user
     *
     * @param user object which values are requested
     * @return the values of the requested object.
     */
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

    /**
     * Get the ContentValues of a Credentials object.
     *
     * @param credentials object which values are requested
     * @return values of the requested object
     */
    private ContentValues getCredentialValues(final Credentials credentials) {
        final ContentValues values = new ContentValues();
        values.put(CredentialsEntry.COLUMN_NAME_PASSWORD, credentials.getPassword());
        values.put(CredentialsEntry.COLUMN_NAME_USER_FK, credentials.getUserId());

        return values;
    }

    /**
     * Get the values of a rental
     *
     * @param rental object which values are requested
     * @return values of the requested object
     */
    private ContentValues getRentalValues(final Rental rental) {
        final ContentValues values = new ContentValues();
        values.put(RentalEntry.COLUMN_NAME_FROM, rental.getRentFrom());
        values.put(RentalEntry.COLUMN_NAME_TO, rental.getRentTo());
        values.put(RentalEntry.COLUMN_NAME_STATUS, rental.getStatus());
        values.put(RentalEntry.COLUMN_NAME_OFFER_FK, rental.getOffer());
        values.put(RentalEntry.COLUMN_NAME_LENDER_FK, rental.getLender());
        values.put(RentalEntry.COLUMN_NAME_HIRER_FK, rental.getHirer());

        return values;
    }


    /**
     * Create tables and inserts test data
     *
     * @param db the SQLiteDatabase
     */
    @Override
    public void onCreate(final SQLiteDatabase db) {
        Log.d(TAG, ": onCreate Database with version " + db.getVersion());

        db.execSQL(SQL_CREATE_TABLE_USER);
        db.execSQL(SQL_CREATE_TABLE_CREDENTIALS);
        db.execSQL(SQL_CREATE_TABLE_BALANCE);
        db.execSQL(SQL_CREATE_TABLE_OFFER);
        db.execSQL(SQL_CREATE_TABLE_RENTAL);

        User user1 = new User();
        user1.setUsername("user1");
        user1.setEmail("user1@ebusiness.de");
        user1.setStreet("21 Pennsylvania Rd.");
        user1.setZipCode("76133");
        user1.setCountry("Germany");
        user1.setDescription("Dummy user. Has a lot of tools.");
        this.insertUser(user1, db);

        Credentials credentials1 = new Credentials();
        credentials1.setPassword("test");
        credentials1.setUserId(user1.getId());
        this.insertCredentials(credentials1, db);

        User user2 = new User();
        user2.setUsername("user2");
        user2.setEmail("user2@ebusiness.de");
        user2.setStreet("98 Mountain Highway");
        user2.setZipCode("76134");
        user2.setCountry("Germany");
        user2.setDescription("Comes from another city.");
        this.insertUser(user2, db);

        Credentials credentials2 = new Credentials();
        credentials2.setPassword("test");
        credentials2.setUserId(user2.getId());
        this.insertCredentials(credentials2, db);

        Offer offer1 = new Offer();
        offer1.setName("Hammer");
        offer1.setImage(null);
        offer1.setDescription("This is a really nice hammer. Watch your fingers!");
        offer1.setPrice(2);
        offer1.setZipCode("76133");
        offer1.setValidFrom(DateTime.now().minusDays(4).getMillis());
        offer1.setValidTo(DateTime.now().plusDays(3).getMillis());
        offer1.setLender(user1.getId());
        this.insertOffer(offer1, db);

        Offer offer = new Offer();
        offer.setName("Another Hammer");
        offer.setImage(null);
        offer.setDescription("This is another hammer.");
        offer.setPrice(4);
        offer.setZipCode("76133");
        offer.setValidFrom(DateTime.now().minusDays(2).getMillis());
        offer.setValidTo(DateTime.now().getMillis());
        offer.setLender(user1.getId());
        this.insertOffer(offer, db);

        Offer offer2 = new Offer();
        offer2.setImage(null);
        offer2.setName("Screwy Screwdriver");
        offer2.setPrice(4);
        offer2.setDescription("Only for motivated homeworkers!");
        offer2.setZipCode("76133");
        offer2.setValidFrom(DateTime.now().minusDays(1).getMillis());
        offer2.setValidTo(DateTime.now().plusDays(4).getMillis());
        offer2.setLender(user1.getId());
        this.insertOffer(offer2, db);

        Offer offer3 = new Offer();
        offer3.setImage(null);
        offer3.setName("A saw");
        offer3.setPrice(10);
        offer3.setDescription("For trees");
        offer3.setZipCode("76133");
        offer3.setValidFrom(DateTime.now().minusDays(6).getMillis());
        offer3.setValidTo(DateTime.now().plusDays(10).getMillis());
        offer3.setLender(user1.getId());
        this.insertOffer(offer3, db);
    }

    /**
     * Define actions to execute on database upgrade
     *
     * @param db         the SQLiteDatabase
     * @param oldVersion number of the db
     * @param newVersion number of the db
     */
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

    /**
     * Define actions to execute on database downgrade
     *
     * @param db         the SQLiteDatabase
     * @param oldVersion number of the db
     * @param newVersion number of the db
     */
    @Override
    public void onDowngrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        Log.d(TAG, ": onDowngrade database from version" + newVersion + " to" + oldVersion);

        onUpgrade(db, oldVersion, newVersion);
    }

}

