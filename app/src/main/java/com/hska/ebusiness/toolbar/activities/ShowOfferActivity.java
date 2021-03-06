package com.hska.ebusiness.toolbar.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hska.ebusiness.toolbar.R;
import com.hska.ebusiness.toolbar.dao.ToolbarDBHelper;
import com.hska.ebusiness.toolbar.model.Offer;
import com.hska.ebusiness.toolbar.model.Rental;
import com.hska.ebusiness.toolbar.model.User;
import com.hska.ebusiness.toolbar.util.RentalMapper;
import com.hska.ebusiness.toolbar.util.ToolbarApplication;

import org.joda.time.DateTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import sun.bob.mcalendarview.MCalendarView;

import static com.hska.ebusiness.toolbar.util.ToolbarConstants.TOOLBAR_OFFER;
import static com.hska.ebusiness.toolbar.util.ToolbarConstants.TOOLBAR_OFFER_IS_EDIT_MODE;

public class ShowOfferActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();

    private Offer offer;
    private User user;

    private ImageView offerImage;
    private TextView offerName;
    private TextView offerDescription;
    private TextView offerFrom;
    private TextView offerTo;
    private TextView offerZipCode;
    private TextView offerPrice;

    private AlertDialog.Builder dialogBuilder;

    /**
     * Used to initialize the layout and field of the Activity
     *
     * @param savedInstanceState to restore saved instance state
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_offer);
        offer = getIntent().getParcelableExtra(TOOLBAR_OFFER);

        Log.d(TAG, " show offer with ID: " + offer.getId());

        user = ((ToolbarApplication) getApplication()).getCurrentUser();
        dialogBuilder = new AlertDialog.Builder(this);

        offerImage = (ImageView) findViewById(R.id.show_image_offer_image);
        offerName = (TextView) findViewById(R.id.label_offer_name);
        offerDescription = (TextView) findViewById(R.id.show_input_offer_description);
        offerFrom = (TextView) findViewById(R.id.show_input_offer_from);
        offerTo = (TextView) findViewById(R.id.show_input_offer_to);
        offerZipCode = (TextView) findViewById(R.id.show_input_zip_code);
        offerPrice = (TextView) findViewById(R.id.show_input_offer_price);

        initContent();
    }

    /**
     * Initialize content if edit mode is activated.
     * Pre-fill fields with order values.
     * Create placeholder for image if there is none.
     */
    private void initContent() {
        Log.d(TAG, "Init content");

        if (offer.getName() != null)
            offerName.setText(getString(R.string.label_offer_name, offer.getName()));
        if (offer.getPrice() > 0)
            offerPrice.setText(getString(R.string.label_offer_price_long, offer.getPrice()));
        if (offer.getDescription() != null)
            offerDescription.setText(offer.getDescription());
        if (offer.getZipCode() != null)
            offerZipCode.setText(getString(R.string.label_offer_zip, offer.getZipCode()));
        if (!new DateTime(0).equals(new DateTime(offer.getValidFrom())))
            offerFrom.setText(getString(R.string.label_offer_from, new DateTime(offer.getValidFrom()).toLocalDate().toString()));
        if (!new DateTime(0).equals(new DateTime(offer.getValidTo())))
            offerTo.setText(getString(R.string.label_offer_to, new DateTime(offer.getValidTo()).toLocalDate().toString()));

        if (offer.getImage() != null) {
            final Uri image = Uri.parse(offer.getImage());
            if (image != null) {
                try {
                    offerImage.setImageBitmap(this.resizeImage(image));
                } catch (final IOException e) {
                    // Exception is ok, since file URIs are not being cut by Android.
                    Log.d(TAG, "Error while initializing image: " + e.getMessage());
                }
            }
        }

        MCalendarView calendarView = ((MCalendarView) findViewById(R.id.calendar));

        Log.d(TAG, "Vor Schleife");
        if (getRentals(offer.getId()) != null) {
            Log.d(TAG, "bin da");
            for (Rental rental : getRentals(offer.getId())) {
                if (rental.getStatus() == 1) {
                    Log.d(TAG, "Immer noch");
                    DateTime fromDate = new DateTime(rental.getRentFrom());
                    DateTime toDate = new DateTime(rental.getRentTo());
                    List<DateTime> dateTimes = getRentalDays(fromDate, toDate);
                    Log.d(TAG, "times: " + dateTimes);
                    for (DateTime date : getRentalDays(fromDate, toDate)) {
                        if (calendarView != null)
                            calendarView.markDate(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth());
                    }
                } else
                    continue;
            }
        }
    }

    /**
     * Get all rental Days from all rentals to mark them in the calendar
     *
     * @param fromDate the begin of timespan
     * @param toDate   the end of timespan
     * @return the list of rentals in the timespan
     */
    public List<DateTime> getRentalDays(final DateTime fromDate, final DateTime toDate) {
        final List<DateTime> daysList = new ArrayList<>();

        for (DateTime date = fromDate; !date.isAfter(toDate); date = date.plusDays(1)) {
            daysList.add(date);
        }
        return daysList;
    }


    /**
     * Gets called when item from menu gets selected
     *
     * @param item the selected menu item
     * @return true if event was handled successfully
     */
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.offer_show_edit:
                if (user.getId() == offer.getLender()) {
                    final Intent intentEdit = new Intent(this, EditOfferActivity.class);
                    intentEdit.putExtra(TOOLBAR_OFFER_IS_EDIT_MODE, true);
                    intentEdit.putExtra(TOOLBAR_OFFER, offer);
                    startActivity(intentEdit);
                } else
                    Toast.makeText(ShowOfferActivity.this, "No permissions to edit!", Toast.LENGTH_LONG).show();
                return true;

            case R.id.offer_show_delete:
                if (user.getId() == offer.getLender()) {
                    dialogBuilder.setTitle("Delete offer")
                            .setMessage("Delete this offer?");
                    dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            ToolbarDBHelper.getInstance(ShowOfferActivity.this).deleteOffer(offer);
                            final Intent intentMain = new Intent(ShowOfferActivity.this, MainActivity.class);
                            startActivity(intentMain);
                        }
                    });
                    dialogBuilder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    dialogBuilder.setIcon(R.drawable.ic_delete_black_24dp);
                    dialogBuilder.create();
                    dialogBuilder.show();
                } else
                    Toast.makeText(ShowOfferActivity.this, "No permissions to delete!", Toast.LENGTH_LONG).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        if (user.getId() == offer.getLender()) {
            getMenuInflater().inflate(R.menu.menu_action_show_offer, menu);
        }
        return true;
    }

    /**
     * Returns a list with all rentals of an offer
     *
     * @param id the offer id
     * @return the list of rentals
     */
    public ArrayList<Rental> getRentals(final long id) {
        final Cursor cursor = ToolbarDBHelper.getInstance(this).findAllRentalsToOffer(id);
        if (cursor != null && cursor.moveToFirst()) {
            final ArrayList<Rental> rentalList = new ArrayList<>();
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                final Rental rental = RentalMapper.map(cursor);
                rentalList.add(rental);
            }
            cursor.close();
            return rentalList;
        }
        if (cursor != null)
            cursor.close();
        return null;
    }

    /**
     * Resizes image to fit in ImageView
     *
     * @param file Uri to the image path
     * @return the resized bitmap
     * @throws IOException if access to external storage fails
     */
    private Bitmap resizeImage(final Uri file) throws IOException {
        final Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), file);

        final float width = bitmap.getWidth();
        final float height = bitmap.getHeight();

        final int aimedHeight = 1024;
        final int aimedWidth = (int) (width / height * (float) aimedHeight);

        return Bitmap.createScaledBitmap(bitmap, aimedWidth, aimedHeight, false);
    }

}
