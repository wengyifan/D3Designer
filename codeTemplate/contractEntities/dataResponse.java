package @package@.apiContract;

import @package@.*;

@imports@

public class @operation@Response {

	private String returnCode;
	private String errorMsg;
	private @datatype@ data;
	

	public @datatype@ getData() {
		return data;
	}
	public void setData(@datatype@ data) {
		this.data = data;
	}
	
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
	
}
