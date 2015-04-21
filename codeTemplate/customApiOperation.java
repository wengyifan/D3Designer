
	public void @operation@Api() {
		HttpServletRequest request = ServletActionContext.getRequest();
    	HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");
		
		String callback = request.getParameter("callback");

		PrintWriter out;
		
		@paras@

		try {

			Gson gson = new Gson();
			

			
			@OpResponse@Response result = new @OpResponse@Response();
			

@document@
			//business logic - @operation@
			//logic ends		
						
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
	
	
	