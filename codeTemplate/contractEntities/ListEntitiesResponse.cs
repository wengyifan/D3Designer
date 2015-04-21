using System;
using System.Collections.Generic;
using @namespace@.Entity;

namespace @namespace@.Response
{
	[Serializable]
	public class List@entity@Response {

		public String returnCode;
		public String errorMsg;
		public int totleNum;
		public IList<@entity@> data;

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

		public IList<@entity@> getData() {
			return data;
		}
		public void setData(IList<@entity@> data) {
			this.data = data;
		}

		public int getTotleNum() {
			return totleNum;
		}
		public void setTotleNum(int totleNum) {
			this.totleNum = totleNum;
		}
	
	}
}
