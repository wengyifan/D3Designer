
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
    		
            
        	var formData = $('#signupForm').serialize();
        	var aurl = "http://engine.e-ai.net/WeiShop/save@Entity@.action";
        	$.ajax({ 
                type: "get", 
                url: aurl, 
                dataType: "jsonp", 
                jsonpCallback:"renderTableCatagoryCallback",
                success: function (data) 
                { 
                	layer.close(pagei);
                }, 
                error: function (XMLHttpRequest, textStatus, errorThrown) 
                { 
                } 
                });		
        	
          }
    });

}
