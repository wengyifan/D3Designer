 
 var currentCandinatepage = 1;
 var candinatePagesize = 20; 
 var currentSelectpage = 1;
 var selectPagesize = 20;
 

function renderCandinateTable()
 {
	var urled = window.location.href;
	var cous = urled.lastIndexOf("/") + 1;
	var uelede = urled.substring(0, cous);
	
	var pagei = $.layer({
	    type: 3,   //0-4的选择,
	    bgcolor: '',
	    border: [0],
	    shade: [0.1,'#000'],
	    loading: {
	        type: 2
	    }
	}); 
	
	//var aurl = "http://engine.e-ai.net/WeiShop/listAll@Entity@.action?pagesize=" + pagesize + "&page=" + currentpage;
	var aurl = uelede + "listAll@CandinateEntity@.action?pagesize=" + pagesize + "&page=" + currentpage;
	$.ajax({ 
        type: "get", 
        url: aurl, 
        dataType: "jsonp", 
        jsonpCallback:"renderTable@CandinateEntity@CallbackCallback",
        success: function (data) 
        { 

        	layer.close(pagei);
        	var tableData = "<thead>@CandinateThead@<th>操作</th></thead><tbody>";
        	var responseData = data.data;
        	
   	        for(var one in responseData)
   	        {
   	        	tableData += "<tr role=\"row\">";
 @CandinateColumns@
				tableData += "<td><a onclick=\'addToSelected(" + responseData[one].id + ")' href='javascript:void(0);'>添加</a>" +
						"</td>";
   	        	tableData += "</tr>";
   	        }
   	        tableData += "</tbody>";


   	    	genPageIndexDiv(data.totleNum,"@Location@","Candinate");

   			$("#@Location@").children().remove(); 
   			$("#@Location@").append(tableData);	
        }, 
        error: function (XMLHttpRequest, textStatus, errorThrown) 
        { 
        	layer.close(pagei);
        	alert("网络连接错误");
        } 
        });		
 }
 

function renderSelectedTable()
 {
	var urled = window.location.href;
	var cous = urled.lastIndexOf("/") + 1;
	var uelede = urled.substring(0, cous);
	
	var pagei = $.layer({
	    type: 3,   //0-4的选择,
	    bgcolor: '',
	    border: [0],
	    shade: [0.1,'#000'],
	    loading: {
	        type: 2
	    }
	}); 
	
	//var aurl = "http://engine.e-ai.net/WeiShop/listAll@Entity@.action?pagesize=" + pagesize + "&page=" + currentpage;
	var aurl = uelede + "listAll@CandinateEntity@.action?pagesize=" + pagesize + "&page=" + currentpage;
	$.ajax({ 
        type: "get", 
        url: aurl, 
        dataType: "jsonp", 
        jsonpCallback:"renderTable@CandinateEntity@CallbackCallback",
        success: function (data) 
        { 

        	layer.close(pagei);
        	var tableData = "<thead><th>名称</th><th>操作</th></thead><tbody>";
        	var responseData = data.data;
        	
   	        for(var one in responseData)
   	        {
   	        	tableData += "<tr role=\"row\">";
 @SelectedColumns@
				tableData += "<td><a onclick=\'removeSelected(" + responseData[one].id + ")' href='javascript:void(0);'>移除</a>" +
						"</td>";
   	        	tableData += "</tr>";
   	        }
   	        tableData += "</tbody>";


   	    	genPageIndexDiv(data.totleNum,"@Location@","Selected");

   			$("#@Location@").children().remove(); 
   			$("#@Location@").append(tableData);	
        }, 
        error: function (XMLHttpRequest, textStatus, errorThrown) 
        { 
        	layer.close(pagei);
        	alert("网络连接错误");
        } 
        });		
 }

 
 function gotoSelectPage(ind)
 {
	 currentpage  = ind;	 
	 renderSelectTable();
 }
 
 function lastSelectPage()
 {
	 currentpage  = currentpage - 1;	 
	 renderSelectTable();
 }
 
 function nextSelectPage()
 {
	 currentpage  = currentpage + 1;	 
	 renderSelectTable();
 }

 function refreshSelect()
 {
	 renderSelectTable();
 }
 

 function gotoCandinatePage(ind)
 {
	 currentpage  = ind;	 
	 renderCandinateTable();
 }
 
 function lastCandinatePage()
 {
	 currentpage  = currentpage - 1;	 
	 renderCandinateTable();
 }
 
 function nextCandinatePage()
 {
	 currentpage  = currentpage + 1;	 
	 renderCandinateTable();
 }

 function refreshCandinate()
 {
	 renderCandinateTable();
 }
 
 
 
 $(document).ready(function() 
 {
	 renderSelectedTable();
	 renderCandinateTable();
 }
 );
	
	 
 
 