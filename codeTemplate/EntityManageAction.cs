using System;
using System.Collections.Generic;
using System.Text;
using System.Web;
using Newtonsoft.Json;
using @project@.Common;
using @project@.Response;
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

		private static HashSet<string> hashParaKey = new HashSet<string> {@Search@};

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
		
		public string Find(Dictionary<string, UrlParaItem> para)
		{
			string response;
			var result = new @entity@Response();
			try
			{
				var v = new @entity@();
				v.id = TypeConvert.ValueOfInt32(RequestResolve.GetValue(para, "id"));
				var entity = @entity@.Find(v);
				if (entity!=null)
				{
					result.setData(entity);
				}
				result.setReturnCode("success");

				response = JsonConvert.SerializeObject(result, Formatting.Indented);

				string callback = RequestResolve.GetValue(para, "callback");
				if (!string.IsNullOrEmpty(callback))
				{
					response = string.Format("{0}({1})", callback, response);
				}

				return response;

			}
			catch (Exception e)
			{
				result.setReturnCode("Error");
				result.setErrorMsg("Error: " + e.Message);

				response = JsonConvert.SerializeObject(result, Formatting.Indented);
				return response;
			}

		}

		public string Save(Dictionary<string, UrlParaItem> para)
		{
			string response;
			var result = new AjaxBaseResponse();
			try
			{
				@entity@ entity = null;
				bool bfind = false;

				var v = new @entity@();
				v.id = TypeConvert.ValueOfInt32(RequestResolve.GetValue(para, "id"));
				if (v.id > 0)
				{
					entity = @entity@.Find(v);
					if (entity != null)
					{
						bfind = true;
					}
					else
					{
						entity = new @entity@();
						bfind = false;
					}
				}
				else
				{
					entity = new @entity@();
					bfind = false;
				}

@setValue@

				if (bfind)
				{
					@entity@.Update(entity);
				}
				else
				{
					@entity@.Insert(entity);
				}

				result.setReturnCode("success");
				result.setData(entity.id.ToString());

				response = JsonConvert.SerializeObject(result, Formatting.Indented);

				string callback = RequestResolve.GetValue(para, "callback");
				if (!string.IsNullOrEmpty(callback))
				{
					response = string.Format("{0}({1})", callback, response);
				}
				return response;
			}
			catch (Exception e)
			{
				result.setReturnCode("Error");
				result.setErrorMsg("Error: " + e.Message);

				response = JsonConvert.SerializeObject(result, Formatting.Indented);
				return response;
			}
		}

		public string Delete(Dictionary<string, UrlParaItem> para)
		{
			string response;
			var result = new @entity@Response();
			try
			{
				var v = new @entity@();
				v.id = TypeConvert.ValueOfInt32(RequestResolve.GetValue(para, "id"));
				@entity@.Delete(v);

				result.setReturnCode("success");

				response = JsonConvert.SerializeObject(result, Formatting.Indented);

				string callback = RequestResolve.GetValue(para, "callback");
				if (!string.IsNullOrEmpty(callback))
				{
					response = string.Format("{0}({1})", callback, response);
				}
				return response;
			}
			catch (Exception e)
			{
				result.setReturnCode("Error");
				result.setErrorMsg("Error: " + e.Message);

				response = JsonConvert.SerializeObject(result, Formatting.Indented);
				return response;
			}
		}

		public string FindList(Dictionary<string, UrlParaItem> para)
		{
			string response;
			var result = new List@entity@Response();
			try
			{
				StringBuilder condition = new StringBuilder();
				string currentPage = RequestResolve.GetValue(para, "page");
				string pageSize = RequestResolve.GetValue(para, "pagesize");
				string orderBy = RequestResolve.GetValue(para, "orderby");
				string isdsc = RequestResolve.GetValue(para, "isdesc");
				string callback = RequestResolve.GetValue(para, "callback");

				if (!string.IsNullOrEmpty(orderBy))
				{
					condition.Append(string.Format("ORDER BY {0} {1} ", orderBy,
						!string.IsNullOrEmpty(isdsc) ? "DESC" : "ASC"));
				}

				int pageSizeInt = 1000;
				int offsetInt = 0;
				if (!string.IsNullOrEmpty(pageSize) && !string.IsNullOrEmpty(currentPage))
				{
					pageSizeInt = TypeConvert.ValueOfInt32(pageSize);
					int currentPageInt = TypeConvert.ValueOfInt32(currentPage);
					offsetInt = pageSizeInt * ((currentPageInt > 0 ? currentPageInt : 1) - 1);
				}

				condition.Append(RequestResolve.GetCondition(para,hashParaKey));
				var count = @entity@.Count(condition.ToString());

				condition.Append(string.Format(" LIMIT {0} OFFSET {1} ", pageSizeInt, offsetInt));

				var entityList = @entity@.FindList(condition.ToString());
				if (entityList!=null)
				{
					result.setData(entityList);
				}
				result.setReturnCode("success");
				result.setTotleNum(count);

				response = JsonConvert.SerializeObject(result, Formatting.Indented);

				if (!string.IsNullOrEmpty(callback))
				{
					response = string.Format("{0}({1})", callback, response);
				}

				return response;
			}
			catch (Exception e)
			{
				result.setReturnCode("Error");
				result.setErrorMsg("Error: " + e.Message);

				response = JsonConvert.SerializeObject(result, Formatting.Indented);
				return response;
			}
		}

	}
}