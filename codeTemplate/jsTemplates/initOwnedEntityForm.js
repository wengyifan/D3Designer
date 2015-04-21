
function renderDiv@Entity@()
 {	 

	var id =UrlParm.parm("id");
	if(id == null)
		return;
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
 


function validate@Entity@()
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
        	var owner =UrlParm.parm("@ownerEntity@ID");
        	var formData = $('#signupForm').serialize();
        	formData += "&@ownerVari@=" + owner;
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



 $(document).ready(function() 
 {
	 validate@Entity@();
	 renderDiv@Entity@();
@renderChoose@
 }
 );
 
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
 
	
 