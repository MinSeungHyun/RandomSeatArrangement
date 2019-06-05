package com.seunghyun.randomseatarrangement;

class CardItem {
    private String titleText, descriptionText, dateText;

    CardItem(String titleTV, String descriptionTV, String dateTV) {
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
