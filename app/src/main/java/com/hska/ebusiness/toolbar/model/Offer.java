package com.hska.ebusiness.toolbar.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.hska.ebusiness.toolbar.util.ToolbarConstants;

import org.joda.time.DateTime;

public class Offer implements Parcelable {

    public Offer() {
    }

    public Offer(final String name, final String description, final String zipCode,
                 final long price, final long validFrom, final long validTo) {
        this.name = name;
        this.image = null;
        this.description = description;
        this.zipCode = zipCode;
        this.price = price;
        this.validFrom = validFrom;
        this.validTo = validTo;
    }

    private long id;
    private String name;
    private String image;
    private String description;
    private String zipCode;
    private long price;
    private long validFrom;
    private long validTo;

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(final String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(final String zipCode) {
        this.zipCode = zipCode;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(final long price) {
        this.price = price;
    }

    public long getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(final long validFrom) {
        this.validFrom = validFrom;
    }

    public long getValidTo() {
        return validTo;
    }

    public void setValidTo(final long validTo) {
        this.validTo = validTo;
    }

    @Override
    public String toString() {
        return "Offer{" +
                "id=" + id +
                ", name=" + name +
                ", image=" + image +
                ", description='" + description + '\'' +
                ", zipCode='" + zipCode + '\'' +
                ", price=" + price +
                ", validFrom=" + new DateTime(validFrom).toString(ToolbarConstants.TOOLBAR_DATE_FORMAT) +
                ", validTo=" + new DateTime(validTo).toString(ToolbarConstants.TOOLBAR_DATE_FORMAT) +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel destination, final int flags) {
        destination.writeString(name);
        destination.writeString(image);
        destination.writeString(description);
        destination.writeString(zipCode);
        destination.writeLong(price);
        destination.writeLong(validFrom);
        destination.writeLong(validTo);
    }

    public static final Parcelable.Creator<Offer> CREATOR = new Creator<Offer>() {
        @Override
        public Offer createFromParcel(final Parcel parcel) {
            return new Offer(parcel);
        }

        @Override
        public Offer[] newArray(final int size) {
            return new Offer[size];
        }
    };

    private Offer(final Parcel source) {
        name = source.readString();
        image = source.readString();
        description = source.readString();
        zipCode = source.readString();
        price = source.readLong();
        validFrom = source.readLong();
        validTo = source.readLong();
    }
}