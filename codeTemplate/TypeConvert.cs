using System;
using System.Globalization;

namespace @namespace@.Common
{
	public static class TypeConvert
	{

		public static string ValueOfString(string str)
		{
			return str;
		}

		public static byte ValueOfInt8(string str)
		{
			byte n;
			byte.TryParse(str, out n);
			return n;
		}

		public static short ValueOfInt16(string str)
		{
			short n = 0;
			short.TryParse(str, out n);
			return n;
		}

		public static int ValueOfInt32(string str)
		{
			int n;
			int.TryParse(str, out n);
			return n;
		}

		public static long ValueOfInt64(string str)
		{
			long n;
			long.TryParse(str, out n);
			return n;
		}

		public static double ValueOfDouble(string str)
		{
			double n;
			double.TryParse(str, out n);
			return n;
		}
		public static DateTime ValueOfDateTime(string str)
		{
			DateTime n;
			DateTime.TryParseExact(str,"yyyy-MM-dd HH:mm:ss",null, DateTimeStyles.None, out n);
			return n;
		}
	}
}