 
 var currentpage = 1;
 var pagesize = 10;
 


 function render@ownerEntity@()
  {	 

 	var id =UrlParm.parm("@ownerEntity@ID");
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

 	
 	var aurl = uelede + "get@ownerEntity@.action?id=" + id;
 	$.ajax({ 
         type: "get", 
         url: aurl, 
         dataType: "jsonp", 
         jsonpCallback:"render@ownerEntity@Callback",
         success: function (data) 
         { 
        	 var divdata = "<div class=\"row form-body form-horizontal\">";
  @renderOwnerInfo@
  		     divdata += "</div>";
			 $("#page-ownerCard-content").append(divdata);
			 
         	layer.close(pagei);
         }, 
         error: function (XMLHttpRequest, textStatus, errorThrown) 
         { 
         } 
         });		
  }
  
 
 $(document).ready(function() 
 {
	 getMenus();
	 @Entity@TableLocation = "page-tableCard-table";
 	 var ownerID =UrlParm.parm("@ownerEntity@ID");
	 renderTable@Entity@("&@ownerVari@=" + ownerID);
	 render@ownerEntity@();
	 
@renderChoose@
 }
 );
	
	 

 function create@Entity@()
  {	 
 	 var ownerID =UrlParm.parm("@ownerEntity@ID");
	 var pagei =  $.layer({
         type : 2,
         title: '创建 - @EntityName@',
         shadeClose: false,
         scrolling:true,
         maxmin: true,
         fix : true,  
         area: ["80%", "80%"],                     
         iframe: {
             src : '@Entity@EditPage.html?@ownerEntity@ID=' + ownerID
         } 
     });
  }
 
 