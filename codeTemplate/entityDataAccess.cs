

		public static @entity@ Find(@entity@ obj)
		{
			if (obj != null)
			{
				const string strKey = "@entity@.Find";
				try
				{
					return MapperInstance.SqlMapper.QueryForObject<@entity@>(strKey, obj);
				}
				catch (Exception e)
				{
					string strStack = e.StackTrace;
					string strMsg = e.Message;
				}
			}
			return null;
		}

		public static void Insert(@entity@ obj)
		{
			if (obj != null)
			{
				const string strId = "@entity@.Insert";
				const string strId2 = "@entity@.LastID";
				try
				{
					MapperInstance.SqlMapper.Insert(strId, obj);

					obj.id = MapperInstance.SqlMapper.QueryForObject<NumBase>(strId2, obj).Num;
				}
				catch (Exception e)
				{
					string strStack = e.StackTrace;
					string strMsg = e.Message;
				}
			}
		}

		public static void Update(@entity@ obj)
		{
			if (obj != null)
			{
				const string strId = "@entity@.Update";
				try
				{
					MapperInstance.SqlMapper.Update(strId, obj);
				}
				catch (Exception e)
				{
					string strStack = e.StackTrace;
					string strMsg = e.Message;
				}
			}
		}

		public static void Delete(@entity@ obj)
		{
			if (obj != null)
			{
				const string strId = "@entity@.Delete";
				try
				{
					MapperInstance.SqlMapper.Delete(strId, obj);
				}
				catch (Exception e)
				{
					string strStack = e.StackTrace;
					string strMsg = e.Message;
				}
			}
		}

		public static IList<@entity@> FindList(string strPara)
		{
			const string strId = "@entity@.FindList";
			try
			{
				IList<@entity@> result = MapperInstance.SqlMapper.QueryForList<@entity@>(strId, strPara);
				return result;
			}
			catch (Exception e)
			{
				string strStack = e.StackTrace;
				string strMsg = e.Message;
				return null;
			}
		}


		public static int Count(string strPara)
		{
			const string strId = "@entity@.Count";
			try
			{
				var n = MapperInstance.SqlMapper.QueryForObject<NumBase>(strId, strPara);
				return n.Num;
			}
			catch (Exception e)
			{
				string strStack = e.StackTrace;
				string strMsg = e.Message;
				return 0;
			}
		}

