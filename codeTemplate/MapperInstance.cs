using System;
using System.IO;
using IBatisNet.DataMapper;
using IBatisNet.DataMapper.Configuration;

namespace @namespace@.Common
{
	public class MapperInstance
	{

		private static MapperInstance _Instance;
		private static readonly object locker = new object();

		private ISqlMapper sqlMapper;

		private MapperInstance()
		{
			try
			{
				FileInfo fi = new FileInfo(AppDomain.CurrentDomain.BaseDirectory + "/sqlmap.config");
				DomSqlMapBuilder builder = new DomSqlMapBuilder();
				sqlMapper = builder.Configure(fi);
			}
			catch (Exception e)
			{
				string strStack = e.StackTrace;
				string strMsg = e.Message;
			}
		}

		private static MapperInstance Instance
		{
			get
			{
				if (_Instance == null)
				{
					lock (locker)
					{
						if (_Instance == null)
						{
							_Instance = new MapperInstance();
						}
					}
				}
				return _Instance;
			}

		}

		public static ISqlMapper SqlMapper
		{
			get
			{
				return Instance.sqlMapper;
			}
		}
	}
}