package com.seunghyun.randomseats;

class SendDialogFIxEvent {
    int position, number;
    boolean isFixSeat;

    SendDialogFIxEvent(int position, int number, boolean isFixSeat) {
        this.position = position;
        this.number = number;
        this.isFixSeat = isFixSeat;
    }
}
