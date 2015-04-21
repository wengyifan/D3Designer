 
 var currentpage = 1;
 var pagesize = 20;
 
 

 var @Entity@TableLocation;
 var memConditon = null;
 
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
	if(condition != null){
		aurl += condition;
		memConditon = condition;
	}
	else if(memConditon !=null)
		aurl += memConditon;
		
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
				tableData += "<td><a onclick=\'edit@Entity@(" + responseData[one].id + ")' href='javascript:void(0);'>编辑</a>" +
						" | " +
						"<a onclick=\'del@Entity@(" + responseData[one].id + ")' href='javascript:void(0);'>删除</a>" +
						"@operation@"
						+
						"</td>";
   	        	tableData += "</tr>";
   	        }
   	        tableData += "</tbody>";


   	    	genPageIndexDiv(data.totleNum,@Entity@TableLocation);

   			$("#" + @Entity@TableLocation).children().remove(); 
   			$("#" + @Entity@TableLocation).append(tableData);	
        }, 
        error: function (XMLHttpRequest, textStatus, errorThrown) 
        { 
        	layer.close(pagei);
        	alert("网络连接错误");
        } 
        });		
 }
 


 function edit@Entity@(id)
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

 
 function goto@Entity@Page(ind)
 {
	 currentpage  = ind;	 
	 renderTable@Entity@();
 }
 
 function last@Entity@Page()
 {
	 currentpage  = currentpage - 1;	 
	 renderTable@Entity@();
 }
 
 function next@Entity@Page()
 {
	 currentpage  = currentpage + 1;	 
	 renderTable@Entity@();
 }

 function refresh@Entity@()
 {
	 renderTable@Entity@();	 
 }
 
 @onSearch@

 function create@Entity@()
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
 
 
 // form edit
 

 function renderDiv@Entity@(location)
  {	 

 	var id =UrlParm.parm("id");
 	if(id == null){
 		$("#page-id").attr("disabled","disabled");
 		return;
 	}
 	 var pagei = $.layer({
 		    type: 3,   //0-4的选择,
 		    bgcolor: '',
 		    border: [0],
 		    shade: [0.1,'#000'],
 		    loading: {
 		        type: 2
 		    }
 		}); 
 		
 	 

 	var urled = window.location.href;
 	var cous = urled.lastIndexOf("/") + 1;
 	var uelede = urled.substring(0, cous);

 	
 	var aurl = uelede + "get@Entity@.action?id=" + id;
 	$.ajax({ 
         type: "get", 
         url: aurl, 
         dataType: "jsonp", 
         jsonpCallback:"render@Entity@Callback",
         success: function (data) 
         { 
  @DivSetting@
         	layer.close(pagei);
         }, 
         error: function (XMLHttpRequest, textStatus, errorThrown) 
         { 
         } 
         });		
  }
  


 function validate@Entity@(location)
 {
     $("#signupForm").validate({
         rules: {
         	@rules@
         },
         messages: {
             @messages@
         },
         
         submitHandler:function(form){

         	var pagei = $.layer({
     		    type: 3,   //0-4的选择,
     		    bgcolor: '',
     		    border: [0],
     		    shade: [0.1,'#000'],
     		    loading: {
     		        type: 2
     		    }
     		}); 

 @chooseNameSetting@


         	var urled = window.location.href;
         	var cous = urled.lastIndexOf("/") + 1;
         	var uelede = urled.substring(0, cous);
         	var formData = $('#signupForm').serialize();
         	var aurl = uelede + "/save@Entity@.action";
         	var id =UrlParm.parm("id");
         	if(id != null)
         		aurl += "?id=" + id;
         
         	$.ajax({ 
                 type: "POST", 
                 url: aurl, 
                 dataType: "jsonp", 
                 jsonpCallback:"save@Entity@Callback",
                 data: formData,
                 success: function (data) 
                 { 
                 	layer.close(pagei);
                 	var index = parent.layer.getFrameIndex(window.name);
                 	parent.renderTable@Entity@();
                 	parent.layer.close(index);
                 }, 
                 error: function (XMLHttpRequest, textStatus, errorThrown) 
                 { 
                 } 
                 });	
         	
         	
         	
           }
     });

 }


  
  @renderChooseFuncs@
  
  

  function chooseSelect(select,val)
  {
  	if(val == undefined)
  		return;
  	
  	val = val + "";
  	
  	if(val.indexOf(',') != -1)
  	{
  		var hh=val.split(',');
  		$(select).children().each(function(){
  			 for(var i=0;i<hh.length;i++){
  				 if($(this).attr('value')==hh[i]){
  					 $(this).attr('selected','selected');
  				 }
  			 }
  		});
  	}
  	else
  		$(select).val(val);	
  }
  
  
  (function($){
 	 $.fn.serializeJson=function(){
 	 var serializeObj={};
 	 var array=this.serializeArray();
 	 var str=this.serialize();
 	 $(array).each(function(){
 	 if(serializeObj[this.name]){
 	 if($.isArray(serializeObj[this.name])){
 	 serializeObj[this.name].push(this.value);
 	 }else{
 	 serializeObj[this.name]=[serializeObj[this.name],this.value];
 	 }
 	 }else{
 	 serializeObj[this.name]=this.value;
 	 }
 	 });
 	 return serializeObj;
 	 };
 	 })(jQuery);
  
 	
  
 