			SqlSession session = Common.getSession();
			DataMapper mapper = session.getMapper(DataMapper.class);
			try {
				@SqlProcess@
				session.commit();
			} catch (Exception e) {
				e.printStackTrace();
	
			} finally {
				session.close();
			}
	