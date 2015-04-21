 
 var currentpage = 1;
 var pagesize = 10;
 

function renderTable@Entity@(condition)
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
	var aurl = uelede + "listAll@Entity@.action?pagesize=" + pagesize + "&page=" + currentpage;
	if(condition != null)
		aurl += condition;
	$.ajax({ 
        type: "get", 
        url: aurl, 
        dataType: "jsonp", 
        jsonpCallback:"renderTable@Entity@CallbackCallback",
        success: function (data) 
        { 

        	layer.close(pagei);
        	var tableData = "<thead>@thead@<th>操作</th></thead><tbody>";
        	var responseData = data.data;
        	
   	        for(var one in responseData)
   	        {
   	        	tableData += "<tr role=\"row\">";
 @Columns@
				tableData += "<td><a onclick=\'eidt@Entity@(" + responseData[one].id + ")' href='javascript:void(0);'>编辑</a>" +
						" | " +
						"<a onclick=\'del@Entity@(" + responseData[one].id + ")' href='javascript:void(0);'>删除</a>" +
						"@operation@"
						+
						"</td>";
   	        	tableData += "</tr>";
   	        }
   	        tableData += "</tbody>";


   	    	genPageIndexDiv(data.totleNum,"@Location@");

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
 


 function eidt@Entity@(id)
 {
	 var pagei =  $.layer({
         type : 2,
         title: '编辑 - @EntityName@',
         shadeClose: false,
         scrolling:true,
         maxmin: true,
         fix : true,  
         area: ["80%", "80%"],                     
         iframe: {
             src : '@Entity@EditPage.html?id=' + id
         } 
     });
 }
 

 function del@Entity@(id)
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
		
		
		var aurl = uelede + "del@Entity@.action?id=" + id;
		$.ajax({ 
	        type: "get", 
	        url: aurl, 
	        dataType: "jsonp", 
	        jsonpCallback:"del@Entity@CallbackCallback",
	        success: function (data) 
	        { 
	        	layer.close(pagei);
	        	renderTable@Entity@();
	        }, 
	        error: function (XMLHttpRequest, textStatus, errorThrown) 
	        { 
	        	layer.close(pagei);
	        	alert("网络连接错误");
	        } 
	        });		
 }

 
 function gotoPage(ind)
 {
	 currentpage  = ind;	 
	 renderTable@Entity@()
 }
 
 function lastPage()
 {
	 currentpage  = currentpage - 1;	 
	 renderTable@Entity@()
 }
 
 function nextPage()
 {
	 currentpage  = currentpage + 1;	 
	 renderTable@Entity@()
 }

 function refresh()
 {
	 renderTable@Entity@();	 
 }
 
 @onSearch@

 function create()
 {
	 var pagei =  $.layer({
         type : 2,
         title: '创建 - @EntityName@',
         shadeClose: false,
         scrolling:true,
         maxmin: true,
         fix : true,  
         area: ["80%", "80%"],                     
         iframe: {
             src : '@Entity@EditPage.html'
         } 
     });
 }
 

 $(document).ready(function() 
 {
	 getMenus();
	 renderTable@Entity@();
@renderChoose@
 }
 );
	
	 

 @renderChooseFuncs@
 
 
 