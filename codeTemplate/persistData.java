	public void persistData()
	{
		SqlSession session = Common.getSession();
		DataMapper mapper = session.getMapper(DataMapper.class);
		try {
			if(id == 0){
				mapper.insert@entity@(this);
				id = mapper.getLastID();
				session.commit();
			}
			else
			{
				mapper.update@entity@(this);
			}
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			session.close();
		}
		
		return id;
	}