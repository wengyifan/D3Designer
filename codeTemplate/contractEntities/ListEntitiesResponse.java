package @package@.apiContract;

import @package@.*;

@imports@


import java.util.List;

public class List@targetEntity@Response {

	private String returnCode;
	private String errorMsg;
	private int totleNum;
	private List<@targetEntity@> data;
	
	public String getReturnCode() {
		return returnCode;
	}
	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	
	public List<@targetEntity@> getData() {
		return data;
	}
	public void setData(List<@targetEntity@> data) {
		this.data = data;
	}

	public int getTotleNum() {
		return totleNum;
	}
	public void setTotleNum(int totleNum) {
		this.totleNum = totleNum;
	}
	
	
}
