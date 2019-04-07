package com.seunghyun.randomseats;

class SendDialogDataEvent {
    int position;
    boolean isNotUseSeat;

    SendDialogDataEvent(int position, boolean isUseSeat) {
        this.position = position;
        this.isNotUseSeat = isUseSeat;
    }
}
