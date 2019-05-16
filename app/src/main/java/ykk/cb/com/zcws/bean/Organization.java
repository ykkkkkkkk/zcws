package ykk.cb.com.zcws.bean;

import java.io.Serializable;

/**
 * @Description:生产账套客户实体
 *
 * @author qxp 2019年3月5日 上午11:22:50
 */
public class Organization implements Serializable {

	private int fitemId;

	private String fName;

	private String fNumber;

	public Organization() {
		super();
	}

	public int getFitemId() {
		return fitemId;
	}

	public String getfName() {
		return fName;
	}

	public String getfNumber() {
		return fNumber;
	}

	public void setFitemId(int fitemId) {
		this.fitemId = fitemId;
	}

	public void setfName(String fName) {
		this.fName = fName;
	}

	public void setfNumber(String fNumber) {
		this.fNumber = fNumber;
	}

	@Override
	public String toString() {
		return "Organization [fitemId=" + fitemId + ", fName=" + fName + ", fNumber=" + fNumber + "]";
	}


}