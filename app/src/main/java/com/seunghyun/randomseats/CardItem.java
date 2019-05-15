package com.seunghyun.randomseats;

import android.widget.GridLayout;

class CardItem {
    private GridLayout gridLayout;
    private String titleText, descriptionText, dateText;

    CardItem(GridLayout gridLayout, String titleTV, String descriptionTV, String dateTV) {
        this.gridLayout = gridLayout;
        this.titleText = titleTV;
        this.descriptionText = descriptionTV;
        this.dateText = dateTV;
    }

    GridLayout getGridLayout() {
        return gridLayout;
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
