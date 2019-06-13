package com.seunghyun.randomseats.recyclerview;

public class CardItem {
    private String titleText, descriptionText, dateText;

    public CardItem(String titleTV, String descriptionTV, String dateTV) {
        this.titleText = titleTV;
        this.descriptionText = descriptionTV;
        this.dateText = dateTV;
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
