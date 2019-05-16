package com.seunghyun.randomseats;

import android.graphics.Bitmap;

class CardItem {
    private Bitmap gridImage;
    private String titleText, descriptionText, dateText;

    CardItem(Bitmap gridImage, String titleTV, String descriptionTV, String dateTV) {
        this.gridImage = gridImage;
        this.titleText = titleTV;
        this.descriptionText = descriptionTV;
        this.dateText = dateTV;
    }

    Bitmap getGridImage() {
        return gridImage;
    }

    String getTitleText() {
        return titleText;
    }

    String getDescriptionText() {
        return descriptionText;
    }

    String getDateText() {
        return dateText;
    }
}
