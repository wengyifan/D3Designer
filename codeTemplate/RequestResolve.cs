using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Text.RegularExpressions;
using System.Web;


namespace @namespace@.Common
{
	public class UrlParaItem
	{
		public UrlParaItem()
		{
			_Relation = null;
			_Operator = null;
		}

		public UrlParaItem(string op)
		{
			_Operator = op;
			_Relation = null;
		}
		public UrlParaItem(string op, string rl)
		{
			_Relation = rl;
			_Operator = op;
		}

		/// <summary>
		/// 参数值
		/// </summary>
		public string Value;
		/// <summary>
		/// 字段键值
		/// </summary>
		public string Key;


		private string _Operator;

		/// <summary>
		/// 操作: 等于 不等于 大于 小于
		/// </summary>
		public string Operator
		{
			get
			{
				return _Operator ?? "=";
			}

		}

		private string _Relation;
		/// <summary>
		/// 关系: AND,OR
		/// </summary>
		public string Relation
		{
			get
			{
				return _Relation ?? "AND";
			}
		}
	}

	public static class RequestResolve
	{

		private static HashSet<string> hashKey = new HashSet<string>{ "id", "user", "passwd", "token", "action", "page", "pagesize", "orderby", "isdesc", "callback" };
		public static Dictionary<string, UrlParaItem> GetParas(string urlPara)
		{
			var dict = new Dictionary<string, UrlParaItem>();

			// 开始分析参数对
			Regex re = new Regex(@"(^|&)?(\w+)=([^&]+)(&|$)?", RegexOptions.Compiled);
			//解码
			urlPara = HttpUtility.UrlDecode(urlPara);
			//结果集
			MatchCollection mc = re.Matches(urlPara ?? string.Empty);

			foreach (Match m in mc)
			{
				string strKey = m.Result("$2");
				string strValue = m.Result("$3");
				UrlParaItem urlPI;
				if (strValue.Contains(","))
				{
					var vC = strValue.Split(',');
					if (vC.Count() == 2)
					{
						if (string.IsNullOrEmpty(vC[0].Trim()))
						{
							urlPI = new UrlParaItem("<=")
							{
								Key = strKey,
								Value = string.Format(" '{0}' ", vC[1].Trim())
							};
						}
						else if (string.IsNullOrEmpty(vC[1].Trim()))
						{

							urlPI = new UrlParaItem(">=")
							{
								Key = strKey,
								Value = string.Format(" '{0}' ", vC[0].Trim())
							};
						}
						else
						{
							urlPI = new UrlParaItem("BETWEEN")
							{
								Key = strKey,
								Value = string.Format(" {0} AND {1} ", vC[0].Trim(), vC[1].Trim())
							};
						}
					}
					else
					{
						urlPI = new UrlParaItem { Key = strKey, Value = string.Format("'{0}'", strValue.Replace(",", "")) };
					}
				}
				else if (strValue.Contains("%"))
				{
					urlPI = new UrlParaItem("LIKE")
					{
						Key = strKey,
						Value = string.Format("'{0}'", strValue)
					};
				}
				else
				{
					urlPI = new UrlParaItem { Key = strKey };
					if (hashKey.Contains(urlPI.Key.ToLower()))
					{
						urlPI.Key = urlPI.Key.ToLower();
						urlPI.Value = string.Format("{0}", strValue);
					}
					else
					{
						urlPI.Value = string.Format("'{0}'", strValue);
					}
				}

				if (dict.ContainsKey(urlPI.Key))
				{
					dict[urlPI.Key] = urlPI;
				}
				else
				{
					dict.Add(urlPI.Key, urlPI);
				}
			}
			return dict;
		}

		public static Dictionary<string, UrlParaItem> GetParasinKeys(Dictionary<string, UrlParaItem> dict, HashSet<string> hashKey)
		{
			return dict.Where(v => hashKey.Contains(v.Key)).ToDictionary(v => v.Key, v => v.Value);
		}

		public static string GetValue(Dictionary<string, UrlParaItem> dict, string key)
		{
			string str = string.Empty;
			if (dict != null && dict.ContainsKey(key))
			{
				str = dict[key].Value;
			}
			return str;
		}


		public static string GetCondition(Dictionary<string, UrlParaItem> para,HashSet<string> hashParaKey)
		{
			StringBuilder sb = new StringBuilder();

			var dict = RequestResolve.GetParasinKeys(para,hashParaKey);

			foreach (var v in dict)
			{

				if (!string.IsNullOrEmpty(v.Value.Value))
				{
					if (!string.IsNullOrEmpty(sb.ToString()))
					{
						sb.Append(string.Format(" {0} ", v.Value.Relation));
					}
					sb.Append(string.Format(" {0} {1} {2} ", v.Value.Key, v.Value.Operator, v.Value.Value));
				}
			}
			string str = sb.ToString();
			if (!string.IsNullOrEmpty(str))
			{
				return string.Format(" WHERE {0} ", str);
			}

			return string.Empty;
		}
	}
}