			if(@para@ != null && !"".equals(@para@)){
				if(!"".equals(filtercondition))
					filtercondition += " and ";				
				filtercondition += "@paraColumn@ = " + @paraValue@; 
			}