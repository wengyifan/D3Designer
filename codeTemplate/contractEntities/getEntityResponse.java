package @package@.apiContract;

import @package@.*;

@imports@

public class Get@targetEntity@Response {

	private String returnCode;
	private String errorMsg;
	private @targetEntity@ data;
	
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
	
	public @targetEntity@ getData() {
		return data;
	}
	public void setData(@targetEntity@ data) {
		this.data = data;
	}
	
	
	
}
