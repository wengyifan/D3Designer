using System;
using System.Collections.Generic;
using System.Text;
using System.Web;
using Newtonsoft.Json;
using @project@.Common;
using @namespace@.Entity;
using @namespace@.Response;

namespace @namespace@.Control
{
	/// <summary>
	/// Control的摘要说明
	/// </summary>
	public class @entity@Control : IHttpHandler
	{
		/// <summary>
		/// 获得类型
		/// </summary>
		private static Type _TypeInstance = typeof(@entity@Control);
		/// <summary>
		/// 创建实例
		/// </summary>
		private static object _ObjInstance = Activator.CreateInstance(_TypeInstance);
		/// <summary>
		/// 反射方法的Key
		/// </summary>
		private const string KeyAction = "action";

		private static HashSet<string> hashParaKey = new HashSet<string> {};

		public void ProcessRequest(HttpContext context)
		{
			string json = string.Empty;

			var dict = RequestResolve.GetParas(context.Request.Url.Query);
			string strAction = RequestResolve.GetValue(dict, KeyAction);
			if (!string.IsNullOrEmpty(strAction))
			{
				var method = _TypeInstance.GetMethod(strAction);
				if (method != null)
				{
					json = method.Invoke(_ObjInstance, new object[] { dict }).ToString();
				}
			}

			context.Response.Clear();
			context.Response.ContentEncoding = Encoding.UTF8;
			context.Response.ContentType = "application/json";
			context.Response.Write(json);
			context.Response.End();
		}

		public bool IsReusable
		{
			get
			{
				return false;
			}
		}


@targetEntity@

	}
}