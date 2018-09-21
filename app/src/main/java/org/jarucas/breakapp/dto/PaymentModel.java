package org.jarucas.breakapp.dto;

/**
 * Created by Javier on 26/08/2018.
 */

public class PaymentModel {

    private String cardNumber;
    private String nickName;
    private String expireDate;
    private String cvv;
    private String type;

    public PaymentModel(String cardNumber, String nickName, String expireDate, String cvv, String type) {
        this.cardNumber = cardNumber;
        this.nickName = nickName;
        this.expireDate = expireDate;
        this.cvv = cvv;
        this.type = type;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
