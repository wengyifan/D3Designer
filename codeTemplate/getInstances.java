

	static public List<@entity@> select(@filterPparas@String pageControl)
	{
		SqlSession session = Common.getSession();
		List<@entity@> result = new ArrayList<@entity@>();
		DataMapper mapper = session.getMapper(DataMapper.class);
		try {
			String filtercondition = "";
@filtercondition@
			result = mapper.select@entity@(filtercondition + pageControl);
			session.commit();
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			session.close();
		}
		
		return result;
	}
	