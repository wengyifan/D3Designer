
	public void listAll@connectEntity@Api() {
		HttpServletRequest request = ServletActionContext.getRequest();
    	HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");

		String currentPage = request.getParameter("page");
		String pageSize = request.getParameter("pagesize");
		String orderBy = request.getParameter("orderby");
		String isDscOrder = request.getParameter("isDesc");
		String callback = request.getParameter("callback");

		PrintWriter out;
		
		try {
			String condition = "";
			int pageSizeInt = 1000;
			int offsetInt = 0;
			if(orderBy != null && !"".equals(orderBy))
			{
								
				if(isDscOrder != null && !"".equals(isDscOrder))
					condition += "order by " + orderBy + " desc ";
				else
					condition += "order by " + orderBy + " asc ";
					
			}
			

			if(pageSize != null && !"".equals(pageSize) && currentPage != null && !"".equals(currentPage))
			{
				pageSizeInt = Integer.valueOf(pageSize);
				int currentPageInt = Integer.valueOf(currentPage);
				offsetInt = pageSizeInt * (currentPageInt -1);				
			}
			
			condition += " limit " + pageSizeInt + " offset " + offsetInt;
			
			List< @connectEntity@ > selectResult = @connectEntity@.select(condition);
			int count = @connectEntity@.count("");
			
			List@connectEntity@Response result = new List@connectEntity@Response();
			result.setReturnCode("success");
			result.setData(selectResult);
			result.setTotleNum(count);
			
			Gson gson = new Gson();
			String backJson = gson.toJson(result);
			out = response.getWriter();

			if(callback!=null && !callback.equals(""))
				out.print(callback + "(" + backJson + ")");
			else
				out.print(backJson);
			out.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	


	public void add@connectEntity@Api() {
		HttpServletRequest request = ServletActionContext.getRequest();
    	HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");

		String ownerID = request.getParameter("owner");
		String targetID = request.getParameter("target");
		String callback = request.getParameter("callback");
				
		PrintWriter out;
		
		try {
						
			AjaxBaseResponse result = new AjaxBaseResponse();
						
			
			@connectEntity@ connectEntity = new @connectEntity@();
			connectEntity.set@ownerEntity@(Integer.valueOf(ownerID));
			connectEntity.set@targetEntity@ID(Integer.valueOf(targetID));
			
			@connectEntity@.insert(connectEntity);
				
				
			result.setReturnCode("success");
			
			Gson gson = new Gson();
			String backJson = gson.toJson(result);
			out = response.getWriter();
			if(callback!=null && !callback.equals(""))
				out.print(callback + "(" + backJson + ")");
			else
				out.print(backJson);
			out.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	

	public void del@connectEntity@Api() {
		HttpServletRequest request = ServletActionContext.getRequest();
    	HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");

		String id = request.getParameter("id");
		String callback = request.getParameter("callback");
				
		PrintWriter out;
		
		try {
						
			AjaxBaseResponse result = new AjaxBaseResponse();
						

			@connectEntity@ theOne = new @connectEntity@();
			theOne.setId(Integer.valueOf(id));
			@connectEntity@.delete(theOne);
			
			result.setReturnCode("success");
			
			Gson gson = new Gson();
			String backJson = gson.toJson(result);
			out = response.getWriter();
			if(callback!=null && !callback.equals(""))
				out.print(callback + "(" + backJson + ")");
			else
				out.print(backJson);
			out.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	
