
	static public void createTable()
	{
		SqlSession session = Common.getSession();
		DataMapper mapper = session.getMapper(DataMapper.class);
		try {
			mapper.createTable@entity@();
			session.commit();
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			session.close();
		}
	}
	
	static public @entity@ get(int id)
	{
		SqlSession session = Common.getSession();
		@entity@ result = null;
		DataMapper mapper = session.getMapper(DataMapper.class);
		try {
			mapper.get@entity@(id);
			session.commit();
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			session.close();
		}
		
		return result;
	}
	
	

	static public void udpate(@entity@ one)
	{
		SqlSession session = Common.getSession();
		DataMapper mapper = session.getMapper(DataMapper.class);
		try {
			mapper.update@entity@(one);
			session.commit();
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			session.close();
		}
	}
	


	static public List<@entity@> select(String condition)
	{
		SqlSession session = Common.getSession();
		List<@entity@> result = new ArrayList<@entity@>();
		DataMapper mapper = session.getMapper(DataMapper.class);
		try {
			result = mapper.select@entity@(condition);
			session.commit();
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			session.close();
		}
		
		return result;
	}
	
	
	