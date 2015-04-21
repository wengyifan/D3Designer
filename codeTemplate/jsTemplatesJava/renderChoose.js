
function renderChoose@Entity@(location,nonchoose)
{
	var urled = window.location.href;
	var cous = urled.lastIndexOf("/") + 1;
	var uelede = urled.substring(0, cous);	
	var aurl = uelede + "listAll@Entity@.action";
	$.ajax({ 
        type: "get", 
        url: aurl, 
        dataType: "jsonp", 
        jsonpCallback:"renderChoose@Entity@Callback",
        success: function (data) 
        { 
        	var responseData = data.data;      
        	var chooseData = "";
        	if(nonchoose != null)
        		chooseData += "<option value=\"\">" + nonchoose + "</option>";
   	        for(var one in responseData)
   	        {
   	        	chooseData += "<option value=\"" + responseData[one].id + "\">" + responseData[one].name + "</option>";
   	        }  	        

   	       
   	        
@rederCode@
        }, 
        error: function (XMLHttpRequest, textStatus, errorThrown) 
        { 
        	alert("网络连接错误");
        } 
    });	
}

    	