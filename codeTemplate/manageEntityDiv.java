



	public void get@targetEntity@Table() {
		HttpServletRequest request = ServletActionContext.getRequest();
    	HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");

		String currentPage = request.getParameter("page");
		String pageSize = request.getParameter("pagesize");
		String orderBy = request.getParameter("orderby");
		String isDscOrder = request.getParameter("isDesc");
		String mode = request.getParameter("mode");

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
			

			if(mode == null || "".equals(mode))
			{
				mode = "div";
			}
			
			

			if(pageSize != null && !"".equals(pageSize) && currentPage != null && !"".equals(currentPage))
			{
				pageSizeInt = Integer.valueOf(pageSize);
				int currentPageInt = Integer.valueOf(currentPage);
				offsetInt = pageSizeInt * (currentPageInt -1);				
			}
			
			condition += " limit " + pageSizeInt + " offset " + offsetInt;
			
			List< @targetEntity@ > selectResult = @targetEntity@.select(condition);
			
			String resDiv = "<thead><tr>@tableHead@</tr></thead>";		
			resDiv += "<tbody>";
			for(@targetEntity@ one:selectResult)
			{
				resDiv += "<tr>";
@tableData@
				resDiv += "</tr>";
			}
			resDiv += "</tbody>";
			

			out = response.getWriter();
			
			if(mode.equals("div"))
			{
				out.print(resDiv);
			}
			else if(mode.equals("ajaxdiv"))
			{
				AjaxDivResponse result = new AjaxDivResponse();
				result.setReturnCode("success");
				result.setDivType("table");
				result.setDivContent(resDiv);
				
				Gson gson = new Gson();
				String backJson = gson.toJson(result);
				out.print(backJson);
			}
			
			
			out.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	


	public void view@targetEntity@Div() {
		HttpServletRequest request = ServletActionContext.getRequest();
    	HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");

		String id = request.getParameter("id");

		PrintWriter out;
		
		try {
						
			@targetEntity@ selectResult = @targetEntity@.get(Integer.valueOf(id));
			
			String resDiv = "@dataHead@";	
			
@divData@
			
			resDiv += "@dataButtom@";
			
			
			out = response.getWriter();
			out.print(resDiv);
			out.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
