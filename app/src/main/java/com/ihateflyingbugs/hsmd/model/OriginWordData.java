package com.ihateflyingbugs.hsmd.model;

import com.ihateflyingbugs.hsmd.OfflineLesson.OLData.OriginWord;

import java.util.List;

/**
 * Created by 영철 on 2016-05-23.
 */
public class OriginWordData {
    int result;
    String status;

    List<OriginWord> originwordlist;
     private String img_address;



    public OriginWordData(){

    }
    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<OriginWord> getOriginwordlist() {
        return originwordlist;
    }

    public void setOriginwordlist(List<OriginWord> originwordlist) {
        this.originwordlist = originwordlist;
    }

    public String getImg_address() {
        return img_address;
    }

    public void setImg_address(String img_address) {
        this.img_address = img_address;
    }

}
