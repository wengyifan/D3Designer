	
	static public @entity@ createInstance(@paras@)
	{
		@entity@ newInstance = new @entity@();
		int id = 0;
		@assignment@	
		SqlSession session = Common.getSession();
		DataMapper mapper = session.getMapper(DataMapper.class);
		try {
			mapper.insert@entity@(newInstance);
			id = mapper.getLastID();
			newInstance.setID(id);
			session.commit();
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			session.close();
		}
		
		return newInstance;
	}