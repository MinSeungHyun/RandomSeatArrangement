package com.seunghyun.randomseats;

import android.support.v7.widget.CardView;
import android.widget.GridLayout;
import android.widget.TextView;

class CardItem {
    private CardView cardView;
    private GridLayout gridLayout;
    private TextView titleTV, descriptionTV, dateTV;

    public CardItem(CardView cardView, GridLayout gridLayout, TextView titleTV, TextView descriptionTV, TextView dateTV) {
        this.cardView = cardView;
        this.gridLayout = gridLayout;
        this.titleTV = titleTV;
        this.descriptionTV = descriptionTV;
        this.dateTV = dateTV;
    }

    public CardView getCardView() {
        return cardView;
    }

    public GridLayout getGridLayout() {
        return gridLayout;
    }

    public TextView getTitleTV() {
        return titleTV;
    }

    public TextView getDescriptionTV() {
        return descriptionTV;
    }

    public TextView getDateTV() {
        return dateTV;
    }
}
