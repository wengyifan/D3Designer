using System;
using @namespace@.Entity;

namespace @namespace@.Response
{
	[Serializable]
	public class @entity@Response {

		public String returnCode;
		public String errorMsg;
		public @entity@ data;

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

		public @entity@ getData() {
			return data;
		}
		public void setData(@entity@ data) {
			this.data = data;
		}
	
	}
	
}
