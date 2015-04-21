
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
			result = mapper.get@entity@(id);
			session.commit();
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			session.close();
		}
		
		return result;
	}
	

	static public void delete(@entity@ one)
	{
		SqlSession session = Common.getSession();
		DataMapper mapper = session.getMapper(DataMapper.class);
		try {
			mapper.delete@entity@(one);
			session.commit();
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			session.close();
		}
		
	}
	

	static public void update(@entity@ one)
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


	static public void insert(@entity@ one)
	{
		SqlSession session = Common.getSession();
		DataMapper mapper = session.getMapper(DataMapper.class);
		try {
			mapper.insert@entity@(one);
			int id = mapper.getLastID();
			one.setId(id);
			session.commit();
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			session.close();
		}
	}
	



	static public int count(String condition)
	{
		SqlSession session = Common.getSession();
		int result = 0;
		DataMapper mapper = session.getMapper(DataMapper.class);
		try {
			result = mapper.count@entity@(condition);
			session.commit();
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			session.close();
		}
		
		return result;
	}
	
	