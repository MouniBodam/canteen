package com.example.canteen.model;


import java.sql.Timestamp;
import java.time.LocalDateTime;


public class Canteen {
    private  Long tokenId;
    private String itemName;
    private long price;
    private String issue;
    private String createdAt;
    

public Canteen() {}

public Canteen(Long tokenId, String itemName, long price, String issue) {
    Timestamp currentTimestamp = Timestamp.valueOf(LocalDateTime.now());
    //Timestamp currentTimestamp = Timer.valueOf(LocalDateTime.now());
    this.tokenId = tokenId;
    this.itemName = itemName;
    this.price = price;
    this.issue = issue;
    this.createdAt = currentTimestamp.toString();
}

public Long getTokenId() {
    return tokenId;
}

public void setTokenId(Long tokenId) {
    this.tokenId = tokenId;
}

public String getItemName() {
    return itemName;
}
public void setItemName(String itemName) {
    this.itemName = itemName;
}
public long getPrice() {
    return price;
}
public void setPrice(long price) {
    this.price = price;
}
public String getIssue() {
    return issue;
}
public void setIssue(String issue) {
    this.issue = issue;
}
public String getCreatedAt() {
    return createdAt;
}
public void setCreatedAt(String createdAt) {
    this.createdAt = createdAt;
}
}

