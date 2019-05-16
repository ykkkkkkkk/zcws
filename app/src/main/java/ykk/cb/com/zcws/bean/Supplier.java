package ykk.cb.com.zcws.bean;

import java.io.Serializable;

/**
 * @Description:供应商
 *
 * @author qxp 2019年3月7日 上午11:44:25
 */
public class Supplier implements Serializable {
	/* id */
	private int id;
	/* 供应商K3id */
	private int supplierId;
	/* 供应商编码 */
	private String FNumber;
	/* 供应商名称 */
	private String FName;
	/* 供应商来源账套Id */
	private int sourceAcctId;
	/* 供应商来源账套名称 */
	private String sourceAcctName;
	/* 供应商对应账套id */
	private int correspondId;
	/* 供应商对应账套名称 */
	private String correspondName;

	/* 创建时间 */
	private String createDate;
	/* 修改时间 */
	private String fModifyDate;

	public Supplier() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(int supplierId) {
		this.supplierId = supplierId;
	}

	public String getFNumber() {
		return FNumber;
	}

	public void setFNumber(String fNumber) {
		FNumber = fNumber;
	}

	public String getFName() {
		return FName;
	}

	public void setFName(String fName) {
		FName = fName;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getfModifyDate() {
		return fModifyDate;
	}

	public void setfModifyDate(String fModifyDate) {
		this.fModifyDate = fModifyDate;
	}

	public int getSourceAcctId() {
		return sourceAcctId;
	}

	public void setSourceAcctId(int sourceAcctId) {
		this.sourceAcctId = sourceAcctId;
	}

	public String getSourceAcctName() {
		return sourceAcctName;
	}

	public void setSourceAcctName(String sourceAcctName) {
		this.sourceAcctName = sourceAcctName;
	}

	public int getCorrespondId() {
		return correspondId;
	}

	public void setCorrespondId(int correspondId) {
		this.correspondId = correspondId;
	}

	public String getCorrespondName() {
		return correspondName;
	}

	public void setCorrespondName(String correspondName) {
		this.correspondName = correspondName;
	}

	@Override
	public String toString() {
		return "Supplier [id=" + id + ", supplierId=" + supplierId + ", FNumber=" + FNumber + ", FName=" + FName
				+ ", sourceAcctId=" + sourceAcctId + ", sourceAcctName=" + sourceAcctName + ", correspondId="
				+ correspondId + ", correspondName=" + correspondName + ", createDate=" + createDate + ", fModifyDate="
				+ fModifyDate + "]";
	}

}
