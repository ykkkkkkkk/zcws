package ykk.cb.com.zcws.bean;

import java.io.Serializable;

/**
 * 库位表stock_position
 */
public class StockPosition implements Serializable {
    //库位Id
    private int fspId;
    //仓位编码
    private String fnumber;
    //仓位名称
    private String fname;
    //仓位详细名称
    private String ffullName;
    // 仓位组Id
    private int fspGroupId;


    public StockPosition() {
        super();
    }


    public int getFspId() {
        return fspId;
    }


    public String getFnumber() {
        return fnumber;
    }


    public String getFname() {
        return fname;
    }


    public String getFfullName() {
        return ffullName;
    }


    public int getFspGroupId() {
        return fspGroupId;
    }


    public void setFspId(int fspId) {
        this.fspId = fspId;
    }


    public void setFnumber(String fnumber) {
        this.fnumber = fnumber;
    }


    public void setFname(String fname) {
        this.fname = fname;
    }


    public void setFfullName(String ffullName) {
        this.ffullName = ffullName;
    }


    public void setFspGroupId(int fspGroupId) {
        this.fspGroupId = fspGroupId;
    }


    @Override
    public String toString() {
        return "StockPosition [fspId=" + fspId + ", fnumber=" + fnumber + ", fname=" + fname + ", ffullName="
                + ffullName + ", fspGroupId=" + fspGroupId + "]";
    }


}
