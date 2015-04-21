
function onChoose@Choose@(id)
 {	 

	var urled = window.location.href;
	var cous = urled.lastIndexOf("/") + 1;
	var uelede = urled.substring(0, cous);

	
	var aurl = uelede + "get@Entity@.action?id=" + id;
	$.ajax({ 
        type: "get", 
        url: aurl, 
        dataType: "json",         
        success: function (data) 
        { 
 @DivSetting@
        }, 
        error: function (XMLHttpRequest, textStatus, errorThrown) 
        { 
        } 
        });		
 }
 


 