
		public string @operation@Api(Dictionary<string, UrlParaItem> para)
		{
			string response;
			var result = new @OpResponse@();
			try
			{
@setValue@
@document@


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
				response = JsonConvert.SerializeObject(result, Formatting.Indented);
				return response;
			}

		}
