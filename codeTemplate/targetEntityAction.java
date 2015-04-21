

	public void listAll@targetEntity@Api() {
		HttpServletRequest request = ServletActionContext.getRequest();
    	HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");

		String currentPage = request.getParameter("page");
		String pageSize = request.getParameter("pagesize");
		String orderBy = request.getParameter("orderby");
		String isDscOrder = request.getParameter("isDesc");
		String callback = request.getParameter("callback");


@filterParasFromRequest@
		
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
			
			
			condition +=  " limit " + pageSizeInt + " offset " + offsetInt;
			
			List< @targetEntity@ > selectResult = @targetEntity@.select(@filterParas@condition);
			int count = @targetEntity@.count("");
			
			List@targetEntity@Response result = new List@targetEntity@Response();
			result.setReturnCode("success");
			result.setData(selectResult);
			result.setTotleNum(count);

		    Gson gson = new GsonBuilder()
		      .setDateFormat("yyyy-MM-dd HH:mm:ss")  
		      .create();  
		    
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
	
	
	
	


	public void get@targetEntity@Api() {
		HttpServletRequest request = ServletActionContext.getRequest();
    	HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");

		String id = request.getParameter("id");
		String callback = request.getParameter("callback");

		PrintWriter out;
		
		try {
						
			@targetEntity@ selectResult = @targetEntity@.get(Integer.valueOf(id));
			Get@targetEntity@Response result = new Get@targetEntity@Response();
			result.setReturnCode("success");
			result.setData(selectResult);
			
		    Gson gson = new GsonBuilder()
		      .setDateFormat("yyyy-MM-dd HH:mm:ss")  
		      .create();  
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
	



	public void save@targetEntity@Api() {
		HttpServletRequest request = ServletActionContext.getRequest();
    	HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");

		String id = request.getParameter("id");
		String callback = request.getParameter("callback");
		java.text.SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy-MM-dd");
				
		PrintWriter out;
		
		try {
						
			AjaxBaseResponse result = new AjaxBaseResponse();
						
			@targetEntity@ selectResult = null;
			if(id == null )
				selectResult = new @targetEntity@();
			else
			{
				selectResult = @targetEntity@.get(Integer.valueOf(id));
			}

@entityAssignment@
			
			if(id == null )
				@targetEntity@.insert(selectResult);
			else
				@targetEntity@.update(selectResult);
			
			result.setReturnCode("success");
			result.setData(String.valueOf(selectResult.getId()));
			
			Gson gson = new Gson();
			String backJson = gson.toJson(result);
			out = response.getWriter();
			if(callback!=null && !callback.equals(""))
				out.print(callback + "(" + backJson + ")");
			else
				out.print(backJson);
			out.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	public void del@targetEntity@Api() {
		HttpServletRequest request = ServletActionContext.getRequest();
    	HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");

		String id = request.getParameter("id");
		String callback = request.getParameter("callback");
				
		PrintWriter out;
		
		try {
						
			AjaxBaseResponse result = new AjaxBaseResponse();
						

			@targetEntity@ newOne = new @targetEntity@();
			newOne.setId(Integer.valueOf(id));
			@targetEntity@.delete(newOne);
			
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
	
	
	
	
	
