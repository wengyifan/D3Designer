

 $(document).ready(function() 
 {
	 getMenus();
	 @Entity@TableLocation = "page-tableCard-table";
	 renderTable@Entity@();
@renderChoose@
 }
 );


 
 function onSearch()
 {
	 onSearch@Entity@();
 }
 
 
 function gotoPage(ind)
 {
	 currentpage  = ind;	 
	 renderTable@Entity@();
 }
 
 function lastPage()
 {
	 currentpage  = currentpage - 1;	 
	 renderTable@Entity@();
 }
 
 function nextPage()
 {
	 currentpage  = currentpage + 1;	 
	 renderTable@Entity@();
 }

 function refresh()
 {
	 renderTable@Entity@();	 
 }