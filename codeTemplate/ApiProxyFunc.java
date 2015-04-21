
	public @ApiResponse@ @ApiFuncName@(@ApiParas@)
	{
		@ApiResponse@ result = null;
		Gson gson = new Gson();
		try{
			String url = "@ApiUrl@?" + @ApiUrlParaAssignment@;
					String dataReturn = HttpGetter.httpGet(url, "GET", ""); 					
					dataReturn = dataReturn.replace("\"\"", "null");
					result = gson.fromJson(dataReturn, @ApiResponse@.class);		
					
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	 
	}

