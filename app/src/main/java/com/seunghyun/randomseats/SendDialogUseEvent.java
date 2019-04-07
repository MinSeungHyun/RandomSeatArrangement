package com.seunghyun.randomseats;

class SendDialogUseEvent {
    int position;
    boolean isNotUseSeat;

    SendDialogUseEvent(int position, boolean isUseSeat) {
        this.position = position;
        this.isNotUseSeat = isUseSeat;
    }
}
